package de.teamlapen.vampirism.entity.player.skills;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import de.teamlapen.vampirism.entity.EntityVampireMinion;
import de.teamlapen.vampirism.entity.ai.IMinion;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.REFERENCE;

public class VampireLordSkill extends DefaultSkill implements ILastingSkill {

	/**
	 * Skill ID, has to be set when this is registered
	 */
	public static int ID;

	@Override
	public int getCooldown() {
		return BALANCE.VP_SKILLS.LORD_COOLDOWN * 20;
	}

	@Override
	public int getDuration(int level) {
		return BALANCE.VP_SKILLS.getVampireLordDuration(level);
	}

	@Override
	public int getMinLevel() {
		return BALANCE.VP_SKILLS.LORD_MIN_LEVEL;
	}

	@Override
	public int getMinU() {
		return 32;
	}

	@Override
	public int getMinV() {
		return 0;
	}

	@Override
	public void onActivated(VampirePlayer vampire, EntityPlayer player) {
		player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, getDuration(vampire.getLevel()), 2));
		IMinion m = (IMinion) Helper.spawnEntityInWorld(player.worldObj, player.boundingBox.expand(2, 1, 2), REFERENCE.ENTITY.VAMPIRE_MINION_NAME, 9);
		if (m != null) {
			m.setLord(vampire);
		}
		
	}

	@Override
	public void onDeactivated(VampirePlayer vampire, EntityPlayer player) {
		player.removePotionEffect(Potion.moveSpeed.id);
		for(EntityVampireMinion e:(List<EntityVampireMinion>)player.worldObj.getEntitiesWithinAABB(EntityVampireMinion.class, player.boundingBox.expand(22, 17, 22))){
			if(vampire.equals(e.getLord())){
				short short1 = 128;
				for (int l = 0; l < short1; ++l) {
					double d6 = l / (short1 - 1.0D);
					float f = (e.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
					float f1 = (e.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
					float f2 = (e.worldObj.rand.nextFloat() - 0.5F) * 0.2F;
					double d7 = e.posX + (50) * d6 + (e.worldObj.rand.nextDouble() - 0.5D) * e.width * 2.0D;
					double d8 = e.posY + (10) * d6 + e.worldObj.rand.nextDouble() * e.height;
					double d9 = e.posZ + (50) * d6 + (e.worldObj.rand.nextDouble() - 0.5D) * e.width * 2.0D;
					e.worldObj.spawnParticle("portal", d7, d8, d9, f, f1, f2);
				}

				e.worldObj.playSoundEffect(e.posX, e.posY, e.posZ, "mob.endermen.portal", 1.0F, 1.0F);
				e.playSound("mob.endermen.portal", 1.0F, 1.0F);

				e.setDead();
			}
		}

	}

	@Override
	public void onUpdate(VampirePlayer vampire, EntityPlayer player) {
		if(shouldSpawnMinion(player,vampire.getLevel())){
			IMinion m = (IMinion) Helper.spawnEntityInWorld(player.worldObj, player.boundingBox.expand(3, 2, 3), REFERENCE.ENTITY.VAMPIRE_MINION_NAME, 2);
			if (m != null) {
				m.setLord(vampire);
			}
		}
	}

	@Override
	public String toString() {
		return "VampireLordSkill ID: " + VampireLordSkill.ID;
	}
	
	/**
	 * Decides if a new minion should be spawned.
	 * Therefore randomly checks the existing minion count
	 * @return
	 */
	protected boolean shouldSpawnMinion(EntityPlayer player,int level) {
		if (player.worldObj.rand.nextInt(80) == 0) {
			int count = getMinionCount(player);
			if (count < getTargetCount(level)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return The number of near minions
	 */
	protected int getMinionCount(EntityPlayer player) {
		return player.worldObj.getEntitiesWithinAABB(EntityVampireMinion.class, player.boundingBox.expand(22, 17, 22)).size();
	}
	
	private int getTargetCount(int level){
		return Math.max(5, Math.min(1, Math.round((float)level/5F)));
	}

}
