package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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
    public static final EnchantmentCategory CROSSBOW_ENCHANTMENT = EnchantmentCategory.create("VAMPIRISM_CROSSBOW", IVampirismCrossbow.class::isInstance);

    public static final RegistryObject<Fluid> BLOOD = RegistryObject.create(new ResourceLocation("vampirism", "blood"), ForgeRegistries.FLUIDS);
    /**
     * Hunter creatures are of this creature type. But when they are counted for spawning they belong to {@link MobCategory#MONSTER}
     */
    public static MobCategory HUNTER_CREATURE_TYPE;
    /**
     * Vampire creatures are of this creature type. But when they are counted for spawning they belong to {@link MobCategory#MONSTER}
     */
    public static MobCategory VAMPIRE_CREATURE_TYPE;
    /**
     * Vampire creatures have this creature attribute.
     * Don't know why this exists alongside EnumCreatureType, but this is used by enchantments
     */
    public static MobType VAMPIRE_CREATURE_ATTRIBUTE;
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
