package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, REFERENCE.MODID);

    /**
     * Attribute which defines sundamage. Registered for all IVampire mobs as well as the EntityPlayer.
     * Applied every 2 seconds if in sun
     */
    public static final RegistryObject<RangedAttribute> SUNDAMAGE = ATTRIBUTES.register("sundamage", () -> new RangedAttribute("vampirism.sundamage", 0.0D, 0.0D, 1000D));
    /**
     * Allows modifying the blood exhaustion. Is multiplied with the value calculated (from movement etc.).
     * Registered for EntityPlayer
     */
    public static final RegistryObject<RangedAttribute> BLOOD_EXHAUSTION = ATTRIBUTES.register("blood_exhaustion", () -> (RangedAttribute) new RangedAttribute("vampirism.blood_exhaustion", 1.0, 0.0, 10).setSyncable(true));

    static void register(IEventBus bus) {
        ATTRIBUTES.register(bus);
    }

}