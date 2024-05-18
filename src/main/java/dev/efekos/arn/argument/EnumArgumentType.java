package dev.efekos.arn.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EnumArgumentType<T extends Enum<?>> implements ArgumentType<Enum<?>> {

    private Class<T> enumClass;
    private final List<String> enumConstants;

    private EnumArgumentType(Class<T> enumClass) {
        this.enumClass = enumClass;
        this.enumConstants = Arrays.stream(enumClass.getEnumConstants()).map(Enum::toString).collect(Collectors.toList());
    }

    public static <T extends Enum<?>> EnumArgumentType<T> enumArg(Class<T> arg){
        return new EnumArgumentType<>(arg);
    }

    public static <T extends Enum<?>> T getEnum(CommandContext<CommandListenerWrapper> context,String name,Class<T> enumClass) {
        String s = context.getArgument(name, Enum.class).name();
        return Arrays.stream(enumClass.getEnumConstants()).filter(t -> t.name().equals(s)).findFirst().orElse(null);
    }

    private final DynamicCommandExceptionType NOT_VALID_EXCEPTION = new DynamicCommandExceptionType((o) ->
            IChatBaseComponent.b("'" + o + "' is not a valid member of " + enumClass.getSimpleName() + ".")
    );

    @Override
    public Enum<?> parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readUnquotedString();
        if (!enumConstants.contains(s)) throw NOT_VALID_EXCEPTION.create(s);
        return Arrays.stream(enumClass.getEnumConstants()).filter(e -> e.name().equals(s)).findFirst().orElse(null);
    }

    @Override
    public CompletableFuture<Suggestions> listSuggestions(CommandContext context, SuggestionsBuilder builder) {
        for (String s : enumConstants.stream().filter(s -> s.startsWith(builder.getRemaining())).collect(Collectors.toList())) {
            builder.suggest(s);
        }

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return enumConstants;
    }

}
