package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.stats.IStatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public class ModStats {

    public static final ResourceLocation weapon_table = new ResourceLocation(REFERENCE.MODID, "weapon_table");
    public static final ResourceLocation blood_table = new ResourceLocation(REFERENCE.MODID, "blood_table");
    public static final ResourceLocation interact_alchemical_cauldron = new ResourceLocation(REFERENCE.MODID, "interact_alchemical_cauldron");

    public static void registerStats(IForgeRegistry<StatType<?>> registry) {
        register(weapon_table);
        register(interact_alchemical_cauldron);
    }

    private static void register(ResourceLocation id) {
        Stats.CUSTOM.get(id);
    }

    private static void register(ResourceLocation id, IStatFormatter formatter) {
        Stats.CUSTOM.get(id, formatter);
    }
}
