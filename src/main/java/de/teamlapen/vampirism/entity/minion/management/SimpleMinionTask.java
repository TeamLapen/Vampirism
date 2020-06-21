package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.minion.DefaultMinionTask;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionInventory;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;


public class SimpleMinionTask extends DefaultMinionTask<IMinionTask.NoDesc> {


    @Nullable
    @Override
    public NoDesc activateTask(@Nullable PlayerEntity lord, @Nullable IMinionEntity minion, IMinionInventory inventory) {
        return new NoDesc(this);
    }

    @Override
    public void deactivateTask(NoDesc desc) {

    }

    @Override
    public NoDesc readFromNBT(CompoundNBT nbt) {
        return new NoDesc(this);
    }
}
