package de.teamlapen.vampirism.api;

import com.google.common.annotations.VisibleForTesting;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.entity.actions.IEntityActionManager;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.player.actions.IActionManager;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillManager;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVisionRegistry;
import de.teamlapen.vampirism.api.items.IExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.api.world.IGarlicChunkHandler;
import de.teamlapen.vampirism.api.world.IVampirismWorld;
import de.teamlapen.vampirism.api.world.IWorldGenManager;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nonnull;

/**
 * Class for core api methods
 * Don't use before init since it is setup in pre-init
 */
public class VampirismAPI {

    private static boolean INIT;
    /**
     * TODO 1.17 remove
     */
    @Deprecated
    private static final IGarlicChunkHandler dummyGarlicChunkHandler = new IGarlicChunkHandler() {
        @Override
        public void clear() {

        }

        @Nonnull
        @Override
        public EnumStrength getStrengthAtChunk(ChunkPos pos) {
            return EnumStrength.NONE;
        }

        @Override
        public int registerGarlicBlock(EnumStrength strength, ChunkPos... pos) {
            return 0;
        }

        @Override
        public void removeGarlicBlock(int id) {

        }
    };
    @SuppressWarnings("FieldMayBeFinal")
    @CapabilityInject(IExtendedCreatureVampirism.class)
    private static Capability<IExtendedCreatureVampirism> CAP_CREATURE = null;
    @SuppressWarnings("FieldMayBeFinal")
    @CapabilityInject(IFactionPlayerHandler.class)
    private static Capability<IFactionPlayerHandler> CAP_FACTION_HANDLER_PLAYER = null;
    @SuppressWarnings("FieldMayBeFinal")
    @CapabilityInject(IVampirismWorld.class)
    private static Capability<IVampirismWorld> CAP_WORLD = null;
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
     * Setup the API registries
     * FOR INTERNAL USAGE ONLY
     *
     * @throws IllegalStateException if the API was already setup
     */
    public static void setUpRegistries(IFactionRegistry factionRegistryIn, ISundamageRegistry sundamageRegistryIn, IVampirismEntityRegistry entityRegistryIn, IActionManager actionManagerIn, ISkillManager skillManagerIn,
                                       IVampireVisionRegistry vampireVisionRegistryIn, IEntityActionManager entityActionManagerIn, IWorldGenManager worldGenRegistryIn, IExtendedBrewingRecipeRegistry extendedBrewingRecipeRegistryIn) {
        if (INIT) throw new IllegalStateException("Vampirism API can only be setup once");
        factionRegistry = factionRegistryIn;
        sundamageRegistry = sundamageRegistryIn;
        entityRegistry = entityRegistryIn;
        actionManager = actionManagerIn;
        skillManager = skillManagerIn;
        vampireVisionRegistry = vampireVisionRegistryIn;
        entityActionManager = entityActionManagerIn;
        worldGenRegistry = worldGenRegistryIn;
        extendedBrewingRecipeRegistry = extendedBrewingRecipeRegistryIn;
        INIT = true;
    }

    /**
     * FOR FUTURE INTERNAL USE ONLY
     * Called once Vampirism has finished preparing the API and it is ready to use.
     */
    @Deprecated
    public static void onSetupComplete() {

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
     * Use getVampirismWorld instead
     * TODO 1.17 remove
     *
     * @return The {@link IGarlicChunkHandler} for the given world
     */
    @Deprecated
    @Nonnull
    public static IGarlicChunkHandler getGarlicChunkHandler(RegistryKey<World> world) {
        World w = DistExecutor.safeRunForDist(() -> () -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                return server.getLevel(world);
            }
            return ClientHelper.getAndCheckWorld(world);
        }, () -> () -> ServerLifecycleHooks.getCurrentServer().getLevel(world));
        if (w != null) {
            return w.getCapability(CAP_WORLD).map(iw -> (IGarlicChunkHandler) iw).orElse(dummyGarlicChunkHandler);
        }
        return dummyGarlicChunkHandler;
    }

    public static LazyOptional<IVampirismWorld> getVampirismWorld(World w) {
        return w.getCapability(CAP_WORLD);
    }


}
