package de.teamlapen.vampirism.entity.minions;

import org.eclipse.jdt.annotation.NonNull;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class CollectBloodCommand extends DefaultMinionCommand {

	protected final EntityRemoteVampireMinion minion;
	protected final EntityAIBase task;
	
	public CollectBloodCommand(int id,EntityRemoteVampireMinion m) {
		super(id);
		minion=m;
		task=null;
	}

	@Override
	public String getUnlocalizedName() {
		return "minioncommand.vampirism.collectblood";
	}

	@Override
	public void onActivated() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeactivated() {
		ItemStack item=minion.getRepresentingEntity().getEquipmentInSlot(0);
		if(item!=null&&(item.getItem().equals(ModItems.bloodBottle)||item.getItem().equals(Items.glass_bottle))){
			minion.getRepresentingEntity().entityDropItem(item, 0.1F);
			minion.getRepresentingEntity().setCurrentItemOrArmor(0, null);
		}
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
	public boolean shouldPickupItem(@NonNull ItemStack item){
		
		if(item.getItem().equals(ModItems.bloodBottle)||item.getItem().equals(Items.glass_bottle)){
			ItemStack old=minion.getRepresentingEntity().getEquipmentInSlot(0);
			if(old==null)return true;
			if(ItemBloodBottle.getBlood(item)<ItemBloodBottle.getBlood(old)){
				return true;
			}
		}
		return false;
	}

}
