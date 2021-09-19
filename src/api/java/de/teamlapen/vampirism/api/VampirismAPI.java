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
import de.teamlapen.vampirism.api.world.IWorldGenManager;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Class for core api methods
 * Don't use before init since it is set up in pre-init
 */
public class VampirismAPI {


    @SuppressWarnings("FieldMayBeFinal")
    @CapabilityInject(IExtendedCreatureVampirism.class)
    private static Capability<IExtendedCreatureVampirism> CAP_CREATURE = null;
    @SuppressWarnings("FieldMayBeFinal")
    @CapabilityInject(IFactionPlayerHandler.class)
    private static Capability<IFactionPlayerHandler> CAP_FACTION_HANDLER_PLAYER = null;
    @SuppressWarnings("FieldMayBeFinal")
    @CapabilityInject(IVampirismWorld.class)
    private static Capability<IVampirismWorld> CAP_WORLD = null;
    @SuppressWarnings("FieldMayBeFinal")
    @CapabilityInject(IVampirePlayer.class)
    private static Capability<IVampirePlayer> CAP_VAMPIRE = null;
    @SuppressWarnings("FieldMayBeFinal")
    @CapabilityInject(IHunterPlayer.class)
    private static Capability<IHunterPlayer> CAP_HUNTER = null;

    private static IFactionRegistry factionRegistry;
    private static ISundamageRegistry sundamageRegistry;
    private static IVampirismEntityRegistry entityRegistry;
    private static IVampireVisionRegistry vampireVisionRegistry;
    private static ISkillManager skillManager;
    private static IActionManager actionManager;
    private static IEntityActionManager entityActionManager;
    private static IWorldGenManager worldGenRegistry;
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

    /**
     * @return The world gen registry
     */
    public static IWorldGenManager worldGenRegistry() {
        return worldGenRegistry;
    }

    public static IExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistry() {
        return extendedBrewingRecipeRegistry;
    }

    /**
     * Set up the API registries
     * FOR INTERNAL USAGE ONLY
     */
    public static void setUpRegistries(IFactionRegistry factionRegistryIn, ISundamageRegistry sundamageRegistryIn, IVampirismEntityRegistry entityRegistryIn, IActionManager actionManagerIn, ISkillManager skillManagerIn,
                                       IVampireVisionRegistry vampireVisionRegistryIn, IEntityActionManager entityActionManagerIn, IWorldGenManager worldGenRegistryIn, IExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistryIn) {
        factionRegistry = factionRegistryIn;
        sundamageRegistry = sundamageRegistryIn;
        entityRegistry = entityRegistryIn;
        actionManager = actionManagerIn;
        skillManager = skillManagerIn;
        vampireVisionRegistry = vampireVisionRegistryIn;
        entityActionManager = entityActionManagerIn;
        worldGenRegistry = worldGenRegistryIn;
        extendedBrewingRecipeRegistry = extendedBrewingRecipeRegistryIn;

    }

    /**
     * FOR FUTURE INTERNAL USE ONLY
     * Called once Vampirism has finished preparing the API, and it is ready to use.
     */
    @Deprecated
    public static void onSetupComplete() {

    }

    /**
     * @return The respective {@link IFactionPlayerHandler}
     */
    public static LazyOptional<IFactionPlayerHandler> getFactionPlayerHandler(Player player) {
        return player.getCapability(CAP_FACTION_HANDLER_PLAYER, null);
    }

    /**
     * @return The respective {@link IVampirePlayer}
     */
    public static LazyOptional<IVampirePlayer> getVampirePlayer(Player player) {
        return player.getCapability(CAP_VAMPIRE, null);
    }

    /**
     * @return The respective {@link de.teamlapen.vampirism.api.entity.hunter.IHunter}
     */
    public static LazyOptional<IHunterPlayer> getHunterPlayer(Player player) {
        return player.getCapability(CAP_HUNTER, null);
    }

    /**
     * Get the {@link IExtendedCreatureVampirism} instance for the given creature
     */
    public static LazyOptional<IExtendedCreatureVampirism> getExtendedCreatureVampirism(PathfinderMob creature) {
        return creature.getCapability(CAP_CREATURE, null);
    }

    public static LazyOptional<IVampirismWorld> getVampirismWorld(Level w) {
        return w.getCapability(CAP_WORLD);
    }


}
