package de.teamlapen.faction.api.factions;

import de.teamlapen.faction.api.FactionAttachments;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

/**
 * Describes how a faction extension registered via {@link FactionProperties#extension} is resolved for a player and
 * cleaned up when the player leaves the faction that provided it.
 */
public sealed interface FactionExtensionType<T> {

    Class<T> type();

    T get(Player player);

    void cleanup(Player player);

    /**
     * Backed by a {@link Holder} of an {@link AttachmentType}: the value is stored directly on the player.
     */
    record Attachment<T>(Class<T> type, Holder<AttachmentType<? extends T>> attachment) implements FactionExtensionType<T> {

        @Override
        public T get(Player player) {
            return player.getData(attachment.value());
        }

        @Override
        public void cleanup(Player player) {
            if (player.hasData(attachment.value())) {
                if (player.getData(attachment.value()) instanceof IFactionExtension ext) {
                    ext.onLeaveFaction(player);
                } else {
                    player.removeData(attachment.value());
                }
            }
        }
    }

    /**
     * Backed by a {@link DataComponentType}: the value is stored inside the player's {@link IFactionPlayerHandler}
     * component map, so it is synced/persisted alongside the rest of the handler's data without needing its own
     * attachment registration.
     */
    record Component<T>(Class<T> type, DataComponentType<T> component, T defaultValue) implements FactionExtensionType<T> {

        private static MutableDataComponentHolder components(Player player) {
            return player.getData(FactionAttachments.FACTION_PLAYER_HANDLER.get());
        }

        @Override
        public T get(Player player) {
            return components(player).getOrDefault(component, defaultValue);
        }

        @Override
        public void cleanup(Player player) {
            MutableDataComponentHolder components = components(player);
            if (components.get(component) instanceof IFactionExtension ext) {
                ext.onLeaveFaction(player);
            }
            components.set(component, defaultValue);
        }
    }
}
