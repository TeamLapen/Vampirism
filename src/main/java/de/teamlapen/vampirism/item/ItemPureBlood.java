package de.teamlapen.vampirism.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemPureBlood extends BasicItem{
	
	public static final String name="pureBlood";
	private static final int COUNT=5;
	public IIcon[] icons=new IIcon[COUNT];

	public ItemPureBlood() {
		super(name);
		this.setHasSubtypes(true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		for(int i=0;i<COUNT;i++){
			icons[i]=iconRegister.registerIcon(getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1)+"_"+i);
		}
	}
	
	@Override
	public IIcon getIconFromDamage(int meta) {
	    if (meta >= COUNT)
	        meta = 0;

	    return this.icons[meta];
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
	    for (int i = 0; i < COUNT; i ++) {
	        list.add(new ItemStack(item, 1, i));
	    }
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
	        return this.getUnlocalizedName()+"_"+stack.getItemDamage();
	}

}
