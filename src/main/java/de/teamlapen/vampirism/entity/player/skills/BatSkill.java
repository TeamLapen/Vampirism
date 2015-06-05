package de.teamlapen.vampirism.entity.player.skills;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentTranslation;
import de.teamlapen.vampirism.entity.player.PlayerModifiers;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;

public class BatSkill extends DefaultSkill implements ILastingSkill {
	public final static float BAT_WIDTH=0.5F;
	public final static float BAT_HEIGHT=0.9F;
	public final static float BAT_EYE_HEIGHT=0.85F*BAT_HEIGHT;
	public final static float PLAYER_WIDTH=0.8F;
	public final static float PLAYER_HEIGHT=1.8F;
	
	public final UUID speedModifierUUID = UUID.fromString("eb7a2e48-ce60-4629-b5f5-7a196d1035af");

	public final UUID healthModifierUUID = UUID.fromString("4392fccb-4bfd-4290-b2e6-5cc91429053c");
	
	@Override
	public int getCooldown() {
		return 1;
	}

	@Override
	public int getMinLevel() {
		return 4;
	}

	@Override
	public int getMinU() {
		return 64;
	}

	@Override
	public int getMinV() {
		return 0;
	}

	@Override
	public boolean onActivated(VampirePlayer vampire, EntityPlayer player) {
		setModifier(player,true);
		double reduc=player.getHealth()-player.getMaxHealth();
		if(reduc<0){
			reduc=0;
		}
		player.setHealth((float) (player.getHealth()-reduc));
		vampire.getExtraDataTag().setDouble("bat_skill_health",reduc);
		return true;
	}

	@Override
	public int getDuration(int level) {
		return 100000;
	}

	@Override
	public void onDeactivated(VampirePlayer vampire, EntityPlayer player) {

		setModifier(player,false);
		if(player.getHealth()>0){
			player.setHealth((float) (vampire.getExtraDataTag().getDouble("bat_skill_health")+player.getHealth()));
			if(player.onGround){
				player.addPotionEffect(new PotionEffect(Potion.resistance.id,20,100));
			}
			player.addPotionEffect(new PotionEffect(Potion.resistance.id,60,100));
			
		}

	}

	@Override
	public boolean onUpdate(VampirePlayer vampire, EntityPlayer player) {
		if(vampire.gettingSundamage()&&!player.worldObj.isRemote){
			player.addChatMessage(new ChatComponentTranslation("text.vampirism:cant_fly_day"));
			return true;
		}
		return false;
	}

	@Override
	public void onReActivated(VampirePlayer vampire, EntityPlayer player) {
		setModifier(player,true);
	}
	
	private void setModifier(EntityPlayer player,boolean enabled){
		if(enabled){
//			IAttributeInstance movement = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
//			//PlayerModifiers.rmMod(movement, speedModifierUUID);
//			movement.applyModifier(new AttributeModifier(speedModifierUUID, "Bat Speed Bonus", BALANCE.VP_SKILLS.BAT_SPEED_MOD, 2).setSaved(false));
			IAttributeInstance health = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
			//PlayerModifiers.rmMod(health, healthModifierUUID);
			health.applyModifier(new AttributeModifier(healthModifierUUID, "Bat Health Reduction", -0.9, 2).setSaved(false));
			player.capabilities.allowFlying=true;
			player.capabilities.isFlying=true;
			player.sendPlayerAbilities();
		}
		else{
//			//Movement speed
//			IAttributeInstance movement = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
//			PlayerModifiers.rmMod(movement, speedModifierUUID);
			
			// Health modifier
			IAttributeInstance health = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
			PlayerModifiers.rmMod(health, healthModifierUUID);
			
			if(!player.capabilities.isCreativeMode){
				player.capabilities.allowFlying=false;
			}
			player.capabilities.isFlying=false;
			player.sendPlayerAbilities();
		}

	}
	
	@Override
	public boolean canBeUsedBy(VampirePlayer vampire,EntityPlayer player){
		return !vampire.gettingSundamage()&&!vampire.isSkillActive(Skills.vampireRage);
	}

	@Override
	public String getUnlocalizedName() {
		return "skill.vampirism.bat_skill";
	}

}
