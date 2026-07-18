package de.teamlapen.vampirism.processor.generator;

import de.teamlapen.vampirism.processor.CreatureModel;
import de.teamlapen.vampirism.processor.ProcessorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates {@code Generated<ModId>ConvertedEntitiesClient}: the client-only class that registers the
 * vanilla entity renderer wrapped with the converted-overlay layer for each converted creature that
 * declares a {@link de.teamlapen.vampirism.annotation.ConvertedCreature#renderer()}.
 */
public final class ClientGenerator {

    private ClientGenerator() {}

    public static String className(String modId) {
        return "Generated" + ProcessorUtil.modIdToClassPart(modId) + "ConvertedEntitiesClient";
    }

    public static String generate(String packageName, String registrarPackage, String modId, List<CreatureModel> models) {
        String className = className(modId);
        String registrarClass = RegistrarGenerator.className(modId);
        List<CreatureModel> renderers = models.stream().filter(CreatureModel::hasRenderer).toList();

        StringBuilder im = new StringBuilder();
        List<String> imps = new ArrayList<>(List.of(
                "de.teamlapen.vampirism.client.renderer.entities.layers.ConvertedVampireEntityLayer",
                registrarPackage + "." + registrarClass,
                "net.minecraft.client.model.EntityModel",
                "net.minecraft.client.renderer.entity.EntityRendererProvider",
                "net.minecraft.client.renderer.entity.LivingEntityRenderer",
                "net.minecraft.client.renderer.entity.state.LivingEntityRenderState",
                "net.minecraft.resources.Identifier",
                "net.minecraft.world.entity.LivingEntity",
                "net.neoforged.api.distmarker.Dist",
                "net.neoforged.bus.api.IEventBus",
                "net.neoforged.bus.api.SubscribeEvent",
                "net.neoforged.fml.common.EventBusSubscriber",
                "net.neoforged.neoforge.client.event.EntityRenderersEvent",
                "org.jetbrains.annotations.NotNull",
                "java.util.Map"));
        for (CreatureModel m : renderers) {
            imps.add(m.rendererFqn());
        }
        ProcessorUtil.imports(im, imps.stream().distinct().toArray(String[]::new));

        StringBuilder regs = new StringBuilder();
        for (CreatureModel m : renderers) {
            regs.append("        event.registerEntityRenderer(%1$s.%2$s.get(), convertedRenderer(%3$s::new));\n"
                    .formatted(registrarClass, m.holderField(), m.rendererSimple()));
        }

        String pkg = packageName.isEmpty() ? "" : "package " + packageName + ";\n\n";
        return pkg + im + "\n" + """
                /**
                 * Generated client-only renderer registration for the converted entities. Wraps the vanilla
                 * renderer with the converted-overlay layer.
                 */
                @EventBusSubscriber(value = Dist.CLIENT, modid = "%3$s")
                public final class %1$s {

                    private %1$s() {}

                    @SubscribeEvent
                    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
                %2$s    }

                    private static @NotNull <T extends LivingEntity, U extends LivingEntityRenderState, Z extends EntityModel<? super U>, O extends LivingEntityRenderState, P extends EntityModel<O>> EntityRendererProvider<T> convertedRenderer(LivingEntityRendererProvider<T, U, Z> provider) {
                        return context -> {
                            //noinspection unchecked
                            var renderer = (LivingEntityRenderer<T, O, P>) provider.create(context);
                            renderer.addLayer(new ConvertedVampireEntityLayer<>(renderer));
                            return renderer;
                        };
                    }

                    private interface LivingEntityRendererProvider<T extends LivingEntity, U extends LivingEntityRenderState, Z extends EntityModel<? super U>> extends EntityRendererProvider<T> {
                        @Override
                        @NotNull
                        LivingEntityRenderer<T, U, Z> create(EntityRendererProvider.@NotNull Context context);
                    }
                }
                """.formatted(className, regs.toString(), modId);
    }
}
