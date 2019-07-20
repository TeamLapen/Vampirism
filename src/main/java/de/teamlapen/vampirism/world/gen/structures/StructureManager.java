package de.teamlapen.vampirism.world.gen.structures;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.world.loot.LootHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

public class StructureManager {

    private static final Logger LOGGER = LogManager.getLogger(StructureManager.class);
    private final static Map<Structure, VampirismTemplate> templates = Maps.newHashMap();
    private final static String TAG = "StructureManager";

    public static void init() {
        LOGGER.debug("Loading structures");
        for (Structure s : Structure.values()) {
            loadTemplate(s);
        }
        LOGGER.debug("Loaded {} structures", Structure.values().length);
    }

    private static void loadTemplate(Structure structure) {
        InputStream input = StructureManager.class.getResourceAsStream("/structures/" + structure.name + ".nbt");
        if (input == null) {
            LOGGER.error("Failed to locate structure file {}", structure.name);
            return;
        }
        try {
            CompoundNBT data = CompressedStreamTools.readCompressed(input);
            VampirismTemplate template = new VampirismTemplate();
            template.read(data);
            templates.put(structure, template);
            if (structure.loot) template.setLootTable(LootHandler.addStructureLootTable(structure.name));

        } catch (IOException e) {
            LOGGER.error(String.format("Failed to load structure file %s", structure.name), e);
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
