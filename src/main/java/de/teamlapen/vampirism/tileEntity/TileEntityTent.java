package de.teamlapen.vampirism.tileEntity;

import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.SimpleSpawnerLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * Tileentity for the hunters tent
 * Handles hunter spawning
 */
public class TileEntityTent extends TileEntity implements IUpdatePlayerListBox{

    private boolean spawn = false;
    private SimpleSpawnerLogic spawnerLogic = new SimpleSpawnerLogic() {


        @Override
        protected void onReset() {
            TileEntityTent.this.worldObj.addBlockEvent(getSpawnerPos(), ModBlocks.blockMainTent, 1, 0);
        }

        @Override
        public World getSpawnerWorld() {
            return TileEntityTent.this.worldObj;
        }

        public BlockPos getSpawnerPos(){
            return TileEntityTent.this.getPos();
        }

        @Override
        public int getSpawnerX() {
            return TileEntityTent.this.pos.getX();
        }

        @Override
        public int getSpawnerY() {
            return TileEntityTent.this.pos.getY();
        }

        @Override
        public int getSpawnerZ() {
            return TileEntityTent.this.pos.getZ();
        }

        @Override
        protected void onSpawned(Entity e) {
            super.onSpawned(e);
            if (e instanceof EntityVampireHunter) {
                ((EntityVampireHunter) e).setCampArea(getSpawningBox());
            }


        }
    };

    public TileEntityTent() {
        spawnerLogic.setEntityName(REFERENCE.ENTITY.VAMPIRE_HUNTER_NAME);
        spawnerLogic.setActivateRange(64);
        spawnerLogic.setSpawnRange(6);
        spawnerLogic.setMinSpawnDelay(400);
        spawnerLogic.setMaxSpawnDelay(800);
    }

    @Override
    public void update() {
        if (spawn) {
            spawnerLogic.updateSpawner();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        spawnerLogic.writeToNbt(nbt);
        nbt.setBoolean("spawn", spawn);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        spawnerLogic.readFromNbt(nbt);
        spawn = nbt.getBoolean("spawn");
    }


    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.pos, 1, nbttagcompound);
    }

    public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_) {
        return this.spawnerLogic.setDelayToMin(p_145842_1_) ? true : super.receiveClientEvent(p_145842_1_, p_145842_2_);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(1, 0, 1);
    }

    public boolean onActivated(EntityPlayer player) {
        return false;
    }

    public void markAsSpawner() {
        spawn = true;
    }
}
