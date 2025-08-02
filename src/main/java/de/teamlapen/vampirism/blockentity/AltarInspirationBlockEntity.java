package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.blockentity.NetworkedBlockEntity;
import de.teamlapen.lib.lib.util.ControllableFluidTank;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.core.ModBlockEntities;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampireLeveling;
import de.teamlapen.vampirism.entity.player.vampire.VampireLeveling.AltarInspirationRequirement;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.vampire.DrinkBloodContext;
import de.teamlapen.vampirism.particle.FlyingBloodEntityParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Optional;

public class AltarInspirationBlockEntity extends NetworkedBlockEntity {

    public static final int CAPACITY = 100 * VReference.FOOD_TO_FLUID_BLOOD;
    private static final int RITUAL_TIME = 60;

    public static final ModelProperty<Integer> FLUID_AMOUNT = new ModelProperty<>();

    public final ControllableFluidTank fluidInventory;

    private int ritualTicksLeft = 0;
    /**
     * Only valid while ritualTicksLeft > 0
     */
    private int targetLevel;
    private Player ritualPlayer;

    public AltarInspirationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALTAR_INSPIRATION.get(), pos, state);
        this.fluidInventory = new ControllableFluidTank(CAPACITY, fluid -> fluid.is(ModFluids.BLOOD)).setOnFluidChanged(fluid -> setChanged()).setAllowOutput(false);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fluidInventory.readFromNBT(registries, tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        fluidInventory.writeToNBT(registries, tag);
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(FLUID_AMOUNT, fluidInventory.getFluid().getAmount())
                .build();
    }

    @Override
    public void loadMetaData(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        fluidInventory.readFromNBT(lookupProvider, tag);
    }

    @Override
    public void saveMetaData(CompoundTag tag, HolderLookup.Provider registries) {
        fluidInventory.writeToNBT(registries, tag);
    }

    public void startRitual(Player player) {
        if (ritualTicksLeft > 0 || !player.isAlive()) return;

        targetLevel = VampirismPlayerAttributes.get(player).vampireLevel + 1;
        Optional<AltarInspirationRequirement> requirement = VampireLeveling.getInspirationRequirement(targetLevel);
        if (requirement.isEmpty()) {
            if (player.level().isClientSide) {
                player.displayClientMessage(Component.translatable("text.vampirism.altar_infusion.ritual_level_wrong"), true);
            }
            return;
        }

        int neededBlood = requirement.get().bloodAmount() * VReference.FOOD_TO_FLUID_BLOOD;
        if (fluidInventory.getFluidAmount() < neededBlood) {
            player.displayClientMessage(Component.translatable("text.vampirism.not_enough_blood"), true);
            return;
        }

        if (!player.level().isClientSide) {
            ModParticles.spawnParticlesServer(player.level(), new FlyingBloodEntityParticleOptions(player.getId(), false), this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1, this.worldPosition.getZ() + 0.5, 40, 0.1F, 0.1f, 0.1f, 0);
        } else {
            super.drain(neededBlood, IFluidHandler.FluidAction.EXECUTE);
        }

        setChanged();
        ritualPlayer = player;
        ritualTicksLeft = RITUAL_TIME;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AltarInspirationBlockEntity blockEntity) {
        if (blockEntity.ritualTicksLeft <= 0 || blockEntity.ritualPlayer == null || !blockEntity.ritualPlayer.isAlive()) return;

        if (blockEntity.ritualTicksLeft == 5) {
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.EVENT);
            if (lightningBolt != null) {
                lightningBolt.moveTo(Vec3.atBottomCenterOf(pos));
                lightningBolt.setVisualOnly(true);
                level.addFreshEntity(lightningBolt);
            }
            blockEntity.ritualPlayer.setHealth(blockEntity.ritualPlayer.getMaxHealth());
        }

        if (blockEntity.ritualTicksLeft == 1) {
            Optional<AltarInspirationRequirement> requirement = VampireLeveling.getInspirationRequirement(blockEntity.targetLevel);
            int blood = requirement.map(VampireLeveling.AltarInspirationRequirement::bloodAmount).orElse(0) * VReference.FOOD_TO_FLUID_BLOOD;
            super.drain(blood, IFluidHandler.FluidAction.EXECUTE);
            blockEntity.ritualPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, blockEntity.targetLevel * 10 * 20));
            FactionPlayerHandler.get(blockEntity.ritualPlayer).setFactionLevel(ModFactions.VAMPIRE, blockEntity.targetLevel);
            VampirePlayer.get(blockEntity.ritualPlayer).drinkBlood(Integer.MAX_VALUE, 0, false, DrinkBloodContext.none());
        }

        blockEntity.ritualTicksLeft--;
    }

    public FluidStack getFluid() {
        return fluidInventory.getFluid();
    }

    public void setFluid(FluidStack fluid) {
        fluidInventory.setFluid(fluid);
    }
}
