package de.teamlapen.vampirism.entity.player.hunter.actions;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class AwarenessHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    private final TargetingConditions vampirePredicate = TargetingConditions.forNonCombat().selector(VampirismAPI.factionRegistry().getPredicate(VReference.HUNTER_FACTION, true, true, false, false, VReference.VAMPIRE_FACTION));

    public AwarenessHunterAction() {
        super();
    }

    @Override
    public boolean canBeUsedBy(@NotNull IHunterPlayer player) {
        return !player.getActionHandler().isActionActive(HunterActions.DISGUISE_HUNTER.get());
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
    protected boolean activate(IHunterPlayer player, ActivationContext context) {
        return true;
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