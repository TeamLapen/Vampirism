package de.teamlapen.vampirism.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.Map;

/**
 * 1.14
 *
 * @author maxanier
 */
public class TestPlayerTemp extends PlayerEntity {

    private static final Map<Pose, EntitySize> SIZE_BY_POSE = ImmutableMap.<Pose, EntitySize>builder().put(Pose.STANDING, STANDING_SIZE).put(Pose.SLEEPING, SLEEPING_SIZE).put(Pose.FALL_FLYING, EntitySize.flexible(0.6F, 0.6F)).put(Pose.SWIMMING, EntitySize.flexible(0.6F, 0.6F)).put(Pose.SPIN_ATTACK, EntitySize.flexible(0.6F, 0.6F)).put(Pose.SNEAKING, EntitySize.flexible(0.6F, 1.5F)).put(Pose.DYING, EntitySize.fixed(0.2F, 0.2F)).build();


    public TestPlayerTemp(World p_i45324_1_, GameProfile p_i45324_2_) {
        super(p_i45324_1_, p_i45324_2_);
    }

    @Override
    public EntitySize getSize(Pose p_213305_1_) {
        if (ASMHooks.overwritePlayerSize(this)) {
            return ASMHooks.getPlayerSize(this, p_213305_1_);
        }
        return SIZE_BY_POSE.getOrDefault(p_213305_1_, STANDING_SIZE);
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }
}
