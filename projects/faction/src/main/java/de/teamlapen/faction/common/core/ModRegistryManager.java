package de.teamlapen.faction.common.core;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ModRegistryManager {

    public void setupRegistries(IEventBus bus) {
        DefaultFactions.register(bus);
        FactionFoodBehaviours.register(bus);
        FactionAdvancements.register(bus);
        FactionBlocks.register(bus);
        FactionBlockEntities.register(bus);
        FactionDataComponents.register(bus);
        FactionEffects.register(bus);
        FactionEntities.register(bus);
        FactionItems.register(bus);
        FactionMenus.register(bus);
        FactionMinionTasks.register(bus);
        FactionParticles.register(bus);
        FactionSounds.register(bus);
        FactionStats.register(bus);
        FactionTasks.register(bus);
        FactionCommands.register(bus);
        FactionAttachments.register(bus);
        FactionSkills.register(bus);
        FactionSkillPointProvider.register(bus);

        registerModEventHandler(bus);
    }

    private void registerModEventHandler(IEventBus eventBus) {
        eventBus.addListener(ModRegistries::registerRegistries);
        eventBus.addListener(ModRegistries::registerDatapackRegistries);
        eventBus.addListener(FMLCommonSetupEvent.class, e -> e.enqueueWork(FactionStats::registerFormatter));
        eventBus.addListener(FactionCreativeTabs::addToExistingCreativeTabs);
    }
}
