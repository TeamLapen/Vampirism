package de.teamlapen.vampirism.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.World;

public class Vampire extends EntityMob {

	public Vampire(World p_i1738_1_) {
		super(p_i1738_1_);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
    }

    @Override
    protected String getLivingSound()
    {
        return "yourmod:YourSound";//this refers to:yourmod/sound/YourSound
    }

    @Override
    protected String getHurtSound()
    {
        return "yourmod:optionalFile.YourSound";//this refers to:yourmod/sound/optionalFile/YourSound
    }

    @Override
    protected String getDeathSound()
    {
        return "yourmod:optionalFile.optionalFile2.YourSound";//etc.
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.4F;
    }

}
