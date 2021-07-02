package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.network.UpdateEntityPacket;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class ForcePlayerSyncCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("forcePlayerSync")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ALL))
                .executes(context -> {
                    return triggerSync(context.getSource(), context.getSource().asPlayer());
                });
    }

    private static int triggerSync(CommandSource commandSource, ServerPlayerEntity asPlayer) {
        UpdateEntityPacket update = UpdateEntityPacket.createJoinWorldPacket(asPlayer);
        update.markAsPlayerItself();
        VampLib.dispatcher.sendTo(update, asPlayer);

        return 0;
    }
}
