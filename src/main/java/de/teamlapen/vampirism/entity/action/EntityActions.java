package de.teamlapen.vampirism.entity.action;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.entity.vampire.actions.HealVampireEntityAction;
import de.teamlapen.vampirism.entity.vampire.actions.InvisibleVampireEntityAction;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Register and holds all actions for vampire entity
 */
@GameRegistry.ObjectHolder(REFERENCE.MODID)
public class EntityActions {
    // vampire actions
    public static final InvisibleVampireEntityAction vampire_invisible = UtilLib.getNull();
    public static final HealVampireEntityAction vampire_heal = UtilLib.getNull();
    // hunter actions

    public static void registerDefaultActions(IForgeRegistry<IEntityAction> registry) {
        // vampire actions
        registry.register(new InvisibleVampireEntityAction().setRegistryName("vampirism", "vampire_invisible"));
        registry.register(new HealVampireEntityAction().setRegistryName("vampirism", "vampire_heal"));
        // hunter actions
    }
}
