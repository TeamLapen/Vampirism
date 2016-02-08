package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.factions.PlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.IPlayerEventListener;
import de.teamlapen.vampirism.api.entity.player.IVampirePlayer;
import net.minecraftforge.fml.common.FMLLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Class for core api methods
 */
public class VampirismAPI {

    private final static HashMap<Integer,Boolean> sundamageDims=new HashMap<Integer, Boolean>();
    private final static HashMap<Integer,Boolean> sundamageConfiguredDims =new HashMap<Integer, Boolean>();
    private final static Set<Integer> noSundamageBiomes=new CopyOnWriteArraySet<Integer>();
    /**
     * Vampire Player Faction
     * Filled during pre-init.
     */
    public static PlayableFaction<IVampirePlayer> VAMPIRE_FACTION;
    /**
     * Hunter Player Faction
     * Filled during pre-init.
     */
    public static PlayableFaction<IHunterPlayer> HUNTER_FACTION;
    private static boolean defaultSundamage=false;
    /**
     * Stores {@link net.minecraftforge.common.IExtendedEntityProperties} keys which should receive PlayerEvents
     */
    private static List<String> eventListenerProps = new ArrayList<>();

    static {
        sundamageDims.put(0,true);
        sundamageDims.put(-1,false);
        sundamageDims.put(1,false);
    }

    /**
     * Specifies the default value for non specified dimensions
     * FOR INTERNAL USAGE ONLY
     * @param val
     */
    public static void setDefaultDimsSundamage(boolean val){
        defaultSundamage=val;
    }

    /**
     * Key of a {@link net.minecraftforge.common.IExtendedEntityProperties} which implements {@link IPlayerEventListener} and should receive the events.
     * Has to be called before init.
     *
     * @param id
     */
    public static void registerPlayerEventReceivingProperty(String id) {
        if (eventListenerProps == null) {
            FMLLog.severe("[VampirismApi] You have to register PlayerEventReceiver BEFORE init. (" + id + ")");
        } else {
            eventListenerProps.add(id);
        }
    }

    /**
     * Create PlayerEventReceivingProperty Array
     * FOR INTERNAL USAGE ONLY
     */
    public static String[] buildPlayerEventReceiver() {
        String[] r = eventListenerProps.toArray(new String[eventListenerProps.size()]);
        eventListenerProps = null;
        return r;
    }

    public static void addNoSundamageBiome(int id) {
        noSundamageBiomes.add(id);
    }

    /**
     * Specifies if vampires should get sundamage in this dimension
     * @param dimensionId
     * @param sundamage
     */
    public static void specifySundamageForDim(int dimensionId, boolean sundamage) {
        sundamageDims.put(dimensionId, sundamage);
    }

    /**
     * Checkd if vampirs can get sundamage in that biome
     * @param id
     * @return
     */
    public static boolean getSundamageInBiome(int id) {
        return !noSundamageBiomes.contains(id);
    }

    /**
     * Resets the configured sundamage dims. E.G. on configuration reload
     * FOR INTERNAL USAGE ONLY
     */
    public static void resetConfiguredSundamgeDims() {
        sundamageConfiguredDims.clear();
    }

    /**
     * Adds settings from Vampirism's config file.
     * FOR INTERNAL USAGE ONLY
     * @param dimensionId
     * @param sundamage
     */
    public static void specifyConfiguredSundamageForDim(int dimensionId, boolean sundamage) {
        sundamageConfiguredDims.put(dimensionId, sundamage);
    }

    /**
     * Checks if vampires can get sundamge in that dimension
     * @param dim
     * @return
     */
    public static boolean getSundamageInDim(int dim) {
        Boolean r = sundamageConfiguredDims.get(dim);
        if (r == null) {
            r = sundamageDims.get(dim);
        }
        return r == null ? defaultSundamage:r;
    }

}
