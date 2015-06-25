package de.teamlapen.vampirism.entity.ai;

import java.util.Random;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import de.teamlapen.vampirism.entity.DefaultVampire;


public class EntityAIFleeSun extends EntityAIBase {

	protected final DefaultVampire vampire;
	private final double speed;
	private final World world;
	private double shelterX;
	private double shelterY;
	private double shelterZ;
	
	private boolean restrictToHome;
	
	public EntityAIFleeSun(DefaultVampire vampire, double speed) {
		this(vampire,speed,false);
	}
	
	/**
	 * 
	 * @param vampire
	 * @param speed
	 * @param restrictToHome If the entitys home should be respected, if there is one.
	 */
	public EntityAIFleeSun(DefaultVampire vampire, double speed,boolean restrictToHome) {
		this.vampire=vampire;
		this.speed=speed;
		this.world=vampire.worldObj;
		this.setMutexBits(1);
		this.restrictToHome=restrictToHome;
	}
	
	public boolean shouldExecute()
    {
		if(vampire.isGettingSundamage()){
			Vec3 vec3 = this.findPossibleShelter();

            if (vec3 == null)
            {
                return false;
            }
            else
            {
                this.shelterX = vec3.xCoord;
                this.shelterY = vec3.yCoord;
                this.shelterZ = vec3.zCoord;
                return true;
            }
		}
		return false;
    }
	
	public void startExecuting()
    {
        vampire.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.speed);
    }
	
    public boolean continueExecuting()
    {
        return !this.vampire.getNavigator().noPath();
    }
    
    protected Vec3 findPossibleShelter()
    {
        Random random = vampire.getRNG();

        for (int i = 0; i < 10; ++i)
        {
            int x = MathHelper.floor_double(vampire.posX + (double)random.nextInt(20) - 10.0D);
            int y = MathHelper.floor_double(this.vampire.boundingBox.minY + (double)random.nextInt(6) - 3.0D);
            int z = MathHelper.floor_double(this.vampire.posZ + (double)random.nextInt(20) - 10.0D);

            if (!this.world.canBlockSeeTheSky(x, y, z))
            {
            	if(restrictToHome&&vampire.hasHome()){
            		if(!vampire.isWithinHomeDistance(x, y, z)){
            			continue;
            		}
            	}
                return Vec3.createVectorHelper((double)x, (double)y, (double)z);
            }
        }

        return null;
    }

}
