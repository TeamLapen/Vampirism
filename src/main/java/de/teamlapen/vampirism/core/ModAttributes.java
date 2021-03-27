package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
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
    /**
     * Allows modifying bite damage.
     * Registered for EntityPlayer
     * TODO 1.17 remove
     */
    public static final RangedAttribute bite_damage = getNull();

    static void registerAttributes(IForgeRegistry<Attribute> registry) {

        registry.register(new RangedAttribute("vampirism.sundamage", 0.0D, 0.0D, 1000D).setRegistryName(REFERENCE.MODID, "sundamage"));
        registry.register(new RangedAttribute("vampirism.blood_exhaustion", 1.0, 0.0, 10).setShouldWatch(true)/*shouldWatch*/.setRegistryName(REFERENCE.MODID, "blood_exhaustion"));
        registry.register(new RangedAttribute("vampirism.bite_damage", 0.0, 0.0, 100).setRegistryName(REFERENCE.MODID, "bite_damage"));
    }

}
