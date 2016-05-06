package de.teamlapen.vampirism.modcompat.waila;

import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.util.List;

/**
 * Provides information about faction players
 */
class PlayerDataProvider implements IWailaEntityProvider {
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world) {
        return null;
    }

    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        if (config.getConfig(WailaHandler.getShowPlayerInfoConf())) {
            if (entity instanceof EntityPlayer) {
                FactionPlayerHandler factionHandler = FactionPlayerHandler.get((EntityPlayer) entity);
                if (factionHandler.getCurrentLevel() > 0) {
                    currenttip.add(factionHandler.getCurrentFaction().getChatColor() + String.format("%s %s: %s", I18n.translateToLocal(factionHandler.getCurrentFaction().getUnlocalizedName()), I18n.translateToLocal("text.vampirism.level"), factionHandler.getCurrentLevel()));
                }
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }
}
