package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.vampire.VampireLevelingConf;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Handle blood storage and leveling
 */
public class TileAltarInspiration extends net.minecraftforge.fluids.capability.TileFluidHandler implements ITickable {
    public static final int CAPACITY = 100 * VReference.FOOD_TO_FLUID_BLOOD;
    private final int RITUAL_TIME = 60;
    private int ritualTicksLeft = 0;
    private EntityPlayer ritualPlayer;

    public TileAltarInspiration() {
        this.tank = new InternalTank(CAPACITY);
    }

    public FluidTankInfo getTankInfo() {
        return tank.getInfo();
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        FluidStack old = tank.getFluid();
        this.readFromNBT(pkt.getNbtCompound());
        if (old != null && !old.isFluidStackIdentical(tank.getFluid()) || old == null && tank.getFluid() != null) {
            this.world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    public void startRitual(EntityPlayer p) {
        if (ritualTicksLeft > 0) return;
        VampirePlayer player = VampirePlayer.get(p);
        int targetLevel = player.getLevel() + 1;
        VampireLevelingConf levelingConf = VampireLevelingConf.getInstance();
        if (!levelingConf.isLevelValidForAltarInspiration(targetLevel)) {
            if (p.world.isRemote)
                p.sendMessage(new TextComponentTranslation("text.vampirism.ritual_level_wrong"));
            return;
        }
        int neededBlood = levelingConf.getRequiredBloodForAltarInspiration(targetLevel) * VReference.FOOD_TO_FLUID_BLOOD;
        if (tank.getFluidAmount() + 99 < neededBlood) {//Since the container can only be filled in 100th steps
            if (p.world.isRemote) p.sendMessage(new TextComponentTranslation("text.vampirism.not_enough_blood"));
            return;
        }
        if (!p.world.isRemote) {
            VampLib.proxy.getParticleHandler().spawnParticles(p.world, ModParticles.FLYING_BLOOD_ENTITY, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, 40, 0.1F, p.getRNG(), player.getRepresentingPlayer(), false);
        } else {
            ((InternalTank) tank).doDrain(neededBlood, true);
            IBlockState state = world.getBlockState(getPos());

            world.notifyBlockUpdate(pos, state, state, 3);
        }
        ritualPlayer = p;
        ritualTicksLeft = RITUAL_TIME;
    }

    @Override
    public void update() {
        if (ritualTicksLeft == 0) return;

        if (!world.isRemote) {
            switch (ritualTicksLeft) {
                case 5:
                    world.addWeatherEffect(new EntityLightningBolt(world, this.pos.getX(), this.pos.getY(), this.pos.getZ(), true));
                    ritualPlayer.setHealth(ritualPlayer.getMaxHealth());
                    VampirePlayer.get(ritualPlayer).drinkBlood(100, 0);

                    break;
                case 1:
                    VampirePlayer player = VampirePlayer.get(ritualPlayer);
                    int targetLevel = player.getLevel() + 1;
                    VampireLevelingConf levelingConf = VampireLevelingConf.getInstance();
                    int blood = levelingConf.getRequiredBloodForAltarInspiration(targetLevel) * VReference.FOOD_TO_FLUID_BLOOD;
                    ((InternalTank) tank).doDrain(blood, true);

                    ritualPlayer.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, targetLevel * 10 * 20));
                    FactionPlayerHandler.get(ritualPlayer).setFactionLevel(VReference.VAMPIRE_FACTION, targetLevel);
                    VampirePlayer.get(ritualPlayer).drinkBlood(Integer.MAX_VALUE, 0, false);
                    markDirty();
                    IBlockState state = world.getBlockState(getPos());
                    this.world.notifyBlockUpdate(pos, state, state, 3);
                    break;
                default:
                    break;
            }
        }


        ritualTicksLeft--;
    }

    private class InternalTank extends FluidTank {

        public InternalTank(int capacity) {
            super(capacity);
            setCanDrain(false);
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            return fluid != null && canFill() && ModFluids.blood.equals(fluid.getFluid());
        }

        public FluidStack doDrain(int maxDrain, boolean doDrain) {
            this.setCanDrain(true);
            FluidStack s = super.drain(maxDrain, doDrain);
            this.setCanDrain(false);
            return s;
        }
    }
}
