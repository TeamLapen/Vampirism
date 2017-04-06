package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.EntityAreaParticleCloud;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
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
        return "action.vampirism.vampire.teleport";
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
            player.playSound(SoundEvents.BLOCK_NOTE_BASS, 1, 1);
            return false;
        }
        BlockPos pos = null;
        if (player.getEntityWorld().getBlockState(target.getBlockPos()).getMaterial().blocksMovement()) {
            pos = target.getBlockPos().up();
        }

        if (pos != null) {
            player.setPosition(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
            if (player.getEntityWorld().getCollisionBoxes(player.getEntityBoundingBox()).isEmpty() && !player.getEntityWorld().containsAnyLiquid(player.getEntityBoundingBox())) {

            } else {
                pos = null;
            }

        }


        if (pos == null) {
            player.setPosition(ox, oy, oz);
            player.playSound(SoundEvents.BLOCK_NOTE_BASEDRUM, 1, 1);
            return false;
        }
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMp = (EntityPlayerMP) player;
            playerMp.mountEntityAndWakeUp();
            playerMp.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
        }
        EntityAreaParticleCloud particleCloud = new EntityAreaParticleCloud(player.getEntityWorld());
        particleCloud.setPosition(ox, oy, oz);
        particleCloud.setRadius(0.7F);
        particleCloud.setHeight(player.height);
        particleCloud.setDuration(5);
        particleCloud.setSpawnRate(15);
        player.getEntityWorld().spawnEntity(particleCloud);
        player.getEntityWorld().playSound(ox, oy, oz, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
        player.getEntityWorld().playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1, 1, false);
        return true;
    }
}
