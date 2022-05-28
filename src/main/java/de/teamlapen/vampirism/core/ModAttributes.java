package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, REFERENCE.MODID);
    /**
     * Attribute which defines sundamage. Registered for all IVampire mobs as well as the EntityPlayer.
     * Applied every 2 seconds if in sun
     */
    public static final RegistryObject<RangedAttribute> SUNDAMAGE = ATTRIBUTES.register("sundamage", () -> new RangedAttribute("vampirism.sundamage", 0.0D, 0.0D, 1000D));
    /**
     * Allows modifying the blood exhaustion. Is multiplied with with the value calculated (from movement etc.).
     * Registered for EntityPlayer
     */
    public static final RegistryObject<RangedAttribute> BLOOD_EXHAUSTION = ATTRIBUTES.register("blood_exhaustion", () -> (RangedAttribute) new RangedAttribute("vampirism.blood_exhaustion", 1.0, 0.0, 10).setSyncable(true));
    /**
     * Allows modifying bite damage.
     * Registered for EntityPlayer
     * TODO 1.17 remove
     */
    public static final RegistryObject<RangedAttribute> BITE_DAMAGE = ATTRIBUTES.register("bite_damage", () -> new RangedAttribute("vampirism.bite_damage", 0.0, 0.0, 100));

    static void registerAttributes(IEventBus bus) {
        ATTRIBUTES.register(bus);
    }

}
