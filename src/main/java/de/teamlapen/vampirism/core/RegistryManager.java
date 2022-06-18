package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.entity.action.EntityActions;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.items.VampirismBoatItem;
import de.teamlapen.vampirism.misc.VampirismDispenseBoatBehavior;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.world.biome.VampirismBiomeFeatures;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Handles registrations of all registrable things as well as a few additional
 * dependent things
 */
public class RegistryManager implements IInitListener {

    public static void setupRegistries(IEventBus modbus) {
        ModAttributes.registerAttributes(modbus);
        ModBiomes.registerBiomes(modbus);
        ModBlocks.registerBlocks(modbus);
        ModContainer.registerContainer(modbus);
        ModEffects.registerEffects(modbus);
        ModEnchantments.registerEnchantments(modbus);
        ModEntities.registerEntities(modbus);
        ModFeatures.registerFeaturesAndStructures(modbus);
        ModFluids.registerFluids(modbus);
        ModItems.registerItems(modbus);
        ModParticles.registerParticles(modbus);
        ModPotions.registerPotions(modbus);
        ModRecipes.registerRecipeTypesAndSerializers(modbus);
        ModRefinements.registerRefinements(modbus);
        ModRefinementSets.registerRefinementSets(modbus);
        ModSounds.registerSounds(modbus);
        ModTasks.registerTasks(modbus);
        ModTiles.registerTiles(modbus);
        ModVillage.registerVillageObjects(modbus);
        VampireActions.registerDefaultActions(modbus);
        HunterActions.registerDefaultActions(modbus);
        EntityActions.registerDefaultActions(modbus);
        MinionTasks.register(modbus);
        VampireSkills.registerVampireSkills(modbus);
        HunterSkills.registerHunterSkills(modbus);
        ModOils.registerOils(modbus);
    }
    @SubscribeEvent
    public void onBuildRegistries(RegistryEvent.NewRegistry event) {
        ModRegistries.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEntities::onModifyEntityTypeAttributes);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEntities::onRegisterEntityTypeAttributes);
    }

    @Override
    public void onGatherData(GatherDataEvent event) {
        ModLoot.registerLootConditions();
        ModLoot.registerLootFunctionType();
    }

    @Override
    public void onInitStep(Step step, ParallelDispatchEvent event) {
        switch (step) {
            case COMMON_SETUP:
                event.enqueueWork(ModBiomes::addBiomesToGeneratorUnsafe);
                ModFeatures.registerIgnoredBiomesForStructures();
                ModEntities.registerConvertibles();
                ModEntities.registerSpawns();
                ModEntities.registerCustomExtendedCreatures();
                ModItems.registerCraftingRecipes();
                ModPotions.registerPotionMixes();
                ModAdvancements.registerAdvancementTrigger();
                ModLoot.registerLootConditions();
                ModLoot.registerLootFunctionType();
                VampirismBiomeFeatures.init();
                ModTiles.registerTileExtensionsUnsafe();
                DispenserBlock.registerBehavior(ModItems.DARK_SPRUCE_BOAT.get(), new VampirismDispenseBoatBehavior(VampirismBoatItem.BoatType.DARK_SPRUCE));
                DispenserBlock.registerBehavior(ModItems.CURSED_SPRUCE_BOAT.get(), new VampirismDispenseBoatBehavior(VampirismBoatItem.BoatType.CURSED_SPRUCE));
                event.enqueueWork(() -> {
                    VampirismBiomeFeatures.registerBiomeFeatures();
                    ModEntities.initializeEntities();
                    ModStats.registerCustomStats();
                    ModCommands.registerArgumentTypesUsage();
                    ModVillage.villagerTradeSetup();
                });
            case LOAD_COMPLETE:
                event.enqueueWork(ModFeatures::registerStructureSeparation);
                if (ModEffects.checkNightVision()) {
                    ModEffects.fixNightVisionEffectTypes();
                }
                ModRecipes.registerDefaultLiquidColors();
                break;
            case PROCESS_IMC:
                break;
            default:
                break;
        }
    }

    @SubscribeEvent
    public void onMissingMappingEntityTypes(RegistryEvent.MissingMappings<EntityType<?>> event) {
        ModEntities.fixMapping(event);
    }

    @SubscribeEvent
    public void onMissingMappingsBlocks(RegistryEvent.MissingMappings<Block> event) {
        ModBlocks.fixMappings(event);
    }

    @SubscribeEvent
    public void onMissingMappingsEnchantments(RegistryEvent.MissingMappings<Enchantment> event) {
        ModEnchantments.fixMapping(event);
    }

    @SubscribeEvent
    public void onMissingMappingsItems(RegistryEvent.MissingMappings<Item> event) {
        ModItems.fixMappings(event);
    }

    @SubscribeEvent
    public void onMissingMappingsPotions(RegistryEvent.MissingMappings<Potion> event) {
        ModPotions.fixMappings(event);
    }

    @SubscribeEvent
    public void onMissingMappingsSkills(RegistryEvent.MissingMappings<ISkill> event) {
        HunterSkills.fixMappings(event);
        VampireSkills.fixMappings(event);
    }

    @SubscribeEvent
    public void onRegisterEffects(RegistryEvent.Register<Effect> event) {
        ModEffects.replaceEffects(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterGlobalLootModifier(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        ModLoot.registerLootModifier(event.getRegistry());
    }
}
