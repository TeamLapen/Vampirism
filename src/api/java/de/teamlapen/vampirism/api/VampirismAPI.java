package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionManager;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.player.actions.IActionManager;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillManager;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVisionRegistry;
import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.api.world.IVampirismWorld;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Class for core api methods
 * Don't use before init since it is set up in pre-init
 */
public class VampirismAPI {

    private static boolean INIT;
    private static final Capability<IExtendedCreatureVampirism> CAP_CREATURE = CapabilityManager.get(new CapabilityToken<>() {
    });
    private static final Capability<IFactionPlayerHandler> CAP_FACTION_HANDLER_PLAYER = CapabilityManager.get(new CapabilityToken<>(){});
    private static final Capability<IVampirismWorld> CAP_WORLD = CapabilityManager.get(new CapabilityToken<>(){});
    private static final Capability<IVampirePlayer> CAP_VAMPIRE = CapabilityManager.get(new CapabilityToken<>(){});
    private static final Capability<IHunterPlayer> CAP_HUNTER = CapabilityManager.get(new CapabilityToken<>(){});

    private static IFactionRegistry factionRegistry;
    private static ISundamageRegistry sundamageRegistry;
    private static IVampirismEntityRegistry entityRegistry;
    private static IVampireVisionRegistry vampireVisionRegistry;
    private static ISkillManager skillManager;
    private static IActionManager actionManager;
    private static IEntityActionManager entityActionManager;
    private static IExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistry;

    public static ISkillManager skillManager() {
        return skillManager;
    }

    public static IActionManager actionManager() {
        return actionManager;
    }

    public static IEntityActionManager entityActionManager() {
        return entityActionManager;
    }

    public static IVampireVisionRegistry vampireVisionRegistry() {
        return vampireVisionRegistry;
    }

    /**
     * @return The faction registry
     */
    public static IFactionRegistry factionRegistry() {
        return factionRegistry;
    }

    /**
     * @return The sun-damage registry
     */
    public static ISundamageRegistry sundamageRegistry() {
        return sundamageRegistry;
    }

    /**
     * @return The vampirism entity registry
     */
    public static IVampirismEntityRegistry entityRegistry() {
        return entityRegistry;
    }


    public static IExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistry() {
        return extendedBrewingRecipeRegistry;
    }

    /**
     * Set up the API registries
     * FOR INTERNAL USAGE ONLY
     *
     * @throws IllegalStateException if the API was already setup
     */
    @ApiStatus.Internal
    public static void setUpRegistries(IFactionRegistry factionRegistryIn, ISundamageRegistry sundamageRegistryIn, IVampirismEntityRegistry entityRegistryIn, IActionManager actionManagerIn, ISkillManager skillManagerIn,
                                       IVampireVisionRegistry vampireVisionRegistryIn, IEntityActionManager entityActionManagerIn, IExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistryIn) {
        if (INIT) throw new IllegalStateException("Vampirism API can only be setup once");
        factionRegistry = factionRegistryIn;
        sundamageRegistry = sundamageRegistryIn;
        entityRegistry = entityRegistryIn;
        actionManager = actionManagerIn;
        skillManager = skillManagerIn;
        vampireVisionRegistry = vampireVisionRegistryIn;
        entityActionManager = entityActionManagerIn;
        extendedBrewingRecipeRegistry = extendedBrewingRecipeRegistryIn;
        INIT = true;
    }

    /**
     * FOR FUTURE INTERNAL USE ONLY
     * Called once Vampirism has finished preparing the API, and it is ready to use.
     */
    @SuppressWarnings("EmptyMethod")
    @ApiStatus.Internal
    public static void onSetupComplete() {

    }

    /**
     * @return The respective {@link IFactionPlayerHandler}
     */
    public static @NotNull LazyOptional<IFactionPlayerHandler> getFactionPlayerHandler(@NotNull Player player) {
        return player.getCapability(CAP_FACTION_HANDLER_PLAYER, null);
    }

    /**
     * @return The respective {@link IVampirePlayer}
     */
    public static @NotNull LazyOptional<IVampirePlayer> getVampirePlayer(@NotNull Player player) {
        return player.getCapability(CAP_VAMPIRE, null);
    }

    /**
     * @return The respective {@link de.teamlapen.vampirism.api.entity.hunter.IHunter}
     */
    public static @NotNull LazyOptional<IHunterPlayer> getHunterPlayer(@NotNull Player player) {
        return player.getCapability(CAP_HUNTER, null);
    }

    /**
     * Get the {@link IExtendedCreatureVampirism} instance for the given creature
     */
    public static @NotNull LazyOptional<IExtendedCreatureVampirism> getExtendedCreatureVampirism(@NotNull PathfinderMob creature) {
        return creature.getCapability(CAP_CREATURE, null);
    }

    public static @NotNull LazyOptional<IVampirismWorld> getVampirismWorld(@NotNull Level w) {
        return w.getCapability(CAP_WORLD);
    }


}
