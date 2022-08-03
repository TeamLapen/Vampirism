package de.teamlapen.vampirism.world;


import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.world.IVampirismWorld;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class VampirismWorld implements IVampirismWorld {

    private final static Logger LOGGER = LogManager.getLogger();
    /**
     * stores all BoundingBoxes of vampire controlled villages per dimension, mapped from origin block positions
     */
    private static final Map<BlockPos, BoundingBox> fogAreas = new ConcurrentHashMap<>();
    private static final Map<BlockPos, BoundingBox> tmpFogAreas = new ConcurrentHashMap<>();
    public static final Capability<IVampirismWorld> CAP = CapabilityManager.get(new CapabilityToken<>(){});

    /**
     * Always prefer #getOpt
     */
    @Deprecated
    public static VampirismWorld get(Level world) {
        return (VampirismWorld) world.getCapability(CAP, null).orElseThrow(() -> new IllegalStateException("Cannot get VampirismWorld from World " + world));
    }

    /**
     * Return a LazyOptional, but print a warning message if not present.
     */
    public static LazyOptional<VampirismWorld> getOpt(@Nonnull Level world) {
        LazyOptional<VampirismWorld> opt = world.getCapability(CAP, null).cast();
        if (!opt.isPresent()) {
            LOGGER.warn("Cannot get world capability. This might break mod functionality.", new Throwable().fillInStackTrace());
        }
        return opt;
    }

    public static ICapabilityProvider createNewCapability(final Level world) {
        return new ICapabilitySerializable<CompoundTag>() {

            final VampirismWorld inst = new VampirismWorld(world);
            final LazyOptional<IVampirismWorld> opt = LazyOptional.of(() -> inst);

            @Override
            public void deserializeNBT(CompoundTag nbt) {
                inst.loadNBTData(nbt);
            }

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {

                return CAP.orEmpty(capability, opt);
            }

            @Override
            public CompoundTag serializeNBT() {
                CompoundTag tag = new CompoundTag();
                inst.saveNBTData(tag);
                return tag;
            }
        };
    }

    private static boolean isHostingClient() {
        return EffectiveSide.get().isClient() && ServerLifecycleHooks.getCurrentServer() != null;
    }

    // VampireFog
    @SuppressWarnings("FieldCanBeLocal")
    @Nonnull
    private final Level world;
    // Garlic Handler ------------
    private final HashMap<ChunkPos, EnumStrength> strengthHashMap = Maps.newHashMap();
    private final HashMap<Integer, Emitter> emitterHashMap = Maps.newHashMap();

    public VampirismWorld(@Nonnull Level world) {
        this.world = world;
    }

    @Override
    public void clearCaches() {
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
    public boolean isInsideArtificialVampireFogArea(BlockPos blockPos) {
        return Stream.concat(fogAreas.entrySet().stream(), tmpFogAreas.entrySet().stream()).anyMatch(entry -> entry.getValue().isInside(blockPos));
    }

    public void printDebug(CommandSourceStack sender) {
        for (Emitter e : emitterHashMap.values()) {
            sender.sendSuccess(new TextComponent("E: " + e.toString()), true);
        }
        for (Map.Entry<ChunkPos, EnumStrength> e : strengthHashMap.entrySet()) {
            sender.sendSuccess(new TextComponent("S: " + e.toString()), true);
        }
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

    @Override
    public void removeGarlicBlock(int id) {
        if (isHostingClient()) {
            return; //If this is happening on client side and the client is also the server, the emitter has already been registered on server side. Avoid duplicate values and concurrent modification issues
        }
        Emitter e = emitterHashMap.remove(id);
        if (e == null) LOGGER.debug("Removed emitter did not exist");
        rebuildStrengthMap();
    }

    @Override
    public void updateArtificialFogBoundingBox(@Nonnull BlockPos totemPos, @Nullable AABB box) {
        if (box == null) {
            fogAreas.remove(totemPos);
            updateTemporaryArtificialFog(totemPos, null);
        } else {
            fogAreas.put(totemPos, UtilLib.AABBtoMB(box));
        }
    }

    @Override
    public void updateTemporaryArtificialFog(@Nonnull BlockPos totemPos, @Nullable AABB box) {
        if (box == null) {
            tmpFogAreas.remove(totemPos);
        } else {
            tmpFogAreas.put(totemPos, UtilLib.AABBtoMB(box));
        }
    }


    //----

    private void loadNBTData(CompoundTag nbt) {
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

    private void saveNBTData(CompoundTag nbt) {

    }

    private record Emitter(EnumStrength strength, ChunkPos[] pos) {

        @Override
        public String toString() {
            return "Emitter{" +
                    "pos=" + Arrays.toString(pos) +
                    ", strength=" + strength +
                    '}';
        }
    }
}
