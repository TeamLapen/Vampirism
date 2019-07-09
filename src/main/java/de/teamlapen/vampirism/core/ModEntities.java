package de.teamlapen.vampirism.core;

import com.google.common.collect.Lists;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.*;
import de.teamlapen.vampirism.entity.converted.EntityConvertedCreature;
import de.teamlapen.vampirism.entity.converted.EntityConvertedSheep;
import de.teamlapen.vampirism.entity.converted.EntityConvertedVillager;
import de.teamlapen.vampirism.entity.hunter.*;
import de.teamlapen.vampirism.entity.minions.vampire.EntityVampireMinionSaveable;
import de.teamlapen.vampirism.entity.special.EntityDraculaHalloween;
import de.teamlapen.vampirism.entity.vampire.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ZombieEntity;
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

    public static final EntityType<EntityBlindingBat> blinding_bat = getNull();
    public static final EntityType<EntityGhost> ghost = getNull();
    public static final EntityType<EntityConvertedCreature> converted_creature = getNull();
    public static final EntityType<EntityConvertedSheep> converted_sheep = getNull();
    public static final EntityType<EntityBasicHunter> vampire_hunter = getNull();
    public static final EntityType<EntityHunterTrainer> hunter_trainer = getNull();
    public static final EntityType<EntityAdvancedHunter> advanced_hunter = getNull();
    public static final EntityType<EntityVampireBaron> vampire_baron = getNull();
    public static final EntityType<EntityVampireMinionSaveable> vampire_minion_s = getNull();
    public static final EntityType<EntityDummyBittenAnimal> dummy_creature = getNull();
    public static final EntityType<EntityConvertedVillager> villager_converted = getNull();
    public static final EntityType<EntityAggressiveVillager> villager_angry = getNull();
    public static final EntityType<EntityCrossbowArrow> crossbow_arrow = getNull();
    public static final EntityType<EntityAreaParticleCloud> particle_cloud = getNull();
    public static final EntityType<EntityThrowableItem> throwable_item = getNull();
    public static final EntityType<EntityDraculaHalloween> special_dracula_halloween = getNull();
    public static final EntityType<EntityDarkBloodProjectile> dark_blood_projectile = getNull();
    public static final EntityType<EntitySoulOrb> soul_orb = getNull();
    public static final EntityType<EntityHunterFactionVillager> villager_hunter_faction = getNull();
    public static final EntityType<EntityVampireFactionVillager> villager_vampire_faction = getNull();
    public static final EntityType<EntityHunterTrainerDummy> hunter_trainer_dummy = getNull();
    public static final EntityType<EntityBasicVampire> vampire = getNull();
    public static final EntityType<EntityHunterTrainerDummy> advanced_vampire = getNull();

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
        registry.addConvertible(EntityType.COW, String.format(base, "cow"));
        registry.addConvertible(EntityType.PIG, String.format(base, "pig"));
        registry.addConvertible(EntityType.OCELOT, String.format(base, "cat"));
        registry.addConvertible(EntityType.HORSE, String.format(base, "horse"));
        registry.addConvertible(EntityType.POLAR_BEAR, String.format(base, "polarbear"));
        registry.addConvertible(EntityType.RABBIT, String.format(base, "rabbit"));
        registry.addConvertible(EntityType.SHEEP, String.format(base, "sheep"), new EntityConvertedSheep.ConvertingHandler());
        registry.addConvertible(EntityType.VILLAGER, null, new EntityConvertedVillager.ConvertingHandler());
        registry.addConvertible(EntityType.LLAMA, String.format(base, "llama"));
    }

    static void registerEntities(IForgeRegistry<EntityType<?>> registry) {
        //simply register EntityType
        registry.register(prepareEntityType("blinding_bat", EntityType.Builder.<EntityBlindingBat>create(EntityBlindingBat::new, EntityClassification.MISC).size(0.5F, 0.9F), true));
        registry.register(prepareEntityType("ghost", EntityType.Builder.<EntityGhost>create(EntityGhost::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.8F, 1.95F), true));
        registry.register(prepareEntityType("converted_creature", EntityType.Builder.<EntityConvertedCreature>create(EntityConvertedCreature::new, VReference.VAMPIRE_CREATURE_TYPE), false));
        registry.register(prepareEntityType("converted_sheep", EntityType.Builder.<EntityConvertedSheep>create(EntityConvertedSheep::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.9F, 1.3F), false));
        registry.register(prepareEntityType("vampire_hunter", EntityType.Builder.<EntityBasicHunter>create(EntityBasicHunter::new, VReference.HUNTER_CREATURE_TYPE).size(0.6F, 1.95F), true));
        registry.register(prepareEntityType("hunter_trainer", EntityType.Builder.<EntityHunterTrainer>create(EntityHunterTrainer::new, VReference.HUNTER_CREATURE_TYPE).size(0.6F, 1.95F), true));
        registry.register(prepareEntityType("advanced_hunter", EntityType.Builder.<EntityAdvancedHunter>create(EntityAdvancedHunter::new, VReference.HUNTER_CREATURE_TYPE).size(0.6F, 1.95F), true));
        registry.register(prepareEntityType("vampire_baron", EntityType.Builder.<EntityVampireBaron>create(EntityVampireBaron::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), true));
        registry.register(prepareEntityType("vampire_minion_s", EntityType.Builder.<EntityVampireMinionSaveable>create(EntityVampireMinionSaveable::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.5F, 1.1F), false));
        registry.register(prepareEntityType("dummy_creature", EntityType.Builder.<EntityDummyBittenAnimal>create(EntityDummyBittenAnimal::new, VReference.VAMPIRE_CREATURE_TYPE), false));
        registry.register(prepareEntityType("villager_converted", EntityType.Builder.<EntityConvertedVillager>create(EntityConvertedVillager::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), false));
        registry.register(prepareEntityType("villager_angry", EntityType.Builder.<EntityAggressiveVillager>create(EntityAggressiveVillager::new, EntityClassification.CREATURE).size(0.6F, 1.95F), false));
        registry.register(prepareEntityType("crossbow_arrow", EntityType.Builder.<EntityCrossbowArrow>create(EntityCrossbowArrow::new, EntityClassification.MISC).size(0.5F, 0.5F), false));
        registry.register(prepareEntityType("particle_cloud", EntityType.Builder.<EntityAreaParticleCloud>create(EntityAreaParticleCloud::new, EntityClassification.MISC).size(6.0F, 0.5F).immuneToFire(), false));
        registry.register(prepareEntityType("throwable_item", EntityType.Builder.<EntityThrowableItem>create(EntityThrowableItem::new, EntityClassification.MISC).size(0.25F, 0.25F), false));
        registry.register(prepareEntityType("special_dracula_halloween", EntityType.Builder.<EntityDraculaHalloween>create(EntityDraculaHalloween::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), true));
        registry.register(prepareEntityType("dark_blood_projectile", EntityType.Builder.<EntityDarkBloodProjectile>create(EntityDarkBloodProjectile::new, EntityClassification.MISC).size(0.6F, 1.95F), false));
        registry.register(prepareEntityType("soul_orb", EntityType.Builder.<EntitySoulOrb>create(EntitySoulOrb::new, EntityClassification.MISC).size(0.3125F, 0.3125F), false));
        registry.register(prepareEntityType("villager_hunter_faction", EntityType.Builder.<EntityHunterFactionVillager>create(EntityHunterFactionVillager::new, VReference.HUNTER_CREATURE_TYPE).size(0.6F, 1.95F), true));
        registry.register(prepareEntityType("villager_vampire_faction", EntityType.Builder.<EntityVampireFactionVillager>create(EntityVampireFactionVillager::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), true));
        registry.register(prepareEntityType("hunter_trainer_dummy", EntityType.Builder.<EntityHunterTrainerDummy>create(EntityHunterTrainerDummy::new, EntityClassification.MISC).size(0.6F, 1.95F), true));
        //RegisterType and add it to biome spawns
        EntityType vampire = prepareEntityType("vampire", EntityType.Builder.<EntityBasicVampire>create(EntityBasicVampire::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), true);
        EntityType advanced_vampire = prepareEntityType("advanced_vampire", EntityType.Builder.<EntityAdvancedVampire>create(EntityAdvancedVampire::new, VReference.VAMPIRE_CREATURE_TYPE).size(0.6F, 1.95F), true);
        registry.register(vampire);
        registry.register(advanced_vampire);
        for (Biome e : getZombieBiomes()) {
            e.getSpawns(EntityClassification.MONSTER).add(new Biome.SpawnListEntry(vampire, Balance.mobProps.VAMPIRE_SPAWN_CHANCE, 1, 2));
            e.getSpawns(EntityClassification.MONSTER).add(new Biome.SpawnListEntry(vampire, Balance.mobProps.ADVANCED_VAMPIRE_SPAWN_PROBE, 1, 1));
        }
    }

    static void registerSpawns() {
        EntitySpawnPlacementRegistry.register(blinding_bat, EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(ghost, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);//TODO new BlockTag#cursed_earth
        EntitySpawnPlacementRegistry.register(converted_creature, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(converted_sheep, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(vampire_hunter, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(advanced_hunter, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(vampire_baron, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(vampire_minion_s, EntitySpawnPlacementRegistry.PlacementType..ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null)
        ;
        EntitySpawnPlacementRegistry.register(dummy_creature, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(villager_converted, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(villager_angry, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(special_dracula_halloween, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(soul_orb, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(villager_hunter_faction, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(villager_vampire_faction, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(hunter_trainer_dummy, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(vampire, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(advanced_vampire, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(hunter_trainer, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
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
        zombieBiomes.remove(Biomes.THE_END);//TODO is right?
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
}
