package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;

import de.teamlapen.lib.lib.util.BasicCommand;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;

import org.apache.logging.log4j.LogManager;

import java.util.List;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class InfoEntityCommand extends BasicCommand{

	public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("info-entity")
        		.requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
        		.executes(context -> {
                    return infoEntity(context.getSource(), context.getSource().asPlayer());
        		});
	}

    private static int infoEntity(CommandSource commandSource, EntityPlayerMP asPlayer) {
		List<Entity> l = asPlayer.getEntityWorld().getEntitiesWithinAABBExcludingEntity(asPlayer, asPlayer.getBoundingBox().grow(3, 2, 3));
        for (Entity o : l) {
            NBTTagCompound nbt = new NBTTagCompound();
            o.writeUnlessRemoved(nbt);
            LogManager.getLogger().info("InfoEntity", "Data %s", nbt);
        }
        commandSource.sendFeedback(new TextComponentString("Printed info to log"), true);
		return 0;
	}
}
