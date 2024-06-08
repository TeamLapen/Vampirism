package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.player.actions.IActionManager;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillManager;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVisionRegistry;
import de.teamlapen.vampirism.api.general.IBloodConversionRegistry;
import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.api.settings.ISettingsProvider;
import de.teamlapen.vampirism.api.world.IFogHandler;
import de.teamlapen.vampirism.api.world.IGarlicChunkHandler;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static de.teamlapen.vampirism.api.VampirismAttachments.*;

/**
 * Class for core api methods
 * Don't use before init since it is set up in pre-init
 */
public class VampirismAPI {

    private static boolean INIT;

    private static IFactionRegistry factionRegistry;
    private static ISundamageRegistry sundamageRegistry;
    private static IVampirismEntityRegistry entityRegistry;
    private static IVampireVisionRegistry vampireVisionRegistry;
    private static ISkillManager skillManager;
    private static IActionManager actionManager;
    private static IExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistry;
    private static ISettingsProvider settings;
    private static IBloodConversionRegistry bloodConversionRegistry;

    @Deprecated(forRemoval = true)
    public static ISkillManager skillManager() {
        return skillManager;
    }

    public static IActionManager actionManager() {
        return actionManager;
    }

    public static IVampireVisionRegistry vampireVisionRegistry() {
        return vampireVisionRegistry;
    }

    public static IFactionRegistry factionRegistry() {
        return factionRegistry;
    }

    public static ISundamageRegistry sundamageRegistry() {
        return sundamageRegistry;
    }

    public static IVampirismEntityRegistry entityRegistry() {
        return entityRegistry;
    }


    public static IExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistry() {
        return extendedBrewingRecipeRegistry;
    }

    public static ISettingsProvider settings() {
        return settings;
    }

    public static IBloodConversionRegistry bloodConversionRegistry() {
        return bloodConversionRegistry;
    }

    /**
     * Set up the API registries
     * FOR INTERNAL USAGE ONLY
     *
     * @throws IllegalStateException if the API was already setup
     */
    @ApiStatus.Internal
    public static void setUpRegistries(IFactionRegistry factionRegistryIn, ISundamageRegistry sundamageRegistryIn, IVampirismEntityRegistry entityRegistryIn, IActionManager actionManagerIn, ISkillManager skillManagerIn,
                                       IVampireVisionRegistry vampireVisionRegistryIn, IExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistryIn, ISettingsProvider settingsIn, IBloodConversionRegistry bloodConversionRegistryIn) {
        if (INIT) throw new IllegalStateException("Vampirism API can only be setup once");
        factionRegistry = factionRegistryIn;
        sundamageRegistry = sundamageRegistryIn;
        entityRegistry = entityRegistryIn;
        actionManager = actionManagerIn;
        skillManager = skillManagerIn;
        vampireVisionRegistry = vampireVisionRegistryIn;
        extendedBrewingRecipeRegistry = extendedBrewingRecipeRegistryIn;
        settings = settingsIn;
        bloodConversionRegistry = bloodConversionRegistryIn;
        INIT = true;
    }

    /**
     * Called once Vampirism has finished preparing the API, and it is ready to use.
     */
    @SuppressWarnings("EmptyMethod")
    @ApiStatus.Internal
    public static void onSetupComplete() {
        settings.syncSettingsCache();
    }

    /**
     * Get the {@link IFactionPlayerHandler} attachment for the given player
     *
     * @param player the player for which the attachment should be returned
     * @return the faction player handler for the given player
     */
    public static @NotNull IFactionPlayerHandler factionPlayerHandler(@NotNull Player player) {
        return player.getData(FACTION_PLAYER_HANDLER);
    }

    /**
     * Get the {@link IVampirePlayer} attachment for the given player
     *
     * @param player the player for which the attachment should be returned
     * @return the vampire player for the given player
     */
    public static @NotNull IVampirePlayer vampirePlayer(@NotNull Player player) {
        return player.getData(VAMPIRE_PLAYER);
    }

    /**
     * Get the {@link IHunterPlayer} attachment for the given player
     *
     * @param player the player for which the attachment should be returned
     * @return the hunter player for the given player
     */
    public static @NotNull IHunterPlayer hunterPlayer(@NotNull Player player) {
        return player.getData(HUNTER_PLAYER);
    }

    /**
     * Get the {@link IExtendedCreatureVampirism} instance for the given creature
     *
     * @param creature the creature for which the attachment should be returned
     * @return the extended creature vampirism for the given creature
     */
    public static @NotNull IExtendedCreatureVampirism extendedCreatureVampirism(@NotNull PathfinderMob creature) {
        return creature.getData(VampirismAttachments.EXTENDED_CREATURE);
    }

    /**
     * Get the {@link IGarlicChunkHandler} attachment for the given world
     *
     * @param w the world for which the attachment should be returned
     * @return the garlic chunk handler for the given world
     */
    public static @NotNull IGarlicChunkHandler garlicHandler(@NotNull Level w) {
        return w.getData(VampirismAttachments.GARLIC_HANDLER);
    }

    /**
     * Get the {@link IFogHandler} attachment for the given world
     *
     * @param w the world for which the attachment should be returned
     * @return the fog handler for the given world
     */
    public static @NotNull IFogHandler fogHandler(@NotNull Level w) {
        return w.getData(VampirismAttachments.FOG_HANDLER);
    }

    /**
     * @deprecated Use {@link #factionPlayerHandler(Player)}
     */
    @Deprecated
    public static @NotNull Optional<IFactionPlayerHandler> getFactionPlayerHandler(@NotNull Player player) {
        return Optional.of(player.getData(FACTION_PLAYER_HANDLER));
    }

    /**
     * @deprecated Use {@link #vampirePlayer(Player)}
     */
    @Deprecated
    public static @NotNull Optional<IVampirePlayer> getVampirePlayer(@NotNull Player player) {
        return Optional.of(player.getData(VAMPIRE_PLAYER));
    }

    /**
     * @deprecated Use {@link #hunterPlayer(Player)}
     */
    @Deprecated
    public static @NotNull Optional<IHunterPlayer> getHunterPlayer(@NotNull Player player) {
        return Optional.of(player.getData(HUNTER_PLAYER));
    }

    /**
     * @deprecated Use {@link #extendedCreatureVampirism(PathfinderMob)}
     */
    @Deprecated
    public static @NotNull Optional<IExtendedCreatureVampirism> getExtendedCreatureVampirism(@NotNull PathfinderMob creature) {
        return Optional.of(creature.getData(VampirismAttachments.EXTENDED_CREATURE));
    }

    /**
     * @deprecated Use {@link #garlicHandler(Level)}
     */
    @Deprecated
    public static @NotNull Optional<IGarlicChunkHandler> getGarlicHandler(@NotNull Level w) {
        return Optional.of(w.getData(VampirismAttachments.GARLIC_HANDLER));
    }

    /**
     * @deprecated Use {@link #fogHandler(Level)}
     */
    @Deprecated
    public static @NotNull Optional<IFogHandler> getFogHandler(@NotNull Level w) {
        return Optional.of(w.getData(VampirismAttachments.FOG_HANDLER));
    }
}
