package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;

/**
 * Interface for all non player faction entities
 */
public interface IFactionMob<T extends IFactionMob> extends IFactionEntity, IVampirismEntity {
}
