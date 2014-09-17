package de.teamlapen.vampirism.entity.player;

import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

public abstract class PlayerModifiers {

	public static final String TAG = "PlayerModifier";

	public static final UUID speedModifierUUID = UUID.fromString("0FCBF922-DBEC-492A-82F5-99F73AFF5065");
	public static final UUID healthModifierUUID = UUID.fromString("56C17EFE-E3EC-4E27-A12F-99D2FE927B70");
	public static final UUID damageModifierUUID = UUID.fromString("7600D8C4-3517-40BE-8CB1-359D46705A0F");

	public static void applyModifiers(int level, EntityPlayer p) {

		double m = 0;
		// Speed modifier
		IAttributeInstance movement = p.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
		rmMod(movement, speedModifierUUID);

		m = calculateSqrtMod(level, 30, 0.3D, 5);
		movement.applyModifier(new AttributeModifier(speedModifierUUID, "Vampire Speed Bonus", m, 2).setSaved(false));

		// Health modifier
		IAttributeInstance health = p.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		rmMod(health, healthModifierUUID);

		m = calculateSqrtMod(level, 50, 1, 1);
		health.applyModifier(new AttributeModifier(healthModifierUUID, "Vampire Health Bonus", m, 2).setSaved(false));

		// Strength modifier
		IAttributeInstance damage = p.getEntityAttribute(SharedMonsterAttributes.attackDamage);
		rmMod(damage, damageModifierUUID);

		m = calculateSqrtMod(level, 50, 1, 1);
		damage.applyModifier(new AttributeModifier(damageModifierUUID, "Vampire Strength Bonus", m, 2).setSaved(false));

		// Nightvision after 1.8 maybe see
		// net.minecraft.client.renderer.EntityRenderer.updateLightmap(float)

	}

	/**
	 * Calculates the modifier effect. In lower levels the effect changes
	 * greater.
	 * 
	 * @param level
	 *            Vampire level
	 * @param lcap
	 *            Level the modifier does not get any stronger
	 * @param maxMod
	 *            Max modifier effect
	 * @param slope
	 * @return modifier effect
	 */
	private static double calculateSqrtMod(int level, int lcap, double maxMod, int slope) {
		return Math.sqrt(slope * (level > lcap ? lcap : level)) / (Math.sqrt(slope * lcap)) * maxMod;
	}

	/**
	 * Removes existing modifiers
	 * 
	 * @param att
	 *            Attribute
	 * @param uuid
	 *            UUID of modifier to remove
	 */
	private static void rmMod(IAttributeInstance att, UUID uuid) {
		AttributeModifier m = att.getModifier(uuid);
		if (m != null) {
			att.removeModifier(m);
		}
	}

}
