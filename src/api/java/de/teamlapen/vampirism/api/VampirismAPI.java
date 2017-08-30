package de.teamlapen.vampirism.api;

import com.google.common.collect.Sets;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.player.actions.IActionManager;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillManager;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVisionRegistry;
import de.teamlapen.vampirism.api.items.IAlchemicalCauldronCraftingManager;
import de.teamlapen.vampirism.api.items.IBloodPotionRegistry;
import de.teamlapen.vampirism.api.items.IHunterWeaponCraftingManager;
import de.teamlapen.vampirism.api.world.IGarlicChunkHandler;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Class for core api methods
 * Don't use before init since it is setup in pre-init
 */
public class VampirismAPI {


    @CapabilityInject(IExtendedCreatureVampirism.class)
    private static final Capability<IExtendedCreatureVampirism> CAP_CREATURE = null;
    @CapabilityInject(IFactionPlayerHandler.class)
    private final static Capability<IFactionPlayerHandler> CAP_FACTION_HANDLER_PLAYER = null;
    @CapabilityInject(IVampirismVillage.class)
    private final static Capability<IVampirismVillage> CAP_VILLAGE = null;
    private static IFactionRegistry factionRegistry;
    private static ISundamageRegistry sundamageRegistry;
    private static IVampirismEntityRegistry biteableRegistry;
    private static IVampireVisionRegistry vampireVisionRegistry;
    private static IHunterWeaponCraftingManager weaponCraftingManager;
    private static IBloodPotionRegistry bloodPotionRegistry;
    private static IGarlicChunkHandler.Provider garlicHandlerProvider;
    private static IAlchemicalCauldronCraftingManager alchemicalCauldronCraftingManager;
    private static Set<Integer> worldGenDimensions = Sets.newHashSet();
    private static ISkillManager skillManager;
    private static IActionManager actionManager;

    public static ISkillManager skillManager() {
        return skillManager;
    }

    public static IActionManager actionManager() {
        return actionManager;
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
     * @return The biteable registry
     */
    public static IVampirismEntityRegistry biteableRegistry() {
        return biteableRegistry;
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
     * Makes Vampirism execute it's worldgen in this dimension
     */
    public static void addDimensionForWorldgen(int id) {
        worldGenDimensions.add(id);
    }

    /**
     * Removes a dimensions from Vampirism's worldgen if it has been added before
     */
    public static void removeDimensionFromWorldgen(int id) {
        worldGenDimensions.remove(id);
    }

    /**
     * If Vampirism's world gen is enabled in this dimension
     */
    public static boolean isWorldGenEnabledFor(int dim) {
        return worldGenDimensions.contains(dim);
    }

    /**
     * Setup the API registries
     * FOR INTERNAL USAGE ONLY
     */
    public static void setUpRegistries(IFactionRegistry factionReg, ISundamageRegistry sundamageReg, IVampirismEntityRegistry biteableReg, IActionManager actionMan, ISkillManager skillMan, IVampireVisionRegistry vampireVisionReg, IBloodPotionRegistry bloodPotionReg) {
        factionRegistry = factionReg;
        sundamageRegistry = sundamageReg;
        biteableRegistry = biteableReg;
        actionManager = actionMan;
        skillManager = skillMan;
        vampireVisionRegistry = vampireVisionReg;
        bloodPotionRegistry = bloodPotionReg;
    }

    /**
     * Setup the API accessors
     * FOR INTERNAL USAGE ONLY
     */
    public static void setUpAccessors(IHunterWeaponCraftingManager weaponCraftingMan, IGarlicChunkHandler.Provider garlicChunkHandlerProv, IAlchemicalCauldronCraftingManager alchemicalCauldronCraftingMan) {
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
     */
    public static IExtendedCreatureVampirism getExtendedCreatureVampirism(EntityCreature creature) {
        return creature.getCapability(CAP_CREATURE, null);
    }

    /**
     * Get the {@link IVampirismVillage} instance for the given village
     */
    public static IVampirismVillage getVampirismVillage(Village village) {
        return village.getCapability(CAP_VILLAGE, null);
    }

    /**
     * @return The {@link IGarlicChunkHandler} for the given world
     */
    @Nonnull
    public static IGarlicChunkHandler getGarlicChunkHandler(World world) {
        return garlicHandlerProvider.getHandler(world);
    }


}
