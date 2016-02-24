package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBase;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VampireAIBiteNearbyEntity extends EntityAIBase {
    private final EntityVampireBase vampire;
    private ExtendedCreature creature;
    private int timer;

    public VampireAIBiteNearbyEntity(EntityVampireBase vampire) {
        this.vampire = vampire;
        this.setMutexBits(3);
    }

    @Override
    public boolean continueExecuting() {
        return creature.getEntity().isEntityAlive() && vampire.getDistanceSqToEntity(creature.getEntity()) < 7 && this.timer > 0;
    }

    @Override
    public void resetTask() {
        creature = null;

    }

    @Override
    public boolean shouldExecute() {
        if (vampire.wantsBlood()) {
            List list = vampire.worldObj.getEntitiesWithinAABB(EntityCreature.class, vampire.getEntityBoundingBox().expand(2.5, 1.5, 2.5));
            if (list.size() > 1) {

                try {
                    Collections.sort(list, new Comparator() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            return vampire.getDistanceSqToEntity((Entity) o1) > vampire.getDistanceSqToEntity((Entity) o2) ? 1 : -1;
                        }
                    });
                } catch (Exception e) {
                    //TODO investigate issue
                    //java.lang.IllegalArgumentException: Comparison method violates its general contract!
                    //at java.util.TimSort.mergeHi(TimSort.java:895)
                    //http://openeye.openmods.info/crashes/796f6b43ea81b10156658bd4f662e0c1
                }

            }

            for (Object o : list) {
                creature = ExtendedCreature.get((EntityCreature) o);
                if (creature.canBeBitten(vampire)) {
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
    public void updateTask() {
        EntityCreature e = creature.getEntity();
        this.vampire.getLookHelper().setLookPosition(e.posX, e.posY + (double) e.getEyeHeight(), e.posZ, 10.0F, (float) vampire.getVerticalFaceSpeed());
        timer--;
        if (timer == 1) {
            int amount = creature.onBite(vampire);
            vampire.worldObj.playSoundAtEntity(vampire, REFERENCE.MODID + ":player.bite", 1.0F, 1.0F);
            vampire.consumeBlood(amount, creature.getBloodSaturation());
        }
    }
}
