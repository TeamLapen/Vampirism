package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.castleDim.TeleporterCastle;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.VampireLordData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Portal block for castle dimension. Unbreakable
 */
public class BlockCastlePortal extends BlockPortal {
	public static final String name="castlePortal";
	public BlockCastlePortal(){
		this.setHardness(1000000F);
		this.setResistance(1000000000F);
		this.setUnlocalizedName(REFERENCE.MODID + "." + name);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		//DO not spawn mobs
	}


	@Override
	public void onEntityCollidedWithBlock(World par1World, BlockPos pos,IBlockState state, Entity par5Entity)
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
					player.addChatComponentMessage(new ChatComponentTranslation("text.vampirism.lord.portal_disabled"));
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

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {

	}


	@Override
	public boolean func_176548_d(World worldIn, BlockPos p_176548_2_) {
		return false;
	}



}
