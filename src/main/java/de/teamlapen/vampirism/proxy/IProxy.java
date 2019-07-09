package de.teamlapen.vampirism.proxy;

import de.teamlapen.lib.lib.util.IInitListener;
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

    boolean isClientPlayerNull();

    /**
     * Probably just check {@link PlayerEntity#isUser()}
     *
     * @param player
     * @return
     */
    @Deprecated
    boolean isPlayerThePlayer(PlayerEntity player);

    void renderScreenFullColor(int ticksOn, int ticksOff, int color);

    @Nullable
    Entity getMouseOverEntity();
}
