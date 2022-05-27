package de.teamlapen.vampirism.entity.action;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.action.hunter.GarlicAOFEntityAction;
import de.teamlapen.vampirism.entity.action.vampire.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Register and holds all actions for vampirism entities
 */
public class EntityActions {
    public static final DeferredRegister<IEntityAction> ENTITY_ACTIONS = DeferredRegister.create(ModRegistries.ENTITYACTIONS_ID, REFERENCE.MODID);

    public static final RegistryObject<InvisibleEntityAction<?>> entity_invisible = ENTITY_ACTIONS.register("entity_invisible", () -> new InvisibleEntityAction<>(EntityActionTier.Medium, EntityClassType.Assassin));
    /**
     * Healing action is preferred to the regeneration action
     */
    public static final RegistryObject<HealEntityAction<?>> entity_heal = ENTITY_ACTIONS.register("entity_heal", () -> new HealEntityAction<>(EntityActionTier.High, EntityClassType.Fighter));
    public static final RegistryObject<RegenerationAOFEntityAction<?>> entity_regeneration_areaofeffect = ENTITY_ACTIONS.register("entity_regeneration_areaofeffect", () -> new RegenerationAOFEntityAction<>(EntityActionTier.Medium, EntityClassType.Support));
    public static final RegistryObject<RegenerationEntityAction<?>> entity_regeneration = ENTITY_ACTIONS.register("entity_regeneration", () -> new RegenerationEntityAction<>(EntityActionTier.Medium, EntityClassType.Fighter));
    public static final RegistryObject<SpeedEntityAction<?>> entity_speed = ENTITY_ACTIONS.register("entity_speed", () -> new SpeedEntityAction<>(EntityActionTier.Medium, EntityClassType.Assassin, EntityClassType.Fighter));
    public static final RegistryObject<BatsSpawnEntityAction<?>> entity_bat_spawn = ENTITY_ACTIONS.register("entity_bat_spawn", () -> new BatsSpawnEntityAction<>(EntityActionTier.Medium, EntityClassType.Caster));
    public static final RegistryObject<DarkProjectileEntityAction<?>> entity_dark_projectile = ENTITY_ACTIONS.register("entity_dark_projectile", () -> new DarkProjectileEntityAction<>(EntityActionTier.High, EntityClassType.Caster));
    public static final RegistryObject<SunscreenEntityAction<?>> entity_sunscreen = ENTITY_ACTIONS.register("entity_sunscreen", () -> new SunscreenEntityAction<>(EntityActionTier.Medium, EntityClassType.Tank));
    public static final RegistryObject<GarlicAOFEntityAction<?>> entity_garlic_areaofeffect = ENTITY_ACTIONS.register("entity_garlic_areaofeffect", () -> new GarlicAOFEntityAction<>(EntityActionTier.High, EntityClassType.Caster));
    public static final RegistryObject<IgnoreSunDamageEntityAction<?>> entity_ignoresundamage = ENTITY_ACTIONS.register("entity_ignoresundamage", () -> new IgnoreSunDamageEntityAction<>(EntityActionTier.High, EntityClassType.Fighter));

    public static void registerDefaultActions(IEventBus bus) {
        ENTITY_ACTIONS.register(bus);
    }
}
