package de.teamlapen.vampirism.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityDeadMob extends EntityTNTPrimed {

	protected String entity;
	
	private String TAG="EntityDeadMob";
	
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
		entity=nbt.getString("entity_id");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setString("entity_id", entity);
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
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
            this.motionY *= -0.5D;
        }
    }
    
    public void setDeadMobId(String id){
    	entity=id;
    }
    
    public void convertToMob(){
    	Entity e=EntityList.createEntityByName(entity, worldObj);
    	if(e!=null){
    		
    	}
    }

}
