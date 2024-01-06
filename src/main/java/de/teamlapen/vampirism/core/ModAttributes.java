package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, REFERENCE.MODID);

    /**
     * Attribute which defines sundamage. Registered for all IVampire mobs as well as the EntityPlayer.
     * Applied every 2 seconds if in sun
     */
    public static final DeferredHolder<Attribute, RangedAttribute> SUNDAMAGE = ATTRIBUTES.register("sundamage", () -> new RangedAttribute("vampirism.sundamage", 0.0D, 0.0D, 1000D));
    /**
     * Allows modifying the blood exhaustion. Is multiplied with the value calculated (from movement etc.).
     * Registered for EntityPlayer
     */
    public static final DeferredHolder<Attribute, RangedAttribute> BLOOD_EXHAUSTION = ATTRIBUTES.register("blood_exhaustion", () -> (RangedAttribute) new RangedAttribute("vampirism.blood_exhaustion", 1.0, 0.0, 10).setSyncable(true));
    /**
     * Allows modifying the duration of the neonatal effect.
     * Registered for EntityPlayer
     */
    public static final DeferredHolder<Attribute, RangedAttribute> NEONATAL_DURATION = ATTRIBUTES.register("neonatal_duration", () -> (RangedAttribute) new RangedAttribute("vampirism.neonatal_duration", 1.0, 0.0, Integer.MAX_VALUE).setSyncable(true));
    /**
     * Allows modifying the length of the resurrection timer.
     * Registered for EntityPlayer
     */
    public static final DeferredHolder<Attribute, RangedAttribute> DBNO_DURATION = ATTRIBUTES.register("dbno_duration", () -> (RangedAttribute) new RangedAttribute("vampirism.dbno_duration", 1.0, 0.0, Integer.MAX_VALUE).setSyncable(true));

    static void register(IEventBus bus) {
        ATTRIBUTES.register(bus);
    }

}