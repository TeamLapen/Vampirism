package de.teamlapen.vampirism.entity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.ai.EntityAIAvoidVampirePlayer;
import de.teamlapen.vampirism.generation.castle.CastlePositionData;
import de.teamlapen.vampirism.network.ISyncable;
import de.teamlapen.vampirism.network.RequestEntityUpdatePacket;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.DifficultyCalculator;
import de.teamlapen.vampirism.util.DifficultyCalculator.Difficulty;
import de.teamlapen.vampirism.util.DifficultyCalculator.IAdjustableLevel;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class VampireEntityEventHandler {

	@SubscribeEvent(receiveCanceled = true)
	public void onEntityConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityCreature && VampireMob.get((EntityCreature) event.entity) == null) {
			VampireMob.register((EntityCreature) event.entity);
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (!event.entity.worldObj.isRemote && event.entity instanceof IAdjustableLevel) {
			IAdjustableLevel e = (IAdjustableLevel) event.entity;
			if (e.getLevel() == 0) {
				Difficulty d = DifficultyCalculator.getLocalDifficulty(event.world, event.entity.posX, event.entity.posZ, 10);
				if (d.isZero()) {
					d = DifficultyCalculator.getWorldDifficulty(event.entity.worldObj);
				}
				int l = e.suggestLevel(d);
				if (l > e.getMaxLevel()) {
					l = e.getMaxLevel();
				} else if (l < 1) {
					if (event.entity.worldObj.rand.nextBoolean()) {
						event.setCanceled(true);
					}
					l = 1;
				}
				e.setLevel(l);
			}
		}
		if (event.world.isRemote) {
			if (event.entity instanceof ISyncable || event.entity instanceof EntityCreature) {
				VampirismMod.modChannel.sendToServer(new RequestEntityUpdatePacket(event.entity));
			}
		}

		if (event.entity instanceof EntityVampireHunter) {
			if (event.world.provider.dimensionId == VampirismMod.castleDimensionId) {
				event.entity.setDead();
			} else {
				// Set the home position of VampireHunters to a near village if one
				// is found
				EntityVampireHunter e = (EntityVampireHunter) event.entity;
				if (!e.isLookingForHome())
					return;

				if (event.world.villageCollectionObj != null) {
					Village v = event.world.villageCollectionObj.findNearestVillage(MathHelper.floor_double(e.posX), MathHelper.floor_double(e.posY), MathHelper.floor_double(e.posZ), 20);
					if (v != null) {
						int r = v.getVillageRadius();
						//AxisAlignedBB box = AxisAlignedBB.getBoundingBox(v.getCenter().posX - r, 0, v.getCenter().posZ - r, v.getCenter().posX + r, event.world.getActualHeight(), v.getCenter().posZ + r);
						ChunkCoordinates cc = v.getCenter();
						e.setHomeArea(cc.posX, cc.posY, cc.posZ, r);
					}
				}
			}

		} else if (event.entity instanceof EntityIronGolem) {
			// Replace the EntityAINearestAttackableTarget of Irongolems, so
			// they do not attack VampireHunters
			EntityIronGolem golem = (EntityIronGolem) event.entity;
			EntityAITasks targetTasks = golem.targetTasks;
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
								return entity instanceof IMob && !(entity instanceof EntityVampireHunter);
							}

						}));
						break;
					}
				}
			}
		} else if (event.entity instanceof EntityCreeper) {
			EntityCreeper creeper = (EntityCreeper) event.entity;
			EntityAITasks tasks = creeper.tasks;
			if (tasks == null) {
				Logger.w("VampireEntityEventHandler", "Cannot change the target tasks of creeper");
			} else {
				tasks.addTask(3, new EntityAIAvoidVampirePlayer(creeper, 12.0F, 1.0D, 1.2D, BALANCE.VAMPIRE_PLAYER_CREEPER_AVOID_LEVEL));
			}
		}
		else if(!event.world.isRemote&&event.entity instanceof EntityDracula){
			CastlePositionData.Position pos=CastlePositionData.get(event.world).findPosAt(MathHelper.floor_double(event.entity.posX),MathHelper.floor_double(event.entity.posZ),true);
			if(pos!=null){
				((EntityDracula)event.entity).makeCastleLord(pos);
			}
			else{
				Logger.w("EntityEventHandler","Dracula was spawned outside a castle");
			}
		} else if (!event.world.isRemote && event.entity instanceof EntityVampire) {
			if (CastlePositionData.get(event.world).isPosAt(MathHelper.floor_double(event.entity.posX), MathHelper.floor_double(event.entity.posZ))) {
				((EntityVampire) event.entity).makeCastleVampire();
			}
		} else if (event.entity instanceof EntityZombie) {
			try {
				((EntityZombie) event.entity).tasks.addTask(3, new EntityAIAttackOnCollide((EntityCreature) event.entity, EntityVampirism.class, 1.0F, false));
			} catch (Exception e) {
				Logger.e("EntityEventHandler", e, "Failed to add attack task to zombie %s", event.entity);
			}

		}
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event) {
		if (event.entity instanceof EntityCreature && !event.entity.worldObj.isRemote && BALANCE.DEAD_MOB_PROP > 0 && EntityDeadMob.canBecomeDeadMob((EntityCreature) event.entity)
				&& (BALANCE.DEAD_MOB_PROP == 0 || event.entity.worldObj.rand.nextInt(BALANCE.DEAD_MOB_PROP) == 0)) {
			event.entity.worldObj.spawnEntityInWorld(EntityDeadMob.createFromEntity((EntityCreature) event.entity));
		}
	}

	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent e) {
		if (e.entityLiving instanceof EntityCreature) {
			VampireMob mob = VampireMob.get((EntityCreature) e.entityLiving);
			if (mob.max_blood > 0 && mob.getBlood() < mob.max_blood / 3) {
				for (EntityItem i : e.drops) {
					ItemStack s = i.getEntityItem();
					if (s.getItem().equals(Items.porkchop) || s.getItem().equals(Items.beef)) {
						i.setEntityItemStack(new ItemStack(Items.rotten_flesh, s.stackSize));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		if (event.entity instanceof EntityCreature) {
			VampireMob.get((EntityCreature) event.entity).onUpdate();
		}
	}

}
