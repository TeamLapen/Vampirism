package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.items.oil.EffectWeaponOil;
import de.teamlapen.vampirism.items.oil.Oil;
import de.teamlapen.vampirism.items.oil.SmeltingOil;
import net.minecraft.potion.Effects;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModOils {

    public static final IOil empty = getNull();
    public static final IOil plant_oil = getNull();
    public static final IOil vampire_blood_oil = getNull();
    public static final EffectWeaponOil poison_oil = getNull();
    public static final EffectWeaponOil weakness_oil = getNull();
    public static final EffectWeaponOil slowness_oil = getNull();
    public static final EffectWeaponOil healing_oil = getNull();
    public static final EffectWeaponOil fire_resistance_oil = getNull();
    public static final EffectWeaponOil swiftness_oil = getNull();
    public static final EffectWeaponOil regeneration_oil = getNull();
    public static final EffectWeaponOil night_vision_oil = getNull();
    public static final EffectWeaponOil strength_oil = getNull();
    public static final EffectWeaponOil jump_oil = getNull();
    public static final EffectWeaponOil water_breathing_oil = getNull();
    public static final EffectWeaponOil invisibility_oil = getNull();
    public static final EffectWeaponOil slow_falling_oil = getNull();
    public static final EffectWeaponOil luck_oil = getNull();
    public static final EffectWeaponOil harm_oil = getNull();
    public static final SmeltingOil smelt_oil = getNull();
    public static final IOil teleport_oil = getNull();

    public static void register(IForgeRegistry<IOil> registry) {
        registry.register(new Oil(16253176).setRegistryName(REFERENCE.MODID, "empty"));
        registry.register(new Oil(0x7e6d27).setRegistryName(REFERENCE.MODID, "plant_oil"));
        registry.register(new Oil(0x922847).setRegistryName(REFERENCE.MODID, "vampire_blood_oil"));
        registry.register(new EffectWeaponOil(Effects.POISON, 50, 15).setRegistryName(REFERENCE.MODID, "poison_oil"));
        registry.register(new EffectWeaponOil(Effects.WEAKNESS, 50, 15).setRegistryName(REFERENCE.MODID, "weakness_oil"));
        registry.register(new EffectWeaponOil(Effects.MOVEMENT_SLOWDOWN, 100, 15).setRegistryName(REFERENCE.MODID, "slowness_oil"));
        registry.register(new EffectWeaponOil(Effects.HEAL, 1, 5).setRegistryName(REFERENCE.MODID, "healing_oil"));
        registry.register(new EffectWeaponOil(Effects.FIRE_RESISTANCE, 200, 20).setRegistryName(REFERENCE.MODID, "fire_resistance_oil"));
        registry.register(new EffectWeaponOil(Effects.MOVEMENT_SPEED, 200, 15).setRegistryName(REFERENCE.MODID, "swiftness_oil"));
        registry.register(new EffectWeaponOil(Effects.REGENERATION, 100, 10).setRegistryName(REFERENCE.MODID, "regeneration_oil"));
        registry.register(new EffectWeaponOil(Effects.NIGHT_VISION, 100, 15).setRegistryName(REFERENCE.MODID, "night_vision_oil"));
        registry.register(new EffectWeaponOil(Effects.DAMAGE_BOOST, 100, 10).setRegistryName(REFERENCE.MODID, "strength_oil"));
        registry.register(new EffectWeaponOil(Effects.JUMP, 100, 20).setRegistryName(REFERENCE.MODID, "jump_oil"));
        registry.register(new EffectWeaponOil(Effects.WATER_BREATHING, 200, 15).setRegistryName(REFERENCE.MODID, "water_breathing_oil"));
        registry.register(new EffectWeaponOil(Effects.INVISIBILITY, 100, 15).setRegistryName(REFERENCE.MODID, "invisibility_oil"));
        registry.register(new EffectWeaponOil(Effects.SLOW_FALLING, 200, 20).setRegistryName(REFERENCE.MODID, "slow_falling_oil"));
        registry.register(new EffectWeaponOil(Effects.LUCK, 200, 20).setRegistryName(REFERENCE.MODID, "luck_oil"));
        registry.register(new EffectWeaponOil(Effects.HARM, 1, 5).setRegistryName(REFERENCE.MODID, "harm_oil"));
        registry.register(new SmeltingOil(0x123456, 10).setRegistryName(REFERENCE.MODID, "smelt_oil"));
        registry.register(new Oil(0x0b4d42).setRegistryName(REFERENCE.MODID, "teleport_oil"));
    }
}
