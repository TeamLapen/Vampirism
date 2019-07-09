package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class VampireAIBiteNearbyEntity<T extends EntityLiving & IVampireMob> extends EntityAIBase {
    private final T vampire;
    private IExtendedCreatureVampirism creature;
    private int timer;

    public VampireAIBiteNearbyEntity(T vampire) {
        this.vampire = vampire;
        this.setMutexBits(3);
    }

    @Override
    public void resetTask() {
        creature = null;

    }

    @Override
    public boolean shouldContinueExecuting() {
        return creature.getEntity().isAlive() && creature.getEntity().getBoundingBox().intersects(getBiteBoundingBox()) && this.timer > 0;
    }

    @Override
    public boolean shouldExecute() {
        if (vampire.wantsBlood()) {
            List<EntityCreature> list = vampire.getEntityWorld().getEntitiesWithinAABB(EntityCreature.class, getBiteBoundingBox(), EntitySelectors.NOT_SPECTATING.and((entity) -> entity != vampire && entity.isAlive()));
            if (list.size() > 1) {
                list.sort((o1, o2) -> (int) (vampire.getDistanceSq(o1) - vampire.getDistanceSq(o2)));
            }

            for (EntityCreature o : list) {
                if (!vampire.getEntitySenses().canSee(o) || o.hasCustomName()) {
                        continue;
                    }

                creature = ExtendedCreature.get(o);
                if (creature.canBeBitten(vampire) && !creature.hasPoisonousBlood()) {
                    return true;
                }

            }
        }
        creature = null;
        return false;
    }

    @Override
    public void startExecuting() {
        timer = 20 + vampire.getRNG().nextInt(20);
    }

    @Override
    public void tick() {
        EntityCreature e = creature.getEntity();
        vampire.getLookHelper().setLookPosition(e.posX, e.posY + (double) e.getEyeHeight(), e.posZ, 10.0F, (float) vampire.getVerticalFaceSpeed());


        timer--;
        if (timer == 1) {
            int amount = creature.onBite(vampire);
            vampire.playSound(ModSounds.player_bite, 1, 1);
            vampire.drinkBlood(amount, creature.getBloodSaturation());
        }
    }

    protected AxisAlignedBB getBiteBoundingBox() {
        return vampire.getBoundingBox().grow(0.5, 0.7, 0.5);
    }
}
