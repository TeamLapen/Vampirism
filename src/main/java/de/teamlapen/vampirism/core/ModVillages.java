package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.util.SRGNAMES;
import de.teamlapen.vampirism.world.gen.village.VillagePieceModChurch;
import de.teamlapen.vampirism.world.gen.village.VillagePieceTrainer;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

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
                registerTrades();
                break;
            default://Do nothing
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
                ReflectionHelper.setPrivateValue(MapGenVillage.class, (MapGenVillage) mapGenVillage, Configs.village_size, "size", SRGNAMES.MapGenVillage_size);
            } catch (ReflectionHelper.UnableToAccessFieldException e) {
                VampirismMod.log.e(TAG, e, "Could not modify field 'terrainType' in MapGenVillage");
            }

            try {
                ReflectionHelper.setPrivateValue(MapGenVillage.class, (MapGenVillage) mapGenVillage, Configs.village_density, "distance", SRGNAMES.MapGenVillage_distance);
            } catch (ReflectionHelper.UnableToAccessFieldException e) {
                VampirismMod.log.e(TAG, e, "Could not modify field for village density in MapGenVillage");
            }
            try {
                ReflectionHelper.setPrivateValue(MapGenVillage.class, (MapGenVillage) mapGenVillage, Configs.village_min_dist, "minTownSeparation", SRGNAMES.MapGenVillage_minTownSeperation);
            } catch (ReflectionHelper.UnableToAccessFieldException e) {
                VampirismMod.log.e(TAG, e, "Could not modify field for village min dist in MapGenVillage");
            }


            VampirismMod.log.d(TAG, "Modified MapGenVillage fields.");

        } else {
            //Should not be possible
            VampirismMod.log.e(TAG, "VillageGen (%s) is not an instance of MapGenVillage, can't modify gen", mapGenVillage);
        }
    }

    private static void registerTrades() {
        VillagerRegistry.VillagerProfession priest = VillagerRegistry.instance().getRegistry().getValue(new ResourceLocation("minecraft", "priest"));
        VillagerRegistry.VillagerCareer hunterPriest = new VillagerRegistry.VillagerCareer(priest, "vampire_hunter");
        hunterPriest.addTrade(1, new EntityVillager.EmeraldForItems(Items.GOLD_INGOT, new EntityVillager.PriceInfo(8, 10)));
        hunterPriest.addTrade(1, new EntityVillager.EmeraldForItems(ModItems.vampireFang, new EntityVillager.PriceInfo(20, 30)));
        hunterPriest.addTrade(2, new EntityVillager.ListItemForEmeralds(ModItems.holyWaterBottle, new EntityVillager.PriceInfo(-2, -8)));
        hunterPriest.addTrade(3, new EntityVillager.ListItemForEmeralds(ModItems.holyWaterBottle.setTier(new ItemStack(ModItems.holyWaterBottle), IItemWithTier.TIER.ENHANCED), new EntityVillager.PriceInfo(-1, -5)));
        hunterPriest.addTrade(3, new EntityVillager.ListItemForEmeralds(ModItems.holySalt, new EntityVillager.PriceInfo(-3, -10)));
        hunterPriest.addTrade(4, new EntityVillager.ListItemForEmeralds(Items.EXPERIENCE_BOTTLE, new EntityVillager.PriceInfo(3, 11)));
    }
}
