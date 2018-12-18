package de.teamlapen.vampirism.entity.action.actions;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.EntityVampirism;

public class InvisibleEntityAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends DefaultEntityAction implements ILastingAction<T> {
    
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
    public boolean onUpdate(T entity) {
        if (!entity.getRepresentingEntity().isInvisible()) {
            entity.getRepresentingEntity().setInvisible(true);
        }
        return false;
    }

}
