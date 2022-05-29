package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.task.Task;
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
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolderRegistry;

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
    public void onRegisterActions(RegistryEvent.Register<IAction> event) {
        VampireActions.registerDefaultActions(event.getRegistry());
        HunterActions.registerDefaultActions(event.getRegistry());
        ObjectHolderRegistry.applyObjectHolders(); //Apply object holders so action skills can use them
    }

    @SubscribeEvent
    public void onRegisterEffects(RegistryEvent.Register<Effect> event) {
        ModEffects.replaceEffects(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterEntities(RegistryEvent.Register<EntityType<?>> event) {
        ModEntities.registerEntities(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterEntityActions(RegistryEvent.Register<IEntityAction> event) {
        EntityActions.registerDefaultActions(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterFeatures(RegistryEvent.Register<Feature<?>> event) {
        ModFeatures.registerFeatures(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterFluids(RegistryEvent.Register<Fluid> event) {
        ModFluids.registerFluids(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterMinionTasks(RegistryEvent.Register<IMinionTask<?, ?>> event) {

        MinionTasks.register(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterParticles(RegistryEvent.Register<ParticleType<?>> event) {
        ModParticles.registerParticles(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterPointOfInterest(RegistryEvent.Register<PointOfInterestType> event) {
        ModVillage.registerVillagePointOfInterestType(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterPotions(RegistryEvent.Register<Potion> event) {
        ModPotions.registerPotions(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterProfessions(RegistryEvent.Register<VillagerProfession> event) {
        ModVillage.registerProfessions(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterRecipeSerializer(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        ModRecipes.registerSerializer(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterRefinementSets(RegistryEvent.Register<IRefinementSet> event) {
        ModRefinementSets.registerRefinementSets(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterRefinements(RegistryEvent.Register<IRefinement> event) {
        ModRefinements.registerRefinements(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterSchedule(RegistryEvent.Register<Schedule> event) {
        ModVillage.registerSchedule(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterSensorTypes(RegistryEvent.Register<SensorType<?>> event) {
        ModVillage.registerSensor(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterSkills(RegistryEvent.Register<ISkill> event) {
        HunterSkills.registerHunterSkills(event.getRegistry());
        VampireSkills.registerVampireSkills(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterSounds(RegistryEvent.Register<SoundEvent> event) {

        ModSounds.registerSounds(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterStats(RegistryEvent.Register<StatType<?>> event) {
        ModStats.registerStats(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterStructures(RegistryEvent.Register<Structure<?>> event) {
        ModFeatures.registerStructures(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterTasks(RegistryEvent.Register<Task> event) {
        ModTasks.registerTasks(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        ModTiles.registerTiles(event.getRegistry());
    }

    @SubscribeEvent
    public void onTreeDecoratorType(RegistryEvent.Register<TreeDecoratorType<?>> event){
        ModFeatures.registerTreeDecoratorTypes(event.getRegistry());
    }
}
