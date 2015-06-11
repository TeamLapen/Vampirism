package de.teamlapen.vampirism.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.util.REFERENCE;

public class ItemVampireArmor extends ItemArmor {

	private static final String name = "vampireArmor";

	public ItemVampireArmor(int armorIndex) {
		super(ItemArmor.ArmorMaterial.CHAIN, 2, armorIndex);

		setCreativeTab(VampirismMod.tabVampirism);
		this.setUnlocalizedName(name + "_" + getSuffixFromId(armorIndex));
		this.setTextureName(REFERENCE.MODID+":"+name + "_" + getSuffixFromId(armorIndex));
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("item.%s%s", REFERENCE.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return String.format("item.%s%s", REFERENCE.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		if (this.armorType == 2) {
			return REFERENCE.MODID + ":textures/items/vampireArmor_2.png";
		}
		return REFERENCE.MODID + ":textures/items/vampireArmor_1.png";
	}

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

	public String getRegisterItemName() {
		return name + "_" + getSuffixFromId(this.armorType);
	}
	
	public static boolean isFullyWorn(EntityPlayer player){
		for(int i=0;i<4;i++){
			ItemStack s=player.getCurrentArmor(i);
			if(s==null||!(s.getItem() instanceof ItemVampireArmor)){
				return false;
			}
		}
		return true;
	}

}
