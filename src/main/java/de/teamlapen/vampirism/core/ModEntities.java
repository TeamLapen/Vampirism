package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.*;
import de.teamlapen.vampirism.entity.converted.ConvertedCreatureEntity;
import de.teamlapen.vampirism.entity.converted.ConvertedSheepEntity;
import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import de.teamlapen.vampirism.entity.hunter.*;
import de.teamlapen.vampirism.entity.minions.vampire.VampireMinionSaveableEntity;
import de.teamlapen.vampirism.entity.special.DraculaHalloweenEntity;
import de.teamlapen.vampirism.entity.vampire.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all entity registrations and reference.
 */
@ObjectHolder(REFERENCE.MODID)
public class ModEntities {

    public static final EntityType<BlindingBatEntity> blinding_bat = prepareEntityType("blinding_bat", EntityType.Builder.create(BlindingBatEntity::new, EntityClassification.MISC).size(0.5F, 0.9F), true);
    public static final EntityType<GhostEntity> ghost = prepareEntityType("ghost", EntityType.Builder.create(GhostEntity::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.8F, 1.95F), true);
    public static final EntityType<ConvertedCreatureEntity> converted_creature = prepareEntityType("converted_creature", EntityType.Builder.create(ConvertedCreatureEntity::new, EntityClassification.CREATURE), false);
    public static final EntityType<ConvertedCreatureEntity.IMob> converted_creature_imob = prepareEntityType("converted_creature_imob", EntityType.Builder.create(ConvertedCreatureEntity.IMob::new, EntityClassification.CREATURE), false);
    public static final EntityType<ConvertedSheepEntity> converted_sheep = prepareEntityType("converted_sheep", EntityType.Builder.create(ConvertedSheepEntity::new, EntityClassification.CREATURE).size(0.9F, 1.3F), false);
    public static final EntityType<BasicHunterEntity> vampire_hunter = prepareEntityType("vampire_hunter", EntityType.Builder.create(BasicHunterEntity::new, VReference.HUNTER_CREATURE_TYPE).size(0.6F, 1.95F), true);
    public static final EntityType<BasicHunterEntity.IMob> vampire_hunter_imob = prepareEntityType("vampire_hunter_imob", EntityType.Builder.create(BasicHunterEntity.IMob::new, VReference.HUNTER_CREATURE_TYPE).size(0.6f, 1.95f), false);
    public static final EntityType<HunterTrainerEntity> hunter_trainer = prepareEntityType("hunter_trainer", EntityType.Builder.create(HunterTrainerEntity::new, VReference.HUNTER_CREATURE_TYPE).size(0.6F, 1.95F), true);
    public static final EntityType<AdvancedHunterEntity> advanced_hunter = prepareEntityType("advanced_hunter", EntityType.Builder.create(AdvancedHunterEntity::new, VReference.HUNTER_CREATURE_TYPE).size(0.6F, 1.95F), true);
    public static final EntityType<AdvancedHunterEntity.IMob> advanced_hunter_imob = prepareEntityType("advanced_hunter_imob", EntityType.Builder.create(AdvancedHunterEntity.IMob::new, VReference.HUNTER_CREATURE_TYPE).size(0.6f, 1.95f), false);
    public static final EntityType<VampireBaronEntity> vampire_baron = prepareEntityType("vampire_baron", EntityType.Builder.create(VampireBaronEntity::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), true);
    public static final EntityType<VampireMinionSaveableEntity> vampire_minion_s = prepareEntityType("vampire_minion_s", EntityType.Builder.create(VampireMinionSaveableEntity::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.5F, 1.1F), false);
    public static final EntityType<DummyBittenAnimalEntity> dummy_creature = prepareEntityType("dummy_creature", EntityType.Builder.create(DummyBittenAnimalEntity::new, EntityClassification.CREATURE), false);
    public static final EntityType<ConvertedVillagerEntity> villager_converted = prepareEntityType("villager_converted", EntityType.Builder.create(ConvertedVillagerEntity::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), false);
    public static final EntityType<AggressiveVillagerEntity> villager_angry = prepareEntityType("villager_angry", EntityType.Builder.create(AggressiveVillagerEntity::new, EntityClassification.CREATURE).size(0.6F, 1.95F), false);
    public static final EntityType<CrossbowArrowEntity> crossbow_arrow = prepareEntityType("crossbow_arrow", EntityType.Builder.<CrossbowArrowEntity>create(CrossbowArrowEntity::new, EntityClassification.MISC).size(0.5F, 0.5F), false);
    public static final EntityType<AreaParticleCloudEntity> particle_cloud = prepareEntityType("particle_cloud", EntityType.Builder.create(AreaParticleCloudEntity::new, EntityClassification.MISC).size(6.0F, 0.5F).immuneToFire(), false);
    public static final EntityType<ThrowableItemEntity> throwable_item = prepareEntityType("throwable_item", EntityType.Builder.<ThrowableItemEntity>create(ThrowableItemEntity::new, EntityClassification.MISC).size(0.25F, 0.25F), false);
    public static final EntityType<DraculaHalloweenEntity> special_dracula_halloween = prepareEntityType("special_dracula_halloween", EntityType.Builder.create(DraculaHalloweenEntity::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), true);
    public static final EntityType<DarkBloodProjectileEntity> dark_blood_projectile = prepareEntityType("dark_blood_projectile", EntityType.Builder.<DarkBloodProjectileEntity>create(DarkBloodProjectileEntity::new, EntityClassification.MISC).size(0.6F, 1.95F), false);
    public static final EntityType<SoulOrbEntity> soul_orb = prepareEntityType("soul_orb", EntityType.Builder.<SoulOrbEntity>create(SoulOrbEntity::new, EntityClassification.MISC).size(0.25F, 0.25F).immuneToFire(), false);
    public static final EntityType<HunterFactionVillagerEntity> villager_hunter_faction = prepareEntityType("villager_hunter_faction", EntityType.Builder.create(HunterFactionVillagerEntity::new, VReference.HUNTER_CREATURE_TYPE).size(0.6F, 1.95F), true);
    public static final EntityType<VampireFactionVillagerEntity> villager_vampire_faction = prepareEntityType("villager_vampire_faction", EntityType.Builder.create(VampireFactionVillagerEntity::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), true);
    public static final EntityType<DummyHunterTrainerEntity> hunter_trainer_dummy = prepareEntityType("hunter_trainer_dummy", EntityType.Builder.create(DummyHunterTrainerEntity::new, EntityClassification.MISC).size(0.6F, 1.95F), true);
    public static final EntityType<BasicVampireEntity> vampire = prepareEntityType("vampire", EntityType.Builder.create(BasicVampireEntity::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), true);
    public static final EntityType<BasicVampireEntity.IMob> vampire_imob = prepareEntityType("vampire_imob", EntityType.Builder.create(BasicVampireEntity.IMob::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6f, 1.95f), false);
    public static final EntityType<AdvancedVampireEntity> advanced_vampire = prepareEntityType("advanced_vampire", EntityType.Builder.create(AdvancedVampireEntity::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), true);
    public static final EntityType<AdvancedVampireEntity.IMob> advanced_vampire_imob = prepareEntityType("advanced_vampire_imob", EntityType.Builder.create(AdvancedVampireEntity.IMob::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6f, 1.95f), false);

