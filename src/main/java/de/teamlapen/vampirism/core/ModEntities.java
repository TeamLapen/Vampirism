package de.teamlapen.vampirism.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.*;
import de.teamlapen.vampirism.entity.converted.EntityConvertedCreature;
import de.teamlapen.vampirism.entity.converted.EntityConvertedSheep;
import de.teamlapen.vampirism.entity.converted.EntityConvertedVillager;
import de.teamlapen.vampirism.entity.hunter.EntityAdvancedHunter;
import de.teamlapen.vampirism.entity.hunter.EntityBasicHunter;
import de.teamlapen.vampirism.entity.hunter.EntityHunterTrainer;
import de.teamlapen.vampirism.entity.hunter.EntityHunterVillager;
import de.teamlapen.vampirism.entity.minions.vampire.EntityVampireMinionSaveable;
import de.teamlapen.vampirism.entity.special.EntityDraculaHalloween;
import de.teamlapen.vampirism.entity.vampire.EntityAdvancedVampire;
import de.teamlapen.vampirism.entity.vampire.EntityBasicVampire;
import de.teamlapen.vampirism.entity.vampire.EntityDummyBittenAnimal;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBaron;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.*;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.IFixableData;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Handles all entity registrations and reference.
 */
public class ModEntities {
    public static final String BASIC_HUNTER_NAME = "vampire_hunter";
    public static final String BASIC_VAMPIRE_NAME = "vampire";
    public static final String DRACULA_NAME = "dracula";
    public static final String GHOST_NAME = "ghost";
    public static final String VAMPIRE_BARON = "vampire_baron";
    public static final String VAMPIRE_MINION_REMOTE_NAME = "vampire_minion_r";
    public static final String VAMPIRE_MINION_SAVEABLE_NAME = "vampire_minion_s";
    public static final String DEAD_MOB_NAME = "dead_mob";
    public static final String BLINDING_BAT_NAME = "blinding_bat";
    public static final String DUMMY_CREATURE = "dummy_creature";
    public static final String PORTAL_GUARD = "portal_guard";
    public static final String CONVERTED_CREATURE = "converted_creature";
    public static final String CONVERTED_VILLAGER = "converted_villager";
    public static final String CONVERTED_SHEEP = "converted_sheep";
    public static final String HUNTER_TRAINER = "hunter_trainer";
    public static final String ADVANCED_HUNTER = "advanced_hunter";
    public static final String ADVANCED_VAMPIRE = "advanced_vampire";
    public static final String HUNTER_VILLAGER = "hunter_villager";
    public static final String CROSSBOW_ARROW = "crossbow_arrow";
    public static final String PARTICLE_CLOUD = "particle_cloud";
    public static final String THROWABLE_ITEM = "throwable_item";
    public static final String SPECIAL_DRACULA_HALLOWEEN = "special_dracula_halloween";

    /**
     * List of entity names which should be spawnable
     */
    public static final List<String> spawnableEntityNames = new ArrayList<>();
    private static final Map<String, String> OLD_TO_NEW_MAP = Maps.newHashMap();
    private static int modEntityId = 0;


    /**
     * Registers special extended creature classes
     */
    static void registerCustomExtendedCreatures() {
        IVampirismEntityRegistry registry = VampirismAPI.biteableRegistry();
    }

    /**
     * Register convertibles for vanilla creatures and maybe for future vampirism creature as well
     */
    static void registerConvertibles() {
        String base = REFERENCE.MODID + ":textures/entity/vanilla/%s_overlay.png";
        IVampirismEntityRegistry registry = VampirismAPI.biteableRegistry();
        registry.addConvertible(EntityCow.class, String.format(base, "cow"));
        registry.addConvertible(EntityPig.class, String.format(base, "pig"));
        registry.addConvertible(EntityOcelot.class, String.format(base, "cat"));
        registry.addConvertible(EntityHorse.class, String.format(base, "horse"));
        registry.addConvertible(EntityPolarBear.class, String.format(base, "polarbear"));
        registry.addConvertible(EntityRabbit.class, String.format(base, "rabbit"));
        registry.addConvertible(EntitySheep.class, String.format(base, "sheep"), new EntityConvertedSheep.ConvertingHandler());
        registry.addConvertible(EntityVillager.class, null, new EntityConvertedVillager.ConvertingHandler());
        registry.addConvertible(EntityLlama.class, String.format(base, "llama"));
    }

