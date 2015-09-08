package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.EntityVampireBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

import java.util.Random;

public class VampireAIFleeSun extends EntityAIBase {

	protected final EntityVampireBase vampire;
	private final double speed;
	private final World world;
	private BlockPos shelter;

	private boolean restrictToHome;

	public VampireAIFleeSun(EntityVampireBase vampire, double speed) {
		this(vampire, speed, false);
	}

	/**
	 * 
	 * @param vampire
	 * @param speed
	 * @param restrictToHome
	 *            If the entitys home should be respected, if there is one.
	 */
	public VampireAIFleeSun(EntityVampireBase vampire, double speed, boolean restrictToHome) {
		this.vampire = vampire;
		this.speed = speed;
		this.world = vampire.worldObj;
		this.setMutexBits(1);
		this.restrictToHome = restrictToHome;
	}

	@Override
	public boolean continueExecuting() {
		return !this.vampire.getNavigator().noPath();
	}

	protected Vec3i findPossibleShelter() {
		Random random = vampire.getRNG();

		for (int i = 0; i < 10; ++i) {
			int x = MathHelper.floor_double(vampire.posX + random.nextInt(20) - 10.0D);
			int y = MathHelper.floor_double(this.vampire.getEntityBoundingBox().minY + random.nextInt(6) - 3.0D);
			int z = MathHelper.floor_double(this.vampire.posZ + random.nextInt(20) - 10.0D);
			BlockPos pos=new BlockPos(x,y,z);
			if (!this.world.canBlockSeeSky(pos)) {
				if (restrictToHome && vampire.hasHome()) {
					if (!vampire.isWithinHomeDistance(pos)) {
						continue;
					}
				}
				return pos;
			}
		}

		return null;
	}

	@Override
	public boolean shouldExecute() {
		if (vampire.isGettingSundamage()) {
			Vec3i vec3 = this.findPossibleShelter();

			if (vec3 == null) {
				return false;
			} else {
				shelter=new BlockPos(vec3);
				return true;
			}
		}
		return false;
	}

	@Override
	public void startExecuting() {
		 PathEntity path=vampire.getNavigator().func_179680_a(shelter);
		vampire.getNavigator().setPath(path,speed);
	}

}
