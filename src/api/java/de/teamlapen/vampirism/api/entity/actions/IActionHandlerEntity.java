package de.teamlapen.vampirism.api.entity.actions;

public interface IActionHandlerEntity {

    boolean isActionActive(IEntityAction action);

    void deactivateAction();
}
