package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.items.oil.EffectWeaponOil;
import de.teamlapen.vampirism.items.oil.Oil;
import net.minecraft.potion.EffectInstance;
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

    public static void register(IForgeRegistry<IOil> registry) {
        registry.register(new Oil(16253176).setRegistryName(REFERENCE.MODID, "empty"));
        registry.register(new Oil(0x7e6d27).setRegistryName(REFERENCE.MODID, "plant_oil"));
        registry.register(new Oil(0x922847).setRegistryName(REFERENCE.MODID, "vampire_blood_oil"));
        registry.register(new EffectWeaponOil(new EffectInstance(Effects.POISON, 200), 10).setRegistryName(REFERENCE.MODID, "poison_oil"));
        registry.register(new EffectWeaponOil(new EffectInstance(Effects.WEAKNESS, 200), 10).setRegistryName(REFERENCE.MODID, "weakness_oil"));
        registry.register(new EffectWeaponOil(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 200), 10).setRegistryName(REFERENCE.MODID, "slowness_oil"));
        registry.register(new EffectWeaponOil(new EffectInstance(Effects.HEAL, 200), 10).setRegistryName(REFERENCE.MODID, "healing_oil"));
        registry.register(new EffectWeaponOil(new EffectInstance(Effects.FIRE_RESISTANCE, 200), 10).setRegistryName(REFERENCE.MODID, "fire_resistance_oil"));
        registry.register(new EffectWeaponOil(new EffectInstance(Effects.MOVEMENT_SPEED, 200), 10).setRegistryName(REFERENCE.MODID, "swiftness_oil"));
        registry.register(new EffectWeaponOil(new EffectInstance(Effects.REGENERATION, 200), 10).setRegistryName(REFERENCE.MODID, "regeneration_oil"));
        registry.register(new EffectWeaponOil(new EffectInstance(Effects.NIGHT_VISION, 200), 10).setRegistryName(REFERENCE.MODID, "night_vision_oil"));
    }
}
