package de.teamlapen.vampirism.common.core;

import de.teamlapen.vampirism.common.util.serialization.CodecModifications;
import de.teamlapen.vampirism.common.world.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.common.world.entity.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.common.world.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.common.world.entity.player.lord.actions.LordActions;
import de.teamlapen.vampirism.common.world.entity.player.lord.skills.LordSkills;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirismVampireVisions;
import de.teamlapen.vampirism.common.world.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.common.world.entity.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.data.ModDataPacks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * Handles registrations of all registrable things as well as a few additional
 * dependent things
 */
@ApiStatus.Internal
public class ModRegistryManager {

    public void setupRegistries(IEventBus eventBus) {
        ModAttributes.register(eventBus);
        ModFoodBehaviours.register(eventBus);
        ModBiomes.register(eventBus);
        ModBlocks.register(eventBus);
        ModMenus.register(eventBus);
        ModEffects.register(eventBus);
        ModEntities.register(eventBus);
        ModFeatures.register(eventBus);
        ModStructures.register(eventBus);
        ModFluids.register(eventBus);
        ModItems.register(eventBus);
        ModCreativeTabs.register(eventBus);
        ModLoot.register(eventBus);
        ModParticles.register(eventBus);
        ModPotions.register(eventBus);
        ModRecipes.register(eventBus);
        ModMapDecorations.register(eventBus);
        ModRefinements.register(eventBus);
        ModSkillPointProvider.register(eventBus);
        ModRefinementSets.register(eventBus);
        ModSounds.register(eventBus);
        ModTasks.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModAi.register(eventBus);
        ModVillage.register(eventBus);
        VampireActions.register(eventBus);
        HunterActions.register(eventBus);
        MinionTasks.register(eventBus);
        LordActions.register(eventBus);
        LordSkills.register(eventBus);
        VampireSkills.register(eventBus);
        HunterSkills.register(eventBus);
        ModCommands.register(eventBus);
        ModOils.register(eventBus);
        ModAttachments.register(eventBus);
        ModAdvancements.register(eventBus);
        ModStats.register(eventBus);
        ModEnchantments.register(eventBus);
        ModDataComponents.register(eventBus);
        ModFactions.register(eventBus);
        VampirismVampireVisions.register(eventBus);
        ModDimensions.register(eventBus);
        ModEnvironmentAttributes.register(eventBus);
        ModActivities.register(eventBus);
        ModMemoryTypes.register(eventBus);
        ModSensors.register(eventBus);
    }

    @SubscribeEvent
    public void onNewDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        ModRegistries.registerDatapackRegistries(event);
    }

    @SubscribeEvent
    public void onRegisterDataMapTypes(RegisterDataMapTypesEvent event) {
        ModDataMaps.registerDataMaps(event);
    }

    public void registerModEventHandler(IEventBus eventBus) {
        eventBus.addListener(ModEntities::onModifyEntityTypeAttributes);
        eventBus.addListener(ModEntities::onRegisterEntityTypeAttributes);
        eventBus.addListener(ModEntities::onRegisterSpawns);
        eventBus.addListener(ModRegistries::registerRegistries);
        eventBus.addListener(ModDataPacks::registerPackRepository);
        eventBus.addListener(ModBlockEntities::registerTileExtensions);
        eventBus.addListener(ModEntities::registerPlayerEventHandler);
    }

    public void registerForgeEventHandler(IEventBus eventBus) {
        eventBus.addListener(ModCommands::registerCommands);
        eventBus.addListener(ModPotions::registerPotionMixes);
    }

    @SubscribeEvent
    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModStats::registerFormatter);
        event.enqueueWork(CodecModifications::changeMobEffectCodec);
        event.enqueueWork(ModItems::registerDispenserBehaviour);
        event.enqueueWork(ModBlocks::registerFlammables);
        event.enqueueWork(ModFluids::registerFluidInteractions);
    }
}
