/*
 * MIT License
 *
 * Copyright (c) 2024 efekos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.efekos.arn.paper;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.efekos.arn.common.ArnInstance;
import dev.efekos.arn.common.data.ExceptionMap;
import dev.efekos.arn.common.exception.ArnSyntaxException;
import dev.efekos.arn.paper.face.PaperArnConfig;
import dev.efekos.arn.paper.face.PaperCmdResolver;
import dev.efekos.arn.paper.face.PaperHndResolver;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;

public class PaperArn implements ArnInstance {


    /**
     * An exception type thrown by command handler when a command is blocked to
     * {@link ConsoleCommandSender}s, but the
     * command sender is a {@link ConsoleCommandSender}.
     */
    public static final SimpleCommandExceptionType CONSOLE_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(
            new LiteralMessage("This command can't be used by the console."));
    /**
     * An exception type thrown by command handler when a command is blocked to
     * {@link BlockCommandSender}s, but the
     * command sender is a {@link BlockCommandSender}.
     */
    public static final SimpleCommandExceptionType CM_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(
            new LiteralMessage("This command can't be used by command blocks."));
    /**
     * An exception type thrown by command handler when a command is blocked to
     * {@link Player}s, but the command sender
     * is a {@link Player}.
     */
    public static final SimpleCommandExceptionType PLAYER_BLOCKED_EXCEPTION = new SimpleCommandExceptionType(
            new LiteralMessage("This command can't be used by players."));
    /**
     * Generic exception type used to handle {@link ArnSyntaxException}s.
     */
    public static final DynamicCommandExceptionType GENERIC = new DynamicCommandExceptionType(
            o -> new LiteralMessage(o.toString()));


    private final List<PaperCmdResolver> commandResolvers = new ArrayList<>();
    private final ExceptionMap<PaperCmdResolver> commandResolverExceptions = new ExceptionMap<>();
    private final ExceptionMap<PaperHndResolver> handlerResolverExceptions = new ExceptionMap<>();
    private final List<PaperHndResolver> handlerResolvers = new ArrayList<>();

    private <T> T instantiate(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e){
            return null;
        }
    }

    private boolean configured = false;

    private void configure(Reflections reflections){
        if(configured)return;
        configured = true;

        List<PaperArnConfig> list = new ArrayList<>(List.of(new PaperArnConfigurer()));

        for (Class<? extends PaperArnConfig> aClass : reflections.getSubTypesOf(PaperArnConfig.class)) {
            PaperArnConfig instantiate = instantiate(aClass);
            if(instantiate!=null)list.add(instantiate);
        }

        for (PaperArnConfig config : list) {
            config.addArgumentResolvers(commandResolvers);
            config.addHandlerMethodArgumentResolvers(handlerResolvers);
            config.putArgumentResolverExceptions(commandResolverExceptions);
            config.putHandlerMethodArgumentResolverExceptions(handlerResolverExceptions);
        }
    }

    @Override
    public void run(Class<?> mainClass) {
        Reflections reflections = new Reflections(mainClass.getPackage().getName());

        configure(reflections);
    }

}
