package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.entity.TaskMasterEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class VampireTaskMasterEntity extends VampireBaseEntity implements TaskMasterEntity {

    public VampireTaskMasterEntity(EntityType<? extends VampireBaseEntity> type, World world) {
        super(type, world, false);
    }

    @Override
    protected boolean processInteract(PlayerEntity playerEntity, Hand hand) {
        if (this.world.isRemote) return true;
        this.processInteraction(playerEntity, Helper.isVampire(playerEntity), Task.Variant.REPEATABLE);
        return true;
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

}
