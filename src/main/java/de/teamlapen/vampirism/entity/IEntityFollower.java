package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.IEntityLeader;
import net.minecraft.world.entity.LivingEntity;

public interface IEntityFollower {

    boolean isFollowing();

    <T extends LivingEntity & IEntityLeader> T getLeader();

    <T extends LivingEntity & IEntityLeader> void setLeader(T leader);
}
