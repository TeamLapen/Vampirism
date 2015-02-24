package de.teamlapen.vampirism.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;

public class VampireEntityEventHandler {

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityCreature && VampireMob.get((EntityCreature) event.entity) == null) {
			VampireMob.register((EntityCreature) event.entity);
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityVampireHunter) {
			// Set the home position of VampireHunters to a near village if one
			// is found
			EntityVampireHunter e = (EntityVampireHunter) event.entity;
			if (e.isLookingForHome() == false)
				return;

			Village v = event.world.villageCollectionObj.findNearestVillage(MathHelper.floor_double(e.posX), MathHelper.floor_double(e.posY),
					MathHelper.floor_double(e.posZ), 20);
			if (v != null) {
				int r = v.getVillageRadius();
				AxisAlignedBB box = AxisAlignedBB.getBoundingBox(v.getCenter().posX - r, 0, v.getCenter().posZ - r, v.getCenter().posX + r,
						event.world.getActualHeight(), v.getCenter().posZ + r);
				ChunkCoordinates cc = v.getCenter();
				e.setHomeArea(cc.posX, cc.posY, cc.posZ, r);
			}
		} else if (event.entity instanceof EntityIronGolem) {
			// Replace the EntityAINearestAttackableTarget of Irongolems, so
			// they do not attack VampireHunters
			EntityIronGolem golem = (EntityIronGolem) event.entity;
			EntityAITasks targetTasks = (EntityAITasks) Helper.Reflection.getPrivateFinalField(EntityLiving.class, golem, Helper.Obfuscation.getPosNames("EntityLiving/targetTasks"));
			if (targetTasks == null) {
				Logger.w("VampireEntityEventHandler", "Cannot change the target tasks of irongolem");
			} else {
				for (Object o : targetTasks.taskEntries) {
					EntityAIBase t = ((EntityAITasks.EntityAITaskEntry) o).action;
					if (t instanceof EntityAINearestAttackableTarget) {
						targetTasks.removeTask(t);
						targetTasks.addTask(3, new EntityAINearestAttackableTarget(golem, EntityLiving.class, 0, false, true, new IEntitySelector() {

							@Override
							public boolean isEntityApplicable(Entity entity) {
								if (entity instanceof IMob && !(entity instanceof EntityVampireHunter)) {
									return true;
								}
								return false;
							}

						}));
						break;
					}
				}
			}
		}
		else if(event.entity instanceof EntityCreeper){
			EntityCreeper creeper=(EntityCreeper)event.entity;
			EntityAITasks tasks=(EntityAITasks) Helper.Reflection.getPrivateFinalField(EntityLiving.class,(EntityLiving)creeper,Helper.Obfuscation.getPosNames("EntityLiving/tasks"));
			if(tasks==null){
				Logger.w("VampireEntityEventHandler","Cannot change the target tasks of creeper");
			}
			else{
				tasks.addTask(3, new EntityAIAvoidVampirePlayer(creeper,12.0F,1.0D,1.2D,BALANCE.VAMPIRE_PLAYER_CREEPER_AVOID_LEVEL));
			}
		}
	}

}
