package de.teamlapen.faction.common.server.commands.minion;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntry;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.factions.minions.MinionData;
import de.teamlapen.faction.common.factions.minions.MinionWorldData;
import de.teamlapen.faction.common.factions.minions.PlayerMinionController;
import de.teamlapen.faction.common.server.commands.BasicCommand;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;


public class MinionCommand extends BasicCommand {
    private static final DynamicCommandExceptionType WRONG_FACTION = new DynamicCommandExceptionType((msg) -> Component.literal("You are not of the same faction as the minion: " + msg));
    private static final SimpleCommandExceptionType CANT_HAVE_MINIONS = new SimpleCommandExceptionType(Component.literal("You cannot have minions"));
    private static final SimpleCommandExceptionType NO_FREE_SLOT = new SimpleCommandExceptionType(Component.literal("You cannot have more minions"));
    private static final SimpleCommandExceptionType ERROR_GETTING_SLOT = new SimpleCommandExceptionType(Component.literal("An error occurred creating the minion"));

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return Commands.literal("minion")
                .then(MinionInventoryCommand.register(buildContext))
                .then(registerNew(buildContext)
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)))
                .then(Commands.literal("recall")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(context -> recall(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> recall(context.getSource(), EntityArgument.getPlayer(context, "target")))))
                .then(Commands.literal("respawn")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(context -> respawn(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> respawn(context.getSource(), EntityArgument.getPlayer(context, "target")))))
                .then(Commands.literal("purge")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(context -> purge(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> purge(context.getSource(), EntityArgument.getPlayer(context, "target")))));
    }

    public static ArgumentBuilder<CommandSourceStack, ?> registerNew(CommandBuildContext buildContext) {
        LiteralArgumentBuilder<CommandSourceStack> spawnNew = Commands.literal("create");
        var minionRegistry = buildContext.lookupOrThrow(FactionRegistries.Keys.MINION);
        for (Holder<IMinionEntry<?>> entry : minionRegistry.listElements().toList()) {
            var minion = entry.value();
            var faction = minion.faction();

            spawnNew.then(
                    Commands.literal(faction.unwrapKey().orElseThrow().identifier().toString())
                            .executes(context -> spawnNewMinion(context.getSource(), faction, entry)));

        }
        return spawnNew;
    }

    @SuppressWarnings("SameReturnValue")
    private static int spawnNewMinion(CommandSourceStack ctx, Holder<? extends IPlayableFaction<?>> faction, Holder<IMinionEntry<?>> entry) throws CommandSyntaxException {
        Player p = ctx.getPlayerOrException();
        FactionPlayerHandler handler = FactionPlayerHandler.get(p);
        if(!handler.isInFaction(faction)) {
            throw WRONG_FACTION.create(faction.value().getName().getString());
        }
        var lordPlayer = handler.getPlayerLord();

        if (lordPlayer.isEmpty()) {
            throw CANT_HAVE_MINIONS.create();
        }

        ILordPlayer fph = lordPlayer.get();

        PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(fph);
        if (controller.hasFreeMinionSlot()) {
                MinionData data = (MinionData) entry.value().createData(handler.factionPlayer(), (IMinionEntry) entry.value());
                int id = controller.createNewMinionSlot(data, entry.value().type().value());
                if (id < 0) {
                    throw ERROR_GETTING_SLOT.create();
                }
                var minion = controller.createMinionEntityAtPlayer(id, p);
                if (minion != null) {
                    minion.setHealth(minion.getMaxHealth());
                }
        } else {
            throw NO_FREE_SLOT.create();
        }

        return 0;
    }

    private static ILordPlayer handler(Player player) {
        return FactionPlayerHandler.get(player).getPlayerLord().filter(x -> x.getMaxMinions() > 0).orElseThrow(() -> new IllegalArgumentException("Can't have minions"));
    }

    @SuppressWarnings("SameReturnValue")
    private static int recall(CommandSourceStack ctx, ServerPlayer player) throws CommandSyntaxException {
        ILordPlayer factionPlayerHandler = handler(player);
        PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(factionPlayerHandler);
        Collection<Integer> ids = controller.recallMinions(true);
        for (Integer id : ids) {
            controller.createMinionEntityAtPlayer(id, player);
        }

        return 0;
    }


    @SuppressWarnings("SameReturnValue")
    private static int respawn(CommandSourceStack ctx, ServerPlayer player) throws CommandSyntaxException {
        ILordPlayer fph = handler(player);
        PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(fph);
        Collection<Integer> ids = controller.getUnclaimedMinions();
        for (Integer id : ids) {
            controller.createMinionEntityAtPlayer(id, player);
        }
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int purge(CommandSourceStack ctx, ServerPlayer player) throws CommandSyntaxException {
        MinionWorldData.getData(ctx.getServer()).purgeController(player.getUUID());
        player.sendSystemMessage(Component.literal("Reload world"), false);
        return 0;
    }
}
