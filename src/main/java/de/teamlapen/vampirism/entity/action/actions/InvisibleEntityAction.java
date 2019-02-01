package de.teamlapen.vampirism.entity.action.actions;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.util.EnumParticleTypes;

public class InvisibleEntityAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends DefaultEntityAction implements ILastingAction<T> {

    public InvisibleEntityAction() {
        super();
    }
    
    @Override
    public int getCooldown(int level) {
        return Balance.ea.INVISIBLE_COOLDOWN * 20; // seconds into ticks
    }

    @Override
    public int getDuration(int level) {
        return Balance.ea.INVISIBLE_DURATION;
    }

    @Override
    public void deactivate(T entity) {
        entity.getRepresentingEntity().setInvisible(false);

    }

    @Override
    public void onUpdate(T entity, int duration) {
        if (!entity.getRepresentingEntity().isInvisible()) {
            entity.getRepresentingEntity().setInvisible(true);
        }
    }

    @Override
    public void activate(T entity) {
        VampLib.proxy.getParticleHandler().spawnParticles(entity.getEntityWorld(), ModParticles.GENERIC_PARTICLE, entity.posX, entity.posY, entity.posZ, 60, 1, entity.getRNG(), EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), 16, 0xF0F0F0);
    }

}
