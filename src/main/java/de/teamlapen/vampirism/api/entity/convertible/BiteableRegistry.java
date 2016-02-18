package de.teamlapen.vampirism.api.entity.convertible;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
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
    private static final Map<Class<? extends EntityCreature>, IConvertingHandler> convertibles = new HashMap<>();
    private static final Map<Class<? extends EntityCreature>, String> convertibleOverlay = new HashMap<>();
    private static final String TAG = "BiteableRegistry";
    /**
     * Stores biteable entries after init
     */
    private static final Map<String, BiteableEntry> biteables = new HashMap<>();
    private static boolean finished = false;
    private static  ICreateDefaultConvertingHandler defaultConvertingHandlerCreator;

    /**
     * @return A map mapping the overlay resource location string to e convertible entity's class
     */
    @SideOnly(Side.CLIENT)
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
     * Register a entity class which can be converted using Vampirism's default {@link IConvertingHandler} and thereby being turned into Vampirim's default {@link IConvertedCreature}
     *
     * @param clazz
     * @param overlay_loc Location of the overlay texture file
     */
    public static void addConvertible(Class<? extends EntityCreature> clazz, String overlay_loc) {
        addConvertible(clazz, overlay_loc, (IConvertingHandler) null);
    }

    /**
     * Register a entity class which can be converted using a default {@link IConvertingHandler} and thereby being turned into Vampirim's default {@link IConvertedCreature}
     *
     * @param clazz
     * @param helper Helper instance for the DefaultHandler to specify some values for the converted creature
     * @param overlay_loc Location of the overlay texture file
     */
    public static void addConvertible(Class<? extends EntityCreature> clazz, String overlay_loc, IConvertingHandler.IDefaultHelper helper) {
        addConvertible(clazz, overlay_loc, defaultConvertingHandlerCreator.create(helper));
    }

    /**
     * Register a entity class which can be converted
     *
     * @param clazz
     * @param overlay_loc Location of the overlay texture file. Only required if Vampirism's default Converted Creature renderer is used, if you handle that stuff yourself, null is just fine.
     * @param handler Handles the conversion
     */
    public static void addConvertible(Class<? extends EntityCreature> clazz, String overlay_loc, IConvertingHandler handler) {
        convertibles.put(clazz, handler);
        if (FMLCommonHandler.instance().getSide().isClient() && overlay_loc != null) {
            convertibleOverlay.put(clazz, overlay_loc);
        }
    }

    /**
     * @param entity
     * @return A converted creature for the given one
     */
    public static @Nullable IConvertedCreature convert(EntityCreature entity) {
        String s = EntityList.getEntityString(entity);
        BiteableEntry b = biteables.get(s);
        if (b != null && b.convertible) {
            return b.convertingHandler.createFrom(entity);
        }

        FMLLog.severe("[VampirismAPI]["+TAG+"] Failed to find convertible entry for %s", entity);
        return null;
    }

    public static BiteableEntry getEntry(EntityCreature creature) {
        return getEntry(EntityList.getEntityString(creature));
    }

    /**
     * Set the creator for Vampirism's default converting handler
     * FOR INTERNAL USAGE ONLY
     * @param
     */
    public static void setDefaultConvertingHandlerCreator(ICreateDefaultConvertingHandler creator){
        defaultConvertingHandlerCreator=creator;
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
        final IConvertingHandler defaultHandler = defaultConvertingHandlerCreator.create(null);
        for (Map.Entry<Class<? extends EntityCreature>, IConvertingHandler> entry : convertibles.entrySet()) {
            String entity_name = EntityList.classToStringMapping.get(entry.getKey());
            if (entity_name == null) {
                FMLLog.warning("[VampirismAPI][" + TAG + "] Cannot register convertible %s since there is no EntityString for it", entry.getKey());
                continue;
            }
            Integer blood = bloodValues.remove(entity_name);
            if (blood == null) {
                FMLLog.warning("[VampirismAPI][" + TAG + "] Missing blood value for convertible creature %s (%s)", entry.getKey().getName(), entity_name);
                continue;
            }
            blood = Math.round(blood * bloodValueMultiplier);
            FMLLog.info("[VampirismAPI][" + TAG + "] Registering convertible %s with blood %d and handler %s", entry.getKey().getName(), blood, entry.getValue());
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

    public interface ICreateDefaultConvertingHandler {
        IConvertingHandler create(IConvertingHandler.IDefaultHelper helper);
    }
}
