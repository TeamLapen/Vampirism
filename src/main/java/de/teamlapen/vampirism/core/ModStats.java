package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public class ModStats {

    public static final ResourceLocation weapon_table = new ResourceLocation(REFERENCE.MODID, "weapon_table");
    public static final ResourceLocation interact_alchemical_cauldron = new ResourceLocation(REFERENCE.MODID, "interact_alchemical_cauldron");
    public static final ResourceLocation capture_village = new ResourceLocation(REFERENCE.MODID, "capture_village");
    public static final ResourceLocation defend_village = new ResourceLocation(REFERENCE.MODID, "defend_village");
    public static final ResourceLocation win_village_capture = new ResourceLocation(REFERENCE.MODID, "win_village_capture");
    public static final ResourceLocation infected_creatures = new ResourceLocation(REFERENCE.MODID, "infected_creatures");
    public static final ResourceLocation interact_with_alchemy_table = new ResourceLocation(REFERENCE.MODID, "interact_with_alchemy_table");

    static void registerCustomStats() {
        register(weapon_table);
        register(interact_alchemical_cauldron);
        register(capture_village);
        register(defend_village);
        register(win_village_capture);
        register(infected_creatures);
        register(interact_with_alchemy_table);
    }

    private static void register(ResourceLocation id) {
        Registry.register(Registry.CUSTOM_STAT, id, id);
        Stats.CUSTOM.get(id, StatFormatter.DEFAULT);
    }
}
