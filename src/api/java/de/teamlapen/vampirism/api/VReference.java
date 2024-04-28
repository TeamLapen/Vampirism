package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.PlantType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * Holds constants (or at init set variables)
 */
public class VReference {
    public static final String MODID = "vampirism";
    public static final ResourceLocation VAMPIRE_FACTION_ID = new ResourceLocation(MODID, "vampire");
    public static final ResourceLocation HUNTER_FACTION_ID = new ResourceLocation(MODID, "hunter");
    /**
     * One blood in the players blood stats represents this amount of mB fluid blood
     */
    public static final int FOOD_TO_FLUID_BLOOD = 100;

    /**
     * Plant type for plants that grow on cursed earth;
     */
    public static final PlantType VAMPIRE_PLANT_TYPE = PlantType.get("vampirism_vampire");

    public static final Supplier<Fluid> BLOOD = DeferredHolder.create(Registries.FLUID, new ResourceLocation("vampirism", "blood"));
    /**
     * Hunter creatures are of this creature type. But when they are counted for spawning they belong to {@link MobCategory#MONSTER}
     */
    public static MobCategory HUNTER_CREATURE_TYPE = MobCategory.create("vampirism_hunter", "vampirism_hunter", 15, false, false, 128);
    /**
     * Vampire creatures are of this creature type. But when they are counted for spawning they belong to {@link MobCategory#MONSTER}
     */
    public static MobCategory VAMPIRE_CREATURE_TYPE = MobCategory.create("vampirism_vampire", "vampirism_vampire", 30, false, false, 128);
    /**
     * Vampire Player Faction
     * Filled during pre-init.
     */
    public static IPlayableFaction<IVampirePlayer> VAMPIRE_FACTION;
    /**
     * Hunter Player Faction
     * Filled during pre-init.
     */
    public static IPlayableFaction<IHunterPlayer> HUNTER_FACTION;
    public static IVampireVision vision_nightVision;
    public static IVampireVision vision_bloodVision;
    public static final ResourceLocation PERMANENT_INVISIBLE_MOB_EFFECT = new ResourceLocation(MODID, "permanent");
}
