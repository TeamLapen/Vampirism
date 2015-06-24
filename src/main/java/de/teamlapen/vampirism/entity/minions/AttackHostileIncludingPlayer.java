package de.teamlapen.vampirism.entity.minions;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import de.teamlapen.vampirism.entity.EntityVampire;
import de.teamlapen.vampirism.entity.player.VampirePlayer;

/**
 * Attacks EntityMobs and EntityPlayers.
 * Only attacks vampires, if his lord is a vampire lord
 * @author Maxanier
 *
 */
public class AttackHostileIncludingPlayer extends DefaultMinionCommand {

	protected final IMinion minion;
	protected final EntityAITarget attack;
	
	public AttackHostileIncludingPlayer(int id,IMinion minion) {
		super(id);
		this.minion=minion;
		attack=new EntityAINearestAttackableTarget(minion.getRepresentingEntity(),EntityLivingBase.class,0,true,false,new IEntitySelector(){

			@Override
			public boolean isEntityApplicable(Entity entity) {
				IMinion m=MinionHelper.getMinionFromEntity(entity);
				if(MinionHelper.isLordSafe(m, AttackHostileIncludingPlayer.this.minion.getLord()))return false;
				if(entity instanceof EntityVampire){
					IMinionLord l=AttackHostileIncludingPlayer.this.minion.getLord();
					if(l!=null&&l instanceof VampirePlayer&&((VampirePlayer)l).isVampireLord()){
						return true;
					}
					return false;
				}
				if(entity instanceof EntityMob||entity instanceof EntityPlayer){
					return true;
				}
				return false;
			}
			
		});
	}

	@Override
	public String getUnlocalizedName() {
		return "minioncommand.vampirism.attackhostile";
	}

	@Override
	public void onActivated() {
		minion.getRepresentingEntity().targetTasks.addTask(3, attack);

	}

	@Override
	public void onDeactivated() {
		minion.getRepresentingEntity().targetTasks.removeTask(attack);

	}

	@Override
	public int getMinU() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinV() {
		// TODO Auto-generated method stub
		return 0;
	}

}
