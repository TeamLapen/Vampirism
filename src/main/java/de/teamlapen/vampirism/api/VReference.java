package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.factions.PlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.DamageSource;

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
    public static DamageSource sundamage = new DamageSource("sun").setDamageBypassesArmor().setMagicDamage();
    /**
     * Vampire Player Faction
     * Filled during pre-init.
     */
    public static PlayableFaction<IVampirePlayer> VAMPIRE_FACTION;
    /**
     * Hunter Player Faction
     * Filled during pre-init.
     */
    public static PlayableFaction<IHunterPlayer> HUNTER_FACTION;
}
