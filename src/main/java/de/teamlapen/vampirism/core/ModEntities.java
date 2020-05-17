package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.entity.*;
import de.teamlapen.vampirism.entity.converted.ConvertedCreatureEntity;
import de.teamlapen.vampirism.entity.converted.ConvertedHorseEntity;
import de.teamlapen.vampirism.entity.converted.ConvertedSheepEntity;
import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import de.teamlapen.vampirism.entity.hunter.*;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.vampire.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all entity registrations and reference.
 */
@ObjectHolder(REFERENCE.MODID)
public class ModEntities {
    private static final Set<EntityType<?>> ALL_ENTITIES = Sets.newHashSet();

    public static final EntityType<AdvancedHunterEntity> advanced_hunter;
    public static final EntityType<AdvancedHunterEntity.IMob> advanced_hunter_imob = getNull();
    public static final EntityType<AdvancedVampireEntity> advanced_vampire;
    public static final EntityType<AdvancedVampireEntity.IMob> advanced_vampire_imob = getNull();
    public static final EntityType<BlindingBatEntity> blinding_bat;
    public static final EntityType<ConvertedCreatureEntity> converted_creature;
    public static final EntityType<ConvertedCreatureEntity.IMob> converted_creature_imob = getNull();
    public static final EntityType<ConvertedSheepEntity> converted_sheep = getNull();
    public static final EntityType<ConvertedHorseEntity> converted_horse = getNull();
    public static final EntityType<CrossbowArrowEntity> crossbow_arrow = getNull();
    public static final EntityType<DarkBloodProjectileEntity> dark_blood_projectile = getNull();
    public static final EntityType<DummyBittenAnimalEntity> dummy_creature;
    public static final EntityType<GhostEntity> ghost;
    public static final EntityType<HunterTrainerEntity> hunter_trainer;
    public static final EntityType<DummyHunterTrainerEntity> hunter_trainer_dummy = getNull();
    public static final EntityType<AreaParticleCloudEntity> particle_cloud = getNull();
    public static final EntityType<SoulOrbEntity> soul_orb = getNull();
    public static final EntityType<ThrowableItemEntity> throwable_item = getNull();
    public static final EntityType<BasicVampireEntity> vampire;
    public static final EntityType<BasicVampireEntity.IMob> vampire_imob = getNull();
    public static final EntityType<VampireBaronEntity> vampire_baron;
    public static final EntityType<BasicHunterEntity> hunter;
    public static final EntityType<BasicHunterEntity.IMob> hunter_imob = getNull();
    public static final EntityType<AggressiveVillagerEntity> villager_angry = getNull();
    public static final EntityType<ConvertedVillagerEntity> villager_converted = getNull();
    public static final EntityType<VampireMinionEntity> vampire_minion = getNull();
    public static final EntityType<HunterMinionEntity> hunter_minion = getNull();

    private static final Logger LOGGER = LogManager.getLogger(ModEntities.class);

    static {
        //IMPORTANT - Must include all entity types that are used in vampire forest spawns
        ghost = prepareEntityType("ghost", EntityType.Builder.create(GhostEntity::new, EntityClassification.MONSTER).size(0.8F, 1.95F), true);
        hunter = prepareEntityType("hunter", EntityType.Builder.create(BasicHunterEntity::new, VReference.HUNTER_CREATURE_TYPE).size(0.6F, 1.95F), true);
        hunter_trainer = prepareEntityType("hunter_trainer", EntityType.Builder.create(HunterTrainerEntity::new, VReference.HUNTER_CREATURE_TYPE).size(0.6F, 1.95F), true);
        advanced_hunter = prepareEntityType("advanced_hunter", EntityType.Builder.create(AdvancedHunterEntity::new, VReference.HUNTER_CREATURE_TYPE).size(0.6F, 1.95F), true);
        vampire_baron = prepareEntityType("vampire_baron", EntityType.Builder.create(VampireBaronEntity::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), true);
        vampire = prepareEntityType("vampire", EntityType.Builder.create(BasicVampireEntity::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), true);
        advanced_vampire = prepareEntityType("advanced_vampire", EntityType.Builder.create(AdvancedVampireEntity::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), true);
        converted_creature = prepareEntityType("converted_creature", EntityType.Builder.create(ConvertedCreatureEntity::new, EntityClassification.CREATURE), false);
        dummy_creature = prepareEntityType("dummy_creature", EntityType.Builder.create(DummyBittenAnimalEntity::new, EntityClassification.CREATURE), true);
        blinding_bat = prepareEntityType("blinding_bat", EntityType.Builder.create(BlindingBatEntity::new, EntityClassification.AMBIENT).size(0.5F, 0.9F), true);
    }

    /**
     * Registers special extended creature classes
     */
    static void registerCustomExtendedCreatures() {
        IVampirismEntityRegistry registry = VampirismAPI.entityRegistry();
    }

