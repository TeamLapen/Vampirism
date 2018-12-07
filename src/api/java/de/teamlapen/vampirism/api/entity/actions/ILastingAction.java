package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;

public interface ILastingAction<T extends IFactionEntity> extends IEntityAction {

    int getDuration(int level);

    int getCooldown(int level);

    void onActivatedClient(T entity);

    void onDeactivated(T entity);

    void onReActivated(T entity);

    boolean onUpdate(T entity);
}
