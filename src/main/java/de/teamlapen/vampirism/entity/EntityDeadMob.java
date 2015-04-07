package de.teamlapen.vampirism.entity;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityDeadMob extends Entity {

	private static final int MAX_TICKS = 1200;

	protected String entityId="Zombie";
	
	private String TAG="EntityDeadMob";
	
	public static List<String> mobs;
	
	static{
		mobs=new ArrayList<String>();
		mobs.add("Zombie");
		mobs.add("Skeleton");
		mobs.add(REFERENCE.ENTITY.GHOST_NAME);
	}
	
	public EntityDeadMob(World p_i1582_1_) {
		super(p_i1582_1_);
		this.setSize(0.98F, 0.4F);
//        this.yOffset = this.height / 2.0F;
	}

	@Override
	protected void entityInit() {

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		entityId=nbt.getString("entity_id");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setString("entity_id", entityId);
	}
	
    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }
    
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }
    
    @Override
    public void onUpdate(){
    	super.onUpdate();
    	
    	if(this.ticksExisted>MAX_TICKS){
    		this.setDead();
    	}
    	this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.03999999910593033D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.onGround)
        {
            this.motionX *= 0.1D;
            this.motionZ *= 0.1D;
            this.motionY *= 0.1D;
        }
    }
    
    public void setDeadMobId(String id){
    	entityId=id;
    }
    
    public EntityCreature convertToMob(){
    	EntityCreature e=(EntityCreature) EntityList.createEntityByName(entityId, worldObj);
    	if(e!=null){
    		e.copyLocationAndAnglesFrom(this);
    		worldObj.spawnEntityInWorld(e);
    	}
    	else{
    		Logger.w(TAG, "Could not create entity: "+entityId);
    	}
    	this.setDead();
    	return e;
    }
    
    public static boolean canBecomeDeadMob(EntityCreature entity){
    	if(entity!=null&&VampireMob.get(entity).isMinion())return false;
    	return mobs.contains(EntityList.getEntityString(entity));
    }
    
    public static Entity createFromEntity(EntityCreature entity){
    	EntityDeadMob e=(EntityDeadMob) EntityList.createEntityByName(REFERENCE.ENTITY.DEAD_MOB_NAME, entity.worldObj);
		e.copyLocationAndAnglesFrom(entity);
		e.setDeadMobId(EntityList.getEntityString(entity));
		return e;
    }

}
