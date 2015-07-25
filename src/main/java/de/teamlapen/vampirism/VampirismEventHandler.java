package de.teamlapen.vampirism;

import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import de.teamlapen.vampirism.entity.VampireMob;
import de.teamlapen.vampirism.generation.WorldGenVampirism;
import de.teamlapen.vampirism.generation.castle.CastlePositionData;
import de.teamlapen.vampirism.item.ItemBloodBottle;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
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
import org.apache.commons.lang3.StringUtils;

public class VampirismEventHandler {
	@SubscribeEvent
	public void playerInteract(PlayerInteractEvent e) {
		if (e.world.isRemote)
			return;
		ItemStack i = null;
		if (e.action == Action.RIGHT_CLICK_BLOCK ){
			if(e.entityPlayer.isSneaking() &&e.world.getBlock(e.x, e.y, e.z).equals(ModBlocks.coffin)
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
		if(event instanceof ServerTickEvent){
			World w=DimensionManager.getWorld(0);
			if(w!=null){
				VampireLordData.get(w).tick((ServerTickEvent) event);
			}

		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent e) {
		if (VampirePlayer.get(e.player).sleepingCoffin) {
			VampirePlayer.get(e.player).wakeUpPlayer(true, true, false, false);
		}
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event){
		VillageVampireData.get(event.world).onWorldTick(event);
	}


	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if (!event.world.isRemote && event.world.provider.dimensionId == 0) {
			//Reset the castle fail notice
			VampirismMod.vampireCastleFail = false;
		}
	}
}
