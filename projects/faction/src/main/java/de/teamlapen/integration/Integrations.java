package de.teamlapen.integration;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.javafmlmod.AutomaticEventSubscriber;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EventBusSubscriber
public class Integrations {

    private static final Marker INTEGRATIONS_MARKER = MarkerManager.getMarker("INTEGRATIONS");
    private static final Logger LOGGER = LogManager.getLogger();
    @Nullable
    private static ImmutableMap<Identifier, IntegrationInstance> integrations;

    private static List<IntegrationProvider> scanForIntegrations()  {
        Function<FMLModContainer, Optional<ModFileScanData>> scanProvider = scanDataProvider();

        return ModList.get().getSortedMods().stream().filter(FMLModContainer.class::isInstance)
                .flatMap((container) -> {
                    List<ModFileScanData.AnnotationData> list = scanProvider.apply((FMLModContainer) container)
                            .stream()
                            .flatMap(scan -> scan.getAnnotatedBy(Integration.class, ElementType.TYPE))
                            .filter(data -> data.annotationData().get("modIds") instanceof String[] s && s.length > 0 && Stream.of(s).allMatch(modId -> ModList.get().isLoaded(modId)))
                            .filter(x -> AutomaticEventSubscriber.getSides(x.annotationData().get("dist")).contains(FMLLoader.getCurrent().getDist())).toList();
                    if (!list.isEmpty()) {
                        return list.stream().map(data -> new IntegrationProvider(container.getModId(), (String) data.annotationData().get("modId"), data.clazz()));
                    }
                    return Stream.empty();
                }).toList();
    }

    private static Function<FMLModContainer, Optional<ModFileScanData>> scanDataProvider() {
        Function<FMLModContainer, Optional<ModFileScanData>> f;
        try {
            var field = FMLModContainer.class.getDeclaredField("scanResults");
            field.setAccessible(true);
            f = container -> {
                try {
                    return Optional.ofNullable((ModFileScanData) field.get(container));
                } catch (Throwable e) {
                    return Optional.empty();
                }
            };
        } catch (Throwable e) {
            f = _ -> Optional.empty();
        }
        return f;
    }

    private static ImmutableMap<Identifier, IntegrationInstance> loadIntegrations(List<IntegrationProvider> providers) {
        var builder = ImmutableMap.<Identifier, IntegrationInstance>builder();
        for (IntegrationProvider provider : providers) {
            ModList.get().getModContainerById(provider.owningModId).ifPresent(container -> {
                Type type = provider.type;
                try {
                    var clzz = Class.forName(type.getClassName());
                    builder.put(Identifier.fromNamespaceAndPath(provider.owningModId, provider.integrationModId), new IntegrationInstance(container, clzz));
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return builder.build();
    }

    private static Map<Identifier, IntegrationInstance> init() {
        var providers = scanForIntegrations();
        return Integrations.integrations = loadIntegrations(providers);
    }

    @SubscribeEvent
    public static void register(NewRegistryEvent event) {
        List<Identifier> loadedIntegrations = new ArrayList<>();
        LOGGER.trace(INTEGRATIONS_MARKER, "Start loading Integrations");
        for (Map.Entry<Identifier, IntegrationInstance> integration : init().entrySet()) {
            LOGGER.trace(INTEGRATIONS_MARKER, "Registering integration {} for class {}", integration.getKey(), integration.getValue().integration.getName());
            IEventBus eventBus = integration.getValue().container().getEventBus();
            if (eventBus != null) {
                try {
                    for (Method method : integration.getValue().integration.getDeclaredMethods()) {
                        if (!method.isAnnotationPresent(SubscribeEvent.class)) {
                            continue;
                        }

                        if (method.getParameterCount() != 1 || !Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                            throw new IllegalArgumentException("Method " + method + " annotated with @SubscribeEvent must have only one parameter that is an Event subtype");
                        }

                        var eventType = method.getParameterTypes()[0];

                        LOGGER.trace(INTEGRATIONS_MARKER, "Registering method {} for integration {}", method.getName(), integration.getKey());
                        if (IModBusEvent.class.isAssignableFrom(eventType)) {
                            eventBus.register(method);
                        } else {
                            NeoForge.EVENT_BUS.register(method);
                        }
                    }

                } catch (Throwable e) {
                    if (FMLLoader.getCurrent().isProduction()) {
                        LOGGER.error(INTEGRATIONS_MARKER, "Failed to register integration {}", integration.getValue().integration.getName(), e);
                    } else {
                        throw e;
                    }
                }
                loadedIntegrations.add(integration.getKey());
            } else {
                LOGGER.warn(INTEGRATIONS_MARKER, "Failed to register integration {}, event bus not found", integration.getValue().integration.getName());
            }
        }
        LOGGER.info(INTEGRATIONS_MARKER, "Completed loading {} integrations: {}", loadedIntegrations.size(), loadedIntegrations.stream().map(Identifier::toString).collect(Collectors.joining(", ")));
        LOGGER.trace(INTEGRATIONS_MARKER, "Completed loading Integrations");
    }

    private record IntegrationProvider(String owningModId, String integrationModId, Type type) {

    }

    private record IntegrationInstance(ModContainer container, Class<?> integration) {

    }

}
