package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.entity.TaskMasterEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class VampireTaskMasterEntity extends VampireBaseEntity implements TaskMasterEntity {

    public VampireTaskMasterEntity(EntityType<? extends VampireBaseEntity> type, World world) {
        super(type, world, false);
    }

    @Override
    protected ActionResultType func_230254_b_(PlayerEntity playerEntity, Hand hand) {
        if (this.world.isRemote) return ActionResultType.SUCCESS;
        this.processInteraction(playerEntity, Helper.isVampire(playerEntity), Task.Variant.REPEATABLE);
        return ActionResultType.SUCCESS;
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
