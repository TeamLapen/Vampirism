package de.teamlapen.vampirism.command.test;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.ArgumentBuilder;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class ResetActionsCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("resetActions")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return resetActions(context.getSource(), Lists.newArrayList(context.getSource().asPlayer()));
                })
                .then(Commands.argument("players", EntityArgument.multiplePlayers())
                        .executes(context -> {
                            return resetActions(context.getSource(), Lists.newArrayList(EntityArgument.getPlayers(context, "players")));
                        }));
    }

    private static int resetActions(CommandSource commandSource, List<EntityPlayerMP> players) {
        for (EntityPlayerMP player : players) {
            IFactionPlayer<?> factionPlayer = FactionPlayerHandler.get(player).getCurrentFactionPlayer();
            if (factionPlayer != null) {
                IActionHandler<?> handler = factionPlayer.getActionHandler();
                if (handler != null) {
                    handler.resetTimers();
                    commandSource.sendFeedback(new TextComponentString("Reset Timers"), true);
                }
            }
        }
        return 0;
    }
}
