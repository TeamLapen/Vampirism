package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.util.FluidTankWithListener;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.items.BloodBottleFluidHandler;
import de.teamlapen.vampirism.particle.FlyingBloodEntityParticleData;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.vampire.VampireLevelingConf;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Handle blood storage and leveling
 */
public class AltarInspirationBlockEntity extends net.minecraftforge.fluids.capability.FluidHandlerBlockEntity implements FluidTankWithListener.IFluidTankListener {
    public static final int CAPACITY = 100 * VReference.FOOD_TO_FLUID_BLOOD;
    public static final ModelProperty<Integer> FLUID_LEVEL_PROP = new ModelProperty<>();

    public static void setBloodValue(BlockGetter worldIn, Random randomIn, BlockPos blockPosIn) {
        BlockEntity tileEntity = worldIn.getBlockEntity(blockPosIn);
        if (tileEntity instanceof AltarInspirationBlockEntity) {
            tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluidHandler -> fluidHandler.fill(new FluidStack(ModFluids.BLOOD.get(), BloodBottleFluidHandler.getAdjustedAmount((int) (CAPACITY * randomIn.nextFloat()))), IFluidHandler.FluidAction.EXECUTE));
        }
    }

    private static final int RITUAL_TIME = 60;

    private int ritualTicksLeft = 0;
    /**
     * Only valid while ritualTicksLeft > 0
     */
    private int targetLevel;
    private Player ritualPlayer;
    private ModelData modelData;

    public AltarInspirationBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.ALTAR_INSPIRATION.get(), pos, state);
        this.tank = new InternalTank(CAPACITY).setListener(this);
    }

    @Nonnull
    @Override
    public ModelData getModelData() {
        if (modelData == null) updateModelData(false);
        return modelData;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (!hasLevel()) return;
        FluidStack old = tank.getFluid();
        this.load(pkt.getTag());
        if (!old.isFluidStackIdentical(tank.getFluid())) {
            updateModelData(true);
        }
    }

    @Override
    public void onTankContentChanged() {
        setChanged();
    }

    @Override
    public void setChanged() {
        if (level != null) {
            if (level.isClientSide)
                updateModelData(true);
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
            super.setChanged();
        }
    }

    public void startRitual(Player p) {
        if (ritualTicksLeft > 0 || !p.isAlive()) return;
        targetLevel = VampirismPlayerAttributes.get(p).vampireLevel + 1;
        VampireLevelingConf levelingConf = VampireLevelingConf.getInstance();
        if (!levelingConf.isLevelValidForAltarInspiration(targetLevel)) {
            if (p.level.isClientSide)
                p.displayClientMessage(Component.translatable("text.vampirism.altar_infusion.ritual_level_wrong"), true);
            return;
        }
        int neededBlood = levelingConf.getRequiredBloodForAltarInspiration(targetLevel) * VReference.FOOD_TO_FLUID_BLOOD;
        if (tank.getFluidAmount() + 99 < neededBlood) {//Since the container can only be filled in 100th steps
            if (p.level.isClientSide)
                p.displayClientMessage(Component.translatable("text.vampirism.not_enough_blood"), true);
            return;
        }
        if (!p.level.isClientSide) {
            ModParticles.spawnParticlesServer(p.level, new FlyingBloodEntityParticleData(ModParticles.FLYING_BLOOD_ENTITY.get(), p.getId(), false), this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1, this.worldPosition.getZ() + 0.5, 40, 0.1F, 0.1f, 0.1f, 0);
        } else {
            ((InternalTank) tank).doDrain(neededBlood, IFluidHandler.FluidAction.EXECUTE);
        }
        ritualPlayer = p;
        ritualTicksLeft = RITUAL_TIME;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AltarInspirationBlockEntity blockEntity) {
        if (blockEntity.ritualTicksLeft == 0 || blockEntity.ritualPlayer == null || !blockEntity.ritualPlayer.isAlive())
            return;

        switch (blockEntity.ritualTicksLeft) {
            case 5 -> {
                LightningBolt lightningboltentity = EntityType.LIGHTNING_BOLT.create(level);
                lightningboltentity.moveTo(Vec3.atBottomCenterOf(pos));
                lightningboltentity.setVisualOnly(true);
                level.addFreshEntity(lightningboltentity);
                blockEntity.ritualPlayer.setHealth(blockEntity.ritualPlayer.getMaxHealth());
            }
            case 1 -> {
                VampireLevelingConf levelingConf = VampireLevelingConf.getInstance();
                int blood = levelingConf.getRequiredBloodForAltarInspiration(blockEntity.targetLevel) * VReference.FOOD_TO_FLUID_BLOOD;
                ((InternalTank) blockEntity.tank).doDrain(blood, IFluidHandler.FluidAction.EXECUTE);
                blockEntity.ritualPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, blockEntity.targetLevel * 10 * 20));
                FactionPlayerHandler.getOpt(blockEntity.ritualPlayer).ifPresent(h->h.setFactionLevel(VReference.VAMPIRE_FACTION, blockEntity.targetLevel));
                VampirePlayer.getOpt(blockEntity.ritualPlayer).ifPresent(v -> v.drinkBlood(Integer.MAX_VALUE, 0, false));
            }
        }

        blockEntity.ritualTicksLeft--;
    }

    private void updateModelData(boolean refresh) {
        FluidStack fluid = tank.getFluid();
        int l = 0;
        if (!fluid.isEmpty()) {
            float i = (fluid.getAmount() / (float) AltarInspirationBlockEntity.CAPACITY * 10);
            l = (i > 0 && i < 1) ? 1 : (int) i;
        }
        this.modelData = ModelData.builder().with(FLUID_LEVEL_PROP, l).build();
        if (refresh) {
            requestModelDataUpdate();
        }
    }

    private static class InternalTank extends FluidTankWithListener {

        private InternalTank(int capacity) {
            super(capacity, fluidStack -> ModFluids.BLOOD.get().isSame(fluidStack.getFluid()));
            setDrainable(false);
        }

        void doDrain(int maxDrain, FluidAction action) {
            this.setDrainable(true);
            super.drain(maxDrain, action);
            this.setDrainable(false);
        }
    }
}
