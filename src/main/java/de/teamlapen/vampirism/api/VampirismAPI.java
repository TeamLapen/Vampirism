package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.player.actions.IActionRegistry;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillRegistry;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVisionRegistry;
import de.teamlapen.vampirism.api.items.IAlchemicalCauldronCraftingManager;
import de.teamlapen.vampirism.api.items.IBloodPotionRegistry;
import de.teamlapen.vampirism.api.items.IHunterWeaponCraftingManager;
import de.teamlapen.vampirism.api.world.IGarlicChunkHandler;
import de.teamlapen.vampirism.api.world.IVampirismVillageProvider;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nonnull;

/**
 * Class for core api methods
 * Don't use before init since it is setup in pre-init
 */
public class VampirismAPI {


    @CapabilityInject(IExtendedCreatureVampirism.class)
    private static final Capability<IExtendedCreatureVampirism> CAP_CREATURE = null;
    @CapabilityInject(IFactionPlayerHandler.class)
    private final static Capability<IFactionPlayerHandler> CAP_FACTION_HANDLER_PLAYER = null;
    private static IFactionRegistry factionRegistry;
    private static ISundamageRegistry sundamageRegistry;
    private static IVampirismEntityRegistry biteableRegistry;
    private static IActionRegistry actionRegistry;
    private static ISkillRegistry skillRegistry;
    private static IVampireVisionRegistry vampireVisionRegistry;
    private static IVampirismVillageProvider.IProviderProvider vampirismVillageProviders;
    private static IHunterWeaponCraftingManager weaponCraftingManager;
    private static IBloodPotionRegistry bloodPotionRegistry;
    private static IGarlicChunkHandler.Provider garlicHandlerProvider;
    private static IAlchemicalCauldronCraftingManager alchemicalCauldronCraftingManager;


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
     * @return The biteable registry
     */
    public static IVampirismEntityRegistry biteableRegistry() {
        return biteableRegistry;
    }

    /**
     * @return The skill registry
     */
    public static ISkillRegistry skillRegistry() {
        return skillRegistry;
    }

    /**
     * @return The action registry
     */
    public static IActionRegistry actionRegistry() {
        return actionRegistry;
    }

    /**
     * @return The crafting manager for the hunter weapon crafting table
     */
    public static IHunterWeaponCraftingManager weaponCraftingManager() {
        return weaponCraftingManager;
    }

    /**
     * @return The blood potion registry
     */
    public static IBloodPotionRegistry bloodPotionRegistry() {
        return bloodPotionRegistry;
    }

    /**
     * @return The crafting manager for the alchemical cauldron (hunter)
     */
    public static IAlchemicalCauldronCraftingManager alchemicalCauldronCraftingManager() {
        return alchemicalCauldronCraftingManager;
    }

    /**
     * Setup the API registries
     * FOR INTERNAL USAGE ONLY
     */
    public static void setUpRegistries(IFactionRegistry factionReg, ISundamageRegistry sundamageReg, IVampirismEntityRegistry biteableReg, IActionRegistry actionReg, ISkillRegistry skillReg, IVampireVisionRegistry vampireVisionReg, IBloodPotionRegistry bloodPotionReg) {
        factionRegistry = factionReg;
        sundamageRegistry = sundamageReg;
        biteableRegistry = biteableReg;
        actionRegistry = actionReg;
        skillRegistry = skillReg;
        vampireVisionRegistry = vampireVisionReg;
        bloodPotionRegistry = bloodPotionReg;
    }

    /**
     * Setup the API accessors
     * FOR INTERNAL USAGE ONLY
     */
    public static void setUpAccessors(IVampirismVillageProvider.IProviderProvider villagePro, IHunterWeaponCraftingManager weaponCraftingMan, IGarlicChunkHandler.Provider garlicChunkHandlerProv, IAlchemicalCauldronCraftingManager alchemicalCauldronCraftingMan) {
        vampirismVillageProviders = villagePro;
        weaponCraftingManager = weaponCraftingMan;
        garlicHandlerProvider = garlicChunkHandlerProv;
        alchemicalCauldronCraftingManager = alchemicalCauldronCraftingMan;
    }


    /**
     * @param player
     * @return The respective {@link IFactionPlayerHandler}
     */
    public static IFactionPlayerHandler getFactionPlayerHandler(EntityPlayer player) {
        return player.getCapability(CAP_FACTION_HANDLER_PLAYER, null);
    }


    /**
     * Get the {@link IExtendedCreatureVampirism} instance for the given creature
     *
     * @return
     */
    public static IExtendedCreatureVampirism getExtendedCreatureVampirism(EntityCreature creature) {
        return creature.getCapability(CAP_CREATURE, null);
    }

    /**
     * @return The {@link IVampirismVillageProvider} for the given world
     */
    public static IVampirismVillageProvider getVampirismVillageProvider(World world) {
        return vampirismVillageProviders.getProviderForWorld(world);
    }

    /**
     * @return The {@link IGarlicChunkHandler} for the given world
     */
    @Nonnull
    public static IGarlicChunkHandler getGarlicChunkHandler(World world) {
        return garlicHandlerProvider.getHandler(world);
    }


}
