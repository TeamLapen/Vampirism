package de.teamlapen.factions.common.server.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.factions.lord.ILordPlayer;
import de.teamlapen.factions.api.factions.lord.IMinionEntryBuilder;
import de.teamlapen.factions.api.world.entities.minion.IMinionData;
import de.teamlapen.factions.api.world.entities.minion.IMinionEntity;
import de.teamlapen.factions.api.world.entities.minion.IMinionEntry;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.factions.minions.MinionData;
import de.teamlapen.factions.common.factions.minions.MinionEntity;
import de.teamlapen.factions.common.factions.minions.MinionWorldData;
import de.teamlapen.factions.common.factions.minions.PlayerMinionController;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;


public class MinionCommand extends BasicCommand {
    private static final DynamicCommandExceptionType WRONG_FACTION = new DynamicCommandExceptionType((msg) -> Component.literal("You are not of the same faction as the minion: " + msg));
    private static final SimpleCommandExceptionType CANT_HAVE_MINIONS = new SimpleCommandExceptionType(Component.literal("You cannot have minions"));
    private static final SimpleCommandExceptionType NO_FREE_SLOT = new SimpleCommandExceptionType(Component.literal("You cannot have more minions"));
    private static final SimpleCommandExceptionType ERROR_GETTING_SLOT = new SimpleCommandExceptionType(Component.literal("An error occurred creating the minion"));

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return Commands.literal("minion")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(registerNew(buildContext))
                .then(Commands.literal("recall")
                        .executes(context -> recall(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> recall(context.getSource(), EntityArgument.getPlayer(context, "target")))))
                .then(Commands.literal("respawnAll")
                        .executes(context -> respawn(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> respawn(context.getSource(), EntityArgument.getPlayer(context, "target")))))
                .then(Commands.literal("purge")
                        .executes(context -> purge(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> purge(context.getSource(), EntityArgument.getPlayer(context, "target")))));
    }

    @SuppressWarnings("unchecked")
    public static ArgumentBuilder<CommandSourceStack, ?> registerNew(CommandBuildContext buildContext) {
        LiteralArgumentBuilder<CommandSourceStack> spawnNew = Commands.literal("spawnNew");
        var minionRegistry = buildContext.lookupOrThrow(FactionRegistries.Keys.MINION);
        for (Holder<IMinionEntry<?, ?>> entry : minionRegistry.listElements().toList()) {
            var minion = entry.value();
            var faction = minion.faction();

            ArgumentBuilder<CommandSourceStack, ?> currentCommand = null;
            List<? extends IMinionEntryBuilder.IMinionCommandBuilder.ICommandArgument<?, ?>> arguments = minion.commandArguments();

            for (int i = arguments.size() - 1; i >= 0; i--) {
                IMinionEntryBuilder.IMinionCommandBuilder.ICommandArgument<?, ?> argument = arguments.get(i);
                int finalI = i;
                var builder = Commands.argument(argument.name(), argument.type()).executes(context -> spawnNewMinionExtra(context, context.getSource(), faction, (Supplier<MinionData>) minion.data(), minion.type(), (Collection<IMinionEntryBuilder.IMinionCommandBuilder.ICommandArgument<MinionData, ?>>) arguments.subList(0, finalI + 1), (Collection<IMinionEntryBuilder.IMinionCommandBuilder.ICommandArgument<MinionData, ?>>) arguments.subList(finalI + 1, arguments.size())));
                if (currentCommand != null) {
                    builder.then(currentCommand);
                }
                currentCommand = builder;
            }
            spawnNew.then(Commands.literal(entry.getKey().identifier().toString()).executes(context -> spawnNewMinionExtra(context, context.getSource(), faction, (Supplier<MinionData>) minion.data(), minion.type(), List.of(), (Collection<IMinionEntryBuilder.IMinionCommandBuilder.ICommandArgument<MinionData, ?>>) arguments)).then(currentCommand));

        }
        return spawnNew;
    }

    @SuppressWarnings("unchecked")
    private static <T extends MinionData> int spawnNewMinionExtra(@NotNull CommandContext<CommandSourceStack> source, @NotNull CommandSourceStack ctx, Holder<? extends IPlayableFaction<?>> faction, @NotNull Supplier<T> data, Supplier<EntityType<? extends IMinionEntity>> type, Collection<IMinionEntryBuilder.IMinionCommandBuilder.ICommandArgument<T, ?>> contextProvider, Collection<IMinionEntryBuilder.IMinionCommandBuilder.ICommandArgument<T, ?>> defaultProvider) throws CommandSyntaxException {
        T t = data.get();
        for (IMinionEntryBuilder.IMinionCommandBuilder.ICommandArgument<T, ?> tiCommandEntry : contextProvider) {
            ((BiConsumer<T, Object>) tiCommandEntry.setter()).accept(t, tiCommandEntry.getter().apply(source, tiCommandEntry.name()));
        }
        for (IMinionEntryBuilder.IMinionCommandBuilder.ICommandArgument<T, ?> tiCommandEntry : defaultProvider) {
            ((BiConsumer<T, Object>) tiCommandEntry.setter()).accept(t, tiCommandEntry.defaultValue());
        }
        t.setHealth(t.getMaxHealth());
        return spawnNewMinion(ctx, faction, t, type.get());
    }

    @SuppressWarnings("SameReturnValue")
    private static <T extends IMinionData> int spawnNewMinion(@NotNull CommandSourceStack ctx, Holder<? extends IPlayableFaction<?>> faction, @NotNull T data, EntityType<? extends IMinionEntity> type) throws CommandSyntaxException {
        Player p = ctx.getPlayerOrException();
        FactionPlayerHandler handler = FactionPlayerHandler.get(p);
        if(!handler.isInFaction(faction)) {
            throw WRONG_FACTION.create(faction.value().getName());
        }
        var lordPlayer = handler.getLordPlayer();

        if (lordPlayer.isEmpty()) {
            throw CANT_HAVE_MINIONS.create();
        }

        ILordPlayer<?> fph = lordPlayer.get();

        PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(fph);
        if (controller.hasFreeMinionSlot()) {

                @SuppressWarnings("unchecked")
                int id = controller.createNewMinionSlot((MinionData) data, (EntityType<? extends MinionEntity<?>>) type);
                if (id < 0) {
                    throw ERROR_GETTING_SLOT.create();
                }
                controller.createMinionEntityAtPlayer(id, p);
        } else {
            throw NO_FREE_SLOT.create();
        }

        return 0;
    }

    private static ILordPlayer<?> handler(Player player) {
        return FactionPlayerHandler.get(player).getLordPlayer().filter(x -> x.getMaxMinions() > 0).orElseThrow(() -> new IllegalArgumentException("Can't have minions"));
    }

    @SuppressWarnings("SameReturnValue")
    private static int recall(@NotNull CommandSourceStack ctx, ServerPlayer player) throws CommandSyntaxException {
        ILordPlayer<?> factionPlayerHandler = handler(player);
        PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(factionPlayerHandler);
        Collection<Integer> ids = controller.recallMinions(true);
        for (Integer id : ids) {
            controller.createMinionEntityAtPlayer(id, player);
        }

        return 0;
    }


    @SuppressWarnings("SameReturnValue")
    private static int respawn(@NotNull CommandSourceStack ctx, ServerPlayer player) throws CommandSyntaxException {
        ILordPlayer<?> fph = handler(player);
        PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(fph);
        Collection<Integer> ids = controller.getUnclaimedMinions();
        for (Integer id : ids) {
            controller.createMinionEntityAtPlayer(id, player);
        }
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int purge(@NotNull CommandSourceStack ctx, ServerPlayer player) throws CommandSyntaxException {
        MinionWorldData.getData(ctx.getServer()).purgeController(player.getUUID());
        player.displayClientMessage(Component.literal("Reload world"), false);
        return 0;
    }
}
