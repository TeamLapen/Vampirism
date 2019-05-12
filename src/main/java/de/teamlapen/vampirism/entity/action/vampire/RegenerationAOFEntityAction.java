package de.teamlapen.vampirism.entity.action.vampire;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionUser;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;
import java.util.Random;

public class RegenerationAOFEntityAction<T extends EntityCreature & IEntityActionUser> extends VampireEntityAction<T> implements ILastingAction<T> {

    public RegenerationAOFEntityAction(EntityActionTier tier, EntityClassType... param) {
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
        List<EntityLiving> entities = entity.getEntityWorld().getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(entity.posX - 4, entity.posY - 1, entity.posZ - 4, entity.posX + 4, entity.posY + 3, entity.posZ + 4));
        for (EntityLiving e : entities) {
            if (VampirismAPI.factionRegistry().getFaction(entity) == VampirismAPI.factionRegistry().getFaction(e)) {
                e.heal(entity.getMaxHealth() / 100 * Balance.ea.REGENERATION_AMOUNT / (getDuration(entity.getLevel()) * 20));
                if (duration % 20 == 0) {
                    VampLib.proxy.getParticleHandler().spawnParticles(entity.getEntityWorld(), new ResourceLocation("vampirism", "heal"), e.posX, e.posY + 1, e.posZ, 3, 0.01D, new Random(), e);
                }
            }
        }

    }

    @Override
    public void activate(T entity) {
    }

    @Override
    public int getWeight(EntityCreature entity) {
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
