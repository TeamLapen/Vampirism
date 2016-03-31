package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.util.IParticleHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLStateEvent;

import java.util.Random;

/**
 * Serverside proxy
 */
public class ServerProxy extends CommonProxy {

    private final IParticleHandler particleHandlerServer = new IParticleHandler() {
        @Override
        public void spawnParticle(World world, Type type, double posX, double posY, double posZ, Object... param) {

        }

        @Override
        public void spawnParticles(World world, Type type, double posX, double posY, double posZ, int count, double maxDist, Random random, Object... param) {

        }
    };

    @Override
    public IParticleHandler getParticleHandler() {
        return particleHandlerServer;
    }

    @Override
    public void renderScreenFullColor(int ticksOn, int ticksOff, int color) {

    }

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
