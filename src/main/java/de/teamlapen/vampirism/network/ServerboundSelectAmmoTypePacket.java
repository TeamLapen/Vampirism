package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.gui.screens.SelectAmmoScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public record ServerboundSelectAmmoTypePacket(boolean hasRestriction, @Nullable Item ammoId) implements CustomPacketPayload {

    public static final Type<ServerboundSelectAmmoTypePacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "select_ammo_type"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSelectAmmoTypePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ServerboundSelectAmmoTypePacket::hasRestriction,
            ByteBufCodecs.optional(ByteBufCodecs.registry(Registries.ITEM)), pkt -> Optional.ofNullable(pkt.ammoId),
            ServerboundSelectAmmoTypePacket::new
    );
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ServerboundSelectAmmoTypePacket(boolean hasRestriction, Optional<Item> ammoId) {
        this(hasRestriction, ammoId.orElse(null));
    }

    public ServerboundSelectAmmoTypePacket(boolean hasRestriction, @Nullable Item ammoId) {
        this.hasRestriction = hasRestriction;
        this.ammoId = ammoId;
        if (hasRestriction) {
            Objects.requireNonNull(ammoId);
        }
    }

    public static ServerboundSelectAmmoTypePacket of(SelectAmmoScreen.AmmoType ammoType) {
        if (ammoType.renderStack == null)  {
            return new ServerboundSelectAmmoTypePacket(false, (Item) null);
        } else {
            return new ServerboundSelectAmmoTypePacket(true, ammoType.renderStack.getItem());
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
