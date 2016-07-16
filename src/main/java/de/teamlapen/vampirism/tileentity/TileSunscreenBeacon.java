package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class TileSunscreenBeacon extends TileEntity implements ITickable {
    @Override
    public void update() {
        if (this.worldObj.getTotalWorldTime() % 80L == 0L) {
            this.updateBeacon();
        }
    }

    private void updateBeacon() {

        if (this.worldObj != null && !this.worldObj.isRemote) {
            int x = this.pos.getX();
            int y = this.pos.getY();
            int z = this.pos.getZ();
            int distance = 32;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double) x, 0, (double) z, (double) (x + 1), (double) (y + 1), (double) (z + 1))).expandXyz(distance).addCoord(0.0D, (double) this.worldObj.getHeight(), 0.0D);
            List<EntityPlayer> list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);

            for (EntityPlayer entityplayer : list) {
                if (VampirePlayer.get(entityplayer).getLevel() > 0) {
                    entityplayer.addPotionEffect(new PotionEffect(ModPotions.sunscreen, 160, 5, true, false));
                }
            }
        }
    }
}