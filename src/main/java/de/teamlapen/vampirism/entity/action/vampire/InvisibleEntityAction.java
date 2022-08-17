package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class InvisibleEntityAction<T extends PathfinderMob & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {

    public InvisibleEntityAction(@NotNull EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public void activate(@NotNull T entity) {
        ModParticles.spawnParticlesServer(entity.getCommandSenderWorld(), ParticleTypes.EXPLOSION, entity.getX(), entity.getY(), entity.getZ(), 60, 1, 1, 1, 0);

    }

    @Override
    public void deactivate(@NotNull T entity) {
        entity.getRepresentingEntity().setInvisible(false);

    }

    @Override
    public int getCooldown(int level) {
        return VampirismConfig.BALANCE.eaInvisibilityCooldown.get() * 20; // seconds into ticks
    }

    @Override
    public int getDuration(int level) {
        return VampirismConfig.BALANCE.eaInvisibilityDuration.get() * 20;
    }

    @Override
    public int getWeight(@NotNull PathfinderMob entity) {
        if (entity.getTarget() == null) return 0;
        double distanceToTarget = new Vec3(entity.getX(), entity.getY(), entity.getZ()).subtract(entity.getTarget().getX(), entity.getTarget().getY(), entity.getTarget().getZ()).length();
        if (distanceToTarget > 4) {
            return 3;
        } else {
            return 1;
        }
    }

    @Override
    public void onUpdate(@NotNull T entity, int duration) {
        if (!entity.getRepresentingEntity().isInvisible()) {
            entity.getRepresentingEntity().setInvisible(true);
        }
    }

    @Override
    public void updatePreAction(@NotNull T entity, int duration) {
        if (duration % 5 == 0) {
            ModParticles.spawnParticlesServer(entity.getCommandSenderWorld(), ParticleTypes.EXPLOSION, entity.getX(), entity.getY(), entity.getZ(), 10, 1, 1, 1, 0);
        }
    }
}
