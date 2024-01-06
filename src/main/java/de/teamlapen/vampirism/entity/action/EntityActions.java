package de.teamlapen.vampirism.entity.action;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.entity.hunter.action.GarlicAOFEntityAction;
import de.teamlapen.vampirism.entity.vampire.action.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Register and holds all actions for vampirism entities
 */
public class EntityActions {
    public static final DeferredRegister<IEntityAction> ENTITY_ACTIONS = DeferredRegister.create(VampirismRegistries.ENTITY_ACTIONS_ID, REFERENCE.MODID);

    public static final DeferredHolder<IEntityAction, InvisibleEntityAction<?>> ENTITY_INVISIBLE = ENTITY_ACTIONS.register("entity_invisible", () -> new InvisibleEntityAction<>(EntityActionTier.Medium, EntityClassType.Assassin));
    /**
     * Healing action is preferred to the regeneration action
     */
    public static final DeferredHolder<IEntityAction, HealEntityAction<?>> ENTITY_HEAL = ENTITY_ACTIONS.register("entity_heal", () -> new HealEntityAction<>(EntityActionTier.High, EntityClassType.Fighter));
    public static final DeferredHolder<IEntityAction, RegenerationAOFEntityAction<?>> ENTITY_REGENERATION_AREAOFEFFECT = ENTITY_ACTIONS.register("entity_regeneration_areaofeffect", () -> new RegenerationAOFEntityAction<>(EntityActionTier.Medium, EntityClassType.Support));
    public static final DeferredHolder<IEntityAction, RegenerationEntityAction<?>> ENTITY_REGENERATION = ENTITY_ACTIONS.register("entity_regeneration", () -> new RegenerationEntityAction<>(EntityActionTier.Medium, EntityClassType.Fighter));
    public static final DeferredHolder<IEntityAction, SpeedEntityAction<?>> ENTITY_SPEED = ENTITY_ACTIONS.register("entity_speed", () -> new SpeedEntityAction<>(EntityActionTier.Medium, EntityClassType.Assassin, EntityClassType.Fighter));
    public static final DeferredHolder<IEntityAction, BatsSpawnEntityAction<?>> ENTITY_BAT_SPAWN = ENTITY_ACTIONS.register("entity_bat_spawn", () -> new BatsSpawnEntityAction<>(EntityActionTier.Medium, EntityClassType.Caster));
    public static final DeferredHolder<IEntityAction, DarkProjectileEntityAction<?>> ENTITY_DARK_PROJECTILE = ENTITY_ACTIONS.register("entity_dark_projectile", () -> new DarkProjectileEntityAction<>(EntityActionTier.High, EntityClassType.Caster));
    public static final DeferredHolder<IEntityAction, SunscreenEntityAction<?>> ENTITY_SUNSCREEN = ENTITY_ACTIONS.register("entity_sunscreen", () -> new SunscreenEntityAction<>(EntityActionTier.Medium, EntityClassType.Tank));
    public static final DeferredHolder<IEntityAction, GarlicAOFEntityAction<?>> ENTITY_GARLIC_AREAOFEFFECT = ENTITY_ACTIONS.register("entity_garlic_areaofeffect", () -> new GarlicAOFEntityAction<>(EntityActionTier.High, EntityClassType.Caster));
    public static final DeferredHolder<IEntityAction, IgnoreSunDamageEntityAction<?>> ENTITY_IGNORESUNDAMAGE = ENTITY_ACTIONS.register("entity_ignoresundamage", () -> new IgnoreSunDamageEntityAction<>(EntityActionTier.High, EntityClassType.Fighter));

    public static void register(IEventBus bus) {
        ENTITY_ACTIONS.register(bus);
    }
}
