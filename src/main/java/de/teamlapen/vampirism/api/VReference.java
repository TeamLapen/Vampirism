package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.EnumPlantType;

/**
 * Holds constants (or at init set variables)
 */
public class VReference {

    /**
     * One blood in the players blood stats represents this amount of mB fluid blood
     */
    public static final int FOOD_TO_FLUID_BLOOD = 100;
    public static final String EXTENDED_CREATURE_PROP = "ExtCreatureVampirism";
    public static final String FACTION_PLAYER_HANDLER_PROP = "FactionHandlerVampirism";
    public static final String FLUID_BLOOD_NAME = "vampirismblood";
    /**
     * Hunter creatures are of this creature type. But when they are counted for spawning they belong to {@link EnumCreatureType#MONSTER}
     */
    public static EnumCreatureType hunterCreatureType;
    /**
     * Vampire creatures are of this creature type. But when they are counted for spawning they belong to {@link EnumCreatureType#MONSTER}
     */
    public static EnumCreatureType vampireCreatureType;
    /**
     * Plant type for plants that grow on cursed earth;
     */
    public static EnumPlantType vampirePlantType;
    public static DamageSource sundamage = new DamageSource("sun").setDamageBypassesArmor().setMagicDamage();
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

    public static int castleDimId = 1000;
}
