package de.teamlapen.vampirism.player.hunter.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Allows a hunter player to disguise himself which makes him less visible and reduces the detection radius for mobs
 */
public class DisguiseHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {
    public static final int FADE_TICKS = 20;

    public DisguiseHunterAction() {
        super();
    }

    @Override
    public boolean activate(@NotNull IHunterPlayer player, ActivationContext context) {
        ((HunterPlayer) player).getSpecialAttributes().activateDisguise();
        return true;
    }

    @Override
    public boolean canBeUsedBy(@NotNull IHunterPlayer player) {
        return !player.getActionHandler().isActionActive(HunterActions.AWARENESS_HUNTER.get());
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
        return VampirismConfig.BALANCE.haDisguiseEnabled.get();
    }

    @Override
    public void onActivatedClient(@NotNull IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().activateDisguise();

    }

    @Override
    public void onDeactivated(@NotNull IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().resetDisguise();

    }

    @Override
    public void onReActivated(@NotNull IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().activateDisguise();

    }

    @Override
    public boolean onUpdate(@NotNull IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().increaseDisguiseTicks();
        return false;
    }

    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }
}
