package ca.wescook.nutrition.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import com.google.common.primitives.Floats;

import ca.wescook.nutrition.data.PlayerDataHandler;
import ca.wescook.nutrition.network.Sync;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;

public class ChatCommand extends CommandBase {

    private final List<String> playerSubCommands = Arrays.asList("get", "set", "add", "subtract", "reset"); // Suggest
                                                                                                            // player
                                                                                                            // names
                                                                                                            // following
                                                                                                            // these
                                                                                                            // subcommands
    private final String helpString = "/nutrition <get/set/add/subtract/reset/reload> <player> <nutrient> <value>";

    private enum actions {
        SET,
        ADD,
        SUBTRACT
    }

    @Override
    public String getCommandName() {
        return "nutrition";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return helpString;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) { // Sub-commands list
            return getListOfStringsFromIterableMatchingLastWord(
                args,
                Arrays.asList("get", "set", "add", "subtract", "reset", "reload"));
        } else if (args.length == 2 && playerSubCommands.contains(args[0])) { // Player list/reload command
            return getListOfStringsMatchingLastWord(
                args,
                MinecraftServer.getServer()
                    .getAllUsernames());
        } else if (args.length == 3) { // Nutrients list
            List<String> nutrientList = new ArrayList<>();
            for (Nutrient nutrient : NutrientList.get()) {
                nutrientList.add(nutrient.name);
            }
            return getListOfStringsFromIterableMatchingLastWord(args, nutrientList);
        }

        // Default
        return Collections.emptyList();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        // Get player
        EntityPlayerMP player = null;
        if (args.length > 0 && playerSubCommands.contains(args[0])) player = CommandBase.getPlayer(sender, args[1]);

        // Which sub-command to execute
        if (args.length == 0 || args[0].equals("help")) commandHelp(sender);
        else if (args[0].equals("get")) commandGetNutrition(player, sender, args);
        else if (args[0].equals("set")) commandSetNutrition(player, sender, args, actions.SET);
        else if (args[0].equals("add")) commandSetNutrition(player, sender, args, actions.ADD);
        else if (args[0].equals("subtract")) commandSetNutrition(player, sender, args, actions.SUBTRACT);
        else if (args[0].equals("reset")) commandResetNutrition(player, sender, args);
        else if (args[0].equals("reload")) commandReload(sender);
    }

    private void commandHelp(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(helpString));
    }

    private void commandReload(ICommandSender sender) {
        DataImporter.reload();
        DataImporter.updatePlayerCapabilitiesOnServer(MinecraftServer.getServer());
        sender.addChatMessage(new ChatComponentText("Nutrients and effects reloaded"));
    }

    private void commandGetNutrition(EntityPlayer player, ICommandSender sender, String[] args) {
        // Write nutrient name and percentage to chat
        Nutrient nutrient = NutrientList.getByName(args[2]);
        if (nutrient != null) {
            Float nutrientValue = PlayerDataHandler.getForPlayer(player)
                .get(nutrient);
            sender.addChatMessage(
                new ChatComponentText(nutrient.name + ": " + String.format("%.2f", nutrientValue) + "%"));
        } else // Write error message
            sender.addChatMessage(new ChatComponentText("'" + args[2] + "' is not a valid nutrient"));
    }

    // Used to set, add, and subtract nutrients (defined under actions)
    private void commandSetNutrition(EntityPlayer player, ICommandSender sender, String[] args, actions action) {
        // Sanity checking
        if (!validNumber(sender, args[3])) return;

        // Set nutrient value and output
        Nutrient nutrient = NutrientList.getByName(args[2]);
        if (nutrient != null) {
            // Update nutrition based on action type
            if (action == actions.SET) {
                PlayerDataHandler.getForPlayer(player)
                    .set(nutrient, Float.parseFloat(args[3]));
            } else if (action == actions.ADD) {
                PlayerDataHandler.getForPlayer(player)
                    .add(nutrient, Float.parseFloat(args[3]));
            } else if (action == actions.SUBTRACT) {
                PlayerDataHandler.getForPlayer(player)
                    .subtract(nutrient, Float.parseFloat(args[3]));
            }

            // Sync nutrition
            Sync.serverRequest(player);

            // Update chat
            sender.addChatMessage(new ChatComponentText(nutrient.name + " updated!"));
        } else { // Write error message
            sender.addChatMessage(new ChatComponentText("'" + args[2] + "' is not a valid nutrient"));
        }
    }

    private void commandResetNutrition(EntityPlayer player, ICommandSender sender, String[] args) {
        // Reset single nutrient
        if (args.length == 3) {
            Nutrient nutrient = NutrientList.getByName(args[2]);
            if (nutrient != null) {
                PlayerDataHandler.getForPlayer(player)
                    .reset(nutrient);
                sender.addChatMessage(
                    new ChatComponentText(
                        "Nutrient " + nutrient.name
                            + " reset for "
                            + player.getGameProfile()
                                .getName()
                            + "!"));
            }
        }
        // Reset all nutrients
        else if (args.length == 2) {
            PlayerDataHandler.getForPlayer(player)
                .reset();
            sender.addChatMessage(
                new ChatComponentText(
                    "Nutrition reset for " + player.getGameProfile()
                        .getName() + "!"));
        }

        // Sync nutrition
        Sync.serverRequest(player);
    }

    // Checks if the supplied nutrient value is valid and in an acceptable range
    // Spits out an error if problem is met
    private boolean validNumber(ICommandSender sender, String value) {
        // Valid number check
        float newValue;
        if (Floats.tryParse(value) != null) newValue = Float.parseFloat(value);
        else {
            sender.addChatMessage(new ChatComponentText("'" + value + "' is not a number."));
            return false;
        }

        // Range check (don't sue me Oracle)
        if (!(newValue >= 0 && newValue <= 100)) {
            sender.addChatMessage(new ChatComponentText("'" + value + "' is not a number between 0 and 100."));
            return false;
        }

        return true;
    }
}
