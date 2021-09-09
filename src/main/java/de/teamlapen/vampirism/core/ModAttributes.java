package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;


@ObjectHolder(REFERENCE.MODID)
public class ModAttributes {
    /**
     * Attribute which defines sundamage. Registered for all IVampire mobs as well as the EntityPlayer.
     * Applied every 2 seconds if in sun
     */
    public static final RangedAttribute sundamage = getNull();
    /**
     * Allows modifying the blood exhaustion. Is multiplied with with the value calculated (from movement etc.).
     * Registered for EntityPlayer
     */
    public static final RangedAttribute blood_exhaustion = getNull();

    static void registerAttributes(IForgeRegistry<Attribute> registry) {
        registry.register(new RangedAttribute("vampirism.sundamage", 0.0D, 0.0D, 1000D).setRegistryName(REFERENCE.MODID, "sundamage"));
        registry.register(new RangedAttribute("vampirism.blood_exhaustion", 1.0, 0.0, 10).setSyncable(true)/*shouldWatch*/.setRegistryName(REFERENCE.MODID, "blood_exhaustion"));
    }

}
