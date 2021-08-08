package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.IInstantAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.core.particles.ParticleTypes;

public class HealEntityAction<T extends PathfinderMob & IEntityActionUser> extends VampireEntityAction<T> implements IInstantAction<T> {

    public HealEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public boolean activate(T entity) {
        entity.getRepresentingEntity().heal(entity.getMaxHealth() / 100 * VampirismConfig.BALANCE.eaHealAmount.get());
        ModParticles.spawnParticlesServer(entity.getCommandSenderWorld(), ParticleTypes.HEART, entity.getX(), entity.getY() + 1, entity.getZ(), 10, 0.3, 0.3, 0.3, 0);
        return true;
    }

    @Override
    public int getCooldown(int level) {
        return VampirismConfig.BALANCE.eaHealCooldown.get() * 20;
    }

    @Override
    public int getWeight(PathfinderMob entity) {
        double healthPercent = entity.getHealth() / entity.getMaxHealth();
        if (healthPercent < 0.1) {
            return 3;
        } else if (healthPercent < 0.4) {
            return 2;
        } else {
            return 1;
        }
    }
}
