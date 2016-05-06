package de.teamlapen.vampirism.modcompat.waila;

import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.util.List;

/**
 * Provide data for creatures
 */
class CreatureDataProvider implements IWailaEntityProvider {
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world) {
        return null;
    }

    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        if (config.getConfig(WailaHandler.getShowCreatureInfoConf())) {
            if (entity instanceof EntityCreature && VampirePlayer.get(accessor.getPlayer()).getLevel() > 0) {
                ExtendedCreature extendedCreature = ExtendedCreature.get((EntityCreature) entity);
                int blood = extendedCreature.getBlood();
                if (blood > 0) {
                    currenttip.add(String.format("%s%s: %d", SpecialChars.RED, I18n.translateToLocal("text.vampirism.entitysblood"), blood));
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
