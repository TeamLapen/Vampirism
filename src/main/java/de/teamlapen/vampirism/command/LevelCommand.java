package de.teamlapen.vampirism.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

/**
 * @author Cheaterpaul, Maxanier
 */
public class LevelCommand extends BasicCommand {


    public static ArgumentBuilder<CommandSource, ?> register() {

        return Commands.literal("level")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("faction", new FactionArgument())
                        .executes(context -> setLevel(context,FactionArgument.getFaction(context, "faction"), 1, Lists.newArrayList(context.getSource().asPlayer())))
                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                .executes(context -> setLevel(context, FactionArgument.getFaction(context, "faction"), IntegerArgumentType.getInteger(context, "level"), Lists.newArrayList(context.getSource().asPlayer())))
                                .then(Commands.argument("player", EntityArgument.entities())
                                        .executes(context -> setLevel(context, FactionArgument.getFaction(context, "faction"), IntegerArgumentType.getInteger(context, "level"), EntityArgument.getPlayers(context, "player"))))))
                .then(Commands.literal("none")
                        .executes(context -> leaveFaction(Lists.newArrayList(context.getSource().asPlayer())))
                        .then(Commands.argument("player", EntityArgument.entities())
                                .executes(context -> leaveFaction(EntityArgument.getPlayers(context, "player")))));
    }

    private static int setLevel(CommandContext<CommandSource> context, IPlayableFaction<IFactionPlayer> faction, int level, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            if (level == 0 && !handler.canLeaveFaction()) {
                context.getSource().sendErrorMessage(new TranslationTextComponent("command.vampirism.base.level.cant_leave", players.size() > 1 ? player.getDisplayName() : "Player", handler.getCurrentFaction().getName()));
            } else {
                level = Math.min(level, faction.getHighestReachableLevel());
                if (handler.setFactionAndLevel(faction, level)) {
                    context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.base.level.successful", player.getName(), faction.getName(), level), true);
                } else {
                    context.getSource().sendErrorMessage(players.size() > 1 ? new TranslationTextComponent("command.vampirism.failed_to_execute.players", player.getDisplayName()) : new TranslationTextComponent("command.vampirism.failed_to_execute"));
                }
            }
        }
        return 0;
    }

    private static int leaveFaction(Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            handler.setFactionAndLevel(null, 0);
        }
        return 0;
    }

}
