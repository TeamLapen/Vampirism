package de.teamlapen.vampirism.entity.action;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.action.hunter.GarlicAOFEntityAction;
import de.teamlapen.vampirism.entity.action.vampire.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

/**
 * Register and holds all actions for vampirism entities
 */
@SuppressWarnings("unused")
public class EntityActions {
    public static final DeferredRegister<IEntityAction> ENTITY_ACTIONS = DeferredRegister.create(ModRegistries.ENTITYACTIONS, REFERENCE.MODID);
    
    
    public static final RegistryObject<InvisibleEntityAction<?>> ENTITY_INVISIBLE = ENTITY_ACTIONS.register("entity_invisible", () -> new InvisibleEntityAction<>(EntityActionTier.Medium, EntityClassType.Assassin));
    /**
     * Healing action is preferred to the regeneration action
     */
    public static final RegistryObject<HealEntityAction<?>> ENTITY_HEAL = ENTITY_ACTIONS.register("entity_heal", () -> new HealEntityAction<>(EntityActionTier.High, EntityClassType.Fighter));
    public static final RegistryObject<RegenerationAOFEntityAction<?>> ENTITY_REGENERATION_AREAOFEFFECT = ENTITY_ACTIONS.register("entity_regeneration_areaofeffect", () -> new RegenerationAOFEntityAction<>(EntityActionTier.Medium, EntityClassType.Support));
    public static final RegistryObject<RegenerationEntityAction<?>> ENTITY_REGENERATION = ENTITY_ACTIONS.register("entity_regeneration", () -> new RegenerationEntityAction<>(EntityActionTier.Medium, EntityClassType.Fighter));
    public static final RegistryObject<SpeedEntityAction<?>> ENTITY_SPEED = ENTITY_ACTIONS.register("entity_speed", () -> new SpeedEntityAction<>(EntityActionTier.Medium, EntityClassType.Assassin, EntityClassType.Fighter));
    public static final RegistryObject<BatsSpawnEntityAction<?>> ENTITY_BAT_SPAWN = ENTITY_ACTIONS.register("entity_bat_spawn", () -> new BatsSpawnEntityAction<>(EntityActionTier.Medium, EntityClassType.Caster));
    public static final RegistryObject<DarkProjectileEntityAction<?>> ENTITY_DARK_PROJECTILE = ENTITY_ACTIONS.register("entity_dark_projectile", () -> new DarkProjectileEntityAction<>(EntityActionTier.High, EntityClassType.Caster));
    public static final RegistryObject<SunscreenEntityAction<?>> ENTITY_SUNSCREEN = ENTITY_ACTIONS.register("entity_sunscreen", () -> new SunscreenEntityAction<>(EntityActionTier.Medium, EntityClassType.Tank));
    public static final RegistryObject<GarlicAOFEntityAction<?>> ENTITY_GARLIC_AREAOFEFFECT = ENTITY_ACTIONS.register("entity_garlic_areaofeffect", () -> new GarlicAOFEntityAction<>(EntityActionTier.High, EntityClassType.Caster));
    public static final RegistryObject<IgnoreSunDamageEntityAction<?>> ENTITY_IGNORESUNDAMAGE = ENTITY_ACTIONS.register("entity_ignoresundamage", () -> new IgnoreSunDamageEntityAction<>(EntityActionTier.High, EntityClassType.Fighter));

    public static void registerDefaultActions(IEventBus bus) {
        ENTITY_ACTIONS.register(bus);
    }
}
