package de.teamlapen.vampirism.entity.player.skills;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class TeleportSkill extends DefaultSkill {

	@Override
	public boolean canBeUsedBy(VampirePlayer vampire, EntityPlayer player) {
		return vampire.isVampireLord();
	}

	@Override
	public int getCooldown() {
		return BALANCE.VP_SKILLS.TELEPORT_COOLDOWN * 20;
	}

	@Override
	public int getMinLevel() {
		return BALANCE.VP_SKILLS.TELEPORT_MIN_LEVEL;
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
	public boolean onActivated(VampirePlayer vampire, EntityPlayer player) {
		MovingObjectPosition pos = Helper.getPlayerLookingSpot(player, BALANCE.VP_SKILLS.TELEPORT_MAX_DISTANCE);
		double ox = player.posX;
		double oy = player.posY;
		double oz = player.posZ;
		if (pos == null) {
			player.worldObj.playSoundAtEntity(player, "note.bass", 1.0F, 1.0F);
			return false;// TODO make something else
		}
		BlockPos target=pos.getBlockPos().up();
		player.setPosition(target.getX(),target.getY(),target.getZ());
		boolean flag = false;
		if (player.worldObj.isBlockLoaded(target)) {
			boolean flag1 = false;

			while (!flag1 && target.getY() > 0) {
				Block block = player.worldObj.getBlockState(target.down()).getBlock();
				if (block.getMaterial().blocksMovement())
					flag1 = true;
				else {
					--player.posY;
					target=target.down();
				}
			}

			if (flag1) {
				player.setPosition(target.getX(),target.getY(),target.getZ());

				if (player.worldObj.getCollidingBoundingBoxes(player, player.getEntityBoundingBox()).isEmpty() && !player.worldObj.isAnyLiquid(player.getEntityBoundingBox())) {
					flag = true;
				} else {
					Logger.d("debug", "CollidingBox not empty or liquid");
				}

			} else {
			}
		}

		if (!flag) {
			player.setPosition(ox, oy, oz);
			player.playSound("note.bd", 1.0F, 1.0F);
			return false;
		}
		if (player instanceof EntityPlayerMP) {
			EntityPlayerMP playerMp = (EntityPlayerMP) player;
			playerMp.mountEntity(null);
			playerMp.setPositionAndUpdate(target.getX(),target.getY(),target.getZ());
		}
		player.worldObj.playSoundEffect(ox, oy, oz, "mob.endermen.portal", 1.0F, 1.0F);
		player.playSound("mob.endermen.portal", 1.0F, 1.0F);
		return true;
	}

}
