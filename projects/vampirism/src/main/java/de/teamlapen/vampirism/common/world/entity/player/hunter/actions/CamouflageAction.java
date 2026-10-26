package de.teamlapen.vampirism.common.world.entity.player.hunter.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Allows a hunter player to camouflage themselves which makes them less visible and reduces the detection radius for mobs
 */
public class CamouflageAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {
    public static final int FADE_TICKS = 20;

    public CamouflageAction() {
        super();
    }

    @Override
    public IActionResult activateServer(IHunterPlayer player, ActivationContext context) {
        ((HunterPlayer) player).getSpecialAttributes().activateCamouflage();
        return IActionResult.SUCCESS;
    }

    @Override
    public IActionResult canBeUsedBy(IHunterPlayer player) {
        return IActionResult.otherAction(player.getActionHandler(), HunterActions.SIXTH_SENSE);
    }

    @Override
    public int getCooldown(IHunterPlayer player) {
        return 0;
    }

    @Override
    public int getDuration(IHunterPlayer player) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.balance().haCamouflageEnabled.get();
    }

    @Override
    public void onActivatedClient(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().activateCamouflage();

    }

    @Override
    public void onDeactivated(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().resetCamouflage();

    }

    @Override
    public void onReActivatedServer(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().activateCamouflage();

    }

    @Override
    public boolean onUpdate(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().increaseCamouflageTicks();
        return false;
    }

}
