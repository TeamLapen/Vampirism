package de.teamlapen.vampirism.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class LevelUpCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("levelup")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT))
                .executes(context -> {
                    return levelUp(context, Lists.newArrayList(context.getSource().asPlayer()));
                }).then(Commands.argument("player", EntityArgument.entities())
                        .executes(context -> {
                            return levelUp(context, EntityArgument.getPlayers(context, "player"));
                        }));
    }

    private static int levelUp(CommandContext<CommandSource> context, Collection<ServerPlayerEntity> players) {
        for (ServerPlayerEntity player : players) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            if (handler.getCurrentLevel() == 0) {
                context.getSource().sendErrorMessage(new TranslationTextComponent("command.vampirism.base.levelup.nofaction"));
            } else if (handler.getCurrentLevel() == handler.getCurrentFaction().getHighestReachableLevel()) {
                context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.base.levelup.max"), true);
            } else {
                if (handler.setFactionAndLevel(handler.getCurrentFaction(), handler.getCurrentLevel() + 1)) {
                    context.getSource().sendFeedback(new StringTextComponent(player.getName() + " " + new TranslationTextComponent("commands.vampirism.base.level.isnowa") + " " + handler.getCurrentFaction().getName() + " " + new TranslationTextComponent("commands.vampirism.base.level.level") + " " + handler.getCurrentLevel()), true);
                } else {
                    context.getSource().sendErrorMessage(new TranslationTextComponent("commands.vampirism.failed_to_execute"));
                }
            }
        }
        return 0;
    }

}
