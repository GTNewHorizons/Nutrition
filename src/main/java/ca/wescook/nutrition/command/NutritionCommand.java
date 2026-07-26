package ca.wescook.nutrition.command;

import static com.gtnewhorizon.gtnhlib.util.CommandUtils.argument;
import static com.gtnewhorizon.gtnhlib.util.CommandUtils.literal;
import static com.gtnewhorizon.gtnhlib.util.CommandUtils.success;

import java.util.concurrent.CompletableFuture;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

import com.gtnewhorizon.gtnhlib.brigadier.BrigadierApi;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import ca.wescook.nutrition.api.NutritionManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.utility.DataImporter;

public class NutritionCommand {

    private static final String ARG_PLAYER = "player";
    private static final String ARG_NUTRIENT = "nutrient";
    private static final String ARG_VALUE = "value";

    public static void register() {
        // spotless:off
        BrigadierApi.getCommandDispatcher()
            .register(literal("nutrition").executes(ctx -> success(ctx.getSource(), "command.nutrition:usage"))

                .then(literal("help")
                    .executes(NutritionCommand::executeHelp))

                .then(literal("get")
                    .then(argument(ARG_PLAYER, StringArgumentType.word())
                        .suggests(NutritionCommand::getPlayerSuggestions)
                        .executes(NutritionCommand::executeGetAll) // no nutrient provided, list all
                        .then(argument(ARG_NUTRIENT, NutrientArgumentType.nutrient())
                            .executes(NutritionCommand::executeGet)))) // nutrient provided, list one



                .then(literal("set")
                    .requires(src -> src.canCommandSenderUseCommand(2, ""))
                    .then(argument(ARG_PLAYER, StringArgumentType.word())
                        .suggests(NutritionCommand::getPlayerSuggestions)
                        .then(argument(ARG_NUTRIENT, NutrientArgumentType.nutrient())
                            .then(argument(ARG_VALUE, FloatArgumentType.floatArg(0, 100))
                                .executes(ctx -> executeSet(ctx, Action.SET))))))

                .then(literal("add")
                    .requires(src -> src.canCommandSenderUseCommand(2, ""))
                    .then(argument(ARG_PLAYER, StringArgumentType.word())
                        .suggests(NutritionCommand::getPlayerSuggestions)
                        .then(argument(ARG_NUTRIENT, NutrientArgumentType.nutrient())
                            .then(argument(ARG_VALUE, FloatArgumentType.floatArg(0, 100))
                                .executes(ctx -> executeSet(ctx, Action.ADD))))))

                .then(literal("subtract")
                    .requires(src -> src.canCommandSenderUseCommand(2, ""))
                    .then(argument(ARG_PLAYER, StringArgumentType.word())
                        .suggests(NutritionCommand::getPlayerSuggestions)
                        .then(argument(ARG_NUTRIENT, NutrientArgumentType.nutrient())
                            .then(argument(ARG_VALUE, FloatArgumentType.floatArg(0, 100))
                                .executes(ctx -> executeSet(ctx, Action.SUBTRACT))))))

                .then(literal("reset")
                    .requires(src -> src.canCommandSenderUseCommand(2, ""))
                    .then(argument(ARG_PLAYER, StringArgumentType.word())
                        .suggests(NutritionCommand::getPlayerSuggestions)
                        .executes(NutritionCommand::executeResetAll) // no nutrient provided, reset all
                        .then(argument(ARG_NUTRIENT, NutrientArgumentType.nutrient())
                            .executes(NutritionCommand::executeReset)))) // nutrient provided, reset one

                .then(literal("reload")
                    .requires(src -> src.canCommandSenderUseCommand(3, ""))
                    .executes(NutritionCommand::executeReload)));
        // spotless:on
    }

