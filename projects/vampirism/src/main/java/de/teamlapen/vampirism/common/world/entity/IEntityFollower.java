package de.teamlapen.vampirism.common.world.entity;

import de.teamlapen.faction.api.world.entities.IEntityLeader;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface IEntityFollower {

    boolean isFollowing();

    @Nullable
    <T extends LivingEntity & IEntityLeader> T getLeader();

    <T extends LivingEntity & IEntityLeader> void setLeader(T leader);
}
