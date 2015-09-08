package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.entity.EntityVampireBase;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.entity.minions.EntityRemoteVampireMinion;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.List;

/**
 * Vampire bites nearby biteable entity
 * 
 * @author Maxanier
 *
 */
public class VampireAIBiteNearbyEntity extends EntityAIBase {

	/**
	 * Bites nearbe entity and fills the equipped bottle
	 * 
	 * @author Max
	 *
	 */
	public static class MinionAIMinionCollectFromNearby extends VampireAIBiteNearbyEntity {

		public MinionAIMinionCollectFromNearby(EntityRemoteVampireMinion vampire) {
			super(vampire, 10);
		}

		@Override
		protected void addBlood(int amount) {
			ItemStack item = vampire.getEquipmentInSlot(0);
			if (item != null) {
				if (item.getItem().equals(Items.glass_bottle)) {
					ItemStack stack1 = new ItemStack(ModItems.bloodBottle, 1, 0);
					ItemBloodBottle.addBlood(stack1, amount);
					vampire.setCurrentItemOrArmor(0, stack1);
					return;
				}
				if (item.getItem().equals(ModItems.bloodBottle)) {
					ItemBloodBottle.addBlood(item, amount);
					return;
				}
			}
		}

	}

	protected final EntityVampireBase vampire;

	protected VampireMob mob;
	private final int chance;

	public VampireAIBiteNearbyEntity(EntityVampireBase vampire, int chance) {
		this.vampire = vampire;
		this.chance = chance;
	}

	protected void addBlood(int amount) {
		vampire.addPotionEffect(new PotionEffect(Potion.regeneration.id, amount * 20));
	}

	@Override
	public boolean shouldExecute() {
		if (vampire.getRNG().nextInt(chance) == 0) {
			List list = vampire.worldObj.getEntitiesWithinAABB(EntityCreature.class, vampire.getEntityBoundingBox().expand(2, 2, 2));
			for (Object o : list) {
				mob = VampireMob.get((EntityCreature) o);
				if (mob.getBlood() > 0)
					return true;
			}
		}
		mob = null;
		return false;
	}

	@Override
	public void startExecuting() {
		int amount = mob.bite(true);
		vampire.worldObj.playSoundAtEntity(vampire, REFERENCE.MODID + ":player.bite", 1.0F, 1.0F);
		addBlood(amount);
	}

}
