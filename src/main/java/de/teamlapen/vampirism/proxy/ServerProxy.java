package de.teamlapen.vampirism.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Serverside proxy
 */
public class ServerProxy extends CommonProxy {

    @Override
    public boolean isClientPlayerNull() {
        return false;
    }

    @Override
    public boolean isPlayerThePlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        super.onInitStep(step, event);
    }
}
