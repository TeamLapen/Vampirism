package de.teamlapen.vampirism.proxy;

import java.util.Iterator;
import java.util.List;

import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.entity.player.skills.BatSkill;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;

public class ServerProxy extends CommonProxy {

	private boolean allPlayersSleepingInCoffin;
	private List playerEntities;
	private WorldServer server = MinecraftServer.getServer().worldServerForDimension(0);

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

	public void updateAllPlayersSleepingFlagCoffin() {
		this.playerEntities = server.playerEntities;
		
		this.allPlayersSleepingInCoffin = !this.playerEntities.isEmpty();
		Iterator iterator = this.playerEntities.iterator();

		while (iterator.hasNext()) {
			EntityPlayer entityplayer = (EntityPlayer) iterator.next();

			if (!entityplayer.isPlayerSleeping()) {
				this.allPlayersSleepingInCoffin = false;
				break;
			}
		}
	}
	
    public boolean areAllPlayersAsleepCoffin()
    {
        if (this.allPlayersSleepingInCoffin) //&& !this.isRemote()
        {
            Iterator iterator = this.playerEntities.iterator();
            EntityPlayer entityplayer;

            do
            {
                if (!iterator.hasNext())
                {
                    return true;
                }

                entityplayer = (EntityPlayer)iterator.next();
            }
            while (VampirePlayer.get(entityplayer).isPlayerFullyAsleepCoffin());

            return false;
        }
        else
        {
            return false;
        }
    }

    public void wakeAllPlayers() {
        this.allPlayersSleepingInCoffin = false;
        Iterator iterator = this.playerEntities.iterator();

        while (iterator.hasNext())
        {
            EntityPlayer entityplayer = (EntityPlayer)iterator.next();

            if (VampirePlayer.get(entityplayer).sleepingCoffin)
            {
                entityplayer.wakeUpPlayer(false, false, true);
            }
        }
        server.provider.resetRainAndThunder();
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

}
