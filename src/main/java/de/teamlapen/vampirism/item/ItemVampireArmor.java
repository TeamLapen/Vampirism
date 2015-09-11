package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.IItemRegistrable;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class ItemVampireArmor extends ItemArmor implements IItemRegistrable {


	private static final String name = "vampireArmor";

	public static String getSuffixFromId(int id) {
		switch (id) {
		case 0:
			return "helmet";
		case 1:
			return "chestplate";
		case 2:
			return "leggings";
		case 3:
			return "boots";
		default:
			return "";
		}
	}

	public static boolean isFullyWorn(EntityPlayer player) {
		for (int i = 0; i < 4; i++) {
			ItemStack s = player.getCurrentArmor(i);
			if (s == null || !(s.getItem() instanceof ItemVampireArmor)) {
				return false;
			}
		}
		return true;
	}

	public ItemVampireArmor(int renderIndex,int armorType) {
		super(ModItems.ARMOR_BLOOD_IRON, renderIndex, armorType);

		setCreativeTab(VampirismMod.tabVampirism);
		this.setUnlocalizedName(REFERENCE.MODID+"."+name + "_" + getSuffixFromId(armorType));
	}

//	@Override
//	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
//		if (this.armorType == 2) {
//			return REFERENCE.MODID + ":textures/items/vampireArmor_2.png";
//		}
//		return REFERENCE.MODID + ":textures/items/vampireArmor_1.png";
//	}

	public String getRegisterItemName() {
		return name + "_" + getSuffixFromId(this.armorType);
	}


	@Override
	public String getBaseName() {
		return name+"_"+getSuffixFromId(armorType);
	}
}
