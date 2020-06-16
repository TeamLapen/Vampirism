package de.teamlapen.vampirism.entity.minion.management;


import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

@ObjectHolder(REFERENCE.MODID)
public class MinionTasks {

    public final static SimpleMinionTask stay = getNull();

    public static void register(IForgeRegistry<IMinionTask<?>> registry) {
        registry.register(new SimpleMinionTask().setRegistryName(REFERENCE.MODID, "stay"));
    }
}