    static void registerEntities() {

        registerEntity(EntityBlindingBat.class, BLINDING_BAT_NAME, "blinding_bat", EntityLiving.SpawnPlacementType.IN_AIR, false);
        registerEntity(EntityGhost.class, GHOST_NAME, "ghost", EntityLiving.SpawnPlacementType.ON_GROUND, true);
        registerEntity(EntityConvertedCreature.class, CONVERTED_CREATURE, "converted.creature", EntityLiving.SpawnPlacementType.ON_GROUND, false);
        registerEntity(EntityConvertedSheep.class, CONVERTED_SHEEP, "converted.sheep", EntityLiving.SpawnPlacementType.ON_GROUND, false);
        registerEntity(EntityBasicHunter.class, BASIC_HUNTER_NAME, "vampireHunter", EntityLiving.SpawnPlacementType.ON_GROUND, true);
        registerEntity(EntityBasicVampire.class, BASIC_VAMPIRE_NAME, "vampire", EntityLiving.SpawnPlacementType.ON_GROUND, true);
        registerEntity(EntityHunterTrainer.class, HUNTER_TRAINER, "hunter_trainer", EntityLiving.SpawnPlacementType.ON_GROUND, true);
        registerEntity(EntityAdvancedHunter.class, ADVANCED_HUNTER, "advanced_hunter", EntityLiving.SpawnPlacementType.ON_GROUND, true);
        registerEntity(EntityVampireBaron.class, VAMPIRE_BARON, "vampireBaron", EntityLiving.SpawnPlacementType.ON_GROUND, true);
        registerEntity(EntityVampireMinionSaveable.class, VAMPIRE_MINION_SAVEABLE_NAME, "vampireMinionS", EntityLiving.SpawnPlacementType.ON_GROUND, false);
        registerEntity(EntityDummyBittenAnimal.class, DUMMY_CREATURE, "dummy_creature", EntityLiving.SpawnPlacementType.ON_GROUND, false);
        registerEntity(EntityAdvancedVampire.class, ADVANCED_VAMPIRE, "advanced_vampire", EntityLiving.SpawnPlacementType.ON_GROUND, true);
        registerEntity(EntityConvertedVillager.class, CONVERTED_VILLAGER, "converted.villager", EntityLiving.SpawnPlacementType.ON_GROUND, false);
        registerEntity(EntityHunterVillager.class, HUNTER_VILLAGER, "hunter_villager", EntityLiving.SpawnPlacementType.ON_GROUND, false);
        registerEntity(EntityCrossbowArrow.class, CROSSBOW_ARROW, "crossbow_arrow", EntityLiving.SpawnPlacementType.IN_AIR, false);
        registerEntity(EntityAreaParticleCloud.class, PARTICLE_CLOUD, "particle_cloud", EntityLiving.SpawnPlacementType.IN_AIR, false);
        registerEntity(EntityThrowableItem.class, THROWABLE_ITEM, "throwable_item", EntityLiving.SpawnPlacementType.IN_AIR, false);
        registerEntity(EntityDraculaHalloween.class, SPECIAL_DRACULA_HALLOWEEN, null, EntityLiving.SpawnPlacementType.ON_GROUND, false);
    }

