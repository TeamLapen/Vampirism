package de.teamlapen.vampirism.entity.action.actions;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.actions.DefaultEntityAction;
import de.teamlapen.vampirism.api.entity.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBase;

public class IgnoreSunDamageEntityAction<T extends EntityVampireBase & IVampire & IAdjustableLevel> extends DefaultEntityAction implements ILastingAction<T> {

    @Override
    public int getCooldown(int level) {
        return Balance.ea.IGNORE_SUNDAMAGE_COOLDOWN;
    }

    @Override
    public int getDuration(int level) {
        return Balance.ea.IGNORE_SUNDAMAGE_DURATION;
    }

    @Override
    public void deactivate(T entity) {
        entity.setIgnoreSundamage(false);
    }

    @Override
    public void onUpdate(T entity, int duration) {
    }

    @Override
    public void activate(T entity) {
        entity.setIgnoreSundamage(true);

    }

    @Override
    public void updatePreAction(T entity, int duration) {
    }

}