    /**
     * Register convertibles for vanilla creatures and maybe for future vampirism creature as well
     */
    static void registerConvertibles() {
        Function<String, ResourceLocation> overlay = (String name) -> new ResourceLocation(REFERENCE.MODID, String.format("textures/entity/vanilla/%s_overlay.png", name));
        IVampirismEntityRegistry registry = VampirismAPI.entityRegistry();

        registry.addConvertible(EntityType.COW, overlay.apply("cow"));
        registry.addConvertible(EntityType.HORSE, overlay.apply("horse"));
        registry.addConvertible(EntityType.LLAMA, overlay.apply("llama"));
        registry.addConvertible(EntityType.OCELOT, overlay.apply("cat"));
        registry.addConvertible(EntityType.PANDA, overlay.apply("panda"));
        registry.addConvertible(EntityType.PIG, overlay.apply("pig"));
        registry.addConvertible(EntityType.POLAR_BEAR, overlay.apply("polarbear"));
        registry.addConvertible(EntityType.RABBIT, overlay.apply("rabbit"));
        registry.addConvertible(EntityType.SHEEP, overlay.apply("sheep"), new ConvertedSheepEntity.ConvertingHandler());
        registry.addConvertible(EntityType.VILLAGER, null, new ConvertedVillagerEntity.ConvertingHandler());
        registry.addConvertible(EntityType.HORSE, overlay.apply("horse"), new ConvertedHorseEntity.ConvertingHandler());
    }

    static void registerEntities(IForgeRegistry<EntityType<?>> registry) {
        //simply register EntityType
        registry.register(advanced_hunter);
        registry.register(prepareEntityType("advanced_hunter_imob", EntityType.Builder.create(AdvancedHunterEntity.IMob::new, VReference.HUNTER_CREATURE_TYPE).size(0.6f, 1.95f), false));
        registry.register(advanced_vampire);
        registry.register(prepareEntityType("advanced_vampire_imob", EntityType.Builder.create(AdvancedVampireEntity.IMob::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6f, 1.95f), false));
        registry.register(blinding_bat);
        registry.register(converted_creature);
        registry.register(prepareEntityType("converted_creature_imob", EntityType.Builder.create(ConvertedCreatureEntity.IMob::new, EntityClassification.CREATURE), false));
        registry.register(prepareEntityType("converted_sheep", EntityType.Builder.create(ConvertedSheepEntity::new, EntityClassification.CREATURE).size(0.9F, 1.3F), false));
        registry.register(prepareEntityType("crossbow_arrow", EntityType.Builder.<CrossbowArrowEntity>create(CrossbowArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F).setCustomClientFactory((spawnEntity, world) -> new CrossbowArrowEntity(ModEntities.crossbow_arrow, world)), false));
        registry.register(prepareEntityType("dark_blood_projectile", EntityType.Builder.<DarkBloodProjectileEntity>create(DarkBloodProjectileEntity::new, EntityClassification.MISC).size(0.6F, 1.95F).immuneToFire().setCustomClientFactory((spawnEntity, world) -> new DarkBloodProjectileEntity(ModEntities.dark_blood_projectile, world)), false));
        registry.register(dummy_creature);
        registry.register(ghost);
        registry.register(hunter_trainer);
        registry.register(prepareEntityType("hunter_trainer_dummy", EntityType.Builder.create(DummyHunterTrainerEntity::new, EntityClassification.MISC).size(0.6F, 1.95F), true));
        registry.register(prepareEntityType("particle_cloud", EntityType.Builder.create(AreaParticleCloudEntity::new, EntityClassification.MISC).size(6.0F, 0.5F).immuneToFire().setCustomClientFactory((spawnEntity, world) -> new AreaParticleCloudEntity(ModEntities.particle_cloud, world)), false));
        registry.register(prepareEntityType("soul_orb", EntityType.Builder.<SoulOrbEntity>create(SoulOrbEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).immuneToFire().setCustomClientFactory((spawnEntity, world) -> new SoulOrbEntity(ModEntities.soul_orb, world)), false));
        registry.register(prepareEntityType("throwable_item", EntityType.Builder.<ThrowableItemEntity>create(ThrowableItemEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).setCustomClientFactory((spawnEntity, world) -> new ThrowableItemEntity(ModEntities.throwable_item, world)), false));
        registry.register(vampire);
        registry.register(prepareEntityType("vampire_imob", EntityType.Builder.create(BasicVampireEntity.IMob::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6f, 1.95f), false));
        registry.register(vampire_baron);
        registry.register(hunter);
        registry.register(prepareEntityType("hunter_imob", EntityType.Builder.create(BasicHunterEntity.IMob::new, VReference.HUNTER_CREATURE_TYPE).size(0.6f, 1.95f), false));
        registry.register(prepareEntityType("villager_angry", EntityType.Builder.create(AggressiveVillagerEntity::new, EntityClassification.CREATURE).size(0.6F, 1.95F), false));
        registry.register(prepareEntityType("villager_converted", EntityType.Builder.create(ConvertedVillagerEntity::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), false));
        registry.register(prepareEntityType("converted_horse", EntityType.Builder.create(ConvertedHorseEntity::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F), false));
        registry.register(prepareEntityType("vampire_minion", EntityType.Builder.create(VampireMinionEntity::new, EntityClassification.CREATURE).size(0.6f, 1.95f), false));
        registry.register(prepareEntityType("hunter_minion", EntityType.Builder.create(HunterMinionEntity::new, EntityClassification.CREATURE).size(0.6f, 1.95f), false));


        //add to biomes
        for (Biome e : getZombieBiomes()) {
            e.getSpawns(EntityClassification.MONSTER).add(new Biome.SpawnListEntry(vampire, BalanceMobProps.mobProps.VAMPIRE_SPAWN_CHANCE, 1, 2));
            e.getSpawns(EntityClassification.MONSTER).add(new Biome.SpawnListEntry(advanced_vampire, BalanceMobProps.mobProps.ADVANCED_VAMPIRE_SPAWN_PROBE, 1, 1));
        }
    }

