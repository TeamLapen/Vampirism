package de.teamlapen.vampirism.api.entity.convertible;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

/**
 * Registration of blood values and converting handler for {@link EntityCreature}'s
 */
public class BiteableRegistry {
    /**
     * Used to store blood values during init
     */
    private static final Map<String, Integer> bloodValues = new HashMap<>();
    /**
     * Used to store overriding values during init. Will override entries in {@link #bloodValues} after init
     */
    private static final Map<String, Integer> overridingValues = new HashMap();
    /**
     * Used to store convertible handlers during init
     */
    private static final Map<Class<? extends EntityCreature>, ConvertingHandler> convertibles = new HashMap<>();
    @SideOnly(Side.CLIENT)
    private static final Map<Class<? extends EntityCreature>, String> convertibleOverlay = new HashMap<>();
    private static final String TAG = "BiteableRegistry";
    /**
     * Stores biteable entries after init
     */
    private static final Map<String, BiteableEntry> biteables = new HashMap<>();
    private static boolean finished = false;

    /**
     * @return A map mapping the overlay resource location string to e convertible entity's class
     */
    public static Map<Class<? extends EntityCreature>, String> getConvertibleOverlay() {
        return convertibleOverlay;
    }

    public static void addBloodValues(Map<String, Integer> values) {
        bloodValues.putAll(values);
    }

    /**
     * Add blood values that override previously registered values. E.g. used for making the values configurable
     *
     * @param values
     */
    public static void overrideBloodValues(Map<String, Integer> values) {
        overridingValues.putAll(values);
    }

    /**
     * Register a entity class which can be converted using a default {@link ConvertingHandler}
     *
     * @param clazz
     * @param overlay_loc Location of the overlay texture file
     */
    public static void addConvertible(Class<? extends EntityCreature> clazz, String overlay_loc) {
        addConvertible(clazz, overlay_loc, null);
    }

    /**
     * Register a entity class which can be converted
     *
     * @param clazz
     * @param overlay_loc Location of the overlay texture file
     * @param handler
     */
    public static void addConvertible(Class<? extends EntityCreature> clazz, String overlay_loc, ConvertingHandler handler) {
        convertibles.put(clazz, handler);
        if (FMLCommonHandler.instance().getSide().isClient()) {
            convertibleOverlay.put(clazz, overlay_loc);
        }
    }

//    public static @Nullable EntityConvertedCreature convert(EntityCreature entity) {
//        String s = EntityList.getEntityString(entity);
//        BiteableEntry b = biteables.get(s);
//        if (b != null && b.convertible) {
//            return b.convertingHandler.createFrom(entity);
//        }
//
//        FMLLog.severe("["+TAG+"] Failed to find convertible entry for %s", entity);
//        return null;
//    }

    public static BiteableEntry getEntry(EntityCreature creature) {
        return getEntry(EntityList.getEntityString(creature));
    }

    public static BiteableEntry getEntry(String entity_name) {
        return biteables.get(entity_name);
    }

    /**
     * Finished registration
     * FOR INTERNAL USAGE ONLY
     *
     * @param defaultDamage Damage that will be used for converted entities that did not have any attack damage before
     */
    public static void finishRegistration(int defaultDamage) {
        if (finished) return;
        bloodValues.putAll(overridingValues);
        float bloodValueMultiplier = 1;
        Integer i = bloodValues.get("multiplier");
        if (i != null) {
            bloodValueMultiplier = i / 10F;
        }
        final ConvertingHandler defaultHandler = new ConvertingHandler<>(defaultDamage);
        for (Map.Entry<Class<? extends EntityCreature>, ConvertingHandler> entry : convertibles.entrySet()) {
            String entity_name = EntityList.classToStringMapping.get(entry.getKey());
            if (entity_name == null) {
                FMLLog.warning("[" + TAG + "] Cannot register convertible %s since there is no EntityString for it", entry.getKey());
                continue;
            }
            Integer blood = bloodValues.remove(entity_name);
            if (blood == null) {
                FMLLog.warning("[" + TAG + "] Missing blood value for convertible creature %s (%s)", entry.getKey().getName(), entity_name);
                continue;
            }
            blood = Math.round(blood * bloodValueMultiplier);
            FMLLog.info("[" + TAG + "] Registering convertible %s with blood %d and handler %s", entry.getKey().getName(), blood, entry.getValue());
            BiteableEntry biteEntry = new BiteableEntry(blood, (entry.getValue() == null ? defaultHandler : entry.getValue()));
            biteables.put(entity_name, biteEntry);
        }
        for (Map.Entry<String, Integer> entry : bloodValues.entrySet()) {
            biteables.put(entry.getKey(), new BiteableEntry(Math.abs(Math.round(entry.getValue() * bloodValueMultiplier))));
        }
        bloodValues.clear();
        convertibles.clear();

        finished = true;
    }
}
