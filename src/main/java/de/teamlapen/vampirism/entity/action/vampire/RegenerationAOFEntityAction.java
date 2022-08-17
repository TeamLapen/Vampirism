package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RegenerationAOFEntityAction<T extends PathfinderMob & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {

    public RegenerationAOFEntityAction(@NotNull EntityActionTier tier, EntityClassType... param) {
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
        List<Mob> entities = entity.getCommandSenderWorld().getEntitiesOfClass(Mob.class, new AABB(entity.getX() - 4, entity.getY() - 1, entity.getZ() - 4, entity.getX() + 4, entity.getY() + 3, entity.getZ() + 4));
        for (Mob e : entities) {
            if (VampirismAPI.factionRegistry().getFaction(entity) == VampirismAPI.factionRegistry().getFaction(e)) {
                e.heal(entity.getMaxHealth() / 100f * VampirismConfig.BALANCE.eaRegenerationAmount.get() / (getDuration(entity.getEntityLevel()) * 20f));
                if (duration % 20 == 0) {
                    ModParticles.spawnParticlesServer(entity.getCommandSenderWorld(), ParticleTypes.HEART, e.getX(), e.getY() + 0.2, e.getZ(), 3, 0.2, 0.2, 0.2, 0);
                }
            }
        }

    }
}
