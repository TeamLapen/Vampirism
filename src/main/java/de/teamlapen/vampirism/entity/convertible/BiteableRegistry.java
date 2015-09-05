package de.teamlapen.vampirism.entity.convertible;

import de.teamlapen.vampirism.client.render.TextureHelper;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.eclipse.jdt.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Max on 15.08.2015.
 */
public class BiteableRegistry {

    /**
     * Used to store blood values during init
     */
    private static final Map<String, Integer> bloodValues = new HashMap<String, Integer>();
    /**
     * Used to store overriding values during init. Will override entries in {@link #bloodValues} after init
     */
    private static final Map<String, Integer> overridingValues = new HashMap<String, Integer>();
    /**
     * Used to store convertible handlers during init
     */
    private static final Map<Class<? extends EntityCreature>, ConvertingHandler> convertibles = new HashMap<Class<? extends EntityCreature>, ConvertingHandler>();
    private static boolean finished = false;
    private static final String TAG = "BiteableRegistry";

    /**
     * Stores biteable entries after init
     */
    private static final Map<String, BiteableEntry> biteables = new HashMap<String, BiteableEntry>();

    public static void addBloodValues(Map<String, Integer> values) {
        bloodValues.putAll(values);
    }

    public static void overrideBloodValues(Map<String, Integer> values) {
        overridingValues.putAll(values);
    }

    public static void addConvertible(Class<? extends EntityCreature> clazz, String overlay_loc) {
        addConvertible(clazz, overlay_loc, null);
    }

    public static void addConvertible(Class<? extends EntityCreature> clazz, String overlay_loc, ConvertingHandler handler) {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            TextureHelper.registerConvertedOverlay(clazz, overlay_loc);
        }
        convertibles.put(clazz, handler);
    }

    public static
    @Nullable
    EntityConvertedCreature convert(EntityCreature entity) {
        String s = EntityList.getEntityString(entity);
        BiteableEntry b = biteables.get(s);
        if (b != null && b.convertible) {
            return b.convertingHandler.createFrom(entity);
        }

        Logger.e(TAG, "Failed to find convertible entry for %s", entity);
        return null;
    }

    public static BiteableEntry getEntry(EntityCreature creature) {
        return getEntry(EntityList.getEntityString(creature));
    }

    public static BiteableEntry getEntry(String entity_name) {
        return biteables.get(entity_name);
    }

    public static void finishRegistration() {
        if (finished) return;
        bloodValues.putAll(overridingValues);
        float bloodValueMultiplier = 1;
        Integer i = bloodValues.get("multiplier");
        if (i != null) {
            bloodValueMultiplier = i / 10F;
        }
        final ConvertingHandler defaultHandler = new ConvertingHandler<EntityCreature>();
        for (Map.Entry<Class<? extends EntityCreature>, ConvertingHandler> entry : convertibles.entrySet()) {
            String entity_name = (String) EntityList.classToStringMapping.get(entry.getKey());
            if (entity_name == null) {
                Logger.w(TAG, "Cannot register convertible %s since there is no EntityString for it", entry.getKey());
                continue;
            }
            Integer blood = bloodValues.remove(entity_name);
            if (blood == null) {
                Logger.w(TAG, "Missing blood value for convertible creature %s (%s)", entry.getKey().getName(), entity_name);
                continue;
            }
            blood = Math.round(blood * bloodValueMultiplier);
            Logger.t("Registering convertible %s with blood %d and handler %s", entry.getKey().getName(), blood, entry.getValue());
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
