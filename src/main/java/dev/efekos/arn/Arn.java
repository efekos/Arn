package dev.efekos.arn;

import dev.efekos.arn.annotation.Command;
import dev.efekos.arn.annotation.Container;
import dev.efekos.arn.annotation.RestCommand;
import dev.efekos.arn.config.ArnConfigurer;
import dev.efekos.arn.exception.ArnConfigurerException;
import dev.efekos.arn.resolver.CommandArgumentResolver;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class Arn {

    private static final Arn instance = new Arn();
    private final List<CommandArgumentResolver> resolvers = new ArrayList<>();

    public static void run(Class<?> mainClass) {
        try {
            instance.configure();
            instance.scanConfigurers(mainClass);
            instance.scanCommands(mainClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configure(){

    }

    private void scanConfigurers(Class<?> mainClass) throws ArnConfigurerException {
        Reflections reflections = new Reflections(mainClass.getPackage().getName());

        Object[] configurers = reflections.getTypesAnnotatedWith(Container.class).stream().filter(aClass -> Arrays.asList(aClass.getInterfaces()).contains(ArnConfigurer.class)).toArray();

        for (Object configurer : configurers) {
            Class<? extends ArnConfigurer> clazz = (Class<? extends ArnConfigurer>) configurer;

            try {
                Constructor<? extends ArnConfigurer> constructor = clazz.getConstructor();
                constructor.setAccessible(true);
                ArnConfigurer configurerInstance = constructor.newInstance();
                ArrayList<CommandArgumentResolver> list = new ArrayList<>();
                configurerInstance.addArgumentResolvers(list);
                resolvers.addAll(list);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new ArnConfigurerException(e);
            }
        }
    }

    private void scanCommands(Class<?> mainClass) {
        Reflections reflections = new Reflections(mainClass.getPackage().getName());

        Set<Class<?>> containers = reflections.getTypesAnnotatedWith(Container.class);
        Set<Class<?>> restCommands = reflections.getTypesAnnotatedWith(RestCommand.class);

        for (Class<?> container : containers) {

            if(container.isAnnotationPresent(RestCommand.class)) continue;

            for (Method method : container.getMethods()) {

                if(method.isAnnotationPresent(Command.class)) instance.command(method.getAnnotation(Command.class),method);

            }
        }

        for (Class<?> restCommand : restCommands) {

            for (Method method : restCommand.getMethods()) {

                if(method.isAnnotationPresent(Command.class)) instance.command(method.getAnnotation(Command.class),method);

            }

        }
    }

    private void command(Command annotation,Method method) {

    }

}