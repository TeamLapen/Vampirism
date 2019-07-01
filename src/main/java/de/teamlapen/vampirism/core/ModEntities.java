package de.teamlapen.vampirism.core;

import com.google.common.collect.Lists;

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
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
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

    private static final Logger LOGGER = LogManager.getLogger(ModEntities.class);//TODO is this a problem with @Objectholder ?


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
        //TODO set right spawnables (if entity should be spawnable through console)
        registry.register(prepareEntityType("blinding_bat", EntityType.Builder.create(EntityBlindingBat.class, EntityBlindingBat::new), true));
        registry.register(prepareEntityType("ghost", EntityType.Builder.create(EntityGhost.class, EntityGhost::new), true));
        registry.register(prepareEntityType("converted_creature", EntityType.Builder.create(EntityConvertedCreature.class, EntityConvertedCreature::new), false));
        registry.register(prepareEntityType("converted_sheep", EntityType.Builder.create(EntityConvertedSheep.class, EntityConvertedSheep::new), false));
        registry.register(prepareEntityType("vampire_hunter", EntityType.Builder.create(EntityBasicHunter.class, EntityBasicHunter::new), true));
        registry.register(prepareEntityType("hunter_trainer", EntityType.Builder.create(EntityHunterTrainer.class, EntityHunterTrainer::new), true));
        registry.register(prepareEntityType("advanced_hunter", EntityType.Builder.create(EntityAdvancedHunter.class, EntityAdvancedHunter::new), true));
        registry.register(prepareEntityType("vampire_baron", EntityType.Builder.create(EntityVampireBaron.class, EntityVampireBaron::new), true));
        registry.register(prepareEntityType("vampire_minion_s", EntityType.Builder.create(EntityVampireMinionSaveable.class, EntityVampireMinionSaveable::new), false));
        registry.register(prepareEntityType("dummy_creature", EntityType.Builder.create(EntityDummyBittenAnimal.class, EntityDummyBittenAnimal::new), false));
        registry.register(prepareEntityType("villager_converted", EntityType.Builder.create(EntityConvertedVillager.class, EntityConvertedVillager::new), false));
        registry.register(prepareEntityType("villager_angry", EntityType.Builder.create(EntityAggressiveVillager.class, EntityAggressiveVillager::new), false));
        registry.register(prepareEntityType("crossbow_arrow", EntityType.Builder.create(EntityCrossbowArrow.class, EntityCrossbowArrow::new), false));
        registry.register(prepareEntityType("particle_cloud", EntityType.Builder.create(EntityAreaParticleCloud.class, EntityAreaParticleCloud::new), false));
        registry.register(prepareEntityType("throwable_item", EntityType.Builder.create(EntityThrowableItem.class, EntityThrowableItem::new), false));
        registry.register(prepareEntityType("special_dracula_halloween", EntityType.Builder.create(EntityDraculaHalloween.class, EntityDraculaHalloween::new), true));
        registry.register(prepareEntityType("dark_blood_projectile", EntityType.Builder.create(EntityDarkBloodProjectile.class, EntityDarkBloodProjectile::new), false));
        registry.register(prepareEntityType("soul_orb", EntityType.Builder.create(EntitySoulOrb.class, EntitySoulOrb::new), false));
        registry.register(prepareEntityType("villager_hunter_faction", EntityType.Builder.create(EntityHunterFactionVillager.class, EntityHunterFactionVillager::new), true));
        registry.register(prepareEntityType("villager_vampire_faction", EntityType.Builder.create(EntityVampireFactionVillager.class, EntityVampireFactionVillager::new), true));
        registry.register(prepareEntityType("hunter_trainer_dummy", EntityType.Builder.create(EntityHunterTrainerDummy.class, EntityHunterTrainerDummy::new), true));
        //RegisterType and add it to biome spawns
        EntityType vampire = prepareEntityType("vampire", EntityType.Builder.create(EntityBasicVampire.class, EntityBasicVampire::new), true);
        EntityType advanced_vampire = prepareEntityType("advanced_vampire", EntityType.Builder.create(EntityAdvancedVampire.class, EntityAdvancedVampire::new), true);
        registry.register(vampire);
        registry.register(advanced_vampire);
        for (Biome e : getZombieBiomes()) {
            e.getSpawns(EnumCreatureType.MONSTER).add(new Biome.SpawnListEntry(vampire, Balance.mobProps.VAMPIRE_SPAWN_CHANCE, 1, 2));
            e.getSpawns(EnumCreatureType.MONSTER).add(new Biome.SpawnListEntry(vampire, Balance.mobProps.ADVANCED_VAMPIRE_SPAWN_PROBE, 1, 1));
        }
    }

    static void registerSpawns() {
        EntitySpawnPlacementRegistry.register(blinding_bat, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);//TODO AIR
        EntitySpawnPlacementRegistry.register(ghost, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);//TODO new BlockTag#cursed_earth
        EntitySpawnPlacementRegistry.register(converted_creature, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(converted_sheep, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(vampire_hunter, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(advanced_hunter, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(vampire_baron, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(vampire_minion_s, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(dummy_creature, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(villager_converted, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(villager_angry, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(special_dracula_halloween, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(soul_orb, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(villager_hunter_faction, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(villager_vampire_faction, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(hunter_trainer_dummy, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(vampire, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(advanced_vampire, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
        EntitySpawnPlacementRegistry.register(hunter_trainer, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, null);
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
                    Iterator<Biome.SpawnListEntry> iterator2 = b.getSpawns(EnumCreatureType.MONSTER).iterator();
                    boolean zombie = false;
                    while (iterator2.hasNext()) {
                        if (iterator2.next().getClass().equals(EntityZombie.class)) {
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
        EntityType.Builder<T> type = builder.tracker(80, 1, true);
        if (!spawnable)
            type.disableSummoning();
        EntityType<T> entry = type.build(REFERENCE.MODID + ":" + id);
        entry.setRegistryName(REFERENCE.MODID, id);
        return entry;
    }
}
