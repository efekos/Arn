package dev.efekos.arn.resolver.impl.command;


import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import dev.efekos.arn.annotation.CommandArgument;
import dev.efekos.arn.annotation.CustomArgument;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import net.minecraft.commands.CommandDispatcher;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * An implementation of {@link CommandArgumentResolver}. Resolves custom {@link Enum} arguments that is annotated with
 * {@link CustomArgument}.
 * @since 0.1
 * @author efekos
 */
public class CmdEnumArg implements CommandArgumentResolver {

    /**
     * Main {@link Enum} class this resolver will handle.
     */
    private final Class<? extends Enum<?>> enumClass;
    /**
     * Constants of {@link #enumClass} in lower-case.
     */
    private final List<String> constants;

    /**
     * Creates a new enumerator resolver. This class is not a static argument resolver. It is dynamically added by
     * {@link dev.efekos.arn.Arn} for every {@link Enum} that is annotated with {@link CustomArgument}.
     * @param enumClass Main {@link Enum} class this resolver will handle.
     */
    public CmdEnumArg(Class<? extends Enum<?>> enumClass) {
        this.enumClass = enumClass;
        this.constants = Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).map(s -> s.toLowerCase(Locale.ENGLISH)).collect(Collectors.toList());
    }

    /**{@inheritDoc}*/
    @Override
    public boolean isApplicable(Parameter parameter) {
        return parameter.getType().equals(enumClass)&&parameter.isAnnotationPresent(CommandArgument.class);
    }

    /**{@inheritDoc}*/
    @Override
    public ArgumentBuilder apply(Parameter parameter) {
        String s = parameter.getAnnotation(CommandArgument.class).value();
        return CommandDispatcher.a(s.isEmpty()?parameter.getName():s, StringArgumentType.word()).suggests((context,builder)->{
            for (String constant : constants) if(constant.startsWith(builder.getRemainingLowerCase())) builder.suggest(constant);
            return builder.buildFuture();
        });
    }
}
