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
 * Makes the minion move to its lord, if a condition specified by child classes is met.
 * If the child class allows it, it teleports to the lord, if it cannot find a path and is further aways than {@value #TELEPORT_SQ_DISTANCE}
 * @author Maxanier
 *
 */
public class EntityAIMoveToLord extends EntityAIBase {

	private final IMinion minion;
	private EntityLivingBase lord;
	private boolean avoidWater;
	private int updateTicks;
	public final static int TELEPORT_SQ_DISTANCE=25;
	
	public EntityAIMoveToLord(IMinion m){
		this.minion=m;
		this.setMutexBits(1);
	}
	
	@Override
	public boolean shouldExecute() {
		IMinionLord l =minion.getLord();
		if(l!=null&&minion.getRepresentingEntity().getDistanceSqToEntity(l.getRepresentingEntity())>TELEPORT_SQ_DISTANCE&&shouldMove(minion)){
			lord=l.getRepresentingEntity();
			return true;
		}
		return false;
	}
	
	@Override
	public void startExecuting(){
		minion.getRepresentingEntity().getNavigator().tryMoveToEntityLiving(lord, 1.0);
		avoidWater=minion.getRepresentingEntity().getNavigator().getAvoidsWater();
		minion.getRepresentingEntity().getNavigator().setAvoidsWater(false);
	}
	
	@Override
	public void resetTask(){
		lord=null;
		minion.getRepresentingEntity().getNavigator().setAvoidsWater(avoidWater);
	}
	
	@Override
	public void updateTask(){
		minion.getRepresentingEntity().getLookHelper().setLookPositionWithEntity(this.lord, 10.0F, (float)minion.getRepresentingEntity().getVerticalFaceSpeed());
		if(--this.updateTicks<=0){
			this.updateTicks=10;
			if(!minion.getRepresentingEntity().getNavigator().tryMoveToEntityLiving(lord,1.0)){
				if(this.minion.getRepresentingEntity().getDistanceSqToEntity(lord)>TELEPORT_SQ_DISTANCE){
					int x=MathHelper.floor_double(lord.posX)-2;
					int z=MathHelper.floor_double(lord.posZ)-2;
					int y=MathHelper.floor_double(lord.boundingBox.minY);
					
					for (int dx = 0; dx <= 4; ++dx)
                    {
                        for (int dz = 0; dz <= 4; ++dz)
                        {
                            if ((dx < 1 || dz < 1 || dx > 3 || dz > 3) && World.doesBlockHaveSolidTopSurface(lord.worldObj, x + dx, y - 1, z + dz) && !lord.worldObj.getBlock(x + dx, y, z + dz).isNormalCube() && !lord.worldObj.getBlock(x + dx, y + 1, z + dz).isNormalCube())
                            {
                                minion.getRepresentingEntity().setLocationAndAngles((double)((float)(x + dx) + 0.5F), (double)y, (double)((float)(z + dz) + 0.5F), MathHelper.wrapAngleTo180_float(lord.rotationYaw+180F), MathHelper.wrapAngleTo180_float(lord.rotationPitch+180F));
                                minion.getRepresentingEntity().getNavigator().clearPathEntity();
                                return;
                            }
                        }
                    }
				}
			}
		}
	}
	
	@Override
	public boolean continueExecuting(){
		if(!canTeleport()&&minion.getRepresentingEntity().getNavigator().noPath()){
			return false;
		}
		if(minion.getRepresentingEntity().getDistanceSqToEntity(lord)<TELEPORT_SQ_DISTANCE)return false;
		return (!lord.isDead&&MinionHelper.isLordSafe(minion, lord));
	}
	
	protected boolean shouldMove(IMinion m){
		return true;
	}
	
	protected boolean canTeleport(){
		return false;
	}
	
	/**
	 * Minion moves to its lord if either the held bottle is full or no item is equipped
	 * @author Maxanier
	 *
	 */
	public static class EntityAIMinionBringBottle extends EntityAIMoveToLord{

		public EntityAIMinionBringBottle(EntityRemoteVampireMinion minion) {
			super(minion);
		}

		@Override
		protected boolean shouldMove(IMinion m) {
			ItemStack stack=m.getRepresentingEntity().getEquipmentInSlot(0);
			if(stack!=null&&stack.getItem().equals(ModItems.bloodBottle)){
				return ItemBloodBottle.getBlood(stack)==ItemBloodBottle.MAX_BLOOD;
			}
			if(stack==null){
				return true;
			}
			return false;
		}
		
		@Override
		public boolean canTeleport(){
			return true;
		}
		
	}

}
