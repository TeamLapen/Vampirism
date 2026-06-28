package de.teamlapen.vampirism.processor.generator;

import de.teamlapen.vampirism.processor.CreatureModel;
import de.teamlapen.vampirism.processor.ProcessorUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates {@code Generated<ModId>ConvertedEntities}: the standalone registrar that owns its own
 * {@code DeferredRegister}, all entity-type holders, and the attribute / spawn-placement listeners.
 */
public final class RegistrarGenerator {

    private RegistrarGenerator() {}

    public static String className(String modId) {
        return "Generated" + ProcessorUtil.modIdToClassPart(modId) + "ConvertedEntities";
    }

    public static String generate(String packageName, String modId, List<CreatureModel> models) {
        String className = className(modId);
        boolean anyImmune = models.stream().anyMatch(m -> m.immuneToSweetBerryBush());
        StringBuilder im = new StringBuilder();
        List<String> imps = new ArrayList<>(List.of(
                "net.minecraft.world.entity.EntityType",
                "net.minecraft.world.entity.MobCategory",
                "net.minecraft.world.entity.SpawnPlacementTypes",
                "net.minecraft.world.level.Level",
                "net.minecraft.world.level.levelgen.Heightmap",
                "net.neoforged.bus.api.IEventBus",
                "net.neoforged.bus.api.SubscribeEvent",
                "net.neoforged.fml.common.EventBusSubscriber",
                "net.neoforged.fml.event.lifecycle.FMLConstructModEvent",
                "net.neoforged.fml.loading.moddiscovery.ModInfo",
                "net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent",
                "net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent",
                "net.neoforged.neoforge.registries.DeferredHolder",
                "net.neoforged.neoforge.registries.DeferredRegister"));
        if (anyImmune) {
            imps.add("net.minecraft.world.level.block.Blocks");
        }
        ProcessorUtil.imports(im, imps.toArray(String[]::new));

        StringBuilder fields = new StringBuilder();
        StringBuilder attrs = new StringBuilder();
        StringBuilder spawns = new StringBuilder();
        for (CreatureModel m : models) {
            String factory = m.hasSubclass()
                    ? m.registeredSimple() + "::new"
                    : "(type, world) -> new %1$s(type, world) {}".formatted(m.className());
            String immune = m.immuneToSweetBerryBush() ? ".immuneTo(Blocks.SWEET_BERRY_BUSH)" : "";
            String sized = m.width() >= 0
                    ? "b.sized(%1$sF, %2$sF)".formatted(ProcessorUtil.str(m.width()), ProcessorUtil.str(m.height()))
                    : "b.sized(EntityType.%1$s.getDimensions().width(), EntityType.%1$s.getDimensions().height()).eyeHeight(EntityType.%1$s.getDimensions().eyeHeight())".formatted(m.entityType());
            fields.append("""
                        public static final DeferredHolder<EntityType<?>, EntityType<%2$s>> %1$s = CONVERTED.<%2$s>registerEntityType("%3$s", %4$s, MobCategory.%5$s, b -> %6$s%7$s.clientTrackingRange(80).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true));
                    """.formatted(m.holderField(), m.registeredSimple(), m.registryName(), factory, m.mobCategory(), sized, immune));
            attrs.append("        event.put(%1$s.get(), %2$s.getAttributeBuilder().build());\n".formatted(m.holderField(), m.registeredSimple()));
            spawns.append("        event.register(%1$s.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, %2$s::checkSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);\n".formatted(m.holderField(), m.registeredSimple()));
        }

        String pkg = packageName.isEmpty() ? "" : "package " + packageName + ";\n\n";
        return pkg + im + "\n" + """
                /**
                 * Generated converted-entity registrar. Standalone: owns its own {@link DeferredRegister} and
                 * registers the attribute / spawn-placement listeners itself.
                 */
                @EventBusSubscriber(modid = "%2$s")
                public final class %1$s {

                    public static final DeferredRegister.Entities CONVERTED = DeferredRegister.createEntities("%2$s");

                %3$s
                    private %1$s() {}

                    @SubscribeEvent
                    public static void onModConstructed(FMLConstructModEvent modBus) {
                        CONVERTED.register(modBus.getContainer().getEventBus());
                    }

                    @SubscribeEvent
                    private static void onAttributes(EntityAttributeCreationEvent event) {
                %4$s    }

                    @SubscribeEvent
                    private static void onSpawnPlacements(RegisterSpawnPlacementsEvent event) {
                %5$s    }
                }
                """.formatted(className, modId, fields.toString(), attrs.toString(), spawns.toString());
    }
}
