package de.teamlapen.vampirism.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;


public class LordCommand extends BasicCommand {

    private static final SimpleCommandExceptionType NO_FACTION = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.lord.no_faction"));
    private static final SimpleCommandExceptionType LEVEL_UP_FAILED = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.lord.level_failed"));
    private static final SimpleCommandExceptionType LORD_FAILED = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.lord.failed"));


    public static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("lord-level")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("level", IntegerArgumentType.integer(0))
                        .executes(context -> setLevel(context, IntegerArgumentType.getInteger(context, "level"), Lists.newArrayList(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("player", EntityArgument.entities())
                                .executes(context -> setLevel(context, IntegerArgumentType.getInteger(context, "level"), EntityArgument.getPlayers(context, "player")))));

    }

    @SuppressWarnings("SameReturnValue")
    private static int setLevel(@NotNull CommandContext<CommandSourceStack> context, final int level, @NotNull Collection<ServerPlayer> players) throws CommandSyntaxException {
        for (ServerPlayer player : players) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            Holder<? extends IPlayableFaction<?>> faction = handler.getFaction();
            if (faction == null) {
                throw NO_FACTION.create();
            }
            int maxLevel = faction.value().getHighestReachableLevel();
            if (handler.getCurrentLevel() == maxLevel && !handler.setFactionLevel(faction, maxLevel)) {
                throw LEVEL_UP_FAILED.create();
            }
            if (!handler.setLordLevel(Math.min(level, faction.value().getHighestLordLevel()))) {
                throw LORD_FAILED.create();
            }
            context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.lord.successful", player.getName(), faction.value().getName(), handler.getLordLevel()), true);
        }
        return 0;
    }

}
