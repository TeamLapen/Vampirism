package de.teamlapen.vampirism.api.entity.player.vampire;

/**
 * Interface for Vampire Player's "vision", e.g. night vision or blood vision
 */
public interface IVampireVision {

    String getTranslationKey();

    void onActivated(IVampirePlayer player);

    void onDeactivated(IVampirePlayer player);

    void tick(IVampirePlayer player);

    default boolean isEnabled() {
        return true;
    }
}
