package de.teamlapen.vampirism.world.gen.structure;

import com.google.common.collect.Maps;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.world.loot.LootHandler;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class StructureManager {

    private final static Map<Structure, VampirismTemplate> templates = Maps.newHashMap();
    private final static String TAG = "StructureManager";

    public static void init() {
        for (Structure s : Structure.values()) {
            loadTemplate(s);
        }
    }

    private static void loadTemplate(Structure structure) {
        InputStream input = StructureManager.class.getResourceAsStream("/structures/" + structure.name + ".nbt");
        if (input == null) {
            VampirismMod.log.e(TAG, "Failed to locate structure file for %s", structure.name);
            return;
        }
        try {
            NBTTagCompound data = CompressedStreamTools.readCompressed(input);
            VampirismTemplate template = new VampirismTemplate();
            template.read(data);
            templates.put(structure, template);
            if (structure.loot) template.setLootTable(LootHandler.addStructureLootTable(structure.name));

        } catch (IOException e) {
            VampirismMod.log.e(TAG, e, "Failed to load structure file %s", structure.name);
        }

    }

    public enum Structure {
        HOUSE("house", true);

        String name;
        boolean loot;

        Structure(String name, boolean loot) {
            this.name = name;
            this.loot = loot;
        }
    }
}
