package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.network.UpdateEntityPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class ForcePlayerSyncCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("forcePlayerSync")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ALL))
                .executes(context -> triggerSync(context.getSource(), context.getSource().getPlayerOrException()));
    }

    private static int triggerSync(CommandSourceStack commandSource, ServerPlayer asPlayer) {
        UpdateEntityPacket update = UpdateEntityPacket.createJoinWorldPacket(asPlayer);
        update.markAsPlayerItself();
        VampLib.dispatcher.sendTo(update, asPlayer);

        return 0;
    }
}
