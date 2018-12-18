package de.teamlapen.vampirism.api.entity.actions;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.entity.EntityVampirism;

public interface IInstantAction<T extends EntityVampirism & IFactionEntity & IAdjustableLevel> extends IEntityAction {

    boolean activate(T entity);
}
