package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.gui.screens.SelectAmmoScreen;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public record ServerboundSelectAmmoTypePacket(boolean hasRestriction, @Nullable ResourceLocation ammoId) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "select_ammo_type");
    public static final Codec<ServerboundSelectAmmoTypePacket> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.BOOL.fieldOf("has_restriction").forGetter(ServerboundSelectAmmoTypePacket::hasRestriction),
                    ExtraCodecs.strictOptionalField(ResourceLocation.CODEC, "ammo_id").forGetter(s -> java.util.Optional.ofNullable(s.ammoId))
            ).apply(inst, ServerboundSelectAmmoTypePacket::new)
    );
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ServerboundSelectAmmoTypePacket(boolean hasRestriction, Optional<ResourceLocation> ammoId) {
        this(hasRestriction, ammoId.orElse(null));
    }

    public ServerboundSelectAmmoTypePacket(boolean hasRestriction, @Nullable ResourceLocation ammoId) {
        this.hasRestriction = hasRestriction;
        this.ammoId = ammoId;
        if (hasRestriction) {
            Objects.requireNonNull(ammoId);
        }
    }

    public static ServerboundSelectAmmoTypePacket of(SelectAmmoScreen.AmmoType ammoType) {
        if (ammoType.renderStack == null)  {
            return new ServerboundSelectAmmoTypePacket(false, (ResourceLocation) null);
        } else {
            return new ServerboundSelectAmmoTypePacket(true, RegUtil.id(ammoType.renderStack.getItem()));
        }
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
