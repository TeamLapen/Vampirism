package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class VillageCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("village")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.literal("capture")
                        .then(Commands.argument("faction", FactionArgument.factionArgument())
                                .executes(context -> capture(context.getSource(), context.getSource().getPlayerOrException(), FactionArgument.getFaction(context, "faction")))))
                .then(Commands.literal("abort")
                        .executes(context -> abort(context.getSource())));

    }

    private static int capture(CommandSourceStack source, ServerPlayer player, IPlayableFaction<?> faction) {
        source.sendSuccess(TotemHelper.forceFactionCommand(faction, player), true);
        return 0;
    }

    private static int abort(CommandSourceStack source) {
        TotemHelper.getTotemNearPos(source.getLevel(), new BlockPos(source.getPosition()), true).ifPresent(TotemTileEntity::breakCapture);
        return 0;
    }
}
