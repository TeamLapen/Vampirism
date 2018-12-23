package de.teamlapen.vampirism.entity.action;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.entity.action.actions.HealEntityAction;
import de.teamlapen.vampirism.entity.action.actions.InvisibleEntityAction;
import de.teamlapen.vampirism.entity.action.actions.RegenerationEntityAction;
import de.teamlapen.vampirism.entity.action.actions.SpeedEntityAction;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Register and holds all actions for vampire entity
 */
@GameRegistry.ObjectHolder(REFERENCE.MODID)
public class EntityActions {
    public static final InvisibleEntityAction entity_invisible = UtilLib.getNull();
    public static final HealEntityAction entity_heal = UtilLib.getNull();
    public static final RegenerationEntityAction entity_regeneration = UtilLib.getNull();
    public static final SpeedEntityAction entity_speed = UtilLib.getNull();

    public static void registerDefaultActions(IForgeRegistry<IEntityAction> registry) {
        registry.register(new InvisibleEntityAction<>().setRegistryName("vampirism", "entity_invisible"));
        registry.register(new HealEntityAction<>().setRegistryName("vampirism", "entity_heal"));
        registry.register(new RegenerationEntityAction<>().setRegistryName("vampirism", "entity_regeneration"));
        registry.register(new SpeedEntityAction<>().setRegistryName("vampirism", "entity_speed"));
    }
}
