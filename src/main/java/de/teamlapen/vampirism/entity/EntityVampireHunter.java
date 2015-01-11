package de.teamlapen.vampirism.entity;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.MobProperties;

public class EntityVampireHunter extends MobVampirism {

	public boolean isLookingForHome;

	public EntityVampireHunter(World p_i1738_1_) {
		super(p_i1738_1_);

		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityVampire.class, 2 * MobProperties.vampireHunter_movementSpeed, false));
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 2 * MobProperties.vampireHunter_movementSpeed, false));
		this.tasks.addTask(2, new EntityAIAttackOnCollide(this,EntityCreature.class,1*MobProperties.vampireHunter_movementSpeed,false));

		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true,false, new IEntitySelector(){

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if(entity instanceof EntityPlayer){
					return VampirePlayer.get((EntityPlayer) entity).getLevel()>BALANCE.VAMPIRE_HUNTER_ATTACK_LEVEL;
				}
				return false;
			}

		}));
		this.targetTasks.addTask(3,new EntityAINearestAttackableTarget(this,EntityVampire.class,0,true));
		this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityCreature.class,0,true,false,new IEntitySelector(){

			@Override
			public boolean isEntityApplicable(Entity entity) {
				if(entity instanceof EntityCreature){
					return VampireMob.get((EntityCreature)entity).isVampire();
				}
				return false;
			}
			
		}));

		// Spawn in village biomes only
		EntityRegistry.addSpawn(EntityVampireHunter.class, 2, 1, 3, EnumCreatureType.monster, 
				new BiomeGenBase[] { BiomeGenBase.desert, BiomeGenBase.plains, BiomeGenBase.savanna });
		isLookingForHome = true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(MobProperties.vampire_maxHealth);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(MobProperties.vampireHunter_attackDamage);
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(MobProperties.vampireHunter_movementSpeed);
	}

	@Override
	protected boolean canDespawn() {
		return false; // was true, false keeps it from despawning when player is far away
	}

	@Override
	protected Item getDropItem() {
		return null;
	}

	@Override
	public void onDeath(DamageSource s) {
	}

	@Override
	protected void onDeathUpdate() {
		this.setInvisible(true);
		this.worldObj.spawnParticle("depthsuspend", posX, posY, posZ, 0.5F, 0.5F, 0.5F);
		this.worldObj.spawnParticle("mobSpellAmbient", posX, posY, posZ, 0.5F, 0.5F, 0.5F);
		super.onDeathUpdate();
	}

    /**
     * From EntityMob
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
		// don't spawn underground or on peaceful mode, also normal checks, allows spawn in daylight
		if (this.posY < 60 || this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL || !super.getCanSpawnHere())
			return false;
		
		// Will be set false if spawned by WorldGenVampirism
		if (!this.isLookingForHome)
			return true;
		
		Village v = this.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.posX), 
		MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 40);
		if (v == null) {
		//	Logger.i("Test", "No village found at: " + this.posX + " " + this.posY + " " + this.posZ); 
			return false;
		}
		int r = v.getVillageRadius();
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(v.getCenter().posX - r, 0, v.getCenter().posZ - r, v.getCenter().posX + r,
				this.worldObj.getActualHeight(), v.getCenter().posZ + r);
		int spawnedHunter = this.worldObj.getEntitiesWithinAABB(EntityVampireHunter.class, box).size();
		if (spawnedHunter < MobProperties.vampireHunter_maxPerVillage) {
			Logger.i("Test", "Vampire Hunter trying to spawn, found village at: " + v.getCenter().posX + " " + v.getCenter().posY 
					+ " " + v.getCenter().posZ + " with " + spawnedHunter + " Hunters");
		    ChunkCoordinates chunkcoordinates = v.getCenter();
		    this.setHomeArea(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, r);
		    this.isLookingForHome = false;
			Logger.i("Test", "Spawning Vampire Hunter at: " + this.posX + " " + this.posY + " " + this.posZ);
			return true;
		}
		else
			return false;				
    }	
}
