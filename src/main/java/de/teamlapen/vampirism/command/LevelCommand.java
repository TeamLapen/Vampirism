package de.teamlapen.vampirism.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LevelCommand extends BasicCommand {


    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {

        return Commands.literal("level")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("faction", FactionArgument.playableFactions(buildContext))
                        .executes(context -> setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), 1, Lists.newArrayList(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                .executes(context -> setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), IntegerArgumentType.getInteger(context, "level"), Lists.newArrayList(context.getSource().getPlayerOrException())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(context -> setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), IntegerArgumentType.getInteger(context, "level"), EntityArgument.getPlayers(context, "player"))))));
    }

    @SuppressWarnings("SameReturnValue")
    private static int setLevel(@NotNull CommandContext<CommandSourceStack> context, @NotNull Holder<IPlayableFaction<?>> faction, final int level, @NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler h = FactionPlayerHandler.get(player);
            if (level == 0 && !h.canLeaveFaction()) {
                context.getSource().sendFailure(Component.translatable("command.vampirism.base.level.cant_leave", players.size() > 1 ? player.getDisplayName() : "Player", h.getFaction().value().getName()));
            } else {
                int finalLevel = Math.min(level, faction.value().getHighestReachableLevel());
                if (h.setFactionAndLevel(faction, finalLevel)) {
                    context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.level.successful", player.getName(), faction.value().getName(), finalLevel), true);
                } else {
                    context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampirism.failed_to_execute.players", player.getDisplayName()) : Component.translatable("command.vampirism.failed_to_execute"));
                }
            }
        }
        return 0;
    }
}
