package de.teamlapen.vampirism.common.world.heritage;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.world.entity.vampire.IVampire;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.util.supporter.Supporter;
import de.teamlapen.vampirism.common.world.entity.vampire.AdvancedVampireEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Associates a completed conversion with the actor that initiated it.
 */
public final class HeritageManager {
    private HeritageManager() {
    }

    public static void beginSanguinareCompletion(ServerPlayer player) {
        HeritageData.get(player).beginPendingTransition();
    }

    public static void cancelPendingTransition(ServerPlayer player) {
        HeritageData.get(player).cancelPendingTransition();
    }

    public static void completeVampireTransition(ServerPlayer player) {
        HeritageData.get(player).completeVampireTransition(player);
    }

    public static void prepareForIndependentConversion(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            HeritageData.get(serverPlayer).prepare(HeritageData.PendingHeritage.independent());
        }
    }

    public static void prepareForVampireConversion(Player player, IVampire vampire) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (vampire instanceof IVampirePlayer vampirePlayer && vampirePlayer.asEntity() instanceof ServerPlayer parentPlayer) {
            HeritageData parentData = HeritageData.get(parentPlayer);
            parentData.ensureIndependentMembership(parentPlayer);
            HeritageMembership parentMembership = parentData.getMembership().orElseThrow();
            HeritageData.get(serverPlayer).prepare(HeritageData.PendingHeritage.player(parentMembership, parentPlayer.getUUID()));
            return;
        }

        Entity entity = vampire.asEntity();
        if (entity instanceof AdvancedVampireEntity advancedVampire) {
            String namedNpc = getNamedNpc(advancedVampire);
            if (namedNpc != null) {
                @Nullable Identifier definitionId = VampirismMod.services().heritageDefinitionId(namedNpc).orElse(null);
                HeritageData.get(serverPlayer).prepare(HeritageData.PendingHeritage.named(namedNpc, definitionId));
                return;
            }
        }
        HeritageData.get(serverPlayer).prepare(HeritageData.PendingHeritage.independent());
    }

    private static @Nullable String getNamedNpc(AdvancedVampireEntity vampire) {
        Supporter supporter = vampire.getData(ModAttachments.SUPPORTER);
        if (!supporter.player().isBlank()) {
            return supporter.player();
        }
        if (vampire.hasCustomName() && !vampire.getCustomName().getString().isBlank()) {
            return vampire.getCustomName().getString();
        }
        return null;
    }
}
