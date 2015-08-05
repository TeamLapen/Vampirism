package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.entity.minions.EntityRemoteVampireMinion;
import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.minions.IMinionLord;
import de.teamlapen.vampirism.entity.minions.MinionHelper;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Makes the minion move to its lord, if a condition specified by child classes is met. If the child class allows it, it teleports to the lord, if it cannot find a path and is further aways than
 * {@value #TELEPORT_SQ_DISTANCE}
 * 
 * @author Maxanier
 *
 */
public class MinionAIMoveToLord extends EntityAIBase {

	/**
	 * Minion moves to its lord if either the held bottle is full or no item is equipped
	 * 
	 * @author Maxanier
	 *
	 */
	public static class MinionAIBringBottle extends MinionAIMoveToLord {

		public MinionAIBringBottle(EntityRemoteVampireMinion minion) {
			super(minion);
		}

		@Override
		public boolean canTeleport() {
			return true;
		}

		@Override
		protected boolean shouldMove(IMinion m) {
			ItemStack stack = m.getRepresentingEntity().getEquipmentInSlot(0);
			if (stack != null && stack.getItem().equals(ModItems.bloodBottle)) {
				return ItemBloodBottle.getBlood(stack) == ItemBloodBottle.MAX_BLOOD;
			}
			return stack == null;
		}

	}
	public final static int TELEPORT_SQ_DISTANCE = 25;
	private final IMinion minion;
	private EntityLivingBase lord;
	private boolean avoidWater;

	private int updateTicks;

	public MinionAIMoveToLord(IMinion m) {
		this.minion = m;
		this.setMutexBits(1);
	}

	protected boolean canTeleport() {
		return false;
	}

	@Override
	public boolean continueExecuting() {
		if (!canTeleport() && minion.getRepresentingEntity().getNavigator().noPath()) {
			return false;
		}
		if (minion.getRepresentingEntity().getDistanceSqToEntity(lord) < TELEPORT_SQ_DISTANCE)
			return false;
		return (!lord.isDead && MinionHelper.isLordSafe(minion, lord));
	}

	@Override
	public void resetTask() {
		lord = null;
		minion.getRepresentingEntity().getNavigator().setAvoidsWater(avoidWater);
	}

	@Override
	public boolean shouldExecute() {
		IMinionLord l = minion.getLord();
		if (l != null && minion.getRepresentingEntity().getDistanceSqToEntity(l.getRepresentingEntity()) > TELEPORT_SQ_DISTANCE && shouldMove(minion)) {
			lord = l.getRepresentingEntity();
			return true;
		}
		return false;
	}

	protected boolean shouldMove(IMinion m) {
		return true;
	}

	@Override
	public void startExecuting() {
		minion.getRepresentingEntity().getNavigator().tryMoveToEntityLiving(lord, 1.0);
		avoidWater = minion.getRepresentingEntity().getNavigator().getAvoidsWater();
		minion.getRepresentingEntity().getNavigator().setAvoidsWater(false);
	}

	@Override
	public void updateTask() {
		minion.getRepresentingEntity().getLookHelper().setLookPositionWithEntity(this.lord, 10.0F, minion.getRepresentingEntity().getVerticalFaceSpeed());
		if (--this.updateTicks <= 0) {
			this.updateTicks = 10;
			if (!minion.getRepresentingEntity().getNavigator().tryMoveToEntityLiving(lord, 1.0)) {
				if (this.minion.getRepresentingEntity().getDistanceSqToEntity(lord) > TELEPORT_SQ_DISTANCE) {
					int x = MathHelper.floor_double(lord.posX) - 2;
					int z = MathHelper.floor_double(lord.posZ) - 2;
					int y = MathHelper.floor_double(lord.boundingBox.minY);

					for (int dx = 0; dx <= 4; ++dx) {
						for (int dz = 0; dz <= 4; ++dz) {
							if ((dx < 1 || dz < 1 || dx > 3 || dz > 3) && World.doesBlockHaveSolidTopSurface(lord.worldObj, x + dx, y - 1, z + dz)
									&& !lord.worldObj.getBlock(x + dx, y, z + dz).isNormalCube() && !lord.worldObj.getBlock(x + dx, y + 1, z + dz).isNormalCube()) {
								minion.getRepresentingEntity().setLocationAndAngles(x + dx + 0.5F, y, z + dz + 0.5F,
										MathHelper.wrapAngleTo180_float(lord.rotationYaw + 180F), MathHelper.wrapAngleTo180_float(lord.rotationPitch + 180F));
								minion.getRepresentingEntity().getNavigator().clearPathEntity();
								return;
							}
						}
					}
				}
			}
		}
	}

}
