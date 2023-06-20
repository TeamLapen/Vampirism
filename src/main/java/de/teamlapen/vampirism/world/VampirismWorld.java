package de.teamlapen.vampirism.world;


import com.google.common.collect.Maps;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.world.IVampirismWorld;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class VampirismWorld implements IVampirismWorld {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Capability<IVampirismWorld> CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    /**
     * Always prefer #getOpt
     */
    @Deprecated
    public static @NotNull VampirismWorld get(@NotNull Level world) {
        return (VampirismWorld) world.getCapability(CAP, null).orElseThrow(() -> new IllegalStateException("Cannot get VampirismWorld from World " + world));
    }

    /**
     * Return a LazyOptional, but print a warning message if not present.
     */
    public static @NotNull LazyOptional<VampirismWorld> getOpt(@NotNull Level world) {
        LazyOptional<VampirismWorld> opt = world.getCapability(CAP, null).cast();
        if (!opt.isPresent()) {
            LOGGER.warn("Cannot get world capability. This might break mod functionality.", new Throwable().fillInStackTrace());
        }
        return opt;
    }

    public static @NotNull ICapabilityProvider createNewCapability(final @NotNull Level world) {
        return new ICapabilitySerializable<CompoundTag>() {

            final VampirismWorld inst = new VampirismWorld(world);
            final LazyOptional<IVampirismWorld> opt = LazyOptional.of(() -> inst);

            @Override
            public void deserializeNBT(CompoundTag nbt) {
                inst.loadNBTData(nbt);
            }

            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction facing) {
                return CAP.orEmpty(capability, opt);
            }

            @Override
            public @NotNull CompoundTag serializeNBT() {
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
    @NotNull
    private final Level level;
    private final ModDamageSources damageSources;

    /**
     * stores all BoundingBoxes of vampire controlled villages per dimension, mapped from origin block positions
     */
    private final Map<BlockPos, BoundingBox> fogAreas = new ConcurrentHashMap<>();
    private final Map<BlockPos, BoundingBox> tmpFogAreas = new ConcurrentHashMap<>();
    // Garlic Handler ------------
    private final HashMap<ChunkPos, EnumStrength> strengthHashMap = Maps.newHashMap();
    private final HashMap<Integer, Emitter> emitterHashMap = Maps.newHashMap();

    public VampirismWorld(@NotNull Level level) {
        this.level = level;
        this.damageSources = new ModDamageSources(level.registryAccess());
    }

    @Override
    public void clearCaches() {
        strengthHashMap.clear();
        emitterHashMap.clear();
    }

    @NotNull
    @Override
    public EnumStrength getStrengthAtChunk(ChunkPos pos) {
        EnumStrength s = strengthHashMap.get(pos);
        return s == null ? EnumStrength.NONE : s;
    }

    @Override
    public boolean isInsideArtificialVampireFogArea(@NotNull BlockPos blockPos) {
        return Stream.concat(fogAreas.entrySet().stream(), tmpFogAreas.entrySet().stream()).anyMatch(entry -> entry.getValue().isInside(blockPos));
    }

    public void printDebug(@NotNull CommandSourceStack sender) {
        for (Emitter e : emitterHashMap.values()) {
            sender.sendSuccess(() -> Component.literal("E: " + e.toString()), true);
        }
        for (Map.Entry<ChunkPos, EnumStrength> e : strengthHashMap.entrySet()) {
            sender.sendSuccess(() -> Component.literal("S: " + e.toString()), true);
        }
    }

    @Override
    public int registerGarlicBlock(EnumStrength strength, ChunkPos @NotNull ... pos) {
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
    public void updateArtificialFogBoundingBox(@NotNull BlockPos totemPos, @Nullable AABB box) {
        if (box == null) {
            fogAreas.remove(totemPos);
            updateTemporaryArtificialFog(totemPos, null);
        } else {
            fogAreas.put(totemPos, UtilLib.AABBtoMB(box));
        }
    }

    @Override
    public void updateTemporaryArtificialFog(@NotNull BlockPos totemPos, @Nullable AABB box) {
        if (box == null) {
            tmpFogAreas.remove(totemPos);
        } else {
            tmpFogAreas.put(totemPos, UtilLib.AABBtoMB(box));
        }
    }


    //----

    @SuppressWarnings("EmptyMethod")
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

    @SuppressWarnings("EmptyMethod")
    private void saveNBTData(CompoundTag nbt) {

    }

    public ModDamageSources damageSources() {
        return this.damageSources;
    }

    private record Emitter(EnumStrength strength, ChunkPos[] pos) {

        @Override
        public @NotNull String toString() {
            return "Emitter{" +
                    "pos=" + Arrays.toString(pos) +
                    ", strength=" + strength +
                    '}';
        }
    }
}
