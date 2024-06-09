package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import de.teamlapen.vampirism.util.TotemHelper;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class VillageCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return Commands.literal("village")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.literal("capture")
                        .then(Commands.argument("faction", FactionArgument.factions(buildContext))
                                .executes(context -> capture(context.getSource(), context.getSource().getPlayerOrException(), FactionArgument.getFaction(context, "faction")))))
                .then(Commands.literal("abort")
                        .executes(context -> abort(context.getSource())));

    }

    @SuppressWarnings("SameReturnValue")
    private static int capture(@NotNull CommandSourceStack source, @NotNull ServerPlayer player, Holder<IFaction<?>> faction) {
        source.sendSuccess(() -> TotemHelper.forceFactionCommand(faction, player), true);
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int abort(@NotNull CommandSourceStack source) {
        Vec3 position = source.getPosition();
        TotemHelper.getTotemNearPos(source.getLevel(), new BlockPos((int) position.x(), (int) position.y(), (int) position.z()), true).ifPresent(TotemBlockEntity::breakCapture);
        return 0;
    }
}
