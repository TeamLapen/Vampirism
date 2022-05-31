package de.teamlapen.vampirism.tileentity;

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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Handle blood storage and leveling
 */
public class AltarInspirationTileEntity extends net.minecraftforge.fluids.capability.TileFluidHandler implements ITickableTileEntity, FluidTankWithListener.IFluidTankListener {
    public static final int CAPACITY = 100 * VReference.FOOD_TO_FLUID_BLOOD;
    public static final ModelProperty<Integer> FLUID_LEVEL_PROP = new ModelProperty<>();

    public static void setBloodValue(IBlockReader worldIn, Random randomIn, BlockPos blockPosIn) {
        TileEntity tileEntity = worldIn.getBlockEntity(blockPosIn);
        if (tileEntity instanceof AltarInspirationTileEntity) {
            tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluidHandler -> fluidHandler.fill(new FluidStack(ModFluids.BLOOD.get(), BloodBottleFluidHandler.getAdjustedAmount((int) (CAPACITY * randomIn.nextFloat()))), IFluidHandler.FluidAction.EXECUTE));
        }
    }
    private final int RITUAL_TIME = 60;
    private int ritualTicksLeft = 0;
    /**
     * Only valid while ritualTicksLeft > 0
     */
    private int targetLevel;
    private PlayerEntity ritualPlayer;
    private IModelData modelData;

    public AltarInspirationTileEntity() {
        super(ModTiles.ALTAR_INSPIRATION.get());
        this.tank = new InternalTank(CAPACITY).setListener(this);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        if (modelData == null) updateModelData(false);
        return modelData;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 1, getUpdateTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        if (!hasLevel()) return;
        FluidStack old = tank.getFluid();
        this.load(this.level.getBlockState(pkt.getPos()), pkt.getTag());
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

    public void startRitual(PlayerEntity p) {
        if (ritualTicksLeft > 0 || !p.isAlive()) return;
        targetLevel = VampirismPlayerAttributes.get(p).vampireLevel + 1;
        VampirismPlayerAttributes attributes = VampirismPlayerAttributes.get(p);
        int targetLevel = attributes.vampireLevel + 1;
        VampireLevelingConf levelingConf = VampireLevelingConf.getInstance();
        if (!levelingConf.isLevelValidForAltarInspiration(targetLevel)) {
            if (p.level.isClientSide)
                p.displayClientMessage(new TranslationTextComponent("text.vampirism.altar_infusion.ritual_level_wrong"), true);
            return;
        }
        int neededBlood = levelingConf.getRequiredBloodForAltarInspiration(targetLevel) * VReference.FOOD_TO_FLUID_BLOOD;
        if (this.tank.getFluidAmount() + 99 < neededBlood) {//Since the container can only be filled in 100th steps
            if (p.level.isClientSide)
                p.displayClientMessage(new TranslationTextComponent("text.vampirism.not_enough_blood"), true);
            return;
        }
        if (!p.level.isClientSide) {
            ModParticles.spawnParticlesServer(p.level, new FlyingBloodEntityParticleData(ModParticles.FLYING_BLOOD_ENTITY.get(), p.getId(), false), this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1, this.worldPosition.getZ() + 0.5, 40, 0.1F, 0.1f, 0.1f, 0);
        } else {
            ((InternalTank) this.tank).doDrain(neededBlood, IFluidHandler.FluidAction.EXECUTE);
        }
        this.ritualPlayer = p;
        this.ritualTicksLeft = RITUAL_TIME;
    }

    @Override
    public void tick() {
        if (ritualTicksLeft == 0 || level == null || ritualPlayer == null || !ritualPlayer.isAlive()) return;

        if (!level.isClientSide) {
            switch (ritualTicksLeft) {
                case 5:
                    LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(this.level);
                    lightningboltentity.moveTo(Vector3d.atBottomCenterOf(worldPosition));
                    lightningboltentity.setVisualOnly(true);
                    this.level.addFreshEntity(lightningboltentity);
                    ritualPlayer.setHealth(ritualPlayer.getMaxHealth());
                    break;
                case 1:
                    VampireLevelingConf levelingConf = VampireLevelingConf.getInstance();
                    if (!levelingConf.isLevelValidForAltarInspiration(targetLevel)) return; //no level available
                    int blood = levelingConf.getRequiredBloodForAltarInspiration(targetLevel) * VReference.FOOD_TO_FLUID_BLOOD;
                    ((InternalTank) tank).doDrain(blood, IFluidHandler.FluidAction.EXECUTE);

                    ritualPlayer.addEffect(new EffectInstance(Effects.REGENERATION, targetLevel * 10 * 20));
                    FactionPlayerHandler.getOpt(ritualPlayer).ifPresent(handler -> handler.setFactionLevel(VReference.VAMPIRE_FACTION, targetLevel));
                    VampirePlayer.getOpt(ritualPlayer).ifPresent(vampire -> vampire.drinkBlood(Integer.MAX_VALUE, 0, false));
                    this.setChanged();
                    break;
                default:
                    break;
            }
        }


        ritualTicksLeft--;
    }

    private void updateModelData(boolean refresh) {
        FluidStack fluid = tank.getFluid();
        int l = 0;
        if (!fluid.isEmpty()) {
            float i = (fluid.getAmount() / (float) AltarInspirationTileEntity.CAPACITY * 10);
            l = (i > 0 && i < 1) ? 1 : (int) i;
        }
        modelData = new ModelDataMap.Builder().withInitial(FLUID_LEVEL_PROP, l).build();
        if (refresh) {
            ModelDataManager.requestModelDataRefresh(this);
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
