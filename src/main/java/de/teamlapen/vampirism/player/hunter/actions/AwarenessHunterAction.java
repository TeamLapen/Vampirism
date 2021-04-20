package de.teamlapen.vampirism.player.hunter.actions;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.hunter.DefaultHunterAction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.player.hunter.HunterPlayer;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class AwarenessHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    private final EntityPredicate vampirePredicate = new EntityPredicate().setCustomPredicate(VampirismAPI.factionRegistry().getPredicate(VReference.HUNTER_FACTION, true,true,false,false,VReference.VAMPIRE_FACTION));

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
        ((HunterPlayer) player).getSpecialAttributes().nearbyVampire(0);
    }

    @Override
    public void onReActivated(IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    public boolean onUpdate(IHunterPlayer player) {
        if(player.getRepresentingEntity().getEntityWorld().isRemote()&&player.getRepresentingEntity().ticksExisted % 8 == 0){
            double dist = nearbyVampire(player);
            double p=0;
            if(dist!=Double.MAX_VALUE){
                p=1f-(dist/(float)VampirismConfig.BALANCE.haAwarenessRadius.get());
            }
            ((HunterPlayer)player).getSpecialAttributes().nearbyVampire(p);
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
        LivingEntity closestVampire = player.getRepresentingEntity().getEntityWorld().func_225318_b(LivingEntity.class,vampirePredicate,null,player.getRepresentingEntity().getPosX(),player.getRepresentingEntity().getPosY(),player.getRepresentingEntity().getPosZ(), new AxisAlignedBB(player.getRepresentingEntity().getPosX() - r, player.getRepresentingEntity().getPosY()
                - r + 1, player.getRepresentingEntity().getPosZ()
                - r, player.getRepresentingEntity().getPosX() + r, player.getRepresentingEntity().getPosY() + r + 1, player.getRepresentingEntity().getPosZ() + r));
        if(closestVampire!=null)return closestVampire.getDistance(player.getRepresentingEntity());
        return Double.MAX_VALUE;
    }

}