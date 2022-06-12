package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.entity.action.EntityActions;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.player.vampire.skills.VampireSkills;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;

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
        ModLoot.registerLoot(modbus);
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
    }

    @SubscribeEvent
    public void onBuildRegistries(NewRegistryEvent event) {
        ModRegistries.init(event);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEntities::onModifyEntityTypeAttributes);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEntities::onRegisterEntityTypeAttributes);
    }

    @Override
    public void onInitStep(Step step, ParallelDispatchEvent event) {
        switch (step) {
            case COMMON_SETUP:
                ModEntities.registerConvertibles();
                ModEntities.registerSpawns();
                ModEntities.registerCustomExtendedCreatures();
                ModItems.registerCraftingRecipes();
                ModPotions.registerPotionMixes();
                ModAdvancements.registerAdvancementTrigger();
                event.enqueueWork(() -> {
                    ModEntities.initializeEntities();
                    ModStats.registerCustomStats();
                    ModCommands.registerArgumentTypesUsage();
                    ModVillage.villagerTradeSetup();
                });
            case LOAD_COMPLETE:
                if (ModEffects.checkNightVision()) {
                    event.enqueueWork(ModEffects::fixNightVisionEffectTypesUnsafe);
                }
                ModRecipes.registerDefaultLiquidColors();
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
    public void onMissingMappingsTiles(RegistryEvent.MissingMappings<BlockEntityType<?>> event) {
        ModTiles.fixMappings(event);
    }

    @SubscribeEvent
    public void onMissingMappingsPotions(RegistryEvent.MissingMappings<Potion> event) {
        ModPotions.fixMappings(event);
    }

    @SubscribeEvent
    public void onMissingMappingsSkills(RegistryEvent.MissingMappings<ISkill<?>> event) {
        HunterSkills.fixMappings(event);
        VampireSkills.fixMappings(event);
    }

    @SubscribeEvent
    public void onRegisterEffects(RegistryEvent.Register<MobEffect> event) {
        ModEffects.replaceEffects(event.getRegistry());
    }
}
