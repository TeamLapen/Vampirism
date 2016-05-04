package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.IBiteableRegistry;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.player.actions.IActionRegistry;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillRegistry;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampireVisionRegistry;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

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
    private static IBiteableRegistry biteableRegistry;
    private static IActionRegistry actionRegistry;
    private static ISkillRegistry skillRegistry;
    private static IVampireVisionRegistry vampireVisionRegistry;


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
     *
     * @return The biteable registry
     */
    public static IBiteableRegistry biteableRegistry() {
        return biteableRegistry;
    }

    /**
     *
     * @return The skill registry
     */
    public static ISkillRegistry skillRegistry() {
        return skillRegistry;
    }

    /**
     *
     * @return The action registry
     */
    public static IActionRegistry actionRegistry() {
        return actionRegistry;
    }

    /**
     * Setup the API
     * FOR INTERNAL USAGE ONLY
     * @param factionReg
     * @param sundamageReg
     */
    public static void setUp(IFactionRegistry factionReg, ISundamageRegistry sundamageReg, IBiteableRegistry biteableReg, IActionRegistry actionReg, ISkillRegistry skillReg, IVampireVisionRegistry vampireVisionReg) {
        factionRegistry = factionReg;
        sundamageRegistry = sundamageReg;
        biteableRegistry = biteableReg;
        actionRegistry = actionReg;
        skillRegistry = skillReg;
        vampireVisionRegistry = vampireVisionReg;
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


}
