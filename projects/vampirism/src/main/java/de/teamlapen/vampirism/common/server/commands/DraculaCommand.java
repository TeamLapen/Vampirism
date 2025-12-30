package de.teamlapen.vampirism.common.server.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.factions.api.factions.LevelingChange;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.server.commands.BasicCommand;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDraculaPlayer;
import de.teamlapen.vampirism.common.core.ModFactions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;

public class DraculaCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal(ModFactions.VAMPIRE.getKey().identifier().toString())
                .then(Commands.literal("dracula")
                        .executes(context -> setDracula(context, List.of(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("players", EntityArgument.players())
                                .executes(context -> setDracula(context, EntityArgument.getPlayers(context, "players")))));
    }

    public static int setDracula(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            if (handler.setFaction(LevelingChange.maxLord(handler.getFaction()).add(new IDraculaPlayer.DraculaChange()))) {
                context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.dracula.success", player.getDisplayName()), true);
            } else {
                context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampirism.failed_to_execute.players", player.getDisplayName()) : Component.translatable("command.vampirism.failed_to_execute"));
            }
        }
        return 0;
    }
}
