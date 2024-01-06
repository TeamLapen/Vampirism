package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.network.ClientboundUpdateEntityPacket;
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
        ClientboundUpdateEntityPacket update = ClientboundUpdateEntityPacket.createJoinWorldPacket(asPlayer);
        update.markAsPlayerItself();
        asPlayer.connection.send(update);
        return 0;
    }
}
