package de.teamlapen.vampirism.world;

import com.google.common.collect.Maps;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.world.IGarlicChunkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements {@link IGarlicChunkHandler} using maps to store
 */
public class GarlicChunkHandler implements IGarlicChunkHandler {

    private static final Logger LOGGER = LogManager.getLogger(GarlicChunkHandler.class);
    private final HashMap<ChunkPos, EnumStrength> strengthHashMap = Maps.newHashMap();
    private final HashMap<Integer, Emitter> emitterHashMap = Maps.newHashMap();

    @Override
    public void clear() {
        strengthHashMap.clear();
        emitterHashMap.clear();
    }

    @Nonnull
    @Override
    public EnumStrength getStrengthAtChunk(ChunkPos pos) {
        EnumStrength s = strengthHashMap.get(pos);
        return s == null ? EnumStrength.NONE : s;
    }

    public void printDebug(CommandSource sender) {
        for (Emitter e : emitterHashMap.values()) {
            sender.sendFeedback(new StringTextComponent("E: " + e.toString()), true);
        }
        for (Map.Entry<ChunkPos, EnumStrength> e : strengthHashMap.entrySet()) {
            sender.sendFeedback(new StringTextComponent("S: " + e.toString()), true);
        }
    }

    private static boolean isHostingClient() {
        return EffectiveSide.get().isClient() && ServerLifecycleHooks.getCurrentServer() != null;
    }

    @Override
    public int registerGarlicBlock(EnumStrength strength, ChunkPos... pos) {
        for (ChunkPos p : pos) {
            if (p == null) {
                throw new IllegalArgumentException("Garlic emitter position should not be null");
            }
        }
        Emitter e = new Emitter(strength, pos);
        int hash = e.hashCode();
        if (isHostingClient()) {
            return hash; //If this is happening on client side and the client is also the server, the emitter has already been registered on server side. Avoid duplicate values and concurrent modification issues
        }
        emitterHashMap.put(hash, e);
        rebuildStrengthMap();
        return hash;
    }

    private void rebuildStrengthMap() {
        strengthHashMap.clear();
        for (Emitter e : emitterHashMap.values()) {
            for (ChunkPos pos : e.pos) {
                EnumStrength old = strengthHashMap.get(pos);
                if (old == null || e.strength.isStrongerThan(old)) {
                    strengthHashMap.put(pos, e.strength);
                }

            }
        }
    }

    @Override
    public void removeGarlicBlock(int id) {
        if (isHostingClient()) {
            return; //If this is happening on client side and the client is also the server, the emitter has already been registered on server side. Avoid duplicate values and concurrent modification issues
        }
        Emitter e = emitterHashMap.remove(id);
        if (e == null) LOGGER.debug("Removed emitter did not exist");
        rebuildStrengthMap();
    }

    @SuppressWarnings("deprecation")
    public static class Provider implements IGarlicChunkHandler.Provider {

        private final HashMap<RegistryKey<World>, IGarlicChunkHandler> handlers = Maps.newHashMap();

        @Override
        public void clear() {
            for (IGarlicChunkHandler handler : handlers.values()) {
                handler.clear();
            }
            handlers.clear();
        }

        @Nonnull
        @Override
        public IGarlicChunkHandler getHandler(IWorld world) {
            return handlers.computeIfAbsent(world.getWorld().func_234923_W_(), k -> new GarlicChunkHandler());
        }
    }

    private static class Emitter {
        final EnumStrength strength;
        final ChunkPos[] pos;

        private Emitter(EnumStrength strength, ChunkPos[] pos) {
            this.strength = strength;
            this.pos = pos;
        }

        @Override
        public String toString() {
            return "Emitter{" +
                    "pos=" + Arrays.toString(pos) +
                    ", strength=" + strength +
                    '}';
        }
    }
}
