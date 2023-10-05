package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.world.VampirismWorld;
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
            commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.garliccheck.strength", VampirismAPI.getVampirismWorld(asPlayer.getCommandSenderWorld()).map(w -> w.getStrengthAtChunk(new ChunkPos(asPlayer.blockPosition()))).orElse(EnumStrength.NONE)), true);
        }
        if (print) {
            VampirismWorld.getOpt(asPlayer.getCommandSenderWorld()).ifPresent(vw -> vw.printDebug(commandSource));
        }
        return 0;
    }
}
