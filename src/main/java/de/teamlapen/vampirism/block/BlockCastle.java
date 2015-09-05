package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

/**
 * Simple block for castles similar to stone bricks
 */
public class BlockCastle extends BasicBlock {
	private final String[]  types;
	public final static String name="castleBlock";
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	public BlockCastle() {

		this(name,new String[]{"purpleBrick","darkBrick","darkBrickBloody"});
	}

	protected BlockCastle(String name,String[] types){
		super(Material.rock, name);
		this.setBlockTextureName(REFERENCE.MODID + ":" + BlockCastle.name);
		this.setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(soundTypePiston);
		this.types=types;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if (meta < 0 || meta >= icons.length)
		{
			meta = 0;
		}

		return this.icons[meta];
	}

	public int damageDropped(int p_149692_1_)
	{
		//Do not drop the bloody stone
		if(p_149692_1_==2)return 1;
		return p_149692_1_;
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list)
	{
		for(int i=0;i<types.length;i++){
			list.add(new ItemStack(item,1,i));
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{
		this.icons = new IIcon[types.length];

		for (int i = 0; i < this.icons.length; ++i)
		{
			String s = this.getTextureName();
				s = s + "_" + types[i];

			this.icons[i] = p_149651_1_.registerIcon(s);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if(world.getBlockMetadata(x,y,z)==2){
			if(rand.nextInt(180)==0){
				world.playSound(x,y,z,"vampirism:ambient.castle",0.8F,1.0F,false);
			}

		}
	}
}
