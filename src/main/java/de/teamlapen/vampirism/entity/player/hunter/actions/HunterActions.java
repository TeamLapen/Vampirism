package de.teamlapen.vampirism.entity.player.hunter.actions;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.entity.player.lord.actions.AttackSpeedLordAction;
import de.teamlapen.vampirism.entity.player.lord.actions.SpeedLordAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers and holds all skills for hunter players
 */
public class HunterActions {
    public static final DeferredRegister<IAction<?>> ACTIONS = DeferredRegister.create(VampirismRegistries.ACTIONS_ID, REFERENCE.MODID);

    public static final RegistryObject<AwarenessHunterAction> AWARENESS_HUNTER = ACTIONS.register("awareness_hunter", AwarenessHunterAction::new);
    public static final RegistryObject<DisguiseHunterAction> DISGUISE_HUNTER = ACTIONS.register("disguise_hunter", DisguiseHunterAction::new);
    public static final RegistryObject<PotionResistanceHunterAction> POTION_RESISTANCE_HUNTER = ACTIONS.register("potion_resistance_hunter", PotionResistanceHunterAction::new);
    public static final RegistryObject<SpeedLordAction<IHunterPlayer>> HUNTER_LORD_SPEED = ACTIONS.register("hunter_lord_speed", () -> new SpeedLordAction<>(VReference.HUNTER_FACTION));
    public static final RegistryObject<AttackSpeedLordAction<IHunterPlayer>> HUNTER_LORD_ATTACK_SPEED = ACTIONS.register("hunter_lord_attack_speed", () -> new AttackSpeedLordAction<>(VReference.HUNTER_FACTION));

    public static void register(IEventBus bus) {
        ACTIONS.register(bus);
    }
}