package de.teamlapen.vampirism.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;

public class ItemPitchfork extends ItemSword {

	public static final String name = "pitchfork";
	public IIcon bigIcon;

	public ItemPitchfork() {
		super(Item.ToolMaterial.IRON);
		this.setNoRepair();
		setUnlocalizedName(name);
		this.setTextureName(REFERENCE.MODID+":"+name);
		this.maxStackSize = 1;
		setCreativeTab(VampirismMod.tabVampirism);
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("item.%s%s", REFERENCE.MODID.toLowerCase() + ".", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return this.getUnlocalizedName();
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(this.getIconString());
		bigIcon = iconRegister.registerIcon(this.getIconString() + ".big");
	}
}
