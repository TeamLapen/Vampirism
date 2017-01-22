package de.teamlapen.vampirism.world;

import com.google.common.collect.Maps;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.world.IGarlicChunkHandler;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * Implements {@link IGarlicChunkHandler} using maps to store
 */
public class GarlicChunkHandler implements IGarlicChunkHandler {

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

    @Override
    public int registerGarlicBlock(EnumStrength strength, ChunkPos... pos) {
        Emitter e = new Emitter(strength, pos);
        int hash = e.hashCode();
        emitterHashMap.put(hash, e);
        rebuildStrengthMap();
        return hash;
    }

    @Override
    public void removeGarlicBlock(int id) {
        Emitter e = emitterHashMap.remove(id);
        if (e == null) VampirismMod.log.t("Emitter was null");
        rebuildStrengthMap();
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

    public static class Provider implements IGarlicChunkHandler.Provider {

        private final HashMap<Integer, IGarlicChunkHandler> handlers = Maps.newHashMap();

        @Override
        public void clear() {
            for (IGarlicChunkHandler handler : handlers.values()) {
                handler.clear();
            }
            handlers.clear();
        }

        @Nonnull
        @Override
        public IGarlicChunkHandler getHandler(World world) {
            IGarlicChunkHandler handler = handlers.get(world.provider.getDimension());
            if (handler == null) {
                handler = new GarlicChunkHandler();
                handlers.put(world.provider.getDimension(), handler);
            }
            return handler;
        }
    }

    private class Emitter {
        final EnumStrength strength;
        final ChunkPos[] pos;

        private Emitter(EnumStrength strength, ChunkPos[] pos) {
            this.strength = strength;
            this.pos = pos;
        }
    }
}
