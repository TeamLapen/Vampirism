package de.teamlapen.vampirism.core;

import com.google.common.collect.Sets;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IBiteableRegistry;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.EntityBlindingBat;
import de.teamlapen.vampirism.entity.EntityGhost;
import de.teamlapen.vampirism.entity.converted.EntityConvertedCreature;
import de.teamlapen.vampirism.entity.converted.EntityConvertedSheep;
import de.teamlapen.vampirism.entity.converted.EntityConvertedVillager;
import de.teamlapen.vampirism.entity.hunter.EntityAdvancedHunter;
import de.teamlapen.vampirism.entity.hunter.EntityBasicHunter;
import de.teamlapen.vampirism.entity.hunter.EntityHunterTrainer;
import de.teamlapen.vampirism.entity.hunter.EntityHunterVillager;
import de.teamlapen.vampirism.entity.minions.vampire.EntityVampireMinionSaveable;
import de.teamlapen.vampirism.entity.vampire.EntityAdvancedVampire;
import de.teamlapen.vampirism.entity.vampire.EntityBasicVampire;
import de.teamlapen.vampirism.entity.vampire.EntityDummyBittenAnimal;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBaron;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.*;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Handles all entity registrations and reference.
 */
public class ModEntities {
    public static final String BASIC_HUNTER_NAME = "vampirism.vampireHunter";
    public static final String BASIC_VAMPIRE_NAME = "vampirism.vampire";
    public static final String DRACULA_NAME = "vampirism.dracula";
    public static final String GHOST_NAME = "vampirism.ghost";
    public static final String VAMPIRE_BARON = "vampirism.vampireBaron";
    public static final String VAMPIRE_MINION_REMOTE_NAME = "vampirism.vampireMinionR";
    public static final String VAMPIRE_MINION_SAVEABLE_NAME = "vampirism.vampireMinionS";
    public static final String DEAD_MOB_NAME = "vampirism.dead_mob";
    public static final String BLINDING_BAT_NAME = "vampirism.blinding_bat";
    public static final String DUMMY_CREATURE = "vampirism.dummy_creature";
    public static final String PORTAL_GUARD = "vampirism.portal_guard";
    public static final String CONVERTED_CREATURE = "vampirism.converted.creature";
    public static final String CONVERTED_VILLAGER = "vampirism.converted.villager";
    public static final String CONVERTED_SHEEP = "vampirism.converted.sheep";
    public static final String HUNTER_TRAINER = "vampirism.hunter_trainer";
    public static final String ADVANCED_HUNTER = "vampirism.advanced_hunter";
    public static final String ADVANCED_VAMPIRE = "vampirism.advanced_vampire";
    public static final String HUNTER_VILLAGER = "vampirism.hunter_villager";

