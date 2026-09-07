package de.teamlapen.vampirism.common.server.commands.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.faction.common.server.commands.BasicCommand;
import de.teamlapen.vampirism.common.world.heritage.HeritageMembership;
import de.teamlapen.vampirism.common.world.heritage.HeritageManager;
import de.teamlapen.vampirism.common.world.heritage.HeritageWorldData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

public final class HeritageDebugCommand extends BasicCommand {
    private HeritageDebugCommand() {
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("heritage")
                .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                .then(Commands.literal("status")
                        .executes(context -> show(context.getSource(), context.getSource().getPlayerOrException())))
                .then(Commands.literal("run_away")
                        .executes(context -> runAway(context.getSource(), context.getSource().getPlayerOrException())));
    }

    private static int show(CommandSourceStack source, ServerPlayer player) {
        HeritageWorldData heritageData = HeritageWorldData.getData(player.level().getServer());
        Optional<HeritageMembership> pending = HeritageManager.getPendingMembership(player);
        Optional<HeritageMembership> stored = HeritageManager.getMembership(player);
        var history = heritageData.getHeritagesForPlayer(player.getUUID());
        if (pending.isEmpty() && stored.isEmpty() && history.isEmpty()) {
            source.sendFailure(Component.literal("No heritage conversion has been detected for you."));
            return 0;
        }
        pending.ifPresent(membership -> source.sendSuccess(() -> describe("Pending", membership, 0), false));
        stored.ifPresent(membership -> {
            int members = heritageData.getMembers(membership.heritageId()).size();
            source.sendSuccess(() -> describe("Current", membership, members), false);
        });
        UUID currentId = stored.map(HeritageMembership::heritageId).orElse(null);
        history.stream()
                .filter(heritage -> !heritage.heritageId().equals(currentId))
                .sorted(Comparator.comparing(heritage -> heritage.heritageId().toString()))
                .forEach(heritage -> source.sendSuccess(() -> describe("Previous", toMembership(heritage), heritageData.getMembers(heritage.heritageId()).size()), false));
        return 1;
    }

    private static int runAway(CommandSourceStack source, ServerPlayer player) {
        if (HeritageManager.getMembership(player).isEmpty()) {
            source.sendFailure(Component.literal("No stored heritage exists for you."));
            return 0;
        }
        HeritageManager.runAwayFromHeritage(player);
        source.sendSuccess(() -> Component.literal("You ran away from your heritage and received a new independent heritage."), false);
        return 1;
    }

    private static Component describe(String state, HeritageMembership membership, int members) {
        String parent = membership.parentPlayerId() == null ? "none" : membership.parentPlayerId().toString();
        String namedNpc = membership.namedNpc() == null ? "none" : membership.namedNpc();
        return Component.literal("%s heritage: origin=%s, id=%s, parent=%s, named_npc=%s, members=%d"
                .formatted(state, membership.origin().getSerializedName(), membership.heritageId(), parent, namedNpc, members));
    }

    private static HeritageMembership toMembership(HeritageWorldData.HeritageHistory history) {
        HeritageWorldData.HeritageMember member = history.member();
        return new HeritageMembership(history.heritageId(), member.origin(), member.parentPlayerId(), history.namedNpc(), member.parentNpcId());
    }
}
