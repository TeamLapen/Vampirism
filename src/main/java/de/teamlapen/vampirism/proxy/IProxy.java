package de.teamlapen.vampirism.proxy;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.network.SkillTreePacket;
import de.teamlapen.vampirism.player.skills.SkillTree;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

/**
 * Proxy interface
 */
public interface IProxy extends IInitListener {

    default float getRenderPartialTick() {
        return 1F;
    }

    @Nullable
    PlayerEntity getClientPlayer();

    void renderScreenFullColor(int ticksOn, int ticksOff, int color);

    @Nullable
    Entity getMouseOverEntity();

    /**
     * Only call after client_load/server started
     *
     * @param client Request the client version. Server ignores this
     * @return The appropriate skill tree
     */
    SkillTree getSkillTree(boolean client);

    default void handleSkillTree(SkillTreePacket msg) {
    }
}