    private static int executeHelp(CommandContext<ICommandSender> ctx) {
        ICommandSender sender = ctx.getSource();
        sender.addChatMessage(new ChatComponentTranslation("command.nutrition:help.1"));
        sender.addChatMessage(new ChatComponentTranslation("command.nutrition:help.2"));
        sender.addChatMessage(new ChatComponentTranslation("command.nutrition:help.3"));
        sender.addChatMessage(new ChatComponentTranslation("command.nutrition:help.4"));
        sender.addChatMessage(new ChatComponentTranslation("command.nutrition:help.5"));
        sender.addChatMessage(new ChatComponentTranslation("command.nutrition:help.6"));
        sender.addChatMessage(new ChatComponentTranslation("command.nutrition:help.7"));

        return Command.SINGLE_SUCCESS;
    }

    private static int executeGet(CommandContext<ICommandSender> ctx) {
        EntityPlayer player = getPlayerArgument(ctx);
        Nutrient nutrient = NutrientArgumentType.getNutrient(ctx, ARG_NUTRIENT);
        float value = NutritionManager.instance()
            .get(player, nutrient);

        return success(
            ctx.getSource(),
            "command.nutrition:get",
            nutrient.getCapitalizedName(),
            String.format("%.2f", value));
    }

    private static int executeGetAll(CommandContext<ICommandSender> ctx) {
        EntityPlayer player = getPlayerArgument(ctx);

        int successes = success(ctx.getSource(), "command.nutrition:get_all_header", player.getCommandSenderName());
        for (Nutrient nutrient : NutrientList.get()) {
            float value = NutritionManager.instance()
                .get(player, nutrient);
            successes += success(
                ctx.getSource(),
                "command.nutrition:get_all_line",
                nutrient.getCapitalizedName(),
                String.format("%.2f", value));
        }

        return successes;
    }

    private static int executeSet(CommandContext<ICommandSender> ctx, Action action) {
        EntityPlayer player = getPlayerArgument(ctx);
        Nutrient nutrient = NutrientArgumentType.getNutrient(ctx, ARG_NUTRIENT);
        float value = FloatArgumentType.getFloat(ctx, ARG_VALUE);

        switch (action) {
            case SET -> NutritionManager.instance()
                .set(player, nutrient, value);

            case ADD -> NutritionManager.instance()
                .add(player, nutrient, value);

            case SUBTRACT -> NutritionManager.instance()
                .subtract(player, nutrient, value);
        }

        float result = NutritionManager.instance()
            .get(player, nutrient);
        return success(
            ctx.getSource(),
            "command.nutrition:set",
            nutrient.getCapitalizedName(),
            player.getCommandSenderName(),
            String.format("%.2f", result));
    }

    private static int executeReset(CommandContext<ICommandSender> ctx) {
        EntityPlayer player = getPlayerArgument(ctx);
        Nutrient nutrient = NutrientArgumentType.getNutrient(ctx, ARG_NUTRIENT);
        NutritionManager.instance()
            .reset(player, nutrient);

        return success(ctx.getSource(), "command.nutrition:reset", nutrient.name, player.getCommandSenderName());
    }

    private static int executeResetAll(CommandContext<ICommandSender> ctx) {
        EntityPlayer player = getPlayerArgument(ctx);
        NutritionManager.instance()
            .reset(player);

        return success(ctx.getSource(), "command.nutrition:reset_all", player.getCommandSenderName());
    }

    private static int executeReload(CommandContext<ICommandSender> ctx) {
        DataImporter.reload();
        DataImporter.updatePlayerCapabilitiesOnServer(MinecraftServer.getServer());

        return success(ctx.getSource(), "command.nutrition:reload");
    }

    private static CompletableFuture<Suggestions> getPlayerSuggestions(CommandContext<ICommandSender> ctx,
        SuggestionsBuilder builder) {
        for (Object p : ctx.getSource()
            .getEntityWorld().playerEntities) {
            if (p instanceof EntityPlayer ep) builder.suggest(ep.getCommandSenderName());
        }
        return builder.buildFuture();
    }

    private static EntityPlayer getPlayerArgument(CommandContext<ICommandSender> ctx) {
        String name = StringArgumentType.getString(ctx, ARG_PLAYER);
        return ctx.getSource()
            .getEntityWorld()
            .getPlayerEntityByName(name);
    }

    private enum Action {
        SET,
        ADD,
        SUBTRACT
    }
}
