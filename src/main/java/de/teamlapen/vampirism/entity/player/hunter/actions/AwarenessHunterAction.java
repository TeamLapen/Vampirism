package de.teamlapen.vampirism.entity.player.hunter.actions;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.actions.IActionResult;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModFactions;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class AwarenessHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    private final TargetingConditions vampirePredicate = TargetingConditions.forNonCombat().selector(VampirismAPI.factionRegistry().getPredicate(ModFactions.HUNTER, true, true, false, false, ModFactions.VAMPIRE));

    public AwarenessHunterAction() {
        super();
    }

    @Override
    public @NotNull IActionResult canBeUsedBy(@NotNull IHunterPlayer player) {
        return IActionResult.otherAction(player.getActionHandler(), HunterActions.DISGUISE_HUNTER);
    }

    @Override
    public int getCooldown(IHunterPlayer player) {
        return VampirismConfig.BALANCE.haAwarenessCooldown.get();
    }

    @Override
    public int getDuration(IHunterPlayer player) {
        return VampirismConfig.BALANCE.haAwarenessDuration.get();
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.haAwarenessEnabled.get();
    }

    @Override
    public void onActivatedClient(@NotNull IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    public void onDeactivated(@NotNull IHunterPlayer player) {
    }

    @Override
    public void onReActivated(@NotNull IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    public boolean onUpdate(@NotNull IHunterPlayer player) {
        return false;
    }

    @Override
    protected IActionResult activate(IHunterPlayer player, ActivationContext context) {
        return IActionResult.SUCCESS;
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }

    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }

}