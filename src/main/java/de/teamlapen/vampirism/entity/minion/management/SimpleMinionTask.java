package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.minion.DefaultMinionTask;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;


public class SimpleMinionTask extends DefaultMinionTask<IMinionTask.NoDesc<MinionData>, MinionData> {


    @Nullable
    @Override
    public NoDesc<MinionData> activateTask(@Nullable PlayerEntity lord, @Nullable IMinionEntity minion, MinionData data) {
        return new NoDesc<>(this);
    }

    @Override
    public void deactivateTask(NoDesc<MinionData> desc) {

    }

    @Override
    public NoDesc<MinionData> readFromNBT(CompoundNBT nbt) {
        return new NoDesc<MinionData>(this);
    }
}
