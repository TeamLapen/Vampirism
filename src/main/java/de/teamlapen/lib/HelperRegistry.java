package de.teamlapen.lib;

import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.ISyncable;

import java.util.ArrayList;
import java.util.List;

/**
 * Register things that should be handled by the library here
 */
public class HelperRegistry {
    private final static String TAG = "HelperRegistry";
    private static List<String> syncablePlayerProperties = new ArrayList<>();
    private static List<String> syncableEntityProperties = new ArrayList<>();
    private static List<String> eventListenerProps = new ArrayList<>();
    private static String[] eventListenerPropsFinal;
    private static String[] syncablePlayerPropertiesFinal;
    private static String[] syncableEntityPropertiesFinal;

    /**
     * FOR INTERNAL USAGE ONLY
     */
    public static String[] getEventListenerProps() {
        return eventListenerPropsFinal;
    }

    /**
     * FOR INTERNAL USAGE ONLY
     */
    public static String[] getSyncablePlayerProperties() {
        return syncablePlayerPropertiesFinal;
    }

    /**
     * FOR INTERNAL USAGE ONLY
     */
    public static String[] getSyncableEntityProperties() {
        return syncableEntityPropertiesFinal;
    }

    /**
     * Register a entity {@link de.teamlapen.lib.lib.network.ISyncable.ISyncableExtendedProperties} which should be synced on world join
     *
     * @param clz
     */
    public static void registerSyncableEntityProperty(String prop, Class<? extends ISyncable.ISyncableExtendedProperties> clz) {
        if (syncableEntityProperties == null) {
            VampLib.log.e(TAG, "You have to register the syncable property %s (%s) before post init", clz, prop);
            return;
        }
        syncableEntityProperties.add(prop);
    }

    /**
     * Register a player {@link de.teamlapen.lib.lib.network.ISyncable.ISyncableExtendedProperties} which should be synced on world join
     *
     * @param clz
     */
    public static void registerSyncablePlayerProperty(String prop, Class<? extends ISyncable.ISyncableExtendedProperties> clz) {
        if (syncablePlayerProperties == null) {
            VampLib.log.e(TAG, "You have to register the syncable property %s (%s) before post init", clz, prop);
            return;
        }
        syncablePlayerProperties.add(prop);
    }

    /**
     * Key of a {@link net.minecraftforge.common.IExtendedEntityProperties} which implements {@link IPlayerEventListener} and should receive the events.
     * Has to be called before post init.
     *
     * @param id
     */
    public static void registerPlayerEventReceivingProperty(String id) {
        if (eventListenerProps == null) {
            VampLib.log.e(TAG, "You have to register PlayerEventReceiver BEFORE post init. (" + id + ")");
        } else {
            eventListenerProps.add(id);
        }
    }

    /**
     * Finishes registration.
     * FOR INTERNAL USAGE ONLY
     */
    public static void finish() {
        syncableEntityPropertiesFinal = syncableEntityProperties.toArray(new String[syncableEntityProperties.size()]);
        syncableEntityProperties = null;
        syncablePlayerPropertiesFinal = syncablePlayerProperties.toArray(new String[syncablePlayerProperties.size()]);
        syncablePlayerProperties = null;
        eventListenerPropsFinal = eventListenerProps.toArray(new String[eventListenerProps.size()]);
        eventListenerProps = null;
    }
}
