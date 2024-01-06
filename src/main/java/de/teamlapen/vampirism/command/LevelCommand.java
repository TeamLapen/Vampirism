package de.teamlapen.vampirism.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class LevelCommand extends BasicCommand {


    public static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("level")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("faction", FactionArgument.playableFactions())
                        .executes(context -> setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), 1, Lists.newArrayList(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                .executes(context -> setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), IntegerArgumentType.getInteger(context, "level"), Lists.newArrayList(context.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(context -> setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), IntegerArgumentType.getInteger(context, "level"), EntityArgument.getPlayers(context, "player"))))))
                .then(Commands.literal("none")
                        .executes(context -> leaveFaction(Lists.newArrayList(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("player", EntityArgument.entities())
                                .executes(context -> leaveFaction(EntityArgument.getPlayers(context, "player")))));
    }

    @SuppressWarnings("SameReturnValue")
    private static int setLevel(@NotNull CommandContext<CommandSourceStack> context, @NotNull IPlayableFaction<?> faction, final int level, @NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            Optional<FactionPlayerHandler> handler = FactionPlayerHandler.getOpt(player);
            handler.ifPresent(h -> {

            if (level == 0 && !h.canLeaveFaction()) {
                context.getSource().sendFailure(Component.translatable("command.vampirism.base.level.cant_leave", players.size() > 1 ? player.getDisplayName() : "Player", h.getCurrentFaction().getName()));
            } else {
                int finalLevel = Math.min(level, faction.getHighestReachableLevel());
                if (h.setFactionAndLevel(faction, finalLevel)) {
                    context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.level.successful", player.getName(), faction.getName(), finalLevel), true);
                } else {
                    context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampirism.failed_to_execute.players", player.getDisplayName()) : Component.translatable("command.vampirism.failed_to_execute"));
                }
            }

            });
        }
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int leaveFaction(@NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler.getOpt(player).ifPresent(s -> s.setFactionAndLevel(null, 0));
        }
        return 0;
    }

}
