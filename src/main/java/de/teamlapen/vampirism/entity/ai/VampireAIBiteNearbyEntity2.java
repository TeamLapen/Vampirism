package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.EntityVampireBase;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Max on 16.08.2015.
 */
public class VampireAIBiteNearbyEntity2 extends EntityAIBase {

    protected final EntityVampireBase vampire;
    private VampireMob mob;
    private int timer;

    public VampireAIBiteNearbyEntity2(EntityVampireBase vampire) {
        this.vampire = vampire;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (vampire.wantsBlood()) {
            List list = vampire.worldObj.getEntitiesWithinAABB(EntityCreature.class, vampire.boundingBox.expand(2.5, 1.5, 2.5));
            if (list.size() > 1) {
                Collections.sort(list, new Comparator() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        return vampire.getDistanceSqToEntity((Entity) o1) > vampire.getDistanceSqToEntity((Entity) o2) ? 1 : -1;
                    }
                });

            }

            for (Object o : list) {
                mob = VampireMob.get((EntityCreature) o);
                if (mob.getBlood() > 0)
                    return true;
            }
        }
        mob = null;
        return false;
    }

    @Override
    public void startExecuting() {
        timer = 20 + vampire.getRNG().nextInt(20);
    }

    @Override
    public boolean continueExecuting() {
        return mob.getRepresentingEntity().isEntityAlive() && vampire.getDistanceSqToEntity(mob.getRepresentingEntity()) < 7 && this.timer > 0;
    }

    @Override
    public void updateTask() {
        EntityCreature e = mob.getRepresentingEntity();
        this.vampire.getLookHelper().setLookPosition(e.posX, e.posY + (double) e.getEyeHeight(), e.posZ, 10.0F, (float) vampire.getVerticalFaceSpeed());
        timer--;
        if (timer == 1) {
            int amount = mob.bite(true);
            vampire.worldObj.playSoundAtEntity(vampire, REFERENCE.MODID + ":player.bite", 1.0F, 1.0F);
            vampire.addBlood(amount);
        }
    }

    @Override
    public void resetTask() {
        mob = null;

    }
}
