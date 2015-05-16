package de.teamlapen.vampirism.proxy;

import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.entity.player.skills.BatSkill;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;

public class ServerProxy extends CommonProxy {

	private boolean allPlayersSleepingCoffin;

	@Override
	public void registerKeyBindings() {
		// Client side only

	}

	@Override
	public void registerRenderer() {
		// Client side only

	}

	@Override
	public void registerSounds() {
		// Client side only

	}

	@Override
	public void registerSubscriptions() {
		super.registerSubscriptions();

	}

	@Override
	public EntityPlayer getSPPlayer() {
		return null;
	}

	@Override
	public String translateToLocal(String s) {
		return s;
	}

	public boolean updateAllPlayersSleepingFlagCoffin() {
		List playerEntities = MinecraftServer.getServer().worldServerForDimension(0).playerEntities;
		
		this.allPlayersSleepingCoffin = !playerEntities.isEmpty();
		Iterator iterator = playerEntities.iterator();

		while (iterator.hasNext()) {
			VampirePlayer player = VampirePlayer.get((EntityPlayer) iterator.next());

			if (!player.sleepingCoffin) {
				this.allPlayersSleepingCoffin = false;
				break;
			}
		}
		return this.allPlayersSleepingCoffin;
	}
	
	private void wakeAllPlayers(WorldServer server)  {
		this.allPlayersSleepingCoffin = false;
		Iterator iterator = server.playerEntities.iterator();
		
		while(iterator.hasNext()) {
			EntityPlayer p = (EntityPlayer) iterator.next();
			VampirePlayer.get(p).sleepingCoffin = false;
			p.wakeUpPlayer(false, false, true);
		}
	}
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent event) {
		Logger.i("ServerProxy", "onServerTick called");
		WorldServer server = MinecraftServer.getServer().worldServerForDimension(0);
		
		if(server.areAllPlayersAsleep()) {
			Logger.i("ServerProxy", "All players are asleep, waking them up...");
			//Set time to next night
			long i = server.getWorldTime() + 24000L;
			server.setWorldTime(i - i % 24000L + 12000L);
			
			wakeAllPlayers(server);
		}
	}
	
    
	@Override
	public ResourceLocation checkVampireTexture(Entity entity, ResourceLocation loc) {
		return loc;
	}

	@Override
	public void setPlayerBat(EntityPlayer player, boolean bat) {
		float width=bat?BatSkill.BAT_WIDTH:BatSkill.PLAYER_WIDTH;
		float height=bat?BatSkill.BAT_HEIGHT:BatSkill.PLAYER_HEIGHT;
		Helper.Reflection.callMethod(Entity.class, player, Helper.Obfuscation.getPosNames("Entity/setSize"),Helper.Reflection.createArray(float.class,float.class),width,height);
		player.setPosition(player.posX,player.posY+(bat?1F:-1F)*(BatSkill.PLAYER_HEIGHT-BatSkill.BAT_HEIGHT),player.posZ);
        //Logger.i("test", BatSkill.BAT_EYE_HEIGHT+": p "+player.getDefaultEyeHeight()+ ": y "+player.yOffset+" :e1 "+player.eyeHeight);
		player.eyeHeight = (bat ?BatSkill.BAT_EYE_HEIGHT: player.getDefaultEyeHeight()) - player.yOffset;// Different from Client side
		//Logger.i("test2", BatSkill.BAT_EYE_HEIGHT+": p "+player.getDefaultEyeHeight()+ ": y "+player.yOffset+" :e2 "+player.eyeHeight);
	}

	@Override
	public void enableMaxPotionDuration(PotionEffect p) {
		// TODO Auto-generated method stub
		
	}

}
