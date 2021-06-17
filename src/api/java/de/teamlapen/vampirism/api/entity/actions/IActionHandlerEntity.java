package de.teamlapen.vampirism.api.entity.actions;

public interface IActionHandlerEntity {

    void deactivateAction();

    boolean isActionActive(IEntityAction action);
}
