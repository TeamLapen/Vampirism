package de.teamlapen.vampirism.common.entity;

import de.teamlapen.factions.api.entities.IEntityLeader;
import net.minecraft.world.entity.LivingEntity;

public interface IEntityFollower {

    boolean isFollowing();

    <T extends LivingEntity & IEntityLeader> T getLeader();

    <T extends LivingEntity & IEntityLeader> void setLeader(T leader);
}