    static void registerSpawns() {
        List<Biome> allBiomes = ForgeRegistries.BIOMES.getValues();
        /*
         * After setting this up this array will contain only biomes in which zombies can spawn.
         */
        List<Biome> zombieBiomes = Lists.newArrayList();
        zombieBiomes.addAll(allBiomes);
        zombieBiomes.remove(Biomes.MUSHROOM_ISLAND);
        zombieBiomes.remove(Biomes.MUSHROOM_ISLAND_SHORE);
        zombieBiomes.remove(Biomes.HELL);
        zombieBiomes.remove(Biomes.SKY);
        Iterator<Biome> iterator = zombieBiomes.iterator();
        while (iterator.hasNext()) {
            Biome b = iterator.next();
            if (b != null) {
                if (!b.getBiomeClass().getName().startsWith("net.minecraft.") && !b.getBiomeClass().getName().startsWith("de.teamlapen.")) {
                    Iterator<Biome.SpawnListEntry> iterator2 = b.getSpawnableList(EnumCreatureType.MONSTER).iterator();
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
        Biome[] biomes = zombieBiomes.toArray(new Biome[zombieBiomes.size()]);

        EntityRegistry.addSpawn(EntityBasicVampire.class, Balance.mobProps.VAMPIRE_SPAWN_CHANCE, 1, 2, EnumCreatureType.MONSTER, biomes);
        EntityRegistry.addSpawn(EntityAdvancedVampire.class, Balance.mobProps.ADVANCED_VAMPIRE_SPAWN_PROBE, 1, 1, EnumCreatureType.MONSTER, biomes);

    }


    public static IFixableData getEntityIDFixer() {
        return new IFixableData() {
            @Nonnull
            @Override
            public NBTTagCompound fixTagCompound(@Nonnull NBTTagCompound compound) {
                String s = OLD_TO_NEW_MAP.get(compound.getString("id"));

                if (s != null) {
                    compound.setString("id", s);
                }

                return compound;
            }

            @Override
            public int getFixVersion() {
                return 1;
            }
        };
    }

    public static IFixableData getPlayerCapabilityFixer() {
        return new IFixableData() {
            @Nonnull
            @Override
            public NBTTagCompound fixTagCompound(@Nonnull NBTTagCompound compound) {
                if (compound.hasKey("ForgeCaps")) {
                    NBTTagCompound caps = compound.getCompoundTag("ForgeCaps");
                    if (caps.hasKey("vampirism:IVampirePlayer")) {
                        NBTTagCompound vampire = caps.getCompoundTag("vampirism:IVampirePlayer");
                        caps.setTag(REFERENCE.VAMPIRE_PLAYER_KEY.toString(), vampire);
                        caps.removeTag("vampirism:IVampirePlayer");

                    }
                    if (caps.hasKey("vampirism:IHunterPlayer")) {
                        NBTTagCompound vampire = compound.getCompoundTag("vampirism:IHunterPlayer");
                        compound.setTag(REFERENCE.HUNTER_PLAYER_KEY.toString(), vampire);
                        compound.removeTag("vampirism:IHunterPlayer");

                    }
                    if (caps.hasKey("vampirism:IFactionPlayerHandler")) {
                        NBTTagCompound vampire = caps.getCompoundTag("vampirism:IFactionPlayerHandler");
                        caps.setTag(REFERENCE.FACTION_PLAYER_HANDLER_KEY.toString(), vampire);
                        caps.removeTag("vampirism:IFactionPlayerHandler");

                    }

                }
                return compound;
            }

            @Override
            public int getFixVersion() {
                return 1;
            }
        };
    }

    public static IFixableData getEntityCapabilityFixer() {
        return new IFixableData() {
            @Nonnull
            @Override
            public NBTTagCompound fixTagCompound(@Nonnull NBTTagCompound compound) {
                if (compound.hasKey("ForgeCaps")) {
                    NBTTagCompound caps = compound.getCompoundTag("ForgeCaps");
                    if (caps.hasKey("vampirism:IExtendedCreature")) {
                        NBTTagCompound vampire = caps.getCompoundTag("vampirism:IExtendedCreature");
                        caps.setTag(REFERENCE.EXTENDED_CREATURE_KEY.toString(), vampire);
                        caps.removeTag("vampirism:IExtendedCreature");

                    }

                }
                return compound;
            }

            @Override
            public int getFixVersion() {
                return 1;
            }
        };
    }

    private static void registerEntity(Class<? extends Entity> clazz, String id, @Nullable String oldName, EntityLiving.SpawnPlacementType placementType, boolean egg) {

        //VampirismMod.log.d("EntityRegister", "Adding " + name + "(" + clazz.getSimpleName() + ") with mod id %d", modEntityId);
        ResourceLocation n = new ResourceLocation(REFERENCE.MODID, id);
        EntityRegistry.registerModEntity(n, clazz, "vampirism." + id, modEntityId++, VampirismMod.instance, 80, 1, true);
        if (oldName != null) {
            OLD_TO_NEW_MAP.put("vampirism." + oldName, n.toString());
        }
        if (egg) {
            EntityRegistry.registerEgg(n, 0x8B15A3, id.hashCode());
            spawnableEntityNames.add(id);
        }

    }
}
