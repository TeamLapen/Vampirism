package de.teamlapen.vampirism.world.villages;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;


public class VampirismVillage implements IVampirismVillage {

    @CapabilityInject(IVampirismVillage.class)
    @Nonnull
    private static Capability<IVampirismVillage> CAP = getNull();

    public static VampirismVillage get(Village v) {
        return (VampirismVillage) v.getCapability(CAP).orElseThrow(() -> new IllegalStateException("Cannot get VampirismVillage from Village"));
    }

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IVampirismVillage.class, new VampirismVillage.Storage(), VampirismVillageDefaultImpl::new);
    }

    public static ICapabilityProvider createNewCapability(@Nonnull final Village village) {
        return new ICapabilitySerializable<CompoundNBT>() {

            IVampirismVillage inst = new VampirismVillage(village);
            LazyOptional<IVampirismVillage> opt = LazyOptional.of(() -> inst);

            @Override
            public void deserializeNBT(CompoundNBT nbt) {
                CAP.getStorage().readNBT(CAP, inst, null, nbt);
            }

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
                return CAP.orEmpty(capability, opt);
            }

            @Override
            public CompoundNBT serializeNBT() {
                return (CompoundNBT) CAP.getStorage().writeNBT(CAP, inst, null);
            }
        };
    }

    @Nonnull
    private final Village village;

    private final List<VillageAggressor> villageAggressors = new ArrayList<>();

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
        return controllingFaction;
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
    public void removeTotemAndReset(@Nonnull BlockPos pos) {
        if (pos.equals(totemLocation)) {
            this.totemLocation = null;
            this.controllingFaction = null;
            this.underAttack = false;
        }

    }

    private int tickCounter;

    public void setUnderAttack(boolean attack) {
        this.underAttack = attack;
    }

    @Override
    public void addOrRenewAggressor(@Nullable Entity entity) {
        IFactionEntity factionEntity = null;
        if (entity instanceof IFactionEntity) {
            factionEntity = (IFactionEntity) entity;
        } else if (entity instanceof PlayerEntity) {
            factionEntity = FactionPlayerHandler.get((PlayerEntity) entity).getCurrentFactionPlayer();
        }
        if (factionEntity == null || factionEntity.getFaction() == this.controllingFaction) return;
        for (VillageAggressor aggressor : this.villageAggressors) {
            if (aggressor.factionEntity.equals(factionEntity)) {
                aggressor.aggressionTime = this.tickCounter;
                return;
            }
        }
        this.villageAggressors.add(new VillageAggressor(factionEntity, this.tickCounter));
    }

    @Override
    @Nullable
    public IFactionEntity findNearestVillageAggressor(@Nonnull LivingEntity entity) {
        double d0 = Double.MAX_VALUE;
        VillageAggressor nearestAggressor = null;

        for (VillageAggressor aggressor : this.villageAggressors) {
            double d1 = aggressor.creature.getDistanceSq(entity);

            if (d1 <= d0) {
                nearestAggressor = aggressor;
                d0 = d1;
            }
        }

        return nearestAggressor != null ? nearestAggressor.factionEntity : null;
    }

    public void read(CompoundNBT nbt) {

    }

    public void setControllingFaction(IFaction faction) {
        if (faction != null && faction != this.controllingFaction) {
            this.villageAggressors.clear();
        }
        this.controllingFaction = faction;
    }

    public void tick(long worldTime) {
        this.tickCounter = (int) worldTime;

        if (totemLocation != null && tickCounter % 1024 == 0) {
            BlockState state = village.world.getBlockState(totemLocation);
            if (!state.getBlock().equals(ModBlocks.totem_top)) {
                removeTotemAndReset(totemLocation);
            }
        }
        if (worldTime % 20 == 14) {
            this.removeDeadAndOldAggressors();
        }
    }

    private void removeDeadAndOldAggressors() {
        villageAggressors.removeIf(aggressorVampire -> !aggressorVampire.creature.isAlive() || Math.abs(this.tickCounter - aggressorVampire.aggressionTime) > 600);

    }

    public void write(CompoundNBT nbt) {

    }

    private static class Storage implements Capability.IStorage<IVampirismVillage> {

        @Override
        public void readNBT(Capability<IVampirismVillage> capability, IVampirismVillage instance, Direction side, INBT nbt) {
            ((VampirismVillage) instance).read((CompoundNBT) nbt);
        }

        @Override
        public INBT writeNBT(Capability<IVampirismVillage> capability, IVampirismVillage instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            ((VampirismVillage) instance).write(nbt);
            return nbt;
        }
    }

    private class VillageAggressor {
        public final LivingEntity creature;
        public final IFactionEntity factionEntity;
        public int aggressionTime;

        private VillageAggressor(IFactionEntity factionEntity, int initialAggressionTime) {
            this.factionEntity = factionEntity;
            this.creature = factionEntity.getRepresentingEntity();
            this.aggressionTime = initialAggressionTime;
        }
    }
}
