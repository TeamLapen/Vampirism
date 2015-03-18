package de.teamlapen.vampirism.block.BlockFlower;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;

public abstract class BasicFlower extends BlockFlower {
	@SideOnly(Side.CLIENT)
    protected IIcon[] iIcon;

	protected BasicFlower(String name, String textureName) {
		super(getUniqueFlowerID()); // Unique ID
		setCreativeTab(VampirismMod.tabVampirism);
		this.setBlockName(name);
		this.setStepSound(soundTypeGrass);
		this.setHardness(0.0F);
		iIcon = new IIcon[1];
	}
	
	// The existing flowers are created in Block class (normally just 2)
	private static int getUniqueFlowerID() {
		int id = 0;
		int flowerID = 0;
		while (Block.blockRegistry.containsId(id)) {
			if (Block.blockRegistry.getObjectById(id) instanceof BlockFlower) {
				flowerID++;
			}
			id++;
		}
		return flowerID;
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("block.%s%s", REFERENCE.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}
	
    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
	// Added to prevent a crash opening creative tab, issue #18
    @Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List p_149666_3_)
    {
        for (int i = 0; i < this.iIcon.length; ++i)
        {
            p_149666_3_.add(new ItemStack(p_149666_1_, 1, i));
        }
    }

    @Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		iIcon[0] = iconRegister.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1));
	}
	
    /**
     * Gets the block's texture. Args: side, meta
     */
	@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return this.iIcon[0];
    }
}
