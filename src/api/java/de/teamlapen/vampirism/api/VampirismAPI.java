package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionManager;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.player.actions.IActionManager;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillManager;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVisionRegistry;
import de.teamlapen.vampirism.api.items.IBloodPotionRegistry;
import de.teamlapen.vampirism.api.world.IGarlicChunkHandler;
import de.teamlapen.vampirism.api.world.IWorldGenManager;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

/**
 * Class for core api methods
 * Don't use before init since it is setup in pre-init
 */
public class VampirismAPI {


    @CapabilityInject(IExtendedCreatureVampirism.class)
    private static Capability<IExtendedCreatureVampirism> CAP_CREATURE = null;
    @CapabilityInject(IFactionPlayerHandler.class)
    private static Capability<IFactionPlayerHandler> CAP_FACTION_HANDLER_PLAYER = null;
    private static IFactionRegistry factionRegistry;
    private static ISundamageRegistry sundamageRegistry;
    private static IVampirismEntityRegistry entityRegistry;
    private static IVampireVisionRegistry vampireVisionRegistry;
    private static IBloodPotionRegistry bloodPotionRegistry;
    private static IGarlicChunkHandler.Provider garlicHandlerProvider;
    private static ISkillManager skillManager;
    private static IActionManager actionManager;
    private static IEntityActionManager entityActionManager;
    private static IWorldGenManager worldGenRegistry;

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
     * @return The blood potion registry
     */
    public static IBloodPotionRegistry bloodPotionRegistry() {
        return bloodPotionRegistry;
    }

    /**
     * @return The world gen registry
     */
    public static IWorldGenManager worldGenRegistry() {
        return worldGenRegistry;
    }

    /**
     * Setup the API registries
     * FOR INTERNAL USAGE ONLY
     */
    public static void setUpRegistries(IFactionRegistry factionRegistryIn, ISundamageRegistry sundamageRegistryIn, IVampirismEntityRegistry entityRegistryIn, IActionManager actionManagerIn, ISkillManager skillManagerIn,
                                       IVampireVisionRegistry vampireVisionRegistryIn, IBloodPotionRegistry bloodPotionRegistryIn, IEntityActionManager entityActionManagerIn, IWorldGenManager worldGenRegistryIn) {
        factionRegistry = factionRegistryIn;
        sundamageRegistry = sundamageRegistryIn;
        entityRegistry = entityRegistryIn;
        actionManager = actionManagerIn;
        skillManager = skillManagerIn;
        vampireVisionRegistry = vampireVisionRegistryIn;
        bloodPotionRegistry = bloodPotionRegistryIn;
        entityActionManager = entityActionManagerIn;
        worldGenRegistry = worldGenRegistryIn;

    }

    /**
     * Setup the API accessors
     * FOR INTERNAL USAGE ONLY
     */
    public static void setUpAccessors(IGarlicChunkHandler.Provider garlicChunkHandlerProv) {
        garlicHandlerProvider = garlicChunkHandlerProv;
    }


    /**
     * @param player
     * @return The respective {@link IFactionPlayerHandler}
     */
    public static LazyOptional<IFactionPlayerHandler> getFactionPlayerHandler(PlayerEntity player) {
        return player.getCapability(CAP_FACTION_HANDLER_PLAYER, null);
    }


    /**
     * Get the {@link IExtendedCreatureVampirism} instance for the given creature
     */
    public static LazyOptional<IExtendedCreatureVampirism> getExtendedCreatureVampirism(CreatureEntity creature) {
        return creature.getCapability(CAP_CREATURE, null);
    }

    /**
     * @return The {@link IGarlicChunkHandler} for the given world
     */
    @Nonnull
    public static IGarlicChunkHandler getGarlicChunkHandler(IWorld world) {
        return garlicHandlerProvider.getHandler(world);
    }


}
