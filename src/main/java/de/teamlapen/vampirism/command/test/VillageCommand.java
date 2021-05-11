package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class VillageCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("village")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT))
                .then(Commands.literal("capture")
                        .then(Commands.argument("faction", FactionArgument.factionArgument())
                                .executes(context -> capture(context.getSource(), context.getSource().asPlayer(), FactionArgument.getFaction(context, "faction")))))
                .then(Commands.literal("abort")
                        .executes(context -> abort(context.getSource())));

    }

    private static int capture(CommandSource source, ServerPlayerEntity player, IPlayableFaction<?> faction) {
        source.sendFeedback(TotemHelper.forceFactionCommand(faction, player), true);
        return 0;
    }

    private static int abort(CommandSource source) {
        TotemHelper.getTotemNearPos(source.getWorld(), new BlockPos(source.getPos()),true).ifPresent(TotemTileEntity::breakCapture);
        return 0;
    }
}
