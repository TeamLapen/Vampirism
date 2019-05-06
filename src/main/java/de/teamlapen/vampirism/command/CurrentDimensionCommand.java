package de.teamlapen.vampirism.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import de.teamlapen.lib.lib.util.BasicCommand;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

public class CurrentDimensionCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("currentDimension")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ALL))
                .executes(context -> {
                    return currentDimension(context, context.getSource().asPlayer());
                });
    }

    private static int currentDimension(CommandContext<CommandSource> context, EntityPlayerMP asPlayer) {
        context.getSource().sendFeedback(new TextComponentString("Dimension ID: " + asPlayer.getEntityWorld().getDimension()), true);
        return 0;
    }
}
