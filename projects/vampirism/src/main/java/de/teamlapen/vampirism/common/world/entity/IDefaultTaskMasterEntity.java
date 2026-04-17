package de.teamlapen.vampirism.common.world.entity;

import de.teamlapen.faction.api.world.entities.ITaskMasterEntity;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.world.entities.ForceLookEntityGoal;
import de.teamlapen.faction.common.world.inventory.TaskBoardMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.OptionalInt;

public interface IDefaultTaskMasterEntity extends ForceLookEntityGoal.TaskOwner, ITaskMasterEntity {

    Component NO_TASK = Component.translatable("message.factionapi.taskmaster.no_tasks");

    /**
     * @return The biome type based on where this entity was spawned
     */
    VillagerData getVillageData();

    default boolean processInteraction(@NotNull Player playerEntity, @NotNull Entity entity) {
        if (FactionPlayerHandler.get(playerEntity).getTaskManager().map(taskManager -> taskManager.hasAvailableTasks(entity.getUUID())).orElse(false)) {
            OptionalInt containerIdOpt = playerEntity.openMenu(new SimpleMenuProvider((containerId, playerInventory, player) -> new TaskBoardMenu(containerId, playerInventory), entity.getDisplayName().plainCopy()));
            if (containerIdOpt.isPresent()) {
                FactionPlayerHandler.get(playerEntity).getTaskManager().ifPresent(taskManager -> {
                    taskManager.openTaskBoardScreen(entity.getUUID());
                });
                return true;
            }
        } else {
            playerEntity.sendOverlayMessage(NO_TASK);
        }
        return false;
    }

}
