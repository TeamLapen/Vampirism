package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;

public interface ILastingAction<T extends IFactionEntity> extends IEntityAction {

    int getDuration(int level);

    int getCooldown(int level);

    void deactivate(T entity);

    boolean onUpdate(T entity);
}
