package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.entity.action.EntityActions;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.player.vampire.skills.VampireSkills;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.ApiStatus;

/**
 * Handles registrations of all registrable things as well as a few additional
 * dependent things
 */
@ApiStatus.Internal
public class RegistryManager implements IInitListener {

    public RegistryManager() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onMissingMappings);
    }

    public static void setupRegistries(IEventBus modbus) {
        ModRegistries.init(modbus);
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

    public void onMissingMappings(MissingMappingsEvent event) {
        VampireSkills.fixMappings(event);
        HunterSkills.fixMappings(event);
        ModPotions.fixMappings(event);
        ModTiles.fixMappings(event);
        ModItems.fixMappings(event);
        ModBlocks.fixMappings(event);
        ModEnchantments.fixMapping(event);
        ModEntities.fixMapping(event);
    }

    @SubscribeEvent
    public void onRegisterEffects(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.MOB_EFFECTS)) {
            ModEffects.replaceEffects(((IForgeRegistry<MobEffect>) (Object)event.getForgeRegistry())); //TODO check
        }
    }
}
