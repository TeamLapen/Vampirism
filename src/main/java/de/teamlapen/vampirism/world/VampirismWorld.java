package de.teamlapen.vampirism.world;


import com.google.common.collect.Maps;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.world.IVampirismWorld;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

public class VampirismWorld implements IVampirismWorld {

    private final static Logger LOGGER = LogManager.getLogger();
    /**
     * stores all BoundingBoxes of vampire controlled villages per dimension, mapped from origin block positions
     */
    private static final Map<BlockPos, MutableBoundingBox> fogAreas = new ConcurrentHashMap<>();
    private static final Map<BlockPos, MutableBoundingBox> tmpFogAreas = new ConcurrentHashMap<>();
    @CapabilityInject(IVampirismWorld.class)
    public static Capability<IVampirismWorld> CAP = getNull();

    @Deprecated
    public static VampirismWorld get(World world) {
        return (VampirismWorld) world.getCapability(CAP, null).orElseThrow(() -> new IllegalStateException("Cannot get VampirismWorld from World " + world));
    }

    /**
     * Return a LazyOptional, but print a warning message if not present.
     */
    public static LazyOptional<VampirismWorld> getOpt(@Nonnull World world) {
        LazyOptional<VampirismWorld> opt = world.getCapability(CAP, null).cast();
        if (!opt.isPresent()) {
            LOGGER.warn("Cannot get world capability. This might break mod functionality.", new Throwable().fillInStackTrace());
        }
        return opt;
    }

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IVampirismWorld.class, new VampirismWorld.Storage(), VampirismWorldDefaultImpl::new);
    }

    public static ICapabilityProvider createNewCapability(final World world) {
        return new ICapabilitySerializable<CompoundNBT>() {

            final IVampirismWorld inst = new VampirismWorld(world);
            final LazyOptional<IVampirismWorld> opt = LazyOptional.of(() -> inst);

            @Override
            public void deserializeNBT(CompoundNBT nbt) {
                CAP.getStorage().readNBT(CAP, inst, null, nbt);
            }

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {

                return CAP.orEmpty(capability, opt);
            }

            @Override
            public CompoundNBT serializeNBT() {
                return (CompoundNBT) CAP.getStorage().writeNBT(CAP, inst, null);
            }
        };
    }

    private static boolean isHostingClient() {
        return EffectiveSide.get().isClient() && ServerLifecycleHooks.getCurrentServer() != null;
    }

    // VampireFog
    @Nonnull
    private final World world;
    // Garlic Handler ------------
    private final HashMap<ChunkPos, EnumStrength> strengthHashMap = Maps.newHashMap();
    private final HashMap<Integer, Emitter> emitterHashMap = Maps.newHashMap();

    public VampirismWorld(@Nonnull World world) {
        this.world = world;
    }

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
    public boolean isInsideArtificialVampireFogArea(BlockPos blockPos) {
        return Stream.concat(fogAreas.entrySet().stream(), tmpFogAreas.entrySet().stream()).anyMatch(entry -> entry.getValue().isInside(blockPos));
    }

    public void printDebug(CommandSource sender) {
        for (Emitter e : emitterHashMap.values()) {
            sender.sendSuccess(new StringTextComponent("E: " + e.toString()), true);
        }
        for (Map.Entry<ChunkPos, EnumStrength> e : strengthHashMap.entrySet()) {
            sender.sendSuccess(new StringTextComponent("S: " + e.toString()), true);
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
    public void updateArtificialFogBoundingBox(@Nonnull BlockPos sourcePos, @Nullable AxisAlignedBB area) {
        if (area == null) {
            fogAreas.remove(sourcePos);
            updateTemporaryArtificialFog(sourcePos, null);
        } else {
            fogAreas.put(sourcePos, UtilLib.AABBtoMB(area));
        }
    }

    @Override
    public void updateTemporaryArtificialFog(@Nonnull BlockPos sourcePos, @Nullable AxisAlignedBB area) {
        if (area == null) {
            tmpFogAreas.remove(sourcePos);
        } else {
            tmpFogAreas.put(sourcePos, UtilLib.AABBtoMB(area));
        }
    }


    //----

    private void loadNBTData(CompoundNBT nbt) {
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

    private void saveNBTData(CompoundNBT nbt) {

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

    private static class Storage implements Capability.IStorage<IVampirismWorld> {

        @Override
        public void readNBT(Capability<IVampirismWorld> capability, IVampirismWorld instance, Direction side, INBT nbt) {
            ((VampirismWorld) instance).loadNBTData((CompoundNBT) nbt);
        }

        @Override
        public INBT writeNBT(Capability<IVampirismWorld> capability, IVampirismWorld instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            ((VampirismWorld) instance).saveNBTData(nbt);
            return nbt;
        }
    }
}
