package de.teamlapen.vampirism.entity.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;

/**
 * Task to (frequently) send messages to a near player
 */
public class EntityAIWarnPlayer extends EntityAIBase {

	protected EntityLiving entityLiving;
	protected int distance;
	protected int perTick=1;
	protected String unLoc;
	private EntityPlayer closestEntity;
	private IEntitySelector selector;
	private int tick;
	public EntityAIWarnPlayer(EntityLiving entity,int distance,String unlocText,int perTick){
		this(entity, distance, unlocText, perTick,null);
	}
	public EntityAIWarnPlayer(EntityLiving entity,int distance,String unlocText,int perTick,IEntitySelector selector){
		this.distance=distance;
		this.perTick=perTick;
		this.entityLiving=entity;
		this.unLoc=unlocText;
		this.selector=selector;
	}
	@Override public boolean shouldExecute() {
		this.closestEntity = entityLiving.worldObj.getClosestPlayerToEntity(entityLiving, (double)distance);
		if(closestEntity!=null&&selector!=null){
			return selector.isEntityApplicable(closestEntity);
		}
		return closestEntity!=null;
	}


	@Override public void startExecuting() {
		tick=0;
	}

	@Override public void updateTask() {
		if(perTick==0||tick++%perTick==0){
			closestEntity.addChatComponentMessage(new ChatComponentTranslation(unLoc));
		}
	}
}
