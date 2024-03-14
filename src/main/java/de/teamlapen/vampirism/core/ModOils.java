package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.items.oil.EffectWeaponOil;
import de.teamlapen.vampirism.items.oil.EvasionOil;
import de.teamlapen.vampirism.items.oil.Oil;
import de.teamlapen.vampirism.items.oil.SmeltingOil;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModOils {
    public static final DeferredRegister<IOil> OILS = DeferredRegister.create(VampirismRegistries.Keys.OIL, REFERENCE.MODID);

    public static final DeferredHolder<IOil, IOil> EMPTY = OILS.register("empty", () -> new Oil(16253176));
    public static final DeferredHolder<IOil, IOil> PLANT = OILS.register("plant", () -> new Oil(0x7e6d27));
    public static final DeferredHolder<IOil, IOil> VAMPIRE_BLOOD = OILS.register("vampire_blood", () -> new Oil(0x922847));
    public static final DeferredHolder<IOil, EffectWeaponOil> POISON = OILS.register("poison", () -> new EffectWeaponOil(MobEffects.POISON, 50, 15));
    public static final DeferredHolder<IOil, EffectWeaponOil> WEAKNESS = OILS.register("weakness", () -> new EffectWeaponOil(MobEffects.WEAKNESS, 50, 15));
    public static final DeferredHolder<IOil, EffectWeaponOil> SLOWNESS = OILS.register("slowness", () -> new EffectWeaponOil(MobEffects.MOVEMENT_SLOWDOWN, 100, 15));
    public static final DeferredHolder<IOil, EffectWeaponOil> HEALING = OILS.register("healing", () -> new EffectWeaponOil(MobEffects.HEAL, 1, 5));
    public static final DeferredHolder<IOil, EffectWeaponOil> FIRE_RESISTANCE = OILS.register("fire_resistance", () -> new EffectWeaponOil(MobEffects.FIRE_RESISTANCE, 200, 20));
    public static final DeferredHolder<IOil, EffectWeaponOil> SWIFTNESS = OILS.register("swiftness", () -> new EffectWeaponOil(MobEffects.MOVEMENT_SPEED, 200, 15));
    public static final DeferredHolder<IOil, EffectWeaponOil> REGENERATION = OILS.register("regeneration", () -> new EffectWeaponOil(MobEffects.REGENERATION, 100, 10));
    public static final DeferredHolder<IOil, EffectWeaponOil> NIGHT_VISION = OILS.register("night_vision", () -> new EffectWeaponOil(MobEffects.NIGHT_VISION, 100, 15));
    public static final DeferredHolder<IOil, EffectWeaponOil> STRENGTH = OILS.register("strength", () -> new EffectWeaponOil(MobEffects.DAMAGE_BOOST, 100, 10));
    public static final DeferredHolder<IOil, EffectWeaponOil> JUMP = OILS.register("jump", () -> new EffectWeaponOil(MobEffects.JUMP, 100, 20));
    public static final DeferredHolder<IOil, EffectWeaponOil> WATER_BREATHING = OILS.register("water_breathing", () -> new EffectWeaponOil(MobEffects.WATER_BREATHING, 200, 15));
    public static final DeferredHolder<IOil, EffectWeaponOil> INVISIBILITY = OILS.register("invisibility", () -> new EffectWeaponOil(MobEffects.INVISIBILITY, 100, 15));
    public static final DeferredHolder<IOil, EffectWeaponOil> SLOW_FALLING = OILS.register("slow_falling", () -> new EffectWeaponOil(MobEffects.SLOW_FALLING, 200, 20));
    public static final DeferredHolder<IOil, EffectWeaponOil> LUCK = OILS.register("luck", () -> new EffectWeaponOil(MobEffects.LUCK, 200, 20));
    public static final DeferredHolder<IOil, EffectWeaponOil> HARM = OILS.register("harm", () -> new EffectWeaponOil(MobEffects.HARM, 1, 5));
    public static final DeferredHolder<IOil, SmeltingOil> SMELT = OILS.register("smelt", () -> new SmeltingOil(0x123456, 30));
    public static final DeferredHolder<IOil, IOil> TELEPORT = OILS.register("teleport", () -> new Oil(0x0b4d42));
    public static final DeferredHolder<IOil, EvasionOil> EVASION = OILS.register("evasion", () -> new EvasionOil(0x888800, 60));
    public static final DeferredHolder<IOil, IOil> GARLIC = OILS.register("garlic", () -> new Oil(0xffffff));
    public static final DeferredHolder<IOil, IOil> SPITFIRE = OILS.register("spitfire", () -> new Oil(0xFF2211));
    public static final DeferredHolder<IOil, IOil> VAMPIRE_KILLER = OILS.register("vampire_killer", () -> new Oil(0x7A0073));
    public static final DeferredHolder<IOil, IOil> BLEEDING = OILS.register("bleeding", () -> new Oil(11141120));

    static void register(IEventBus bus) {
        OILS.register(bus);
    }
}
