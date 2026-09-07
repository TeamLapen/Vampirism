package de.teamlapen.vampirism.common.world.heritage;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.world.entity.vampire.IVampire;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.util.supporter.Supporter;
import de.teamlapen.vampirism.common.world.entity.vampire.AdvancedVampireEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * Coordinates heritage state transitions and associates conversions with their source.
 */
public final class HeritageManager {
    private HeritageManager() {
    }

    public static Optional<HeritageMembership> getMembership(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return Optional.empty();
        }
        return getData(serverPlayer).getMembership(serverPlayer.getUUID());
    }

    public static Optional<HeritageMembership> getPendingMembership(ServerPlayer player) {
        return getData(player).getPendingMembership(player.getUUID());
    }

    public static void beginSanguinareCompletion(ServerPlayer player) {
        getData(player).beginPendingTransition(player.getUUID());
    }

    public static void cancelPendingTransition(ServerPlayer player) {
        getData(player).cancelPendingTransition(player.getUUID());
    }

    public static void completeVampireTransition(ServerPlayer player) {
        getData(player).completeVampireTransition(player);
    }

    public static void prepareForIndependentConversion(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            getData(serverPlayer).prepare(serverPlayer.getUUID(), independentMembership());
        }
    }

    public static void runAwayFromHeritage(ServerPlayer player) {
        getData(player).runAwayFromHeritage(player);
    }

    public static void prepareForVampireConversion(Player player, IVampire vampire) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (vampire instanceof IVampirePlayer vampirePlayer && vampirePlayer.asEntity() instanceof ServerPlayer parentPlayer) {
            HeritageWorldData parentData = getData(parentPlayer);
            parentData.ensureIndependentMembership(parentPlayer);
            HeritageMembership parentMembership = parentData.getMembership(parentPlayer.getUUID()).orElseThrow();
            getData(serverPlayer).prepare(serverPlayer.getUUID(), new HeritageMembership(
                    parentMembership.heritageId(),
                    HeritageOrigin.INHERITED,
                    parentPlayer.getUUID(),
                    parentMembership.namedNpc(),
                    null
            ));
            return;
        }

        Entity entity = vampire.asEntity();
        if (entity instanceof AdvancedVampireEntity advancedVampire) {
            Supporter supporter = advancedVampire.getData(ModAttachments.SUPPORTER);
            if (!supporter.player().isBlank()) {
                var heritage = VampirismMod.services().supporterManager().getPredefinedHeritage(supporter.player());
                if (heritage.isPresent()) {
                    getData(serverPlayer).prepare(serverPlayer.getUUID(), namedMembership(heritage.get().id(), supporter.player()));
                    return;
                }
            }
            String namedNpc = getNamedNpc(advancedVampire);
            if (namedNpc != null) {
                getData(serverPlayer).prepare(serverPlayer.getUUID(), namedMembership(namedNpc, null));
                return;
            }
        }
        getData(serverPlayer).prepare(serverPlayer.getUUID(), independentMembership());
    }

    private static @Nullable String getNamedNpc(AdvancedVampireEntity vampire) {
        Supporter supporter = vampire.getData(ModAttachments.SUPPORTER);
        if (supporter.heritage().isPresent() && !supporter.player().isBlank()) {
            return supporter.player();
        }
        if (vampire.hasCustomName() && !vampire.getCustomName().getString().isBlank()) {
            return vampire.getCustomName().getString();
        }
        return null;
    }

    private static HeritageWorldData getData(ServerPlayer player) {
        return HeritageWorldData.getData(player.level().getServer());
    }

    private static HeritageMembership independentMembership() {
        return new HeritageMembership(UUID.randomUUID(), HeritageOrigin.INDEPENDENT, null, null, null);
    }

    private static HeritageMembership namedMembership(String namedNpc, @Nullable String parentNpcId) {
        return new HeritageMembership(HeritageWorldData.idForNamedNpc(namedNpc), HeritageOrigin.INHERITED, null, namedNpc, parentNpcId);
    }
}
