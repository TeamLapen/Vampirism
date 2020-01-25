package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

public class BiteNearbyEntityVampireGoal<T extends MobEntity & IVampireMob> extends Goal {
    private final T vampire;
    private IExtendedCreatureVampirism creature;
    private int timer;

    public BiteNearbyEntityVampireGoal(T vampire) {
        this.vampire = vampire;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
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
            List<CreatureEntity> list = vampire.getEntityWorld().getEntitiesWithinAABB(CreatureEntity.class, getBiteBoundingBox(), EntityPredicates.NOT_SPECTATING.and((entity) -> entity != vampire && entity.isAlive()));
            if (list.size() > 1) {
                try {
                    list.sort((o1, o2) -> (int) (vampire.getDistanceSq(o1) - vampire.getDistanceSq(o2)));
                } catch (IllegalArgumentException e) {
                    //Might be caused by two entities with the same id (Entity#equals -> true) being included in the list for some reason, which habe different positions (compare -> !=0). This violates the comparator contract.
                    //Alternatively, some mod has a strange equals implementation
                    //However, as this is not a regular scenario, let's just ignore it and skip sorting the list.
                    //java.lang.IllegalArgumentException: Comparison method violates its general contract
                }
            }

            for (CreatureEntity o : list) {
                if (!vampire.getEntitySenses().canSee(o) || o.hasCustomName()) {
                    continue;
                }

                creature = ExtendedCreature.getUnsafe(o);
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
        CreatureEntity e = creature.getEntity();
        vampire.lookAt(EntityAnchorArgument.Type.EYES, new Vec3d(e.getPosX(), e.getPosY() + (double) e.getEyeHeight(), e.getPosZ()));


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
