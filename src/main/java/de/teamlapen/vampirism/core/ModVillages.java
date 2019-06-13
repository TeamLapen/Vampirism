package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.util.SRGNAMES;
import de.teamlapen.vampirism.world.gen.village.VillagePieceModChurch;
import de.teamlapen.vampirism.world.gen.village.VillagePieceTotem;
import de.teamlapen.vampirism.world.gen.village.VillagePieceTrainer;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles Village related stuff
 */
public class ModVillages {
    @ObjectHolder("vampirism:hunter_expert")
    public static final VillagerRegistry.VillagerProfession profession_hunter_expert = getNull();
    @ObjectHolder("vampirism:vampire_expert")
    public static final VillagerRegistry.VillagerProfession profession_vampire_expert = getNull();
    private final static Logger LOGGER = LogManager.getLogger(ModVillages.class);

    static void init() {
        registerCreationHandlers();
        registerPieces();
        registerTrades();
    }

    private static void registerPieces() {
        MapGenStructureIO.registerStructureComponent(VillagePieceTrainer.class, "Vampirism-TR");
        MapGenStructureIO.registerStructureComponent(VillagePieceModChurch.class, "Vampirism-MC");
        MapGenStructureIO.registerStructureComponent(VillagePieceTotem.class, "Vampirism-To");
    }

    private static void registerCreationHandlers() {
        if (!Configs.disable_all_worldgen) {
            VillagerRegistry.instance().registerVillageCreationHandler(new VillagePieceTrainer.CreationHandler());
            VillagerRegistry.instance().registerVillageCreationHandler(new VillagePieceModChurch.CreationHandler());
            VillagerRegistry.instance().registerVillageCreationHandler(new VillagePieceTotem.CreationHandler());
        }
    }

    public static void modifyVillageSize(MapGenBase mapGenVillage) {
        if (mapGenVillage instanceof MapGenVillage) {


            try {
                ReflectionHelper.setPrivateValue(MapGenVillage.class, (MapGenVillage) mapGenVillage, Configs.village_size, "size", SRGNAMES.MapGenVillage_size);
            } catch (ReflectionHelper.UnableToAccessFieldException e) {
                LOGGER.error(e, "Could not modify field 'terrainType' in MapGenVillage");
            }

            try {
                ReflectionHelper.setPrivateValue(MapGenVillage.class, (MapGenVillage) mapGenVillage, Configs.village_density, "distance", SRGNAMES.MapGenVillage_distance);
            } catch (ReflectionHelper.UnableToAccessFieldException e) {
                LOGGER.error(e, "Could not modify field for village density in MapGenVillage");
            }
            try {
                ReflectionHelper.setPrivateValue(MapGenVillage.class, (MapGenVillage) mapGenVillage, Configs.village_min_dist, "minTownSeparation", SRGNAMES.MapGenVillage_minTownSeperation);
            } catch (ReflectionHelper.UnableToAccessFieldException e) {
                LOGGER.error(e, "Could not modify field for village min dist in MapGenVillage");
            }


            LOGGER.debug("Modified MapGenVillage fields.");

        } else {
            //Should not be possible
            LOGGER.error("VillageGen (%s) is not an instance of MapGenVillage, can't modify gen", mapGenVillage);
        }
    }

    private static void registerTrades() {
        VillagerRegistry.VillagerProfession priest = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft", "priest"));
        if (priest != null) {
            VillagerRegistry.VillagerCareer hunterPriest = new VillagerRegistry.VillagerCareer(priest, "vampirism.hunter_priest");
            hunterPriest.addTrade(1, new EntityVillager.EmeraldForItems(Items.GOLD_INGOT, new EntityVillager.PriceInfo(8, 10)));
            hunterPriest.addTrade(2, new EntityVillager.ListItemForEmeralds(ModItems.holy_water_bottle_normal, new EntityVillager.PriceInfo(-8, -2)));
            hunterPriest.addTrade(3, new EntityVillager.ListItemForEmeralds(ModItems.holy_water_bottle_enhanced, new EntityVillager.PriceInfo(-5, -1)));
            hunterPriest.addTrade(3, new EntityVillager.ListItemForEmeralds(ModItems.holy_salt, new EntityVillager.PriceInfo(-10, -3)));
            hunterPriest.addTrade(4, new EntityVillager.ListItemForEmeralds(Items.EXPERIENCE_BOTTLE, new EntityVillager.PriceInfo(3, 11)));
        } else {
            LOGGER.warn("Did not find vanilla priest profession");
        }
        VillagerRegistry.VillagerCareer normal_hunter_expert = new VillagerRegistry.VillagerCareer(profession_hunter_expert, "vampirism.hunter_expert");
        normal_hunter_expert.addTrade(1, new EntityVillager.EmeraldForItems(ModItems.vampire_fang, new EntityVillager.PriceInfo(20, 30)));
        normal_hunter_expert.addTrade(2, new EntityVillager.EmeraldForItems(ModItems.vampire_book, new EntityVillager.PriceInfo(1, 1)));
        //TODO modify recipes
        VillagerRegistry.VillagerCareer normal_vampire_expert = new VillagerRegistry.VillagerCareer(profession_vampire_expert, "vampirism.vampire_expert");
        normal_vampire_expert.addTrade(1, new EntityVillager.EmeraldForItems(ModItems.vampire_fang, new EntityVillager.PriceInfo(20, 30)));
        normal_vampire_expert.addTrade(2, new EntityVillager.EmeraldForItems(ModItems.vampire_book, new EntityVillager.PriceInfo(1, 1)));
    }

    static void registerProfessions(IForgeRegistry<VillagerRegistry.VillagerProfession> registry) {
        registry.register(new VillagerRegistry.VillagerProfession("vampirism:hunter_expert", "vampirism:textures/entity/villager_hunter_expert.png", "vampirism:textures/entity/villager_hunter_expert_zombie.png"));
        registry.register(new VillagerRegistry.VillagerProfession("vampirism:vampire_expert", "vampirism:textures/entity/villager_vampire_expert.png", "vampirism:textures/entity/villager_vampire_expert_zombie.png"));
    }
}
