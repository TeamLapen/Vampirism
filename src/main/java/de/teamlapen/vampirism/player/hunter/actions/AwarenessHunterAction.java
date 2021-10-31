package de.teamlapen.vampirism.player.hunter.actions;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class AwarenessHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    private final TargetingConditions vampirePredicate = TargetingConditions.forNonCombat().selector(VampirismAPI.factionRegistry().getPredicate(VReference.HUNTER_FACTION, true, true, false, false, VReference.VAMPIRE_FACTION));

    public AwarenessHunterAction() {
        super();
    }

    @Override
    public boolean canBeUsedBy(IHunterPlayer player) {
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
    public void onActivatedClient(IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    public void onDeactivated(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().nearbyVampire(0);
    }

    @Override
    public void onReActivated(IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    public boolean onUpdate(IHunterPlayer player) {
        if (player.getRepresentingEntity().getCommandSenderWorld().isClientSide() && player.getRepresentingEntity().tickCount % 8 == 0) {
            double dist = nearbyVampire(player);
            double p = 0;
            if (dist != Double.MAX_VALUE) {
                p = 1f - (dist / (float) VampirismConfig.BALANCE.haAwarenessRadius.get());
            }
            ((HunterPlayer) player).getSpecialAttributes().nearbyVampire(p);
        }
        return false;
    }

    @Override
    protected boolean activate(IHunterPlayer player) {
        onUpdate(player);
        return true;
    }

    private double nearbyVampire(IHunterPlayer player) {
        int r = VampirismConfig.BALANCE.haAwarenessRadius.get();
        LivingEntity closestVampire = player.getRepresentingEntity().getCommandSenderWorld().getNearestEntity(LivingEntity.class, vampirePredicate, null, player.getRepresentingEntity().getX(), player.getRepresentingEntity().getY(), player.getRepresentingEntity().getZ(), new AABB(player.getRepresentingEntity().getX() - r, player.getRepresentingEntity().getY()
                - r + 1, player.getRepresentingEntity().getZ()
                - r, player.getRepresentingEntity().getX() + r, player.getRepresentingEntity().getY() + r + 1, player.getRepresentingEntity().getZ() + r));
        if (closestVampire != null) return closestVampire.distanceTo(player.getRepresentingEntity());
        return Double.MAX_VALUE;
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