package de.teamlapen.vampirism.api.world.entity.player.vampire;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import net.minecraft.core.Holder;

/**
 * Interface for Vampire Player's "vision", e.g. night vision or blood vision
 */
public interface IVampireVision {

    Codec<Holder<IVampireVision>> CODEC = Codec.lazyInitialized(() -> VampirismRegistries.VAMPIRE_VISION.get().holderByNameCodec());

    String getTranslationKey();

    void onActivated(IVampirePlayer player);

    void onDeactivated(IVampirePlayer player);

    void tick(IVampirePlayer player);

    default boolean isEnabled() {
        return true;
    }
}
