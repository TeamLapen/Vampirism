package de.teamlapen.vampirism.world.gen.structure;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.world.loot.LootHandler;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StructureManager {

    private final static Map<Structure, VampirismTemplate> templates = Maps.newHashMap();
    private final static String TAG = "StructureManager";

    public static void init() {
        VampirismMod.log.d(TAG, "Loading structures");
        for (Structure s : Structure.values()) {
            loadTemplate(s);
        }
        VampirismMod.log.d(TAG, "Loaded %s structures", Structure.values().length);
    }

    private static void loadTemplate(Structure structure) {
        InputStream input = StructureManager.class.getResourceAsStream("/structures/" + structure.name + ".nbt");
        if (input == null) {
            LOGGER.error("Failed to locate structure file %s", structure.name);
            return;
        }
        try {
            NBTTagCompound data = CompressedStreamTools.readCompressed(input);
            VampirismTemplate template = new VampirismTemplate();
            template.read(data);
            templates.put(structure, template);
            if (structure.loot) template.setLootTable(LootHandler.addStructureLootTable(structure.name));

        } catch (IOException e) {
            LOGGER.error(e, "Failed to load structure file %s", structure.name);
        }

    }

    @Nullable
    public static VampirismTemplate get(@Nonnull Structure s) {
        return templates.get(s);
    }

    public enum Structure {
        HOUSE1("house1", true);

        String name;
        boolean loot;

        Structure(String name, boolean loot) {
            this.name = name;
            this.loot = loot;
        }

        public static Set<String> getNames() {
            Set<String> names = Sets.newHashSet();
            for (Structure e : values()) {
                names.add(e.name);
            }
            return names;
            
        }
    }
}
