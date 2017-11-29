package de.teamlapen.vampirism.entity.converted;

import com.google.common.collect.Maps;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.BiteableEntry;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.config.Configs;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class VampirismEntityRegistry implements IVampirismEntityRegistry {
    /**
     * Used to store blood values during init
     */
    private final Map<ResourceLocation, Integer> bloodValues = new HashMap<>();
    /**
     * Used to store overriding values during init. Will override entries in {@link #bloodValues} after init
     */
    private final Map<ResourceLocation, Integer> overridingValues = new HashMap<>();

    /**
     * Used to store convertible handlers during init
     */
    private final Map<Class<? extends EntityCreature>, IConvertingHandler> convertibles = new HashMap<>();
    private final Map<Class<? extends EntityCreature>, String> convertibleOverlay = new HashMap<>();
    /**
     * Stores custom extended creature constructors
     */
    private final Map<Class<? extends EntityCreature>, Function> extendedCreatureConstructors = new HashMap<>();
    private final String TAG = "VampirismEntityRegistry";
    /**
     * Stores biteable entries after init
     */
    private final Map<ResourceLocation, BiteableEntry> biteables = new HashMap<>();

    /**
     * Store biteable entries loaded from world or dynamically generated
     */
    private final Map<ResourceLocation, BiteableEntry> generatedBiteables = Maps.newHashMap();

    private boolean finished = false;
    private ICreateDefaultConvertingHandler defaultConvertingHandlerCreator;

    @Override
    public void addBloodValue(ResourceLocation entityId, int value) {
        bloodValues.put(entityId, value);
    }

    @Override
    public void addBloodValues(Map<ResourceLocation, Integer> values) {
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
    public <T extends EntityCreature> void addCustomExtendedCreature(Class<? extends T> clazz, Function<T, IExtendedCreatureVampirism> constructor) {
        extendedCreatureConstructors.put(clazz, constructor);
    }

    @Override
    public
    @Nullable
    IConvertedCreature convert(EntityCreature entity) {
        ResourceLocation id = EntityList.getKey(entity);
        BiteableEntry b = biteables.get(id);
        if (b != null && b.convertingHandler != null) {
            return b.convertingHandler.createFrom(entity);
        }
        VampirismMod.log.w(TAG, "Failed to find convertible entry for %s", entity);
        return null;
    }

    /**
     * Finished registration
     * Should be called during post init
     */
    public void finishRegistration() {
        if (finished) return;
        bloodValues.putAll(overridingValues);
        float bloodValueMultiplier = 1;
        Integer i = bloodValues.get(new ResourceLocation("multiplier"));
        if (i != null) {
            bloodValueMultiplier = i / 10F;
        }
        final IConvertingHandler defaultHandler = defaultConvertingHandlerCreator.create(null);
        for (Map.Entry<Class<? extends EntityCreature>, IConvertingHandler> entry : convertibles.entrySet()) {
            ResourceLocation id = EntityList.getKey(entry.getKey());
            if (id == null) {
                VampirismMod.log.w(TAG, "Cannot register convertible %s since there is no EntityString for it", entry.getKey());
                continue;
            }
            Integer blood = bloodValues.remove(id);
            if (blood == null) {
                VampirismMod.log.w(TAG, "Missing blood value for convertible creature %s (%s)", entry.getKey().getName(), id);
                continue;
            }
            blood = Math.round(blood * bloodValueMultiplier);
            VampirismMod.log.i(TAG, " Registering convertible %s with blood %d and handler %s", entry.getKey().getName(), blood, entry.getValue());
            BiteableEntry biteEntry = new BiteableEntry(blood, (entry.getValue() == null ? defaultHandler : entry.getValue()));
            biteables.put(id, biteEntry);
        }
        for (Map.Entry<ResourceLocation, Integer> entry : bloodValues.entrySet()) {
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

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends EntityCreature> Function<T, IExtendedCreatureVampirism> getCustomExtendedCreatureConstructor(T entity) {
        return extendedCreatureConstructors.get(entity.getClass());
    }


    @Nullable
    @Override
    public BiteableEntry getEntry(EntityCreature creature) {
        return getEntry(EntityList.getKey(creature));
    }

    public void calculateBloodEntry(EntityCreature creature) {
        ResourceLocation id = EntityList.getKey(creature);
        BiteableEntry entry = getEntry(id);
        if (entry != null) return;
        if (!Configs.autoCalculateEntityBlood || !(creature instanceof EntityAnimal)) {
            biteables.put(id, BiteableEntry.NOT_BITEABLE);
            return;
        }
        AxisAlignedBB bb = creature.getEntityBoundingBox();
        double v = bb.maxX - bb.minX;
        v *= bb.maxY - bb.minY;
        v *= bb.maxZ - bb.minZ;
        if (creature.isChild()) {
            v *= 8; //Rough approximation. Should work for most vanilla animals. Avoids having to change the entities scale
        }
        int blood = 0;

        if (v > 0.0) {
            blood = (int) v;
            blood = Math.min(20, blood);
        }
        entry = blood == 0 ? BiteableEntry.NOT_BITEABLE : new BiteableEntry(blood);
        biteables.put(id, entry);
        VampirismMod.log.d(TAG, "Calculated size %s and blood value %s for entity %s", v, blood, id);
    }

    @Nullable
    @Override
    public BiteableEntry getEntry(ResourceLocation entity_id) {
        return biteables.get(entity_id);
    }

    @Override
    public void overrideBloodValues(Map<ResourceLocation, Integer> values) {
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

    public void addSavedBloodValues(@Nonnull Map<ResourceLocation, Integer> values) {
        for (Map.Entry<ResourceLocation, Integer> entry : values.entrySet()) {
            generatedBiteables.put(entry.getKey(), new BiteableEntry(entry.getValue()));
        }
    }

    @Nonnull
    public Map<ResourceLocation, Integer> getBloodValuesToSave() {
        Map<ResourceLocation, Integer> values = Maps.newHashMap();
        for (Map.Entry<ResourceLocation, BiteableEntry> entry : generatedBiteables.entrySet()) {
            int blood = entry.getValue().blood;
            if (blood > 0) {
                values.put(entry.getKey(), blood);
            }
        }
        return values;
    }
}