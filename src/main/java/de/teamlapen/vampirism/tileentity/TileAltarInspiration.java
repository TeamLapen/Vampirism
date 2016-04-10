package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.client.render.particle.ModParticles;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.vampire.VampireLevelingConf;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.*;

/**
 * Handle blood storage and leveling
 */
public class TileAltarInspiration extends TileFluidHandler implements ITickable {
    public static final int CAPACITY = 100 * VReference.FOOD_TO_FLUID_BLOOD;
    private final int RITUAL_TIME = 60;
    private int ritualTicksLeft = 0;
    private EntityPlayer ritualPlayer;

    public TileAltarInspiration() {
        this.tank = new FluidTank(CAPACITY);
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return ModFluids.blood.equals(fluid);
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        if (canFill(from, resource == null ? null : resource.getFluid())) {
            return super.fill(from, resource, doFill);

        }
        return 0;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
    }

    public FluidTankInfo getTankInfo() {
        return super.getTankInfo(null)[0];
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        FluidStack old = tank.getFluid();
        this.readFromNBT(pkt.getNbtCompound());
        if (old != null && !old.isFluidStackIdentical(tank.getFluid()) || old == null && tank.getFluid() != null) {
            this.worldObj.notifyBlockUpdate(getPos(), worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
        }
    }

    public void startRitual(EntityPlayer p) {
        if (ritualTicksLeft > 0) return;
        VampirePlayer player = VampirePlayer.get(p);
        int targetLevel = player.getLevel() + 1;
        VampireLevelingConf levelingConf = VampireLevelingConf.getInstance();
        if (!levelingConf.isLevelValidForAltarInspiration(targetLevel)) {
            if (p.worldObj.isRemote)
                p.addChatMessage(new TextComponentTranslation("text.vampirism.ritual_level_wrong"));
            return;
        }
        int neededBlood = levelingConf.getRequiredBloodForAltarInspiration(targetLevel) * VReference.FOOD_TO_FLUID_BLOOD;
        if (tank.getFluidAmount() + 99 < neededBlood) {//Since the container can only be filled in 100th steps
            if (p.worldObj.isRemote) p.addChatMessage(new TextComponentTranslation("text.vampirism.not_enough_blood"));
            return;
        }
        if (!p.worldObj.isRemote) {
            VampLib.proxy.getParticleHandler().spawnParticles(p.worldObj, ModParticles.FLYING_BLOOD_ENTITY, this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5, 40, 0.1F, p.getRNG(), player.getRepresentingPlayer(), false);
        } else {
            super.drain(null, neededBlood, true);
            IBlockState state = worldObj.getBlockState(getPos());

            worldObj.notifyBlockUpdate(pos, state, state, 3);
        }
        ritualPlayer = p;
        ritualTicksLeft = RITUAL_TIME;
    }

    @Override
    public void update() {
        if (ritualTicksLeft == 0) return;

        if (!worldObj.isRemote) {
            switch (ritualTicksLeft) {
                case 5:
                    worldObj.addWeatherEffect(new EntityLightningBolt(worldObj, this.pos.getX(), this.pos.getY(), this.pos.getZ(), true));
                    ritualPlayer.setHealth(ritualPlayer.getMaxHealth());

                    VampirePlayer.get(ritualPlayer).getBloodStats().addBlood(100, 0);

                    break;
                case 1:
                    VampirePlayer player = VampirePlayer.get(ritualPlayer);
                    int targetLevel = player.getLevel() + 1;
                    VampireLevelingConf levelingConf = VampireLevelingConf.getInstance();
                    int blood = levelingConf.getRequiredBloodForAltarInspiration(targetLevel) * VReference.FOOD_TO_FLUID_BLOOD;
                    super.drain(null, blood, true);

                    ritualPlayer.addPotionEffect(new PotionEffect(MobEffects.regeneration, targetLevel * 10 * 20));
                    FactionPlayerHandler.get(ritualPlayer).setFactionLevel(VReference.VAMPIRE_FACTION, targetLevel);
                    markDirty();
                    IBlockState state = worldObj.getBlockState(getPos());
                    this.worldObj.notifyBlockUpdate(pos, state, state, 3);
                    break;
            }
        }


        ritualTicksLeft--;
    }
}
