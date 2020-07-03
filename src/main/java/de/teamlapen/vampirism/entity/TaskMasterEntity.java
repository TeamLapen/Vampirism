package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.inventory.container.TaskMasterContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;
import java.util.OptionalInt;

public interface TaskMasterEntity extends ForceLookEntityGoal.TaskOwner, ITaskMasterEntity {

    ITextComponent CONTAINER_NAME = new TranslationTextComponent("container.vampirism.taskmaster");
    ITextComponent NO_TASK = new TranslationTextComponent("text.vampirism.taskmaster.no_tasks");
    ITextComponent NO_TASK_UNIQUE = new TranslationTextComponent("text.vampirism.taskmaster.no_tasks_unique");

    default boolean processInteraction(PlayerEntity playerEntity, Task.Variant variant) {
        if (FactionPlayerHandler.getOpt(playerEntity).map(FactionPlayerHandler::getCurrentFactionPlayer).filter(Optional::isPresent).map(Optional::get).map(IFactionPlayer::getTaskManager).map(iTaskManager -> iTaskManager.hasAvailableTasks(variant)).orElse(false)) {
            OptionalInt containerIdOpt = playerEntity.openContainer(new SimpleNamedContainerProvider((containerId, playerInventory, player) -> new TaskMasterContainer(containerId, playerInventory, variant), CONTAINER_NAME.deepCopy()));
            if (containerIdOpt.isPresent()) {
                FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(iFactionPlayer -> iFactionPlayer.getTaskManager().updateClient()));
                return true;
            }
        } else {
            playerEntity.sendStatusMessage(variant == Task.Variant.UNIQUE?NO_TASK_UNIQUE:NO_TASK, true);
        }
        return false;
    }
    
}
