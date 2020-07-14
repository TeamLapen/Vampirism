package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.inventory.container.TaskBoardContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;
import java.util.OptionalInt;

public interface TaskMasterEntity extends ForceLookEntityGoal.TaskOwner, ITaskMasterEntity {

    ITextComponent CONTAINER_NAME = new TranslationTextComponent("container.vampirism.taskmaster");
    ITextComponent NO_TASK = new TranslationTextComponent("text.vampirism.taskmaster.no_tasks");

    default boolean processInteraction(PlayerEntity playerEntity, Entity entity) {
        if (FactionPlayerHandler.getOpt(playerEntity).map(FactionPlayerHandler::getCurrentFactionPlayer).filter(Optional::isPresent).map(Optional::get).map(IFactionPlayer::getTaskManager).map(taskManager -> taskManager.hasAvailableTasks(entity.getUniqueID())).orElse(false)) {
            OptionalInt containerIdOpt = playerEntity.openContainer(new SimpleNamedContainerProvider((containerId, playerInventory, player) -> new TaskBoardContainer(containerId, playerInventory), CONTAINER_NAME.deepCopy()));
            if (containerIdOpt.isPresent()) {
                FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(iFactionPlayer -> {
                    iFactionPlayer.getTaskManager().openTaskMasterScreen(entity.getUniqueID());
                }));
                return true;
            }
        } else {
            playerEntity.sendStatusMessage(NO_TASK, true);
        }
        return false;
    }

}
