package de.teamlapen.faction.common.server.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.common.server.commands.arguments.FactionArgument;
import de.teamlapen.faction.common.util.TotemHelper;
import de.teamlapen.faction.common.world.blockentity.TotemBlockEntity;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class VillageCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return Commands.literal("village")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("capture")
                        .then(Commands.argument("faction", FactionArgument.factions(buildContext))
                                .executes(context -> capture(context.getSource(), context.getSource().getPlayerOrException(), FactionArgument.getFaction(context, "faction")))))
                .then(Commands.literal("abort")
                        .executes(context -> abort(context.getSource())));

    }

    @SuppressWarnings("SameReturnValue")
    private static int capture(CommandSourceStack source, ServerPlayer player, Holder<IFaction<?>> faction) {
        source.sendSuccess(() -> TotemHelper.forceFactionCommand(faction, player), true);
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int abort(CommandSourceStack source) {
        Vec3 position = source.getPosition();
        TotemHelper.getTotemNearPos(source.getLevel(), new BlockPos((int) position.x(), (int) position.y(), (int) position.z()), true).ifPresent(TotemBlockEntity::breakCapture);
        return 0;
    }
}
