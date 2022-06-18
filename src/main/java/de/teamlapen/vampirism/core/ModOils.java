package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.items.oil.EffectWeaponOil;
import de.teamlapen.vampirism.items.oil.EvasionOil;
import de.teamlapen.vampirism.items.oil.Oil;
import de.teamlapen.vampirism.items.oil.SmeltingOil;
import net.minecraft.potion.Effects;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class ModOils {
    public static final DeferredRegister<IOil> OILS = DeferredRegister.create(ModRegistries.OILS, REFERENCE.MODID);

    public static final RegistryObject<IOil> EMPTY = OILS.register("empty", () -> new Oil(16253176));
    public static final RegistryObject<IOil> PLANT = OILS.register("plant", () -> new Oil(0x7e6d27));
    public static final RegistryObject<IOil> VAMPIRE_BLOOD = OILS.register("vampire_blood",() -> new Oil(0x922847));
    public static final RegistryObject<EffectWeaponOil> POISON = OILS.register("poison",() -> new EffectWeaponOil(Effects.POISON, 50, 15));
    public static final RegistryObject<EffectWeaponOil> WEAKNESS = OILS.register("weakness",() -> new EffectWeaponOil(Effects.WEAKNESS, 50, 15));
    public static final RegistryObject<EffectWeaponOil> SLOWNESS = OILS.register("slowness", () -> new EffectWeaponOil(Effects.MOVEMENT_SLOWDOWN, 100, 15));
    public static final RegistryObject<EffectWeaponOil> HEALING = OILS.register("healing",()-> new EffectWeaponOil(Effects.HEAL, 1, 5));
    public static final RegistryObject<EffectWeaponOil> FIRE_RESISTANCE = OILS.register("fire_resistance",() -> new EffectWeaponOil(Effects.FIRE_RESISTANCE, 200, 20));
    public static final RegistryObject<EffectWeaponOil> SWIFTNESS = OILS.register("swiftness",() -> new EffectWeaponOil(Effects.MOVEMENT_SPEED, 200, 15));
    public static final RegistryObject<EffectWeaponOil> REGENERATION = OILS.register("regeneration",() -> new EffectWeaponOil(Effects.REGENERATION, 100, 10));
    public static final RegistryObject<EffectWeaponOil> NIGHT_VISION = OILS.register("night_vision",() -> new EffectWeaponOil(Effects.NIGHT_VISION, 100, 15));
    public static final RegistryObject<EffectWeaponOil> STRENGTH = OILS.register("strength",() -> new EffectWeaponOil(Effects.DAMAGE_BOOST, 100, 10));
    public static final RegistryObject<EffectWeaponOil> JUMP = OILS.register("jump",() -> new EffectWeaponOil(Effects.JUMP, 100, 20));
    public static final RegistryObject<EffectWeaponOil> WATER_BREATHING = OILS.register("water_breathing",() -> new EffectWeaponOil(Effects.WATER_BREATHING, 200, 15));
    public static final RegistryObject<EffectWeaponOil> INVISIBILITY = OILS.register("invisibility",() -> new EffectWeaponOil(Effects.INVISIBILITY, 100, 15));
    public static final RegistryObject<EffectWeaponOil> SLOW_FALLING = OILS.register("slow_falling",() -> new EffectWeaponOil(Effects.SLOW_FALLING, 200, 20));
    public static final RegistryObject<EffectWeaponOil> LUCK = OILS.register("luck",() -> new EffectWeaponOil(Effects.LUCK, 200, 20));
    public static final RegistryObject<EffectWeaponOil> HARM = OILS.register("harm",() -> new EffectWeaponOil(Effects.HARM, 1, 5));
    public static final RegistryObject<SmeltingOil> SMELT = OILS.register("smelt",() -> new SmeltingOil(0x123456, 10));
    public static final RegistryObject<IOil> TELEPORT = OILS.register("teleport",() -> new Oil(0x0b4d42));
    public static final RegistryObject<EvasionOil> EVASION = OILS.register("evasion",() ->new EvasionOil(0x888800, 60));

    static void registerOils(IEventBus bus) {
        OILS.register(bus);
    }
}
