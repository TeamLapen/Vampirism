package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.TaskMasterContainer;
import de.teamlapen.vampirism.network.TaskStatusPacket;
import de.teamlapen.vampirism.player.TaskManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.OptionalInt;

public class TaskMasterEntity extends VampirismEntity {

    public TaskMasterEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected boolean processInteract(PlayerEntity playerEntity, Hand hand) {
        if (this.world.isRemote) return true;
        if (FactionPlayerHandler.getOpt(playerEntity).map(player -> player.getCurrentFaction() != null).orElse(false)) {
            OptionalInt containerIdOpt = playerEntity.openContainer(new SimpleNamedContainerProvider((containerId, playerInventory, player) -> new TaskMasterContainer(containerId, playerInventory), new TranslationTextComponent("container.taskmaster")));
            if (containerIdOpt.isPresent()) {
                VampirismMod.dispatcher.sendTo(new TaskStatusPacket(TaskManager.getTaskForPlayer(playerEntity), containerIdOpt.getAsInt()), (ServerPlayerEntity) playerEntity);
            }
        }
        return true;
    }
}
