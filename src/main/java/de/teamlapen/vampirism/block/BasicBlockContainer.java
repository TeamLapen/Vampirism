package de.teamlapen.vampirism.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;

public abstract class BasicBlockContainer extends BlockContainer {

	public BasicBlockContainer(Material material, String name) {
		super(material);
		setCreativeTab(VampirismMod.tabVampirism);
		this.setBlockName(name);
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("block.%s%s", REFERENCE.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		int l = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (l == 0)
			world.setBlockMetadataWithNotify(x, y, z, 0, 2);
		else if (l == 1)
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		else if (l == 2)
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		else if (l == 3)
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
}
