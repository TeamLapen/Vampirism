package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.BiteableEntry;
import de.teamlapen.vampirism.api.entity.IBiteableRegistry;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BiteableRegistry implements IBiteableRegistry {
    /**
     * Used to store blood values during init
     */
    private final Map<String, Integer> bloodValues = new HashMap<>();
    /**
     * Used to store overriding values during init. Will override entries in {@link #bloodValues} after init
     */
    private final Map<String, Integer> overridingValues = new HashMap();
    /**
     * Used to store convertible handlers during init
     */
    private final Map<Class<? extends EntityCreature>, IConvertingHandler> convertibles = new HashMap<>();
    private final Map<Class<? extends EntityCreature>, String> convertibleOverlay = new HashMap<>();
    private final String TAG = "BiteableRegistry";
    /**
     * Stores biteable entries after init
     */
    private final Map<String, BiteableEntry> biteables = new HashMap<>();
    private boolean finished = false;
    private ICreateDefaultConvertingHandler defaultConvertingHandlerCreator;

    @Override
    public void addBloodValues(Map<String, Integer> values) {
        bloodValues.putAll(values);
    }

    @Override
    public void addConvertible(Class<? extends EntityCreature> clazz, String overlay_loc) {
        addConvertible(clazz, overlay_loc, (IConvertingHandler) null);
    }

    @Override
    public void addConvertible(Class<? extends EntityCreature> clazz, String overlay_loc, IConvertingHandler.IDefaultHelper helper) {
        addConvertible(clazz, overlay_loc, defaultConvertingHandlerCreator.create(helper));
    }

    @Override
    public void addConvertible(Class<? extends EntityCreature> clazz, String overlay_loc, IConvertingHandler handler) {
        convertibles.put(clazz, handler);
        if (FMLCommonHandler.instance().getSide().isClient() && overlay_loc != null) {
            convertibleOverlay.put(clazz, overlay_loc);
        }
    }

    @Override
    public
    @Nullable
    IConvertedCreature convert(EntityCreature entity) {
        String s = EntityList.getEntityString(entity);
        BiteableEntry b = biteables.get(s);
        if (b != null && b.convertible) {
            return b.convertingHandler.createFrom(entity);
        }
        VampirismMod.log.w(TAG, "Failed to find convertible entry for %s", entity);
        return null;
    }

    /**
     * Finished registration
     *
     * @param defaultDamage Damage that will be used for converted entities that did not have any attack damage before
     */
    public void finishRegistration(int defaultDamage) {
        if (finished) return;
        bloodValues.putAll(overridingValues);
        float bloodValueMultiplier = 1;
        Integer i = bloodValues.get("multiplier");
        if (i != null) {
            bloodValueMultiplier = i / 10F;
        }
        final IConvertingHandler defaultHandler = defaultConvertingHandlerCreator.create(null);
        for (Map.Entry<Class<? extends EntityCreature>, IConvertingHandler> entry : convertibles.entrySet()) {
            String entity_name = EntityList.CLASS_TO_NAME.get(entry.getKey());
            if (entity_name == null) {
                VampirismMod.log.w(TAG, "Cannot register convertible %s since there is no EntityString for it", entry.getKey());
                continue;
            }
            Integer blood = bloodValues.remove(entity_name);
            if (blood == null) {
                VampirismMod.log.w(TAG, "Missing blood value for convertible creature %s (%s)", entry.getKey().getName(), entity_name);
                continue;
            }
            blood = Math.round(blood * bloodValueMultiplier);
            VampirismMod.log.i(TAG, " Registering convertible %s with blood %d and handler %s", entry.getKey().getName(), blood, entry.getValue());
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

    @Override
    @SideOnly(Side.CLIENT)
    public Map<Class<? extends EntityCreature>, String> getConvertibleOverlay() {
        return convertibleOverlay;
    }

    @Override
    public BiteableEntry getEntry(EntityCreature creature) {
        return getEntry(EntityList.getEntityString(creature));
    }

    @Override
    public BiteableEntry getEntry(String entity_name) {
        return biteables.get(entity_name);
    }

    @Override
    public void overrideBloodValues(Map<String, Integer> values) {
        overridingValues.putAll(values);
    }

    /**
     * Set the creator for Vampirism's default converting handler
     * FOR INTERNAL USAGE ONLY
     *
     * @param
     */
    public void setDefaultConvertingHandlerCreator(ICreateDefaultConvertingHandler creator) {
        defaultConvertingHandlerCreator = creator;
    }
}