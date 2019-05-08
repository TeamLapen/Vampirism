package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;

import de.teamlapen.lib.lib.util.BasicCommand;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class EntityCommand extends BasicCommand {

	public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("entity")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ALL))
        		.executes(context -> {
                    return entity(context.getSource(), context.getSource().asPlayer());
        		});
    }

    private static int entity(CommandSource commandSource, EntityPlayerMP asPlayer) {
        List<?> l = asPlayer.getEntityWorld().getEntitiesWithinAABBExcludingEntity(asPlayer, asPlayer.getBoundingBox().grow(3, 2, 3));
        for (Object o : l) {
            if (o instanceof EntityCreature) {

                ResourceLocation id = EntityType.getId(((Entity) o).getType());
                commandSource.sendFeedback(new TextComponentString(id.toString()), true);
            } else {
                commandSource.sendFeedback(new TextComponentString("Not biteable " + o.getClass().getName()), true);
            }
        }
		return 0;
	}
}