    public static final VillagerProfession hunter_expert = getNull();
    public static final VillagerProfession vampire_expert = getNull();

    private static final Logger LOGGER = LogManager.getLogger(ModEntities.class);


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
        String base = REFERENCE.MODID + ":textures/entity/vanilla/%s_overlay.png";
        IVampirismEntityRegistry registry = VampirismAPI.entityRegistry();
        registry.addConvertible(EntityType.COW, CowEntity.class, String.format(base, "cow"));
        registry.addConvertible(EntityType.PIG, PigEntity.class, String.format(base, "pig"));
        registry.addConvertible(EntityType.OCELOT, OcelotEntity.class, String.format(base, "cat"));
        registry.addConvertible(EntityType.HORSE, HorseEntity.class, String.format(base, "horse"));
        registry.addConvertible(EntityType.POLAR_BEAR, PolarBearEntity.class, String.format(base, "polarbear"));
        registry.addConvertible(EntityType.RABBIT, RabbitEntity.class, String.format(base, "rabbit"));
        registry.addConvertible(EntityType.SHEEP, SheepEntity.class, String.format(base, "sheep"), new ConvertedSheepEntity.ConvertingHandler());
        registry.addConvertible(EntityType.VILLAGER, VillagerEntity.class, null, new ConvertedVillagerEntity.ConvertingHandler());
        registry.addConvertible(EntityType.LLAMA, LlamaEntity.class, String.format(base, "llama"));
        registry.addConvertible(EntityType.PANDA, PandaEntity.class, String.format(base, "panda"));
    }

    static void registerEntities(IForgeRegistry<EntityType<?>> registry) {
        //simply register EntityType
        registry.register(blinding_bat);
        registry.register(ghost);
        registry.register(converted_creature);
        registry.register(converted_creature_imob);
        registry.register(converted_sheep);
        registry.register(vampire_hunter);
        registry.register(vampire_hunter_imob);
        registry.register(hunter_trainer);
        registry.register(advanced_hunter);
        registry.register(advanced_hunter_imob);
        registry.register(vampire_baron);
        registry.register(vampire_minion_s);
        registry.register(dummy_creature);
        registry.register(villager_converted);
        registry.register(villager_angry);
        registry.register(crossbow_arrow);
        registry.register(particle_cloud);
        registry.register(throwable_item);
        registry.register(special_dracula_halloween);
        registry.register(dark_blood_projectile);
        registry.register(soul_orb);
        registry.register(villager_hunter_faction);
        registry.register(villager_vampire_faction);
        registry.register(hunter_trainer_dummy);
        registry.register(vampire);
        registry.register(vampire_imob);
        registry.register(advanced_vampire);
        registry.register(advanced_vampire_imob);
        //add to biomes
        for (Biome e : getZombieBiomes()) {
            e.getSpawns(VReference.VAMPIRE_CREATURE_TYPE).add(new Biome.SpawnListEntry(vampire, Balance.mobProps.VAMPIRE_SPAWN_CHANCE, 1, 2));
            e.getSpawns(VReference.VAMPIRE_CREATURE_TYPE).add(new Biome.SpawnListEntry(vampire, Balance.mobProps.ADVANCED_VAMPIRE_SPAWN_PROBE, 1, 1));
        }
    }

    static void registerSpawns() {
        EntitySpawnPlacementRegistry.register(blinding_bat, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BlindingBatEntity::spawnPredicate);
        EntitySpawnPlacementRegistry.register(ghost, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GhostEntity::spawnPredicateGhost);
        EntitySpawnPlacementRegistry.register(converted_creature, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate);
        EntitySpawnPlacementRegistry.register(converted_sheep, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ConvertedCreatureEntity::spawnPredicate);
        EntitySpawnPlacementRegistry.register(vampire_hunter, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VampirismEntity::spawnPredicateHunter);
        EntitySpawnPlacementRegistry.register(advanced_hunter, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VampirismEntity::spawnPredicateHunter);
        EntitySpawnPlacementRegistry.register(vampire_baron, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VampireBaronEntity::spawnPredicateBaron);
        EntitySpawnPlacementRegistry.register(vampire_minion_s, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VampirismEntity::spawnPredicateVampire);
        //EntitySpawnPlacementRegistry.register(dummy_creature, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::func_223315_a);
        EntitySpawnPlacementRegistry.register(villager_converted, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::func_223315_a);
        EntitySpawnPlacementRegistry.register(villager_angry, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::func_223315_a);
        EntitySpawnPlacementRegistry.register(special_dracula_halloween, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VampirismEntity::spawnPredicateVampire);
        EntitySpawnPlacementRegistry.register(villager_hunter_faction, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::func_223315_a);
        EntitySpawnPlacementRegistry.register(villager_vampire_faction, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::func_223315_a);
        EntitySpawnPlacementRegistry.register(hunter_trainer_dummy, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::func_223315_a);
        EntitySpawnPlacementRegistry.register(vampire, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VampirismEntity::spawnPredicateVampire);
        EntitySpawnPlacementRegistry.register(advanced_vampire, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, VampirismEntity::spawnPredicateVampire);
        EntitySpawnPlacementRegistry.register(hunter_trainer, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::func_223315_a);
    }

    static Biome[] getZombieBiomes() {
        Collection<Biome> allBiomes = ForgeRegistries.BIOMES.getValues();
        /*
         * After setting this up this array will contain only biomes in which zombies can spawn.
         */
        List<Biome> zombieBiomes = Lists.newArrayList();
        zombieBiomes.addAll(allBiomes);
        zombieBiomes.remove(Biomes.MUSHROOM_FIELDS);
        zombieBiomes.remove(Biomes.MUSHROOM_FIELD_SHORE);
        zombieBiomes.remove(Biomes.NETHER);
        zombieBiomes.remove(Biomes.THE_END);
        zombieBiomes.remove(ModBiomes.vampire_forest);
        Iterator<Biome> iterator = zombieBiomes.iterator();
        while (iterator.hasNext()) {
            Biome b = iterator.next();
            if (b != null) {
                if (!b.getClass().getName().startsWith("net.minecraft.") && !b.getClass().getName().startsWith("de.teamlapen.")) {
                    Iterator<Biome.SpawnListEntry> iterator2 = b.getSpawns(EntityClassification.MONSTER).iterator();
                    boolean zombie = false;
                    while (iterator2.hasNext()) {
                        if (iterator2.next().getClass().equals(ZombieEntity.class)) {
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
        return entry;
    }

    static void registerProfessions(IForgeRegistry<VillagerProfession> registry) {
        registry.register(new VillagerProfession("hunter_expert", PointOfInterestType.UNEMPLOYED, ImmutableSet.of(), ImmutableSet.of()).setRegistryName(REFERENCE.MODID, "hunter_expert"));
        registry.register(new VillagerProfession("vampire_expert", PointOfInterestType.UNEMPLOYED, ImmutableSet.of(), ImmutableSet.of()).setRegistryName(REFERENCE.MODID, "vampire_expert"));
    }
}
