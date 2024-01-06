package de.teamlapen.vampirism.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LevelUpCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("levelup")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .executes(context -> levelUp(context, Lists.newArrayList(context.getSource().getPlayerOrException()))).then(Commands.argument("player", EntityArgument.entities())
                        .executes(context -> levelUp(context, EntityArgument.getPlayers(context, "player"))));
    }

    @SuppressWarnings("SameReturnValue")
    private static int levelUp(@NotNull CommandContext<CommandSourceStack> context, @NotNull Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler.getOpt(player).ifPresent(handler -> {
                if (handler.getCurrentLevel() == 0) {
                    context.getSource().sendFailure(Component.translatable("command.vampirism.base.levelup.nofaction", players.size() > 1 ? player.getDisplayName() : "Player"));
                } else if (handler.getCurrentLevel() == handler.getCurrentFaction().getHighestReachableLevel()) {
                    context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.levelup.max", players.size() > 1 ? player.getDisplayName() : "Player"), true);
                } else {
                    if (handler.setFactionAndLevel(handler.getCurrentFaction(), handler.getCurrentLevel() + 1)) {
                        context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.levelup.newlevel", player.getName(), handler.getCurrentFaction().getName(), handler.getCurrentLevel()), true);
                    } else {
                        context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampirism.failed_to_execute.players", player.getDisplayName()) : Component.translatable("command.vampirism.failed_to_execute"));
                    }
                }
            });
        }
        return 0;
    }

}
