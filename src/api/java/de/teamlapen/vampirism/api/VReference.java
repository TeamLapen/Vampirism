package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.PlantType;

/**
 * Holds constants (or at init set variables)
 */
public class VReference {

    /**
     * One blood in the players blood stats represents this amount of mB fluid blood
     */
    public static final int FOOD_TO_FLUID_BLOOD = 100;
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
     * Plant type for plants that grow on cursed earth;
     */
    public static final PlantType VAMPIRE_PLANT_TYPE = PlantType.create("VAMPIRISM_VAMPIRE");
    public static final DamageSource SUNDAMAGE = new DamageSource("sun").setDamageBypassesArmor().setMagicDamage();
    public static final DamageSource VAMPIRE_ON_FIRE = new DamageSource("vampire_on_fire").setDamageBypassesArmor().setMagicDamage();
    public static final DamageSource VAMPIRE_IN_FIRE = new DamageSource("vampire_in_fire").setMagicDamage();
    public static final DamageSource HOLY_WATER = new DamageSource("holy_water").setMagicDamage();
    public static final DamageSource NO_BLOOD = new DamageSource("blood_loss").setDamageBypassesArmor().setMagicDamage();
    /**
     * Enchantment type for crossbows
     */
    public static final EnchantmentType CROSSBOW_ENCHANTMENT = EnchantmentType.create("VAMPIRISM_CROSSBOW", input -> input instanceof IVampirismCrossbow);
    public static Fluid blood_fluid;
    /**
     * Hunter creatures are of this creature type. But when they are counted for spawning they belong to {@link EntityClassification#MONSTER}
     */
    public static EntityClassification HUNTER_CREATURE_TYPE;
    /**
     * Vampire creatures are of this creature type. But when they are counted for spawning they belong to {@link EntityClassification#MONSTER}
     */
    public static EntityClassification VAMPIRE_CREATURE_TYPE;
    /**
     * Vampire creatures have this creature attribute.
     * Don't know why this exists alongside EnumCreatureType, but this is used by enchanments
     */
    public static CreatureAttribute VAMPIRE_CREATURE_ATTRIBUTE;
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
}
