package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;

public class RegenerationEntityAction<T extends PathfinderMob & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {

    public RegenerationEntityAction(@NotNull EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public void activate(T entity) {
    }

    @Override
    public void deactivate(T entity) {
    }

    @Override
    public int getCooldown(int level) {
        return VampirismConfig.BALANCE.eaRegenerationCooldown.get() * 20;
    }

    @Override
    public int getDuration(int level) {
        return VampirismConfig.BALANCE.eaRegenerationDuration.get() * 20;
    }

    @Override
    public int getWeight(@NotNull PathfinderMob entity) {
        double healthPercent = entity.getHealth() / entity.getMaxHealth();
        if (healthPercent < 0.1) {
            return 3;
        } else if (healthPercent < 0.4) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public void onUpdate(@NotNull T entity, int duration) {
        entity.getRepresentingEntity().heal(entity.getMaxHealth() / 100f * VampirismConfig.BALANCE.eaRegenerationAmount.get() / (getDuration(entity.getEntityLevel()) * 20f)); // seconds in ticks
        if (duration % 15 == 0) {
            ModParticles.spawnParticlesServer(entity.getCommandSenderWorld(), ParticleTypes.HEART, entity.getX(), entity.getY() + 1, entity.getZ(), 3, 0.2, 0.2, 0.2, 0);
        }

    }
}