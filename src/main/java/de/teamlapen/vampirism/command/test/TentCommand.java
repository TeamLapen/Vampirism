package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.blockentity.TentBlockEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class TentCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("tent")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .executes(context -> tent(context.getSource(), context.getSource().getPlayerOrException(), false))
                .then(Commands.literal("advanced")
                        .executes(context -> tent(context.getSource(), context.getSource().getPlayerOrException(), true)));
    }

    @SuppressWarnings("SameReturnValue")
    private static int tent(@NotNull CommandSourceStack commandSource, @NotNull ServerPlayer asPlayer, boolean advanced) {
        HitResult result = UtilLib.getPlayerLookingSpot(asPlayer, 5);
        if (result.getType() == HitResult.Type.BLOCK) {

            BlockEntity tent = asPlayer.getCommandSenderWorld().getBlockEntity(((BlockHitResult) result).getBlockPos());
            if (tent instanceof TentBlockEntity) {
                ((TentBlockEntity) tent).setSpawn(true);
                if (advanced) ((TentBlockEntity) tent).setAdvanced(true);
                commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.tent.success"), false);
            }

        }
        return 0;
    }
}
