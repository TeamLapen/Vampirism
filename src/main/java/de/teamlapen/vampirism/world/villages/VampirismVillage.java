package de.teamlapen.vampirism.world.villages;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraftforge.common.capabilities.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;


public class VampirismVillage implements IVampirismVillage {

    @CapabilityInject(IVampirismVillage.class)
    @Nonnull
    public final static Capability<IVampirismVillage> CAP = getNull();

    public static VampirismVillage get(Village v) {
        return (VampirismVillage) v.getCapability(CAP, null);
    }

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IVampirismVillage.class, new VampirismVillage.Storage(), VampirismVillageDefaultImpl::new);
    }

    public static ICapabilityProvider createNewCapability(@Nonnull final Village village) {
        return new ICapabilitySerializable<NBTTagCompound>() {

            IVampirismVillage inst = new VampirismVillage(village);

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
                CAP.getStorage().readNBT(CAP, inst, null, nbt);
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                return CAP.equals(capability) ? CAP.<T>cast(inst) : null;
            }

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return CAP.equals(capability);
            }

            @Override
            public NBTTagCompound serializeNBT() {
                return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, inst, null);
            }
        };
    }

    @Nonnull
    private final Village village;
    /**
     * This is a cached value. The guaranteed value is stored in the totem tileentity
     */
    private IFaction controllingFaction;
    private boolean underAttack;
    /**
     * Location where the totem supposedly is. Totem might have been removed though.
     * If null there is no totem in the village
     */
    @Nullable
    private BlockPos totemLocation;

    public VampirismVillage(@Nonnull Village village) {
        this.village = village;
    }

    @Nullable
    @Override
    public IFaction getControllingFaction() {
        return null;
    }

    @Nonnull
    @Override
    public Village getVillage() {
        return village;
    }

    /**
     * Null if no totem has been registered
     *
     * @return Last reported totem position. There might not be one at the given location
     */
    @Nullable
    public BlockPos getTotemLocation() {
        return totemLocation;
    }

    /**
     * Force set a (new) totem location.
     * Check {@link #getTotemLocation()} first to check if there already is a totem
     *
     * @param pos
     */
    public void registerTotem(BlockPos pos) {
        this.totemLocation = pos;
    }

    /**
     * Called when a totem tile is removed in this village.
     * Reset totem location and other information. But only if the removed totem was the active one (blockpos matches)
     *
     * @param pos Position of the removed totem
     */
    public void removeTotemAndReset(BlockPos pos) {
        this.totemLocation = null;
        this.controllingFaction = null;
        this.underAttack = false;
    }

    public void setControllingFaction(IFaction faction) {
        this.controllingFaction = faction;
    }

    public void setUnderAttack(boolean attack) {
        this.underAttack = attack;
    }

    public void readFromNBT(NBTTagCompound nbt) {

    }

    public void tick() {
        if (totemLocation != null && village.world.getTotalWorldTime() % 1024 == 0) {
            IBlockState state = village.world.getBlockState(totemLocation);
            if (!state.getBlock().equals(ModBlocks.totem_top)) {
                removeTotemAndReset(totemLocation);
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {

    }

    private static class Storage implements Capability.IStorage<IVampirismVillage> {

        @Override
        public void readNBT(Capability<IVampirismVillage> capability, IVampirismVillage instance, EnumFacing side, NBTBase nbt) {
            ((VampirismVillage) instance).readFromNBT((NBTTagCompound) nbt);
        }

        @Override
        public NBTBase writeNBT(Capability<IVampirismVillage> capability, IVampirismVillage instance, EnumFacing side) {
            NBTTagCompound nbt = new NBTTagCompound();
            ((VampirismVillage) instance).writeToNBT(nbt);
            return nbt;
        }
    }
}
