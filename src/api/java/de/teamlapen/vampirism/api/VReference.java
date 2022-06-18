package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityClassification;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.fml.RegistryObject;

/**
 * Holds constants (or at init set variables)
 */
public class VReference {

    /**
     * One blood in the players blood stats represents this amount of mB fluid blood
     */
    public static final int FOOD_TO_FLUID_BLOOD = 100;

    /**
     * Plant type for plants that grow on cursed earth;
     */
    public static final PlantType VAMPIRE_PLANT_TYPE = PlantType.get("vampirism_vampire");
    public static final DamageSource SUNDAMAGE = new DamageSource("sun").bypassArmor().setMagic();
    public static final DamageSource VAMPIRE_ON_FIRE = new DamageSource("vampire_on_fire").bypassArmor().setMagic().setIsFire();
    public static final DamageSource VAMPIRE_IN_FIRE = new DamageSource("vampire_in_fire").setMagic().setIsFire();
    public static final DamageSource HOLY_WATER = new DamageSource("holy_water").setMagic();
    public static final DamageSource NO_BLOOD = new DamageSource("blood_loss").bypassArmor().setMagic();
    /**
     * Enchantment type for crossbows
     */
    public static final EnchantmentType CROSSBOW_ENCHANTMENT = EnchantmentType.create("VAMPIRISM_CROSSBOW", input -> input instanceof IVampirismCrossbow);
    @Deprecated
    public static Fluid blood_fluid;
    public static RegistryObject<Fluid> blood_fluid_supplier;
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
