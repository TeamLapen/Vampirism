package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.IBiteableRegistry;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IFactionRegistry;
import de.teamlapen.vampirism.api.entity.player.vampire.ISkillRegistry;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Class for core api methods
 * Don't use before init since it is setup in pre-init
 */
public class VampirismAPI {


    private static IFactionRegistry factionRegistry;
    private static ISundamageRegistry sundamageRegistry;
    private static IBiteableRegistry biteableRegistry;
    private static ISkillRegistry skillRegistry;

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
     * Setup the API
     * FOR INTERNAL USAGE ONLY
     * @param factionReg
     * @param sundamageReg
     */
    public static void setUp(IFactionRegistry factionReg, ISundamageRegistry sundamageReg, IBiteableRegistry biteableReg, ISkillRegistry skillReg) {
        factionRegistry = factionReg;
        sundamageRegistry = sundamageReg;
        biteableRegistry = biteableReg;
        skillRegistry = skillReg;
    }


    /**
     * @param player
     * @return The respective {@link IFactionPlayerHandler}
     */
    public static IFactionPlayerHandler getFactionPlayerHandler(EntityPlayer player) {
        return (IFactionPlayerHandler) player.getExtendedProperties(VReference.FACTION_PLAYER_HANDLER_PROP);
    }



    /**
     * Get the Vampirism's extended entity property which every {@link EntityCreature} has
     *
     * @return
     */
    public static IExtendedCreatureVampirism getExtendedCreatureVampirism(EntityCreature creature) {
        return (IExtendedCreatureVampirism) creature.getExtendedProperties(VReference.EXTENDED_CREATURE_PROP);
    }


}
