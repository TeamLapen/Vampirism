package de.teamlapen.vampirism.entity.player.skills;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import de.teamlapen.vampirism.entity.EntityDeadMob;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;

public class ReviveFallenSkill extends DefaultSkill {

	@Override
	public int getCooldown() {
		return BALANCE.VP_SKILLS.REVIVE_FALLEN_COOLDOWN * 20;
	}

	@Override
	public int getMinLevel() {
		return BALANCE.VP_SKILLS.REVIVE_FALLEN_MIN_LEVEL;
	}

	@Override
	public int getMinU() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinV() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getUnlocalizedName() {
		return "skill.vampirism.revive_fallen";
	}

	@Override
	public boolean onActivated(VampirePlayer vampire, EntityPlayer player) {
		int max = vampire.getMinionsLeft(true);
		if (max == 0)
			return false;
		for (Object o : player.worldObj.getEntitiesWithinAABB(EntityDeadMob.class, player.boundingBox.expand(10, 10, 10))) {
			EntityCreature e = ((EntityDeadMob)o).convertToMob();
			if (e != null) {
				VampireMob.get(e).makeMinion(vampire);
				if (--max == 0)
					break;

			}

		}
		return true;
	}

}
