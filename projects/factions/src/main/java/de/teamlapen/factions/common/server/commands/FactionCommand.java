package de.teamlapen.factions.common.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.factions.LevelingChange;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class FactionCommand extends BasicCommand {
    private static final SimpleCommandExceptionType LEVEL_UP_FAILED = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.lord.level_failed"));
    private static final SimpleCommandExceptionType LORD_FAILED = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.lord.failed"));

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        HolderLookup.RegistryLookup<IFaction<?>> factions = buildContext.lookupOrThrow(FactionRegistries.Keys.FACTION);

        var root = Commands.literal("faction")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT));

        for (var listHolder : factions.listElements().toList()) {
            if (!(listHolder.value() instanceof IPlayableFaction<?> faction)){
                continue;
            }

            //noinspection unchecked
            var holder = (Holder.Reference<IPlayableFaction<?>>) (Object) listHolder;

            var factionCommand = Commands.literal(holder.key().location().toString());

            factionCommand.executes(context -> setLevel(context, holder, 1, Lists.newArrayList(context.getSource().getPlayerOrException())));

            if (faction.getHighestReachableLevel() > 1) {
                factionCommand.then(Commands.literal("level")
                        .executes(context -> setLevel(context, holder, 1, Lists.newArrayList(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, faction.getHighestReachableLevel()))
                                .executes(context -> setLevel(context, holder, IntegerArgumentType.getInteger(context, "level"), Lists.newArrayList(context.getSource().getPlayerOrException())))
                                .then(Commands.argument("players", EntityArgument.players())
                                        .executes(context -> setLevel(context, holder, IntegerArgumentType.getInteger(context, "level"), EntityArgument.getPlayers(context, "players"))))
                        )
                );
            }
            if (faction.getHighestLordLevel() > 0) {
                factionCommand.then(Commands.literal("lord")
                        .executes(context -> setLordLevel(context, holder, 1, Lists.newArrayList(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, faction.getHighestLordLevel()))
                                .executes(context -> setLordLevel(context, holder, IntegerArgumentType.getInteger(context, "level"), Lists.newArrayList(context.getSource().getPlayerOrException())))
                                .then(Commands.argument("players", EntityArgument.players())
                                        .executes(context -> setLordLevel(context, holder, IntegerArgumentType.getInteger(context, "level"), EntityArgument.getPlayers(context, "players"))))
                        )
                );
            }
            root.then(factionCommand);
        }

        return root;
    }

    public static int setLevel(CommandContext<CommandSourceStack> context, Holder<IPlayableFaction<?>> faction, final int level, Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler factionPlayerHandler = FactionPlayerHandler.get(player);
            int finalLevel = Math.min(level, faction.value().getHighestReachableLevel());
            if (factionPlayerHandler.setFaction(LevelingChange.builder().faction(faction).level(finalLevel).build())) {
                context.getSource().sendSuccess(() -> Component.translatable("command.factions.base.level.successful", player.getName(), faction.value().getName(), finalLevel), true);
            } else {
                context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.vampirism.failed_to_execute.players", player.getDisplayName()) : Component.translatable("command.vampirism.failed_to_execute"));
            }
        }
        return 0;
    }

    private static int setLordLevel(CommandContext<CommandSourceStack> context, Holder<IPlayableFaction<?>> faction, final int level, Collection<ServerPlayer> players) throws CommandSyntaxException {
        for (ServerPlayer player : players) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            if (!handler.setFaction(LevelingChange.maxLevel(faction).lordLevel(level).build())) {
                throw LORD_FAILED.create();
            }
            context.getSource().sendSuccess(() -> Component.translatable("command.vampirism.base.lord.successful", player.getName(), faction.value().getName(), handler.getLordLevel()), true);
        }
        return 0;
    }
}
