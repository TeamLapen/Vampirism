package de.teamlapen.vampirism.common.server.commands.test;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.factions.common.server.commands.BasicCommand;
import de.teamlapen.vampirism.api.VampirismApi;
import de.teamlapen.vampirism.common.world.attachments.LevelGarlic;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.NotNull;

public class GarlicCheckCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("garlicCheck")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .executes(context -> garlicCheck(context.getSource(), context.getSource().getPlayerOrException(), false))
                .then(Commands.argument("print", BoolArgumentType.bool())
                        .executes(context -> garlicCheck(context.getSource(), context.getSource().getPlayerOrException(), BoolArgumentType.getBool(context, "print"))));
    }

    @SuppressWarnings("SameReturnValue")
    private static int garlicCheck(@NotNull CommandSourceStack commandSource, @NotNull ServerPlayer asPlayer, boolean print) {
        if (commandSource.getEntity() != null && commandSource.getEntity() instanceof Player) {
            commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.garliccheck.strength", VampirismApi.garlicHandler(asPlayer.level()).getStrengthAtChunk(new ChunkPos(asPlayer.blockPosition())).getSerializedName()), true);
        }
        if (print) {
            LevelGarlic.get(asPlayer.level()).printDebug(commandSource);
        }
        return 0;
    }
}
