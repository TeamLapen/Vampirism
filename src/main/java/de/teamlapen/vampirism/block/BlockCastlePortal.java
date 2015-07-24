package de.teamlapen.vampirism.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.VampireLordData;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.castleDim.TeleporterCastle;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

/**
 * Portal block for castle dimension. Unbreakable
 */
public class BlockCastlePortal extends BlockPortal {
	public static final String name="castlePortal";
	public BlockCastlePortal(){
		this.setBlockName(name);
		this.setHardness(1000000F);
		this.setResistance(1000000000F);
		this.setBlockTextureName(REFERENCE.MODID + ":" + name);
	}

	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity)
	{
		if ((par5Entity.ridingEntity == null) && (par5Entity.riddenByEntity == null) && ((par5Entity instanceof EntityPlayerMP)))
		{
			EntityPlayerMP player = (EntityPlayerMP) par5Entity;

			MinecraftServer mServer = MinecraftServer.getServer();

			if (player.timeUntilPortal > 0)
			{
				player.timeUntilPortal = 10;
			}
			else if (player.dimension != VampirismMod.castleDimensionId)
			{
				player.timeUntilPortal = 10;
				if(!VampireLordData.get(par1World).isPortalEnabled()){
					player.timeUntilPortal=40;
					player.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.lord_portal_disabled"));
				}
				else if(VampirePlayer.get(player).getLevel()<REFERENCE.HIGHEST_REACHABLE_LEVEL){
					player.timeUntilPortal=10;
				}
				else{
					player.mcServer.getConfigurationManager().transferPlayerToDimension(player, VampirismMod.castleDimensionId, new TeleporterCastle(mServer.worldServerForDimension(VampirismMod.castleDimensionId)));
				}

			}
			else
			{
				player.timeUntilPortal = 10;
				player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 0, new TeleporterCastle(mServer.worldServerForDimension(0)));
			}
		}
	}



	@Override
	public String getUnlocalizedName() {
		return String.format("block.%s%s", REFERENCE.MODID.toLowerCase() + ".", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_) {
	}

	@Override public boolean func_150000_e(World p_150000_1_, int p_150000_2_, int p_150000_3_, int p_150000_4_) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{

		this.blockIcon = p_149651_1_.registerIcon(this.getTextureName());
	}

}
