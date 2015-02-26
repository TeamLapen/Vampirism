package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltarTier4;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBloodAltarTier4 extends BasicBlockContainer {
		private final static String TAG = "BlockBloodAltarTier4";
		public final static String name = "bloodAltarTier4";

		public BlockBloodAltarTier4() {
			super(Material.iron, name);
			this.setBlockTextureName("vampirism:iconBloodAltarTier2");
		}

		@Override
		public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
			return new TileEntityBloodAltarTier4();
		}
		
		@Override
		public boolean onBlockActivated(World world, int par2, int par3, int par4,
				EntityPlayer player, int par6, float par7, float par8, float par9) {
			if (!world.isRemote) {
				
			}
			return false;
		}
}
