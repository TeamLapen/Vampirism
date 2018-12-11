package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;

public interface IInstantAction<T extends IFactionEntity> extends IEntityAction {

    int getCooldown(int level);

    boolean activate(T entity);
}
