package de.teamlapen.vampirism.common.world.entity;

import de.teamlapen.faction.api.world.entities.IEntityLeader;
import net.minecraft.world.entity.LivingEntity;

public interface IEntityFollower {

    boolean isFollowing();

    <T extends LivingEntity & IEntityLeader> T getLeader();

    <T extends LivingEntity & IEntityLeader> void setLeader(T leader);
}
