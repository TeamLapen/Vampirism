package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import org.jetbrains.annotations.NotNull;

public class ModStats {

    public static final ResourceLocation WEAPON_TABLE = new ResourceLocation(REFERENCE.MODID, "weapon_table");
    public static final ResourceLocation INTERACT_ALCHEMICAL_CAULDRON = new ResourceLocation(REFERENCE.MODID, "interact_alchemical_cauldron");
    public static final ResourceLocation CAPTURE_VILLAGE = new ResourceLocation(REFERENCE.MODID, "capture_village");
    public static final ResourceLocation DEFEND_VILLAGE = new ResourceLocation(REFERENCE.MODID, "defend_village");
    public static final ResourceLocation WIN_VILLAGE_CAPTURE = new ResourceLocation(REFERENCE.MODID, "win_village_capture");
    public static final ResourceLocation INFECTED_CREATURES = new ResourceLocation(REFERENCE.MODID, "infected_creatures");
    public static final ResourceLocation INTERACT_WITH_ALCHEMY_TABLE = new ResourceLocation(REFERENCE.MODID, "interact_with_alchemy_table");
    public static final ResourceLocation MOTHER_DEFEATED = new ResourceLocation(REFERENCE.MODID, "mother_defeated");

    static void registerCustomStats() {
        register(WEAPON_TABLE);
        register(INTERACT_ALCHEMICAL_CAULDRON);
        register(CAPTURE_VILLAGE);
        register(DEFEND_VILLAGE);
        register(WIN_VILLAGE_CAPTURE);
        register(INFECTED_CREATURES);
        register(INTERACT_WITH_ALCHEMY_TABLE);
        register(MOTHER_DEFEATED);
    }

    private static void register(@NotNull ResourceLocation id) {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
        Stats.CUSTOM.get(id, StatFormatter.DEFAULT);
    }
}
