package de.teamlapen.vampirism.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IMinionEntryBuilder;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntry;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;


public class MinionCommand extends BasicCommand {
    private static final DynamicCommandExceptionType fail = new DynamicCommandExceptionType((msg) -> Component.literal("Failed: " + msg));

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("minion")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(registerNew())
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
    public static ArgumentBuilder<CommandSourceStack, ?> registerNew() {
        LiteralArgumentBuilder<CommandSourceStack> spawnNew = Commands.literal("spawnNew");
        @Unmodifiable Map<Holder<? extends IPlayableFaction<?>>, List<Pair<ResourceKey<IMinionEntry<?, ?>>, IMinionEntry<?, ?>>>> minionEntries = VampirismAPI.factionRegistry().getFactionMinionEntries();
        for (Map.Entry<Holder<? extends IPlayableFaction<?>>, List<Pair<ResourceKey<IMinionEntry<?, ?>>, IMinionEntry<?, ?>>>> factionEntry : minionEntries.entrySet()) {

            List<Pair<ResourceKey<IMinionEntry<?, ?>>, IMinionEntry<?, ?>>> minions = factionEntry.getValue();
            for (Pair<ResourceKey<IMinionEntry<?, ?>>, IMinionEntry<?, ?>> minion : minions) {
                if (minion.getValue().type() == null) {
                    continue;
                }

                Holder<? extends IPlayableFaction<?>> faction = factionEntry.getKey();
                ArgumentBuilder<CommandSourceStack, ?> currentCommand = null;
                List<? extends IMinionEntryBuilder.IMinionCommandBuilder.ICommandEntry<?, ?>> iCommandEntries = minion.getValue().commandArguments();
                for (int i = iCommandEntries.size() - 1; i >= 0; i--) {
                    IMinionEntryBuilder.IMinionCommandBuilder.ICommandEntry<?, ?> iCommandEntry = iCommandEntries.get(i);
                    int finalI = i;
                    var builder = Commands.argument(iCommandEntry.name(), iCommandEntry.type()).executes(context -> spawnNewMinionExtra(context, context.getSource(), faction, (Supplier<MinionData>) minion.getValue().data(), minion.getValue().type(), (Collection<IMinionEntryBuilder.IMinionCommandBuilder.ICommandEntry<MinionData, ?>>) iCommandEntries.subList(0, finalI + 1), (Collection<IMinionEntryBuilder.IMinionCommandBuilder.ICommandEntry<MinionData, ?>>) iCommandEntries.subList(finalI + 1, iCommandEntries.size())));
                    if (currentCommand != null) {
                        builder.then(currentCommand);
                    }
                    currentCommand = builder;
                }
                spawnNew.then(Commands.literal(minion.getKey().location().toString()).executes(context -> spawnNewMinionExtra(context, context.getSource(), faction, (Supplier<MinionData>) minion.getValue().data(), minion.getValue().type(), List.of(), (Collection<IMinionEntryBuilder.IMinionCommandBuilder.ICommandEntry<MinionData, ?>>) iCommandEntries)).then(currentCommand));
            }
        }
        return spawnNew;
    }

    @SuppressWarnings("unchecked")
    private static <T extends MinionData> int spawnNewMinionExtra(@NotNull CommandContext<CommandSourceStack> source, @NotNull CommandSourceStack ctx, Holder<? extends IPlayableFaction<?>> faction, @NotNull Supplier<T> data, Supplier<EntityType<? extends IMinionEntity>> type, Collection<IMinionEntryBuilder.IMinionCommandBuilder.ICommandEntry<T, ?>> contextProvider, Collection<IMinionEntryBuilder.IMinionCommandBuilder.ICommandEntry<T, ?>> defaultProvider) throws CommandSyntaxException {
        T t = data.get();
        for (IMinionEntryBuilder.IMinionCommandBuilder.ICommandEntry<T, ?> tiCommandEntry : contextProvider) {
            ((BiConsumer<T, Object>) tiCommandEntry.setter()).accept(t, tiCommandEntry.getter().apply(source, tiCommandEntry.name()));
        }
        for (IMinionEntryBuilder.IMinionCommandBuilder.ICommandEntry<T, ?> tiCommandEntry : defaultProvider) {
            ((BiConsumer<T, Object>) tiCommandEntry.setter()).accept(t, tiCommandEntry.defaultValue());
        }
        t.setHealth(t.getMaxHealth());
        return spawnNewMinion(ctx, faction, t, type.get());
    }

    @SuppressWarnings("SameReturnValue")
    private static <T extends IMinionData> int spawnNewMinion(@NotNull CommandSourceStack ctx, Holder<? extends IPlayableFaction<?>> faction, @NotNull T data, EntityType<? extends IMinionEntity> type) throws CommandSyntaxException {
        Player p = ctx.getPlayerOrException();
        FactionPlayerHandler fph = handler(p);

        PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(fph);
        if (controller.hasFreeMinionSlot()) {

            if (IFaction.is(fph.getFaction(), faction)) {
                @SuppressWarnings("unchecked") int id = controller.createNewMinionSlot((MinionData) data, (EntityType<? extends MinionEntity<?>>) type);
                if (id < 0) {
                    throw fail.create("Failed to get new minion slot");
                }
                controller.createMinionEntityAtPlayer(id, p);
            } else {
                throw fail.create("Wrong faction");
            }


        } else {
            throw fail.create("No free slot");
        }

        return 0;
    }

    private static FactionPlayerHandler handler(Player player) {
        FactionPlayerHandler handler = FactionPlayerHandler.get(player);
        if (handler.getMaxMinions() <= 0) {
            throw new IllegalArgumentException("Can't have minions");
        }
        return handler;
    }

    @SuppressWarnings("SameReturnValue")
    private static int recall(@NotNull CommandSourceStack ctx, ServerPlayer player) throws CommandSyntaxException {
        FactionPlayerHandler factionPlayerHandler = handler(player);
        PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(factionPlayerHandler);
        Collection<Integer> ids = controller.recallMinions(true);
        for (Integer id : ids) {
            controller.createMinionEntityAtPlayer(id, player);
        }

        return 0;
    }


    @SuppressWarnings("SameReturnValue")
    private static int respawn(@NotNull CommandSourceStack ctx, ServerPlayer player) throws CommandSyntaxException {
        FactionPlayerHandler fph = handler(player);
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
        ((Player) player).displayClientMessage(Component.literal("Reload world"), false);
        return 0;
    }
}
