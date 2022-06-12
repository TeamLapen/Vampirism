package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.vampire.IVampireMob;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class BiteNearbyEntityVampireGoal<T extends Mob & IVampireMob> extends Goal {
    private final T vampire;
    private IExtendedCreatureVampirism creature;
    private int timer;

    public BiteNearbyEntityVampireGoal(T vampire) {
        this.vampire = vampire;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canContinueToUse() {
        return this.timer > 0 && creature.getEntity().isAlive() && creature.getEntity().getBoundingBox().intersects(getBiteBoundingBox());
    }

    @Override
    public boolean canUse() {
        if (vampire.wantsBlood()) {
            List<PathfinderMob> list = vampire.getCommandSenderWorld().getEntitiesOfClass(PathfinderMob.class, getBiteBoundingBox(), EntitySelector.NO_SPECTATORS.and((entity) -> entity != vampire && entity.isAlive()));
            if (list.size() > 1) {
                try {
                    list.sort((o1, o2) -> (int) (vampire.distanceToSqr(o1) - vampire.distanceToSqr(o2)));
                } catch (IllegalArgumentException e) {
                    //Might be caused by two entities with the same id (Entity#equals -> true) being included in the list for some reason, which habe different positions (compare -> !=0). This violates the comparator contract.
                    //Alternatively, some mod has a strange equals implementation
                    //However, as this is not a regular scenario, let's just ignore it and skip sorting the list.
                    //java.lang.IllegalArgumentException: Comparison method violates its general contract
                }
            }

            for (PathfinderMob o : list) {
                if (!vampire.getSensing().hasLineOfSight(o) || o.hasCustomName()) {
                    continue;
                }
                if (ExtendedCreature.getSafe(o).filter(this::canFeed).map(creature -> {
                    this.creature = creature;
                    return true;
                }).orElse(false)) {
                    return true;
                }
            }
        }
        creature = null;
        return false;
    }

    @Override
    public void start() {
        timer = 20 + vampire.getRandom().nextInt(20);
    }

    @Override
    public void stop() {
        creature = null;

    }

    @Override
    public void tick() {
        PathfinderMob e = creature.getEntity();
        vampire.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(e.getX(), e.getY() + (double) e.getEyeHeight(), e.getZ()));


        timer--;
        if (timer == 1) {
            if (canFeed(creature)) {
                int amount = creature.onBite(vampire);
                vampire.playSound(ModSounds.PLAYER_BITE.get(), 1, 1);
                vampire.drinkBlood(amount, creature.getBloodSaturation());
            }
        }
    }

    protected boolean canFeed(IExtendedCreatureVampirism entity) {
        return entity.canBeBitten(vampire) && !entity.hasPoisonousBlood() && (!(entity.getEntity() instanceof Villager) || entity.getBlood() > (entity.getMaxBlood() / 2f));
    }

    protected AABB getBiteBoundingBox() {
        return vampire.getBoundingBox().inflate(0.5, 0.7, 0.5);
    }
}
