package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.minions.IMinion;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;


public class VanillaAIModifier {

	public static void makeMinion(IMinion minion, EntityCreature entity) {
		EntityAITasks tasks = entity.tasks;
		tasks.taskEntries.clear();
		tasks.addTask(0, new EntityAISwimming(entity));
		tasks.addTask(2, new net.minecraft.entity.ai.EntityAIAttackOnCollide(entity, EntityLivingBase.class, 1.0D, false));
		tasks.addTask(7, new MinionAIFollowBoss(minion, 1.0D));
		tasks.addTask(16, new EntityAIWatchClosest(entity, EntityPlayer.class, 10));

		EntityAITasks targetTasks = entity.targetTasks;
		targetTasks.taskEntries.clear();
		targetTasks.addTask(8, new EntityAIHurtByTarget(entity, false));
	}

}
