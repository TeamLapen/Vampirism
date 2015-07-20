package de.teamlapen.vampirism;

import cpw.mods.fml.common.gameevent.TickEvent;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.generation.WorldGenVampirism;
import de.teamlapen.vampirism.generation.castle.CastlePositionData;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.tileEntity.TileEntityCoffin;
import de.teamlapen.vampirism.villages.VillageVampireData;

public class VampirismEventHandler {
	@SubscribeEvent
	public void dye(PlayerInteractEvent e) {
		if (e.world.isRemote)
			return;
		ItemStack i = null;
		if (e.entityPlayer.isSneaking() && e.action == Action.RIGHT_CLICK_BLOCK && e.world.getBlock(e.x, e.y, e.z).equals(ModBlocks.coffin)
				&& (i = (e.entityPlayer).inventory.getCurrentItem()) != null && i.getItem() instanceof ItemDye) {
			int color = i.getItemDamage();
			TileEntityCoffin t = (TileEntityCoffin) e.world.getTileEntity(e.x, e.y, e.z);
			if (t == null)
				return;
			t = t.getPrimaryTileEntity();
			if (t == null)
				return;
			t.changeColor(color);
			e.useBlock = Result.DENY;
			e.useItem = Result.DENY;
			if (!e.entityPlayer.capabilities.isCreativeMode) {
				i.stackSize--;
			}
		}
	}

	@SubscribeEvent
	public void onBonemeal(BonemealEvent event) {
		if (Configs.disable_vampire_biome) {
			if (Blocks.grass.equals(event.block)) {
				if (event.world.rand.nextInt(9) == 0) {
					EntityItem flower = new EntityItem(event.world, event.x, event.y + 1, event.z, new ItemStack(ModBlocks.vampireFlower, 1));
					event.world.spawnEntityInWorld(flower);
				}
			}
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent event) {
		VampirismMod.proxy.onTick(event);
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent e) {
		if (VampirePlayer.get(e.player).sleepingCoffin) {
			VampirePlayer.get(e.player).wakeUpPlayer(true, true, false, false);
		}
	}


	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if(!event.world.isRemote&&event.world.provider.dimensionId==0){
			//Reset the castle fail notice
			VampirismMod.vampireCastleFail=false;
		}
//		if(event.world instanceof WorldServer&&event.world.provider.dimensionId==VampirismMod.castleDimensionId){
//			IChunkProvider chunkProvider=((WorldServer) event.world).theChunkProviderServer.currentChunkProvider;
//			CastlePositionData.reset(event.world);
//			for(int x=0;x<6;x++){
//				for(int z=0;z<6;z++){
//					Chunk old=event.world.getChunkFromChunkCoords(x,z);
//					Chunk c=chunkProvider.provideChunk(x, z);
//					old.setStorageArrays(c.getBlockStorageArray());
//					old.entityLists=c.entityLists;
//					WorldGenVampirism.castleGenerator.checkBiome(event.world,x,z,event.world.rand,true);
//				}
//			}
//		}

		// Loading VillageVampireData
		FMLCommonHandler.instance().bus().register(VillageVampireData.get(event.world));// Not sure if this is the right position or if it could lead to a memory leak
	}

//	@SubscribeEvent
//	public void onWorldUnload(WorldEvent.Unload event){
//		if(!event.world.isRemote&&event.world.provider.dimensionId==VampirismMod.castleDimensionId){
//			for(int x=0;x<6;x++){
//				for(int z=0;z<6;z++){
//					Chunk c=new Chunk(event.world,new Block[32768],x,z);
//					event.world.getChunkFromChunkCoords(x,z).setStorageArrays(c.getBlockStorageArray());
//				}
//			}
//		}
//	}


//	@SubscribeEvent
//	public void onChunkLoad(ChunkDataEvent.Load event){
//		int x=event.getData().getCompoundTag("Level").getInteger("xPos");
//		int z=event.getData().getCompoundTag("Level").getInteger("zPos");
//		event.getChunk().setStorageArrays();
//	}
}
