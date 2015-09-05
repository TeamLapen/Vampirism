package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.IIcon;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPitchfork extends ItemSword {

	public static final String name = "pitchfork";
	public IIcon bigIcon;

	public ItemPitchfork() {
		super(Item.ToolMaterial.IRON);
		this.setNoRepair();
		setUnlocalizedName(name);
		this.setTextureName(REFERENCE.MODID+":"+name);
		this.maxStackSize = 1;
		this.setCreativeTab(null);
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
