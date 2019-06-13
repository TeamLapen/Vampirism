package de.teamlapen.vampirism.entity.action;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.entity.action.hunter.GarlicAOFEntityAction;
import de.teamlapen.vampirism.entity.action.vampire.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Register and holds all actions for vampirism entities
 */
@ObjectHolder(REFERENCE.MODID)
public class EntityActions {
    public static final InvisibleEntityAction<?> entity_invisible = UtilLib.getNull();
    /**
     * Healing action is preferred to the regeneration action
     */
    public static final HealEntityAction<?> entity_heal = UtilLib.getNull();
    public static final RegenerationAOFEntityAction<?> entity_regeneration_areaofeffect = UtilLib.getNull();
    public static final RegenerationEntityAction<?> entity_regeneration = UtilLib.getNull();
    public static final SpeedEntityAction<?> entity_speed = UtilLib.getNull();
    public static final BatsSpawnEntityAction<?> entity_bat_spawn = UtilLib.getNull();
    public static final DarkProjectileEntityAction<?> entity_dark_projectile = UtilLib.getNull();
    public static final SunscreenEntityAction<?> entity_sunscreen = UtilLib.getNull();
    public static final GarlicAOFEntityAction<?> entity_garlic_areaofeffect = UtilLib.getNull();
    public static final IgnoreSunDamageEntityAction<?> entity_ignoresundamage = UtilLib.getNull();

    public static void registerDefaultActions(IForgeRegistry<IEntityAction> registry) {
        registry.register(new InvisibleEntityAction<>(EntityActionTier.Medium, EntityClassType.Assassin).setRegistryName("vampirism", "entity_invisible"));
        registry.register(new HealEntityAction<>(EntityActionTier.High, EntityClassType.Fighter).setRegistryName("vampirism", "entity_heal"));
        registry.register(new RegenerationAOFEntityAction<>(EntityActionTier.Medium, EntityClassType.Support).setRegistryName("vampirism", "entity_regeneration_areaofeffect"));
        registry.register(new RegenerationEntityAction<>(EntityActionTier.Medium, EntityClassType.Fighter).setRegistryName("vampirism", "entity_regeneration"));
        registry.register(new SpeedEntityAction<>(EntityActionTier.Medium, EntityClassType.Assassin, EntityClassType.Fighter).setRegistryName("vampirism", "entity_speed"));
        registry.register(new BatsSpawnEntityAction<>(EntityActionTier.Medium, EntityClassType.Caster).setRegistryName("vampirism", "entity_bat_spawn"));
        registry.register(new DarkProjectileEntityAction<>(EntityActionTier.High, EntityClassType.Caster).setRegistryName("vampirism", "entity_dark_projectile"));
        registry.register(new SunscreenEntityAction<>(EntityActionTier.Medium, EntityClassType.Tank).setRegistryName("vampirism", "entity_sunscreen"));
        registry.register(new GarlicAOFEntityAction<>(EntityActionTier.High, EntityClassType.Caster).setRegistryName("vampirism", "entity_garlic_areaofeffect"));
        registry.register(new IgnoreSunDamageEntityAction<>(EntityActionTier.High, EntityClassType.Fighter).setRegistryName("vampirism", "entity_ignoresundamage"));
    }
}
