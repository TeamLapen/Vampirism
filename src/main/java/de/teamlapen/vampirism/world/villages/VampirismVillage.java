package de.teamlapen.vampirism.world.villages;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
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

    public static ICapabilityProvider createNewCapability(final Village village) {
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

    private final Village village;
    private IFaction controllingFaction;
    private boolean underAttack;

    public VampirismVillage(Village village) {
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

    public void readFromNBT(NBTTagCompound nbt) {

    }

    public void tick() {

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