    /**
     * List of entity names which should be spawnable
     */
    public static final List<String> spawnableEntityNames = new ArrayList<String>();
    private static int modEntityId = 0;

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                preInit((FMLPreInitializationEvent) event);
                break;
            case INIT:
                init((FMLInitializationEvent) event);
                break;
        }

    }

    private static void preInit(FMLPreInitializationEvent event) {

    }

    /**
     * Register convertibles for vanilla creatures and maybe for future vampirism creature as well
     */
    private static void registerConvertibles() {
        String base = REFERENCE.MODID + ":textures/entity/vanilla/%sOverlay.png";
        IBiteableRegistry registry = VampirismAPI.biteableRegistry();
        registry.addConvertible(EntityCow.class, String.format(base, "cow"));
        registry.addConvertible(EntityPig.class, String.format(base, "pig"));
        registry.addConvertible(EntityOcelot.class, String.format(base, "cat"));
        registry.addConvertible(EntityHorse.class, String.format(base, "horse"));
        registry.addConvertible(EntitySheep.class, String.format(base, "sheep"), new EntityConvertedSheep.ConvertingHandler());
        registry.addConvertible(EntityVillager.class, null, new EntityConvertedVillager.ConvertingHandler());
    }

    private static void init(FMLInitializationEvent event) {
        Set<BiomeGenBase> allBiomes = BiomeGenBase.EXPLORATION_BIOMES_LIST;
        allBiomes.remove(Biomes.HELL);
        allBiomes.remove(Biomes.SKY);

        /**
         * After setting this up this array will contain only biomes in which zombies can spawn.
         */
        Set<BiomeGenBase> zombieBiomes = Sets.newHashSet();
        zombieBiomes.addAll(allBiomes);
        Iterator<BiomeGenBase> iterator = zombieBiomes.iterator();
        while (iterator.hasNext()) {
            BiomeGenBase b = iterator.next();
            if (b != null) {
                if (!b.getBiomeClass().getName().startsWith("net.minecraft.") && !b.getBiomeClass().getName().startsWith("de.teamlapen.")) {
                    Iterator<BiomeGenBase.SpawnListEntry> iterator2 = b.getSpawnableList(EnumCreatureType.MONSTER).iterator();
                    boolean zombie = false;
                    while (iterator2.hasNext()) {
                        if (iterator2.next().entityClass.equals(EntityZombie.class)) {
                            zombie = true;
                            break;
                        }
                    }
                    if (!zombie) {
                        VampirismMod.log.d("ModEntities", "In biome %s no vampire will spawn", b);
                        iterator.remove();
                    }
                }
            }
        }


        registerEntity(EntityBlindingBat.class, BLINDING_BAT_NAME, EntityLiving.SpawnPlacementType.IN_AIR, false);
        registerEntity(EntityGhost.class, GHOST_NAME, EntityLiving.SpawnPlacementType.ON_GROUND, true);
        registerEntity(EntityConvertedCreature.class, CONVERTED_CREATURE, EntityLiving.SpawnPlacementType.ON_GROUND, false);
        registerEntity(EntityConvertedSheep.class, CONVERTED_SHEEP, EntityLiving.SpawnPlacementType.ON_GROUND, false);
        registerEntity(EntityBasicHunter.class, BASIC_HUNTER_NAME, EntityLiving.SpawnPlacementType.ON_GROUND, true);
        registerEntity(EntityBasicVampire.class, BASIC_VAMPIRE_NAME, EntityLiving.SpawnPlacementType.ON_GROUND, Balance.mobProps.VAMPIRE_SPAWN_CHANCE, 1, 2, EnumCreatureType.MONSTER, zombieBiomes.toArray(new BiomeGenBase[zombieBiomes.size()]));
        registerEntity(EntityHunterTrainer.class, HUNTER_TRAINER, EntityLiving.SpawnPlacementType.ON_GROUND, true);
        registerEntity(EntityAdvancedHunter.class, ADVANCED_HUNTER, EntityLiving.SpawnPlacementType.ON_GROUND, true);
        registerEntity(EntityVampireBaron.class, VAMPIRE_BARON, EntityLiving.SpawnPlacementType.ON_GROUND, true);
        registerEntity(EntityVampireMinionSaveable.class, VAMPIRE_MINION_SAVEABLE_NAME, EntityLiving.SpawnPlacementType.ON_GROUND, false);
        registerEntity(EntityDummyBittenAnimal.class, DUMMY_CREATURE, EntityLiving.SpawnPlacementType.ON_GROUND, false);
        registerEntity(EntityAdvancedVampire.class, ADVANCED_VAMPIRE, EntityLiving.SpawnPlacementType.ON_GROUND, Balance.mobProps.ADVANCED_VAMPIRE_SPAWN_PROBE, 1, 1, EnumCreatureType.MONSTER, zombieBiomes.toArray(new BiomeGenBase[zombieBiomes.size()]));
        registerEntity(EntityConvertedVillager.class, CONVERTED_VILLAGER, EntityLiving.SpawnPlacementType.ON_GROUND, false);
        registerEntity(EntityHunterVillager.class, HUNTER_VILLAGER, EntityLiving.SpawnPlacementType.ON_GROUND, false);
        registerConvertibles();
    }

    private static void registerEntity(Class<? extends Entity> clazz, String name, EntityLiving.SpawnPlacementType placementType, boolean egg) {

        VampirismMod.log.d("EntityRegister", "Adding " + name + "(" + clazz.getSimpleName() + ") with mod id %d", modEntityId);
        EntityRegistry.registerModEntity(clazz, name.replace("vampirism.", ""), modEntityId++, VampirismMod.instance, 80, 1, true);
        if (egg) {
            EntityRegistry.registerEgg(clazz, 0x8B15A3, name.hashCode());
            spawnableEntityNames.add(name);
        }

    }

    /**
     * Registers the entity and add a spawn entry for it
     *
     * @param clazz
     * @param name
     * @param probe
     * @param min
     * @param max
     * @param type
     * @param biomes
     */
    private static void registerEntity(Class<? extends EntityLiving> clazz, String name, EntityLiving.SpawnPlacementType placementType, int probe, int min, int max, EnumCreatureType type, BiomeGenBase... biomes) {
        registerEntity(clazz, name, placementType, true);
        VampirismMod.log.d("EntityRegister", "Adding spawn with probe of " + probe);
        EntityRegistry.addSpawn(clazz, probe, min, max, type, biomes);
    }
}
