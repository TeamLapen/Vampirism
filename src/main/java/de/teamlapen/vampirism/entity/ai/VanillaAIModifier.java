package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;


public class VanillaAIModifier {

	public static void addVampireMobTasks(EntityCreature entity) {
		EntityAITasks tasks = entity.tasks;
		// Attack player
		tasks.addTask(1, new net.minecraft.entity.ai.EntityAIAttackOnCollide(entity, EntityPlayer.class, 1.0D, false));
		// Attack vampire hunter
		tasks.addTask(1, new net.minecraft.entity.ai.EntityAIAttackOnCollide(entity, EntityVampireHunter.class, 1.0D, true));

		EntityAITasks targetTasks = entity.targetTasks;
		targetTasks.addTask(3, new EntityAINearestAttackableTarget(entity, EntityPlayer.class, 0, true, false, new IEntitySelector() {

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if (entity instanceof EntityPlayer) {
					return VampirePlayer.get((EntityPlayer) entity).getLevel() <= 0;
				}
				return false;
			}

		}));
		// Search for vampire hunters
		targetTasks.addTask(3, new EntityAINearestAttackableTarget(entity, EntityVampireHunter.class, 0, true));
	}

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
