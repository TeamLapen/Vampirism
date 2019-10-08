package de.teamlapen.vampirism.player.hunter.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class AwarenessHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    public AwarenessHunterAction() {
        super();
    }

    @Override
    public boolean canBeUsedBy(IHunterPlayer player) {
        return !player.getActionHandler().isActionActive(HunterActions.disguise_hunter);
    }

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.haAwarenessCooldown.get();
    }

    @Override
    public int getDuration(int level) {
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
        ((HunterPlayer) player).getSpecialAttributes().resetVampireNearby();
    }

    @Override
    public void onReActivated(IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    public boolean onUpdate(IHunterPlayer player) {
        if (!(player.getRepresentingEntity().ticksExisted % 20 == 0)) {
            if (((HunterPlayer) player).getSpecialAttributes().isVampireNearby())
                ((HunterPlayer) player).getSpecialAttributes().nearbyVampire();
        } else if (nearbyVampire(player)) {
            ((HunterPlayer) player).getSpecialAttributes().nearbyVampire();
        } else {
            ((HunterPlayer) player).getSpecialAttributes().resetVampireNearby();
        }
        return false;
    }

    @Override
    protected boolean activate(IHunterPlayer player) {
        onUpdate(player);
        return true;
    }

    private boolean nearbyVampire(IHunterPlayer player) {
        int r = VampirismConfig.BALANCE.haAwarenessRadius.get();
        List<LivingEntity> entities = player.getRepresentingEntity().getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(player.getRepresentingEntity().posX - r, player.getRepresentingEntity().posY
                - r + 1, player.getRepresentingEntity().posZ
                - r, player.getRepresentingEntity().posX + r, player.getRepresentingEntity().posY + r + 1, player.getRepresentingEntity().posZ + r));
        for (LivingEntity e : entities) {
            if (Helper.isVampire(e))
                return true;
        }
        return false;
    }

}