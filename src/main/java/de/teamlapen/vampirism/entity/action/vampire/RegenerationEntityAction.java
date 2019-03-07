package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.ResourceLocation;
import java.util.Random;

public class RegenerationEntityAction<T extends EntityCreature & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {

    public RegenerationEntityAction(EntityActionTier tier, EntityClassType... param) {
        super(tier, param);
    }

    @Override
    public int getDuration(int level) {
        return Balance.ea.REGENERATION_DURATION * 20;
    }

    @Override
    public int getCooldown(int level) {
        return Balance.ea.REGENERATION_COOLDOWN * 20;
    }

    @Override
    public void deactivate(T entity) {
    }

    @Override
    public void onUpdate(T entity, int duration) {
        entity.getRepresentingEntity().heal(entity.getMaxHealth() / 100 * Balance.ea.REGENERATION_AMOUNT / (getDuration(entity.getLevel()) * 20)); // seconds in ticks
        if (duration % 20 == 0) {
            VampLib.proxy.getParticleHandler().spawnParticles(entity.getEntityWorld(), new ResourceLocation("vampirism", "heal"), entity.posX, entity.posY + 1, entity.posZ, 3, 0.01D, new Random(), entity);
        }

    }

    @Override
    public void activate(T entity) {
    }

    @Override
    public void updatePreAction(T entity, int duration) {
    }

    @Override
    public int getWeight(T entity) {
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