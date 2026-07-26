package ca.wescook.nutrition.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;

public class NutrientArgumentType implements ArgumentType<Nutrient> {

    private static final Collection<String> EXAMPLES = Arrays.asList("dairy", "fruit", "grain", "protein", "vegetable");

    private NutrientArgumentType() {/**/}

    public static NutrientArgumentType nutrient() {
        return new NutrientArgumentType();
    }

    @Override
    public Nutrient parse(StringReader reader) throws CommandSyntaxException {
        final String text = reader.readUnquotedString();
        final Nutrient nutrient = NutrientList.getByName(text);
        if (nutrient == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException()
                .createWithContext(reader, text);
        }
        return nutrient;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();

        for (Nutrient nutrient : NutrientList.get()) {
            if (remaining == null || remaining.isEmpty() || nutrient.name.startsWith(remaining)) {
                builder.suggest(nutrient.name);
            }
        }
        return builder.buildFuture();
    }

    public static Nutrient getNutrient(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Nutrient.class);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public String toString() {
        return "nutrient()";
    }
}
