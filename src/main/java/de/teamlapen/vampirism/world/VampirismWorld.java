package de.teamlapen.vampirism.world;


import de.teamlapen.vampirism.api.world.IVampirismWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

public class VampirismWorld implements IVampirismWorld {

    private final static Logger LOGGER = LogManager.getLogger();
    @CapabilityInject(IVampirismWorld.class)
    public static Capability<IVampirismWorld> CAP = getNull();

    /**
     * Must check Entity#isAlive before
     */
    public static IVampirismWorld get(World world) {
        return world.getCapability(CAP, null).orElseThrow(() -> new IllegalStateException("Cannot get FactionPlayerHandler from EntityPlayer " + world));
    }

    /**
     * Return a LazyOptional, but print a warning message if not present.
     */
    public static LazyOptional<IVampirismWorld> getOpt(@Nonnull World world) {
        LazyOptional<IVampirismWorld> opt = world.getCapability(CAP, null).cast();
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


    @Nonnull
    private final World world;

    public VampirismWorld(@Nonnull World world) {
        this.world = world;
    }

    private void loadNBTData(CompoundNBT nbt) {
    }

    private void saveNBTData(CompoundNBT nbt) {

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
