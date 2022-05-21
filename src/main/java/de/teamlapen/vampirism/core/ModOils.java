package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.items.oil.Oil;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class ModOils {

    public static final Oil empty = getNull();
    public static final Oil plant_oil = getNull();
    public static final Oil vampire_blood_oil = getNull();

    public static void register(IForgeRegistry<IOil> registry) {
        registry.register(new Oil(16253176).setRegistryName(REFERENCE.MODID, "empty"));
        registry.register(new Oil(0x7e6d27).setRegistryName(REFERENCE.MODID, "plant_oil"));
        registry.register(new Oil(0x922847).setRegistryName(REFERENCE.MODID, "vampire_blood_oil"));
    }
}
