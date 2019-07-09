package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.util.SRGNAMES;
import de.teamlapen.vampirism.world.gen.village.VillagePieceModChurch;
import de.teamlapen.vampirism.world.gen.village.VillagePieceTotem;
import de.teamlapen.vampirism.world.gen.village.VillagePieceTrainer;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.ChunkGenSettings;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
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
        Structures.registerStructureComponent(VillagePieceTrainer.class, "Vampirism-TR");
        Structures.registerStructureComponent(VillagePieceModChurch.class, "Vampirism-MC");
        Structures.registerStructureComponent(VillagePieceTotem.class, "Vampirism-To");
    }

    private static void registerCreationHandlers() {
        if (!Configs.disable_all_worldgen) {
            VillagerRegistry.instance().registerVillageCreationHandler(new VillagePieceTrainer.CreationHandler());
            VillagerRegistry.instance().registerVillageCreationHandler(new VillagePieceModChurch.CreationHandler());
            VillagerRegistry.instance().registerVillageCreationHandler(new VillagePieceTotem.CreationHandler());
        }
    }

    static void modifyVillageSize(ChunkGenSettings settings) {

        if (!Configs.village_modify) {
            LOGGER.trace("Not modifying village");
            return;
        }
            try {
                ObfuscationReflectionHelper.setPrivateValue(ChunkGenSettings.class, settings, Configs.village_distance, SRGNAMES.ChunkGenSettings_villageDistance);
            } catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
                LOGGER.error("Could not modify field 'villageDistance' in ChunkGenSettings", e);
            }


        try {
            ObfuscationReflectionHelper.setPrivateValue(ChunkGenSettings.class, settings, Configs.village_separation, SRGNAMES.ChunkGenSettings_villageSeparation);
            } catch (ObfuscationReflectionHelper.UnableToAccessFieldException e) {
            LOGGER.error("Could not modify field for villageSeparation in ChunkGenSettings", e);
            }


            LOGGER.debug("Modified MapGenVillage fields.");

    }

    private static void registerTrades() {
        VillagerRegistry.VillagerProfession priest = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation("minecraft", "priest"));
        if (priest != null) {
            VillagerRegistry.VillagerCareer hunterPriest = new VillagerRegistry.VillagerCareer(priest, "vampirism.hunter_priest");
            hunterPriest.addTrade(1, new VillagerEntity.EmeraldForItems(Items.GOLD_INGOT, new VillagerEntity.PriceInfo(8, 10)));
            hunterPriest.addTrade(2, new VillagerEntity.ListItemForEmeralds(ModItems.holy_water_bottle_normal, new VillagerEntity.PriceInfo(-8, -2)));
            hunterPriest.addTrade(3, new VillagerEntity.ListItemForEmeralds(ModItems.holy_water_bottle_enhanced, new VillagerEntity.PriceInfo(-5, -1)));
            hunterPriest.addTrade(3, new VillagerEntity.ListItemForEmeralds(ModItems.holy_salt, new VillagerEntity.PriceInfo(-10, -3)));
            hunterPriest.addTrade(4, new VillagerEntity.ListItemForEmeralds(Items.EXPERIENCE_BOTTLE, new VillagerEntity.PriceInfo(3, 11)));
        } else {
            LOGGER.warn("Did not find vanilla priest profession");
        }
        VillagerRegistry.VillagerCareer normal_hunter_expert = new VillagerRegistry.VillagerCareer(profession_hunter_expert, "vampirism.hunter_expert");
        normal_hunter_expert.addTrade(1, new VillagerEntity.EmeraldForItems(ModItems.vampire_fang, new VillagerEntity.PriceInfo(20, 30)));
        normal_hunter_expert.addTrade(2, new VillagerEntity.EmeraldForItems(ModItems.vampire_book, new VillagerEntity.PriceInfo(1, 1)));
        //TODO modify recipes
        VillagerRegistry.VillagerCareer normal_vampire_expert = new VillagerRegistry.VillagerCareer(profession_vampire_expert, "vampirism.vampire_expert");
        normal_vampire_expert.addTrade(1, new VillagerEntity.EmeraldForItems(ModItems.vampire_fang, new VillagerEntity.PriceInfo(20, 30)));
        normal_vampire_expert.addTrade(2, new VillagerEntity.EmeraldForItems(ModItems.vampire_book, new VillagerEntity.PriceInfo(1, 1)));
    }

    static void registerProfessions(IForgeRegistry<VillagerRegistry.VillagerProfession> registry) {
        registry.register(new VillagerRegistry.VillagerProfession("vampirism:hunter_expert", "vampirism:textures/entity/villager_hunter_expert.png", "vampirism:textures/entity/villager_hunter_expert_zombie.png"));
        registry.register(new VillagerRegistry.VillagerProfession("vampirism:vampire_expert", "vampirism:textures/entity/villager_vampire_expert.png", "vampirism:textures/entity/villager_vampire_expert_zombie.png"));
    }
}
