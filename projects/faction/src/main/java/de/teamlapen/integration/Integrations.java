package de.teamlapen.integration;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
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
    private static ImmutableListMultimap<Identifier, IntegrationInstance> integrations;

    private static List<IntegrationProvider> scanForIntegrations()  {
        Function<FMLModContainer, Optional<ModFileScanData>> scanProvider = scanDataProvider();

        return ModList.get().getSortedMods().stream().filter(FMLModContainer.class::isInstance)
                .flatMap((container) -> {
                    List<ModFileScanData.AnnotationData> list = scanProvider.apply((FMLModContainer) container)
                            .stream()
                            .flatMap(scan -> scan.getAnnotatedBy(Integration.class, ElementType.TYPE))
                            .filter(data -> requiredModIds(data).stream().allMatch(modId -> ModList.get().isLoaded(modId)))
                            .filter(x -> AutomaticEventSubscriber.getSides(x.annotationData().get("dist")).contains(FMLLoader.getCurrent().getDist())).toList();
                    if (!list.isEmpty()) {
                        return list.stream().map(data -> new IntegrationProvider(container.getModId(), String.join("_", requiredModIds(data)), data.clazz()));
                    }
                    return Stream.empty();
                }).toList();
    }

    /// Although the {@link Integration#modIds()} field is of type {@code String[]} is is read by the mod file scanner as {@code ArrayList<String>}
    @SuppressWarnings("unchecked")
    private static List<String> requiredModIds(ModFileScanData.AnnotationData data) {
        return data.annotationData().get("modIds") instanceof List<?> list ? (List<String>) list : List.of();
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

    private static ImmutableListMultimap<Identifier, IntegrationInstance> loadIntegrations(List<IntegrationProvider> providers) {
        var builder = ImmutableListMultimap.<Identifier, IntegrationInstance>builder();
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

    private static ListMultimap<Identifier, IntegrationInstance> init() {
        var providers = scanForIntegrations();
        return Integrations.integrations = loadIntegrations(providers);
    }

    @SubscribeEvent
    public static void register(NewRegistryEvent event) {
        List<Identifier> loadedIntegrations = new ArrayList<>();
        LOGGER.trace(INTEGRATIONS_MARKER, "Start loading Integrations");
        for (Map.Entry<Identifier, IntegrationInstance> integration : init().entries()) {
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

                    instantiate(integration.getValue().integration, integration.getValue().container(), eventBus);
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

    /// Instantiates the integration class using the constructor with the most supported parameters.
    /// Supported constructor parameters are the {@link IEventBus} (mod event bus) and the {@link ModContainer} of the owning mod, in any order.
    /// A constructor without parameters is also accepted.
    private static Object instantiate(Class<?> clazz, ModContainer container, IEventBus modBus) {
        Constructor<?> selected = null;
        Object[] selectedArgs = null;
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];
            boolean supported = true;
            boolean hasBus = false;
            boolean hasContainer = false;
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == IEventBus.class && !hasBus) {
                    args[i] = modBus;
                    hasBus = true;
                } else if (parameterTypes[i] == ModContainer.class && !hasContainer) {
                    args[i] = container;
                    hasContainer = true;
                } else {
                    supported = false;
                    break;
                }
            }
            if (supported && (selected == null || parameterTypes.length > selected.getParameterCount())) {
                selected = constructor;
                selectedArgs = args;
            }
        }
        if (selected == null) {
            throw new IllegalArgumentException("Integration " + clazz.getName() + " must declare a constructor without parameters or taking an IEventBus and/or ModContainer");
        }
        LOGGER.trace(INTEGRATIONS_MARKER, "Instantiating integration {} with constructor {}", clazz.getName(), selected);
        try {
            selected.setAccessible(true);
            return selected.newInstance(selectedArgs);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to instantiate integration " + clazz.getName(), e);
        }
    }

    private record IntegrationProvider(String owningModId, String integrationModId, Type type) {

    }

    private record IntegrationInstance(ModContainer container, Class<?> integration) {

    }

}
