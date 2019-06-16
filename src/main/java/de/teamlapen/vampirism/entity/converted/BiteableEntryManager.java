package de.teamlapen.vampirism.entity.converted;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.teamlapen.vampirism.api.entity.BiteableEntry;
import de.teamlapen.vampirism.config.Configs;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;


/**
 * Manages biteable entries.
 * Get's values from various sources
 * Static values (included in Vampirism jar) from {@link VampirismEntityRegistry}
 * Dynamically calculated values from itself
 * Dynamically saved values on world load from {@link de.teamlapen.vampirism.config.BloodValueLoader}
 * <p>
 * <p>
 * Dynamic values are reset on starting (server)/ on connecting (client).
 * Dynamic values are calculated during gameplay and saved on stopping (server).
 * Values are currently not synced between server and client, however, ExtendedCreatures do so.
 */
public class BiteableEntryManager {

    private final static Logger LOGGER = LogManager.getLogger(BiteableEntryManager.class);
    private final Map<ResourceLocation, BiteableEntry> hardcoded;

    private final Map<ResourceLocation, BiteableEntry> dynamic = Maps.newHashMap();
    private final Set<ResourceLocation> blacklist = Sets.newHashSet();
    private boolean init;

    /**
     * @param hardcoded Values added from JAR or API
     * @param blacklist IDs for which no dynamic values should be calculated
     */
    public BiteableEntryManager(Map<ResourceLocation, BiteableEntry> hardcoded, Set<ResourceLocation> blacklist) {
        this.hardcoded = ImmutableMap.copyOf(hardcoded);
        this.blacklist.addAll(blacklist);
        init = false;
    }

    /**
     * Adds a dynamic value.
     * Respects the convertible status from the hardcoded list
     *
     * @return The created entry
     */
    public BiteableEntry addDynamic(ResourceLocation id, int blood) {
        BiteableEntry existing = dynamic.get(id);
        if (existing != null) {
            existing = existing.modifyBloodValue(blood);
        } else {
            existing = new BiteableEntry(blood);
        }
        dynamic.put(id, existing);
//        sendToClients(id,blood);
        return existing;
    }

    public void addDynamic(Map<ResourceLocation, Integer> map) {
        for (Map.Entry<ResourceLocation, Integer> e : map.entrySet()) {
            addDynamic(e.getKey(), e.getValue());
        }
    }

    /**
     * Calculate the blood value for the given creature
     * If the result is 0 blood this returns null and the entity is blacklisted
     * If the entity is blacklisted this returns null immediately
     * If the bitableentry already exists it is returned
     *
     * @return The created/existing entry or null
     */
    public @Nullable
    BiteableEntry calculate(EntityCreature creature) {
        ResourceLocation id = new ResourceLocation(creature.getEntityString());
        if (blacklist.contains(id)) return null;
        BiteableEntry entry = get(id);
        if (entry != null) return entry;
        if (!Configs.autoCalculateEntityBlood || !(creature instanceof EntityAnimal)) {
            blacklist.add(id);
            return null;
        }
        AxisAlignedBB bb = creature.getBoundingBox();
        double v = bb.maxX - bb.minX;
        v *= bb.maxY - bb.minY;
        v *= bb.maxZ - bb.minZ;
        if (creature.isChild()) {
            v *= 8; //Rough approximation. Should work for most vanilla animals. Avoids having to change the entities scale
        }
        int blood = 0;

        if (v >= 0.3) {
            blood = (int) (v * 10d);
            blood = Math.min(15, blood);//Make sure there are no too crazy values
        }
        if (creature.getMaxHealth() > 50) {
            blood = 0;//Make sure very strong creatures cannot be easily killed by sucking their blood
        }
        LOGGER.debug("Calculated size %s and blood value %s for entity %s", Math.round(v * 100) / 100F, blood, id);
        if (blood == 0) {
            blacklist.add(id);
            return null;
        } else {
            return addDynamic(id, blood);
        }
    }

    public @Nullable
    BiteableEntry get(ResourceLocation id) {
        return init ? dynamic.get(id) : hardcoded.get(id);
    }

    public Map<ResourceLocation, Integer> getDynamicAll() {
        Map<ResourceLocation, Integer> map = Maps.newHashMap();

        for (Map.Entry<ResourceLocation, BiteableEntry> entry : dynamic.entrySet()) {
            map.put(entry.getKey(), entry.getValue().blood);
        }
        return map;
    }

    /**
     * Get all dynamic values which id's are not present in the hardcoded list
     *
     * @return
     */
    public Map<ResourceLocation, Integer> getValuesToSave() {
        Map<ResourceLocation, Integer> map = Maps.newHashMap();
        for (Map.Entry<ResourceLocation, BiteableEntry> entry : dynamic.entrySet()) {
            if (!hardcoded.containsKey(entry.getKey())) {
                map.put(entry.getKey(), entry.getValue().blood);
            }
        }
        return map;
    }

    /**
     * Prepare the dynamic list
     */
    public void initDynamic() {
        dynamic.clear();
        dynamic.putAll(hardcoded);
        init = true;
    }

//    Not used for now. Values are only used in ExtendedCreatures and we can sync values there
//
//    private void sendToClients(ResourceLocation id, int value){
//        Map<ResourceLocation,Integer> map=Maps.newHashMap();
//        map.put(id,value);
//        sendToClients(map);
//    }
//    private void sendToClients(Map<ResourceLocation,Integer> map){
//        BloodValuePacket packet = new BloodValuePacket(map);
//        VampirismMod.dispatcher.sendToAll(packet);
//    }
//
//    public void sendDynamicToClient(EntityPlayerMP player){
//        BloodValuePacket packet = new BloodValuePacket(getDynamicAll());
//        VampirismMod.dispatcher.sendTo(packet,player);
//    }

    public void resetDynamic() {
        init = false;
        dynamic.clear();
    }


}
