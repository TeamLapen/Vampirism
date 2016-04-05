package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;


public class TeleportVampireAction extends DefaultVampireAction {


    public TeleportVampireAction() {
        super(null);
    }

    @Override
    public boolean canBeUsedBy(IVampirePlayer vampire) {
        return !vampire.getActionHandler().isActionActive(VampireActions.batAction);
    }

    @Override
    public int getCooldown() {
        return Balance.vpa.TELEPORT_COOLDOWN * 20;
    }

    @Override
    public int getMinU() {
        return 112;
    }

    @Override
    public int getMinV() {
        return 0;
    }

    @Override
    public String getUnlocalizedName() {
        return "skill.vampirism.teleport";
    }

    @Override
    public boolean isEnabled() {
        return Balance.vpa.TELEPORT_ENABLED;
    }

    @Override
    public boolean onActivated(IVampirePlayer vampire) {
        EntityPlayer player = vampire.getRepresentingPlayer();
        RayTraceResult target = UtilLib.getPlayerLookingSpot(player, Balance.vpa.TELEPORT_MAX_DISTANCE);
        double ox = player.posX;
        double oy = player.posY;
        double oz = player.posZ;
        if (target == null) {
            player.worldObj.playSoundAtEntity(player, "note.bass", 1.0F, 1.0F);
            return false;
        }
        BlockPos pos = null;
        if (player.worldObj.getBlockState(target.getBlockPos()).getBlock().getMaterial().blocksMovement()) {
            pos = target.getBlockPos().up();
        }

        if (pos != null) {
            player.setPosition(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);

            if (player.worldObj.getCollidingBoundingBoxes(player, player.getEntityBoundingBox()).isEmpty() && !player.worldObj.isAnyLiquid(player.getEntityBoundingBox())) {

            } else {
                pos = null;
            }

        }


        if (pos == null) {
            player.setPosition(ox, oy, oz);
            player.worldObj.playSoundAtEntity(player, "note.bd", 1.0F, 1.0F);
            return false;
        }
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMp = (EntityPlayerMP) player;
            playerMp.mountEntity(null);
            playerMp.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
        }
        player.worldObj.playSoundEffect(ox, oy, oz, "mob.endermen.portal", 1.0F, 1.0F);
        player.worldObj.playSoundAtEntity(player, "mob.endermen.portal", 1.0F, 1.0F);
        return true;
    }
}
