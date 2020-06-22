package de.teamlapen.vampirism.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.entity.FactionVillagerProfession;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TotemUtils {

    /**
     * weighted entitylist for capture entity spawn based on Faction
     * setup once
     */
    private static final Map<IFaction, List<TotemTileEntity.CaptureEntityEntry>> captureEntities = Maps.newHashMap();

    /**
     * mapping from faction to villageprofession
     */
    private static final Map<IFaction, FactionVillagerProfession> professions = Maps.newHashMap();

    /**
     * temporary storage
     */
    private static final Set<FactionVillagerProfession> professionsTmp = Sets.newHashSet();

    /**
     * add entries before {@link InterModProcessEvent}
     */
    public static void addCaptureEntry(IFaction faction, List<TotemTileEntity.CaptureEntityEntry> entries) {
        captureEntities.compute(faction, ((iFaction, captureEntityEntries) -> {
            if(captureEntityEntries == null) {
                return entries;
            }else {
                captureEntityEntries.addAll(entries);
                return captureEntityEntries;
            }
        }));

        professionsTmp.forEach(profession -> professions.put(profession.getFaction(), profession));
        professionsTmp.clear();
    }

    public static List<TotemTileEntity.CaptureEntityEntry> getCaptureEntries(IFaction faction) {
        return captureEntities.get(faction);
    }

    public static void processEntries() {
        captureEntities.replaceAll((f, v) -> ImmutableList.copyOf(captureEntities.get(f)));
    }

    public static void addProfession(FactionVillagerProfession profession) {
        professionsTmp.add(profession);
    }

    public static Collection<FactionVillagerProfession> getProfessions() {
        return professions.values();
    }

    public static FactionVillagerProfession getProfession(IFaction faction) {
        return professions.get(faction);
    }
}
