package de.teamlapen.vampirism.modcompat.waila;

import de.teamlapen.vampirism.tileentity.TileAltarInspiration;
import de.teamlapen.vampirism.tileentity.TileBloodContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Mod compatibility with Waila (What am I looking at)
 */
public class WailaHandler {
    public static void onRegister(IWailaRegistrar registrar) {
        registrar.addConfig(REFERENCE.MODID, getShowCreatureInfoConf(), true);
        registrar.addConfig(REFERENCE.MODID, getShowPlayerInfoConf(), true);

        registrar.registerBodyProvider(new CreatureDataProvider(), EntityCreature.class);
        registrar.registerBodyProvider(new PlayerDataProvider(), EntityPlayer.class);
        IWailaDataProvider tankDataProvider = new TankDataProvider();
        registrar.registerBodyProvider(tankDataProvider, TileAltarInspiration.class);
        registrar.registerBodyProvider(tankDataProvider, TileBloodContainer.class);
    }

    static String getShowCreatureInfoConf() {
        return REFERENCE.MODID + ".showCreatureInfo";
    }

    static String getShowPlayerInfoConf() {
        return REFERENCE.MODID + ".showPlayerInfo";
    }
}
