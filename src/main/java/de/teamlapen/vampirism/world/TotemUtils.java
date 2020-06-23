package de.teamlapen.vampirism.world;

import com.google.common.collect.*;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.entity.FactionVillagerProfession;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
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
    private static Map<IFaction, List<TotemTileEntity.CaptureEntityEntry>> captureEntities;

    /**
     * mapping from faction to basic village guard super class
     */
    private static Map<IFaction, Class<? extends MobEntity>> guardEntities;

    /**
     * mapping from faction to villageprofession
     */
    private static Map<IFaction, FactionVillagerProfession> professions;

    private static Tmp temporaryStorage = new Tmp();

    public static List<TotemTileEntity.CaptureEntityEntry> getCaptureEntries(IFaction faction) {
        return captureEntities.get(faction);
    }

    public static Collection<FactionVillagerProfession> getProfessions() {
        return professions.values();
    }

    public static FactionVillagerProfession getProfession(IFaction faction) {
        return professions.get(faction);
    }

    public static Class<? extends MobEntity> getGuardClass(IFaction faction) {
        return guardEntities.getOrDefault(faction, MobEntity.class);
    }



    public static void processEntries() {
        captureEntities = ImmutableMap.copyOf(temporaryStorage.captureEntities);

        ImmutableMap.Builder<IFaction, FactionVillagerProfession> builder = ImmutableMap.builder();
        temporaryStorage.professions.forEach(profession -> builder.put(profession.getFaction(), profession));
        professions = builder.build();

        guardEntities = ImmutableMap.copyOf(temporaryStorage.guards);

        temporaryStorage = null;
    }

    /**
     * add entries before {@link InterModProcessEvent}
     */
    public static void addCaptureEntry(IFaction faction, List<TotemTileEntity.CaptureEntityEntry> entries) {
        temporaryStorage.captureEntities.computeIfAbsent(faction, f -> Lists.newArrayList()).addAll(entries);

    }

    /**
     * add entries before {@link InterModProcessEvent}
     */
    public static void addProfession(FactionVillagerProfession profession) {
        temporaryStorage.professions.add(profession);
    }

    /**
     * add entries before {@link InterModProcessEvent}
     */
    public static void setGuardEntityClass(IFaction faction, Class<? extends MobEntity> clazz) {
        temporaryStorage.guards.put(faction, clazz);
    }

    private static class Tmp {
        private final Set<FactionVillagerProfession> professions = Sets.newHashSet();
        private final Map<IFaction, List<TotemTileEntity.CaptureEntityEntry>> captureEntities = Maps.newHashMap();
        private final Map<IFaction, Class<? extends MobEntity>> guards = Maps.newHashMap();
    }
}
