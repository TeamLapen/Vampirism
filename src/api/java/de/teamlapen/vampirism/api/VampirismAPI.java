package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.player.actions.IActionManager;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
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

import static de.teamlapen.vampirism.api.VampirismAttachments.*;

/**
 * Class for core api methods
 * Don't use before init since it is set up in pre-init
 */
public class VampirismAPI {

    private static boolean INIT;

    private static IVampirismServices services;

    /**
     * @deprecated Use services() with {@link IVampirismServices#actionManager()}
     */
    @Deprecated
    public static IActionManager actionManager() {
        return services.actionManager();
    }

    /**
     * @deprecated Use services() with {@link IVampirismServices#visionRegistry()}
     */
    @Deprecated
    public static IVampireVisionRegistry vampireVisionRegistry() {
        return services.visionRegistry();
    }

    /**
     * @deprecated Use services() with {@link IVampirismServices#factionRegistry()}
     */
    @Deprecated
    public static IFactionRegistry factionRegistry() {
        return services.factionRegistry();
    }

    /**
     * @deprecated Use services() with {@link IVampirismServices#sundamageRegistry()}
     */
    @Deprecated
    public static ISundamageRegistry sundamageRegistry() {
        return services.sundamageRegistry() ;
    }

    /**
     * @deprecated Use services() with {@link IVampirismServices#entityRegistry()}
     */
    @Deprecated
    public static IVampirismEntityRegistry entityRegistry() {
        return services.entityRegistry();
    }


    /**
     * @deprecated Use services() with {@link IVampirismServices#extendedBrewingRecipeRegistry()}
     */
    @Deprecated
    public static IExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistry() {
        return services.extendedBrewingRecipeRegistry();
    }

    /**
     * @deprecated Use services() with {@link IVampirismServices#settings()}
     */
    @Deprecated
    public static ISettingsProvider settings() {
        return services.settings();
    }

    /**
     * @deprecated Use services() with {@link IVampirismServices#bloodConversionRegistry()}
     */
    @Deprecated
    public static IBloodConversionRegistry bloodConversionRegistry() {
        return services.bloodConversionRegistry();
    }

    public static IVampirismServices services() {
        return services;
    }

    /**
     * Set up the API registries
     * FOR INTERNAL USAGE ONLY
     *
     * @throws IllegalStateException if the API was already setup
     */
    @ApiStatus.Internal
    public static void setUpRegistries(IVampirismServices services) {
        if (INIT) throw new IllegalStateException("Vampirism API can only be setup once");
        VampirismAPI.services = services;
        INIT = true;
    }

    /**
     * Get the {@link IFactionPlayerHandler} attachment for the given player
     *
     * @param player the player for which the attachment should be returned
     * @return the faction player handler for the given player
     */
    public static IFactionPlayerHandler factionPlayerHandler(Player player) {
        return player.getData(FACTION_PLAYER_HANDLER);
    }

    /**
     * Get the {@link IVampirePlayer} attachment for the given player
     *
     * @param player the player for which the attachment should be returned
     * @return the vampire player for the given player
     */
    public static IVampirePlayer vampirePlayer(Player player) {
        return player.getData(VAMPIRE_PLAYER);
    }

    /**
     * Get the {@link IHunterPlayer} attachment for the given player
     *
     * @param player the player for which the attachment should be returned
     * @return the hunter player for the given player
     */
    public static IHunterPlayer hunterPlayer(Player player) {
        return player.getData(HUNTER_PLAYER);
    }

    /**
     * Get the {@link IExtendedCreatureVampirism} instance for the given creature
     *
     * @param creature the creature for which the attachment should be returned
     * @return the extended creature vampirism for the given creature
     */
    public static IExtendedCreatureVampirism extendedCreatureVampirism(PathfinderMob creature) {
        return creature.getData(VampirismAttachments.EXTENDED_CREATURE);
    }

    /**
     * Get the {@link IGarlicChunkHandler} attachment for the given world
     *
     * @param w the world for which the attachment should be returned
     * @return the garlic chunk handler for the given world
     */
    public static IGarlicChunkHandler garlicHandler(Level w) {
        return w.getData(VampirismAttachments.GARLIC_HANDLER);
    }

    /**
     * Get the {@link IFogHandler} attachment for the given world
     *
     * @param w the world for which the attachment should be returned
     * @return the fog handler for the given world
     */
    public static IFogHandler fogHandler(Level w) {
        return w.getData(VampirismAttachments.FOG_HANDLER);
    }

}
