package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Collection of simple input events that do not need any additional information
 */
public record ServerboundSimpleInputEvent(Type type) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "simple_input");

    public static final Codec<ServerboundSimpleInputEvent> CODEC = StringRepresentable.fromEnum(Type::values).xmap(ServerboundSimpleInputEvent::new, msg -> msg.type);
    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public enum Type implements StringRepresentable {
        FINISH_SUCK_BLOOD("finish_suck_blood"),
        RESET_SKILLS("reset_skills"),
        REVERT_BACK("revert_back"),
        TOGGLE_VAMPIRE_VISION("toggle_vampire_vision"),
        TRAINER_LEVELUP("trainer_levelup"),
        BASIC_HUNTER_LEVELUP("basic_hunter_levelup"),
        SHOW_MINION_CALL_SELECTION("show_minion_call_selection"),
        VAMPIRISM_MENU("vampirism_menu"),
        RESURRECT("resurrect"),
        GIVE_UP("give_up");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
