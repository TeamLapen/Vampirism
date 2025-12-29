package de.teamlapen.factions.common.proxy;

import de.teamlapen.factions.common.sounds.ISoundHandler;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class CommonProxy implements IProxy {

    protected ISoundHandler soundHandler = new ISoundHandler.NoOpSoundHandler();

    @Override
    public ISoundHandler getSoundHandler() {
        return this.soundHandler;
    }

    @Override
    public @Nullable Player getClientPlayer() {
        return null;
    }
}
