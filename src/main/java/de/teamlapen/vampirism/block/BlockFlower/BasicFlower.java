package de.teamlapen.vampirism.block.BlockFlower;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;

public abstract class BasicFlower extends BlockFlower {
	@SideOnly(Side.CLIENT)
    protected IIcon iIcon;

	protected BasicFlower(String name, String textureName) {
		super(getUniqueFlowerID()); // Unique ID
		setCreativeTab(VampirismMod.tabVampirism);
		this.setBlockName(name);
		this.setStepSound(soundTypeGrass);
		this.setHardness(0.0F);
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
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		iIcon = iconRegister.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1));
	}
	
    /**
     * Gets the block's texture. Args: side, meta
     */
	@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return this.iIcon;
    }
}
