package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.world.gen.village.VillagePieceModChurch;
import de.teamlapen.vampirism.world.gen.village.VillagePieceTrainer;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.lang.reflect.Field;

/**
 * Handles Village related stuff
 */
public class ModVillages {
    private final static String TAG = "ModVillages";

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case INIT:
                registerCreationHandlers();
                registerPieces();
                break;
        }

    }

    private static void registerPieces() {
        MapGenStructureIO.registerStructureComponent(VillagePieceTrainer.class, "Vampirism-TR");
        MapGenStructureIO.registerStructureComponent(VillagePieceModChurch.class, "Vampirism-MC");
    }

    private static void registerCreationHandlers() {
        VillagerRegistry.instance().registerVillageCreationHandler(new VillagePieceTrainer.CreationHandler());
        VillagerRegistry.instance().registerVillageCreationHandler(new VillagePieceModChurch.CreationHandler());
    }

    public static void modifyVillageSize(MapGenBase mapGenVillage) {
        if (mapGenVillage instanceof MapGenVillage) {


            try {
                Field type = null;
                Field density = null;
                Field minDist = null;

                Field[] fields = mapGenVillage.getClass().getDeclaredFields();
                for (Field f : fields) {
                    String name = f.getName();
                    if (name.equals("terrainType")) {
                        type = f;
                    } else if (name.equals("field_82665_g")) {
                        density = f;
                    } else if (name.equals("field_82666_h")) {
                        minDist = f;
                    }
                }

                if (type != null) {
                    type.setAccessible(true);
                    type.setInt(mapGenVillage, Configs.village_size);
                } else {
                    VampirismMod.log.w(TAG, "Could not find field 'terrainType' in MapGenVillage");
                }
                if (density != null) {
                    density.setAccessible(true);
                    density.setInt(mapGenVillage, Configs.village_density);
                } else {
                    VampirismMod.log.w(TAG, "Could not find field 'field_82665_g'(density) in MapGenVillage");
                }
                if (minDist != null) {
                    minDist.setAccessible(true);
                    minDist.setInt(mapGenVillage, Configs.village_min_dist);
                } else {
                    VampirismMod.log.w(TAG, "Could not find field 'field_82666_h'(minDist) in MapGenVillage");
                }
                VampirismMod.log.d(TAG, "Modified MapGenVillage fields.");
            } catch (Exception exc) {
                VampirismMod.log.e(TAG, exc, "Could not modify MapGenVillage, consider disabling village_modify_gen in configs");
            }
        } else {
            //Should not be possible
            VampirismMod.log.e(TAG, "VillageGen (%s) is not an instance of MapGenVillage, can't modify gen", mapGenVillage);
        }
    }
}
