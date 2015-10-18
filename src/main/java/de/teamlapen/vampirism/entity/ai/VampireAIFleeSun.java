package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.EntityVampireBase;
import net.minecraft.world.World;

/**
 * Vampire AI to flee the sun
 */
public class VampireAIFleeSun extends EntityAIFlee {

	protected final EntityVampireBase vampire;


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
		super(vampire, speed, restrictToHome);
		this.vampire = vampire;

	}


	@Override
	protected boolean isPositionAcceptable(World world, int x, int y, int z) {
		return !world.canBlockSeeTheSky(x, y, z);
	}

	@Override
	protected boolean shouldFlee() {
		return vampire.isGettingSundamage();
	}


}
