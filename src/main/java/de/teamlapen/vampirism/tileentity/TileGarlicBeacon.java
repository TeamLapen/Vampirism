package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.blocks.BlockGarlicBeacon;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;


public class TileGarlicBeacon extends TileEntity implements ITickable {
    private int id;
    private EnumStrength strength = EnumStrength.MEDIUM;
    private EnumStrength defaultStrength = EnumStrength.MEDIUM;
    private int r = 1;
    private boolean registered = false;
    private int fueled = 0;


    public TileGarlicBeacon() {
        super(ModTiles.garlic_beacon);
    }

    public int getFuelTime() {
        return fueled;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.write(new NBTTagCompound());
    }

    @Override
    public void invalidate() {
        super.invalidate();
        unregister();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        IBlockState state = world.getBlockState(pos);
        this.world.notifyBlockUpdate(pos, state, state, 3);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound nbt = pkt.getNbtCompound();
        handleUpdateTag(nbt);
    }

    public void onFueled() {
        setFueledTime(20 * 60);//*20);
        this.markDirty();
    }

    public void onTouched(EntityPlayer player) {
        VampirePlayer vampire = VampirePlayer.get(player);
        if (vampire.getLevel() > 0) {
            DamageHandler.affectVampireGarlicDirect(vampire, strength);
        }
    }

    @Override
    public void read(NBTTagCompound compound) {
        super.read(compound);
        r = compound.getInt("radius");
        defaultStrength = EnumStrength.getFromStrenght(compound.getInt("strength"));
        setFueledTime(compound.getInt("fueled"));
    }

    public void setType(BlockGarlicBeacon.Type type) {
        switch (type) {
            case WEAK:
                r = Balance.hps.GARLIC_DIFFUSOR_WEAK_DISTANCE;
                defaultStrength = EnumStrength.WEAK;
                break;
            case NORMAL:
                r = Balance.hps.GARLIC_DIFFUSOR_NORMAL_DISTANCE;
                defaultStrength = EnumStrength.MEDIUM;
                break;
            case IMPROVED:
                defaultStrength = EnumStrength.MEDIUM;
                r = Balance.hps.GARLIC_DIFFUSOR_ENHANCED_DISTANCE;
                break;
        }
        strength = defaultStrength;
    }

    @Override
    public void tick() {
        if (fueled > 0) {
            if (fueled == 1) {
                setFueledTime(0);
                this.markDirty();
            } else {
                fueled--;
            }
        }
    }

    @Override
    public void validate() {
        super.validate();
        register();
    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        super.write(compound);
        compound.putInt("radius", r);
        compound.putInt("strength", defaultStrength.getStrength());
        compound.putInt("fueled", fueled);
        return compound;
    }

    @Override
    protected void setWorldCreate(World world) {
        this.setWorld(world);
    }

    private void register() {
        if (registered) {
            return;
        }
        int baseX = (getPos().getX() >> 4);
        int baseZ = (getPos().getZ() >> 4);
        ChunkPos[] chunks = new ChunkPos[(2 * r + 1) * (2 * r + 1)];
        int i = 0;
        for (int x = -r; x <= +r; x++) {
            for (int z = -r; z <= r; z++) {
                chunks[i++] = new ChunkPos(x + baseX, z + baseZ);
            }
        }
        id = VampirismAPI.getGarlicChunkHandler(getWorld()).registerGarlicBlock(strength, chunks);
        registered = true;

    }

    private void setFueledTime(int time) {
        int old = fueled;
        fueled = time;
        if (fueled > 0) {
            strength = EnumStrength.STRONG;
        } else {
            strength = defaultStrength;
        }
        if (time > 0 && old == 0 || time == 0 && old > 0) {
            if (!isInvalid()) {
                unregister();
                register();
            }
        }
    }

    private void unregister() {
        if (registered) {
            VampirismAPI.getGarlicChunkHandler(getWorld()).removeGarlicBlock(id);
            registered = false;
        }
    }
}