    static void registerSpawns() {
        EntitySpawnPlacementRegistry.register(advanced_hunter, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HunterBaseEntity::spawnPredicateHunter);
        EntitySpawnPlacementRegistry.register(advanced_vampire, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VampireBaseEntity::spawnPredicateVampire);
        EntitySpawnPlacementRegistry.register(blinding_bat, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BlindingBatEntity::spawnPredicate);
        EntitySpawnPlacementRegistry.register(dummy_creature, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DummyBittenAnimalEntity::spawnPredicate);
        EntitySpawnPlacementRegistry.register(converted_creature, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate);
        EntitySpawnPlacementRegistry.register(converted_sheep, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate);
        EntitySpawnPlacementRegistry.register(ghost, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GhostEntity::spawnPredicateGhost);
        EntitySpawnPlacementRegistry.register(hunter_trainer, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canSpawnOn);
        EntitySpawnPlacementRegistry.register(hunter_trainer_dummy, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canSpawnOn);
        EntitySpawnPlacementRegistry.register(vampire, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VampireBaseEntity::spawnPredicateVampire);
        EntitySpawnPlacementRegistry.register(vampire_baron, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VampireBaronEntity::spawnPredicateBaron);
        EntitySpawnPlacementRegistry.register(hunter, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HunterBaseEntity::spawnPredicateHunter);
        EntitySpawnPlacementRegistry.register(villager_angry, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canSpawnOn);
        EntitySpawnPlacementRegistry.register(villager_converted, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canSpawnOn);
    }

    private static Biome[] getZombieBiomes() {
        Collection<Biome> allBiomes = ForgeRegistries.BIOMES.getValues();
        /*
         * After setting this up this array will contain only biomes in which zombies can spawn.
         */
        List<Biome> zombieBiomes = Lists.newArrayList();
        zombieBiomes.addAll(allBiomes);
        zombieBiomes.remove(ModBiomes.vampire_forest);
        Iterator<Biome> iterator = zombieBiomes.iterator();
        while (iterator.hasNext()) {
            Biome b = iterator.next();
            if (b != null) {
                if (!b.getClass().getName().startsWith("de.teamlapen.")) {
                    Iterator<Biome.SpawnListEntry> iterator2 = b.getSpawns(EntityClassification.MONSTER).iterator();
                    boolean zombie = false;
                    while (iterator2.hasNext()) {
                        if (iterator2.next().entityType.equals(EntityType.ZOMBIE)) {
                            zombie = true;
                            break;
                        }
                    }
                    if (!zombie) {
                        LOGGER.debug("In biome {} no vampire will spawn", b);
                        iterator.remove();
                    }
                }
            }
        }

        return zombieBiomes.toArray(new Biome[zombieBiomes.size()]);


    }

    private static <T extends Entity> EntityType<T> prepareEntityType(String id, EntityType.Builder<T> builder, boolean spawnable) {
        EntityType.Builder<T> type = builder.setTrackingRange(80).setUpdateInterval(1).setShouldReceiveVelocityUpdates(true);
        if (!spawnable)
            type.disableSummoning();
        EntityType<T> entry = type.build(REFERENCE.MODID + ":" + id);
        entry.setRegistryName(REFERENCE.MODID, id);
        ALL_ENTITIES.add(entry);
        return entry;
    }

    static void fixMapping(RegistryEvent.MissingMappings<EntityType<?>> missingMappings) {
        missingMappings.getAllMappings().forEach((mapping) -> {
            if (mapping.key.equals(new ResourceLocation("vampirism:vampire_hunter"))) {
                mapping.remap(ModEntities.hunter);
            } else if (mapping.key.equals(new ResourceLocation("vampirism:vampire_hunter_imob"))) {
                mapping.remap(ModEntities.hunter_imob);
            }
        });
    }

    public static Set<EntityType<?>> getAllEntities(){
        return ImmutableSet.copyOf(ALL_ENTITIES);
    }
}
