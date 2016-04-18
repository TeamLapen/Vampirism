package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
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
    public static final String FLUID_BLOOD_NAME = "vampirismblood";
    /**
     * Attribute which defines sundamage. Registered for all IVampire mobs as well as the EntityPlayer.
     * Applied every 2 seconds if in sun
     */
    public final static IAttribute sunDamage = (new RangedAttribute(null, "vampirism.sundamage", 0.0D, 0.0D, 1000D));
    /**
     * Allows modifying the blood exhaustion. Is multiplied with with the value calculated (from movement etc.).
     * Registered for EntityPlayer
     */
    public final static IAttribute bloodExhaustion = (new RangedAttribute(null, "vampirism.blood_exhaustion", 1.0, 0.0, 10)).setShouldWatch(true);

    /**
     * Allows modifying bite damage.
     * Registered for EntityPlayer
     */
    public final static IAttribute biteDamage = (new RangedAttribute(null, "vampirism.bite_damage", 0.0, 0.0, 100));

    /**
     * Attribute which defines farlic. Registered for all IVampire mobs as well as the EntityPlayer.
     * Applied every 2 seconds if in garlic
     */
    public final static IAttribute garlicDamage = (new RangedAttribute(null, "vampirism.garlicdamage", 0.0D, 0.0D, 1000D));
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
    public static IVampireVision vision_nightVision;
    public static IVampireVision vision_bloodVision;
}
