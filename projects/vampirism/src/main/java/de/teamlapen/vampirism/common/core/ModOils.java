package de.teamlapen.vampirism.common.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.world.items.oil.IOil;
import de.teamlapen.vampirism.common.world.items.oil.EffectWeaponOil;
import de.teamlapen.vampirism.common.world.items.oil.EvasionOil;
import de.teamlapen.vampirism.common.world.items.oil.Oil;
import de.teamlapen.vampirism.common.world.items.oil.SmeltingOil;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModOils {
    public static final DeferredRegister<IOil> OILS = DeferredRegister.create(VampirismRegistries.Keys.OIL, REFERENCE.MODID);

    public static final DeferredHolder<IOil, IOil> EMPTY = OILS.register("empty", () -> new Oil(16253176));
    public static final DeferredHolder<IOil, IOil> PLANT = OILS.register("plant", () -> new Oil(0x7e6d27));
    public static final DeferredHolder<IOil, IOil> VAMPIRE_BLOOD = OILS.register("vampire_blood", () -> new Oil(0x922847));
    public static final DeferredHolder<IOil, IOil> SOVEREIGN_BLOOD = OILS.register("sovereign_blood", () -> new Oil(0xb90a5f));
    public static final DeferredHolder<IOil, EffectWeaponOil> POISON = OILS.register("poison", () -> new EffectWeaponOil(MobEffects.POISON, 50, 15));
    public static final DeferredHolder<IOil, EffectWeaponOil> POISON_STRONG = OILS.register("poison_strong", () -> new EffectWeaponOil(MobEffects.POISON, 50, 30));
    public static final DeferredHolder<IOil, EffectWeaponOil> WEAKNESS = OILS.register("weakness", () -> new EffectWeaponOil(MobEffects.WEAKNESS, 50, 15));
    public static final DeferredHolder<IOil, EffectWeaponOil> WEAKNESS_STRONG = OILS.register("weakness_strong", () -> new EffectWeaponOil(MobEffects.WEAKNESS, 50, 30));
    public static final DeferredHolder<IOil, EffectWeaponOil> SLOWNESS = OILS.register("slowness", () -> new EffectWeaponOil(MobEffects.SLOWNESS, 100, 15));
    public static final DeferredHolder<IOil, EffectWeaponOil> SLOWNESS_STRONG = OILS.register("slowness_strong", () -> new EffectWeaponOil(MobEffects.SLOWNESS, 100, 30));
    public static final DeferredHolder<IOil, EffectWeaponOil> HEALING = OILS.register("healing", () -> new EffectWeaponOil(MobEffects.REGENERATION, 1, 5));
    public static final DeferredHolder<IOil, EffectWeaponOil> HEALING_STRONG = OILS.register("healing_strong", () -> new EffectWeaponOil(MobEffects.REGENERATION, 1, 10));
    public static final DeferredHolder<IOil, EffectWeaponOil> FIRE_RESISTANCE = OILS.register("fire_resistance", () -> new EffectWeaponOil(MobEffects.FIRE_RESISTANCE, 200, 20));
    public static final DeferredHolder<IOil, EffectWeaponOil> FIRE_RESISTANCE_STRONG = OILS.register("fire_resistance_strong", () -> new EffectWeaponOil(MobEffects.FIRE_RESISTANCE, 200, 40));
    public static final DeferredHolder<IOil, EffectWeaponOil> SWIFTNESS = OILS.register("swiftness", () -> new EffectWeaponOil(MobEffects.SPEED, 200, 15));
    public static final DeferredHolder<IOil, EffectWeaponOil> SWIFTNESS_STRONG = OILS.register("swiftness_strong", () -> new EffectWeaponOil(MobEffects.SPEED, 200, 30));
    public static final DeferredHolder<IOil, EffectWeaponOil> REGENERATION = OILS.register("regeneration", () -> new EffectWeaponOil(MobEffects.REGENERATION, 100, 10));
    public static final DeferredHolder<IOil, EffectWeaponOil> REGENERATION_STRONG = OILS.register("regeneration_strong", () -> new EffectWeaponOil(MobEffects.REGENERATION, 100, 20));
    public static final DeferredHolder<IOil, EffectWeaponOil> NIGHT_VISION = OILS.register("night_vision", () -> new EffectWeaponOil(MobEffects.NIGHT_VISION, 100, 15));
    public static final DeferredHolder<IOil, EffectWeaponOil> NIGHT_VISION_STRONG = OILS.register("night_vision_strong", () -> new EffectWeaponOil(MobEffects.NIGHT_VISION, 100, 30));
    public static final DeferredHolder<IOil, EffectWeaponOil> STRENGTH = OILS.register("strength", () -> new EffectWeaponOil(MobEffects.STRENGTH, 100, 10));
    public static final DeferredHolder<IOil, EffectWeaponOil> STRENGTH_STRONG = OILS.register("strength_strong", () -> new EffectWeaponOil(MobEffects.STRENGTH, 100, 20));
    public static final DeferredHolder<IOil, EffectWeaponOil> JUMP = OILS.register("jump", () -> new EffectWeaponOil(MobEffects.JUMP_BOOST, 100, 20));
    public static final DeferredHolder<IOil, EffectWeaponOil> JUMP_STRONG = OILS.register("jump_strong", () -> new EffectWeaponOil(MobEffects.JUMP_BOOST, 100, 40));
    public static final DeferredHolder<IOil, EffectWeaponOil> WATER_BREATHING = OILS.register("water_breathing", () -> new EffectWeaponOil(MobEffects.WATER_BREATHING, 200, 15));
    public static final DeferredHolder<IOil, EffectWeaponOil> WATER_BREATHING_STRONG = OILS.register("water_breathing_strong", () -> new EffectWeaponOil(MobEffects.WATER_BREATHING, 200, 30));
    public static final DeferredHolder<IOil, EffectWeaponOil> INVISIBILITY = OILS.register("invisibility", () -> new EffectWeaponOil(MobEffects.INVISIBILITY, 100, 15));
    public static final DeferredHolder<IOil, EffectWeaponOil> INVISIBILITY_STRONG = OILS.register("invisibility_strong", () -> new EffectWeaponOil(MobEffects.INVISIBILITY, 100, 30));
    public static final DeferredHolder<IOil, EffectWeaponOil> SLOW_FALLING = OILS.register("slow_falling", () -> new EffectWeaponOil(MobEffects.SLOW_FALLING, 200, 20));
    public static final DeferredHolder<IOil, EffectWeaponOil> SLOW_FALLING_STRONG = OILS.register("slow_falling_strong", () -> new EffectWeaponOil(MobEffects.SLOW_FALLING, 200, 40));
    public static final DeferredHolder<IOil, EffectWeaponOil> LUCK = OILS.register("luck", () -> new EffectWeaponOil(MobEffects.LUCK, 200, 20));
    public static final DeferredHolder<IOil, EffectWeaponOil> LUCK_STRONG = OILS.register("luck_strong", () -> new EffectWeaponOil(MobEffects.LUCK, 200, 40));
    public static final DeferredHolder<IOil, EffectWeaponOil> HARM = OILS.register("harm", () -> new EffectWeaponOil(MobEffects.INSTANT_DAMAGE, 1, 5));
    public static final DeferredHolder<IOil, EffectWeaponOil> HARM_STRONG = OILS.register("harm_strong", () -> new EffectWeaponOil(MobEffects.INSTANT_DAMAGE, 1, 10));
    public static final DeferredHolder<IOil, SmeltingOil> SMELT = OILS.register("smelt", () -> new SmeltingOil(0x123456, 30));
    public static final DeferredHolder<IOil, SmeltingOil> SMELT_STRONG = OILS.register("smelt_strong", () -> new SmeltingOil(0x123456, 60));
    public static final DeferredHolder<IOil, IOil> TELEPORT = OILS.register("teleport", () -> new Oil(0x0b4d42));
    public static final DeferredHolder<IOil, EvasionOil> EVASION = OILS.register("evasion", () -> new EvasionOil(0x888800, 60));
    public static final DeferredHolder<IOil, EvasionOil> EVASION_STRONG = OILS.register("evasion_strong", () -> new EvasionOil(0x888800, 120));
    public static final DeferredHolder<IOil, IOil> GARLIC = OILS.register("garlic", () -> new Oil(0xffffff));
    public static final DeferredHolder<IOil, IOil> SPITFIRE = OILS.register("spitfire", () -> new Oil(0xFF2211));
    public static final DeferredHolder<IOil, IOil> VAMPIRE_KILLER = OILS.register("vampire_killer", () -> new Oil(0x7A0073));
    public static final DeferredHolder<IOil, IOil> BLEEDING = OILS.register("bleeding", () -> new Oil(11141120));

    static void register(IEventBus bus) {
        OILS.register(bus);
    }
}
