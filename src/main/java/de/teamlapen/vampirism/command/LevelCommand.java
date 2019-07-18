package de.teamlapen.vampirism.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class LevelCommand extends BasicCommand {
    
    final static IPlayableFaction[] pfactions = VampirismAPI.factionRegistry().getPlayableFactions();
    final static String[] pfaction_names = new String[pfactions.length];
    static {
        for (int i = 0; i < pfactions.length; i++) {
            pfaction_names[i] = pfactions[i].name();
        }
    }

    public static ArgumentBuilder<CommandSource, ?> register() {
        LiteralArgumentBuilder<CommandSource> argument = Commands.literal("level")
                .requires(context->context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT));
        
        for(IPlayableFaction faction : pfactions) {
            argument.then(Commands.literal(faction.name())
                    .then(Commands.argument("level", IntegerArgumentType.integer(0, faction.getHighestReachableLevel()))
                            .executes(context -> {
                                return setLevel(context, faction, IntegerArgumentType.getInteger(context, "level"), Lists.newArrayList(context.getSource().asPlayer()));
                            })
                            .then(Commands.argument("player", EntityArgument.entities())
                                    .executes(context -> {
                                        return setLevel(context, faction, IntegerArgumentType.getInteger(context, "level"), EntityArgument.getPlayers(context, "player"));
                                    }))));
        }

        return argument;
    }

    private static int setLevel(CommandContext<CommandSource> context, IPlayableFaction<IFactionPlayer> faction, int level, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            if (level == 0 && !handler.canLeaveFaction()) {
                context.getSource().sendErrorMessage(new TranslationTextComponent("command.vampirism.base.level.cant_leave"));
            }
            if (handler.setFactionAndLevel(faction, level)) {
                context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.base.level.successful", player.getName(), faction.getName(), level), true);
            } else {
                context.getSource().sendErrorMessage(new TranslationTextComponent("commands.vampirism.failed_to_execute"));
            }
        }
        return 0;
    }

}
