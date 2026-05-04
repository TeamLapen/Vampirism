package de.teamlapen.faction.common.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.LevelingChange;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.server.commands.arguments.FactionArgument;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.List;

public class FactionCommand extends BasicCommand {
    private static final SimpleCommandExceptionType LEVEL_UP_FAILED = new SimpleCommandExceptionType(Component.translatable("command.factionapi.base.lord.level_failed"));
    private static final SimpleCommandExceptionType LORD_FAILED = new SimpleCommandExceptionType(Component.translatable("command.factionapi.base.lord.failed"));

    public static void registerDev(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        HolderLookup.RegistryLookup<IFaction<?>> factions = buildContext.lookupOrThrow(FactionRegistries.Keys.FACTION);

        dispatcher.register(Commands.literal("level")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("faction", FactionArgument.factions(buildContext))
                .executes(context -> setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), Integer.MAX_VALUE, List.of(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(context -> setLevel(context, FactionArgument.getPlayableFaction(context, "faction"), IntegerArgumentType.getInteger(context, "level"), List.of(context.getSource().getPlayerOrException()))))));

        dispatcher.register(Commands.literal("lord")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            Holder<? extends IPlayableFaction<?>> faction = FactionPlayerHandler.get(player).getFaction();
                            return setLordLevel(context, faction, Integer.MAX_VALUE, List.of(player));
                        })
                .then(Commands.argument("faction", FactionArgument.factions(buildContext))
                .executes(context -> setLordLevel(context, FactionArgument.getPlayableFaction(context, "faction"), Integer.MAX_VALUE, List.of(context.getSource().getPlayerOrException())))
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                        .executes(context -> setLordLevel(context, FactionArgument.getPlayableFaction(context, "faction"), IntegerArgumentType.getInteger(context, "level"), List.of(context.getSource().getPlayerOrException()))))));
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        HolderLookup.RegistryLookup<IFaction<?>> factions = buildContext.lookupOrThrow(FactionRegistries.Keys.FACTION);

        var root = Commands.literal("faction").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS));

        var setCommand = Commands.literal("set");

        for (var listHolder : factions.listElements().toList()) {
            if (!(listHolder.value() instanceof IPlayableFaction<?> faction)){
                continue;
            }

            //noinspection unchecked
            var holder = (Holder.Reference<IPlayableFaction<?>>) (Object) listHolder;

            var factionCommand = Commands.literal(holder.key().identifier().toString());

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

            setCommand.then(factionCommand);
        }

        root.then(setCommand);

        root.then(Commands.literal("get")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> getFactionInfo(context, EntityArgument.getPlayer(context, "player")))
                )
        );

        return root;
    }

    public static int setLevel(CommandContext<CommandSourceStack> context, Holder<? extends IPlayableFaction<?>> faction, final int level, Collection<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            FactionPlayerHandler factionPlayerHandler = FactionPlayerHandler.get(player);
            int finalLevel = Math.min(level, faction.value().getHighestReachableLevel());
            if (factionPlayerHandler.setFaction(LevelingChange.builder().faction(faction).level(finalLevel).build())) {
                context.getSource().sendSuccess(() -> Component.translatable("command.factionapi.base.level.successful", player.getName(), faction.value().getName(), finalLevel), true);
            } else {
                context.getSource().sendFailure(players.size() > 1 ? Component.translatable("command.factionapi.failed_to_execute.players", player.getDisplayName()) : Component.translatable("command.factionapi.failed_to_execute"));
            }
        }
        return 0;
    }

    private static int setLordLevel(CommandContext<CommandSourceStack> context, Holder<? extends IPlayableFaction<?>> faction, final int level, Collection<ServerPlayer> players) throws CommandSyntaxException {
        for (ServerPlayer player : players) {
            FactionPlayerHandler handler = FactionPlayerHandler.get(player);
            if (!handler.setFaction(LevelingChange.maxLevel(faction).lordLevel(level).build())) {
                throw LORD_FAILED.create();
            }
            context.getSource().sendSuccess(() -> Component.translatable("command.factionapi.base.lord.successful", player.getName(), faction.value().getName(), handler.getLordLevel()), true);
        }
        return 0;
    }

    private static int getFactionInfo(CommandContext<CommandSourceStack> context, ServerPlayer player) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        context.getSource().sendSuccess(() -> Component.translatable("command.factionapi.base.get_level.successful", player.getDisplayName(), handler.getFaction().value().getName(), handler.getCurrentLevel(), handler.getLordLevel()), true);
        return 0;
    }
}
