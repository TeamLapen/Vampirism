package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class VampireAIBiteNearbyEntity extends EntityAIBase {
    private final IVampireMob vampire;
    private final EntityLivingBase vampireEntity;
    private IExtendedCreatureVampirism creature;
    private int timer;

    public VampireAIBiteNearbyEntity(IVampireMob vampire) {
        this.vampire = vampire;
        this.vampireEntity = vampire.getRepresentingEntity();
        this.setMutexBits(3);
    }

    @Override
    public void resetTask() {
        creature = null;

    }

    protected AxisAlignedBB getBiteBoundingBox() {
        return vampireEntity.getEntityBoundingBox().grow(0.5, 0.7, 0.5);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return creature.getEntity().isEntityAlive() && creature.getEntity().getEntityBoundingBox().intersects(getBiteBoundingBox()) && this.timer > 0;
    }

    @Override
    public boolean shouldExecute() {
        if (vampire.wantsBlood()) {
            List list = vampireEntity.getEntityWorld().getEntitiesWithinAABB(EntityCreature.class, getBiteBoundingBox());
            if (list.size() > 1) {

                try {
                    list.sort((o1, o2) -> vampireEntity.getDistanceSq((Entity) o1) > vampireEntity.getDistanceSq((Entity) o2) ? 1 : -1);
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
        timer = 20 + vampireEntity.getRNG().nextInt(20);
    }

    @Override
    public void updateTask() {
        EntityCreature e = creature.getEntity();
        if (vampireEntity instanceof EntityLiving) {
            ((EntityLiving) vampireEntity).getLookHelper().setLookPosition(e.posX, e.posY + (double) e.getEyeHeight(), e.posZ, 10.0F, (float) ((EntityLiving) vampireEntity).getVerticalFaceSpeed());

        }
        timer--;
        if (timer == 1) {
            int amount = creature.onBite(vampire);
            vampireEntity.playSound(ModSounds.player_bite, 1, 1);
            vampire.drinkBlood(amount, creature.getBloodSaturation());
        }
    }
}
