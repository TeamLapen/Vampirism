package de.teamlapen.vampirism.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityRemoteVampireMinion extends EntityVampireMinion {

	public EntityRemoteVampireMinion(World world) {
		super(world);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean shouldBeSavedWithLord() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
