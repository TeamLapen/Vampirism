package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.data.PackRepositories;
import de.teamlapen.vampirism.entity.action.EntityActions;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.entity.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.lord.actions.LordActions;
import de.teamlapen.vampirism.entity.player.lord.skills.LordSkills;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.ParallelDispatchEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Handles registrations of all registrable things as well as a few additional
 * dependent things
 */
@ApiStatus.Internal
public class RegistryManager implements IInitListener {

    private final IEventBus eventBus;

    public RegistryManager(@NotNull IEventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    public void setupRegistries() {
        ModAttributes.register(eventBus);
        ModBiomes.register(eventBus);
        ModBlocks.register(eventBus);
        ModMenus.register(eventBus);
        ModEffects.register(eventBus);
        ModEnchantments.register(eventBus);
        ModEntities.register(eventBus);
        ModFeatures.register(eventBus);
        ModStructures.register(eventBus);
        ModFluids.register(eventBus);
        ModItems.register(eventBus);
        ModLoot.register(eventBus);
        ModParticles.register(eventBus);
        ModPotions.register(eventBus);
        ModRecipes.register(eventBus);
        ModRefinements.register(eventBus);
        ModRefinementSets.register(eventBus);
        ModSounds.register(eventBus);
        ModTasks.register(eventBus);
        ModTiles.register(eventBus);
        ModAi.register(eventBus);
        ModVillage.register(eventBus);
        VampireActions.register(eventBus);
        HunterActions.register(eventBus);
        EntityActions.register(eventBus);
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
        ModSkills.init();
        ModDataComponents.register(eventBus);
        ModArmorMaterials.register(eventBus);
    }

    @Override
    public void onInitStep(@NotNull Step step, @NotNull ParallelDispatchEvent event) {
        switch (step) {
            case COMMON_SETUP:
                event.enqueueWork(ModVillage::villagerTradeSetup);
                event.enqueueWork(ModTiles::registerTileExtensionsUnsafe);
                event.enqueueWork(ModItems::registerDispenserBehaviourUnsafe);
                ModRecipes.Categories.init();
            default:
                break;
        }
    }

    @SubscribeEvent
    public void onNewDatapackRegistries(@NotNull DataPackRegistryEvent.NewRegistry event) {
        ModRegistries.registerDatapackRegistries(event);
    }

    @SubscribeEvent
    public void onRegisterDataMapTypes(RegisterDataMapTypesEvent event) {
        ModDataMaps.registerDataMaps(event);
    }

    public void registerModEventHandler() {
        this.eventBus.addListener(ModEntities::onModifyEntityTypeAttributes);
        this.eventBus.addListener(ModEntities::onRegisterEntityTypeAttributes);
        this.eventBus.addListener(ModEntities::onRegisterSpawns);
        this.eventBus.addListener(ModItems::registerOtherCreativeTabItems);
        this.eventBus.addListener(ModRegistries::registerRegistries);
        this.eventBus.addListener(PackRepositories::registerPackRepository);
        this.eventBus.addListener(ModRecipes::registerCategories);
    }

    public void registerForgeEventHandler() {
        IEventBus eventBus = NeoForge.EVENT_BUS;
        eventBus.addListener(ModItems::registerCraftingRecipes);
        eventBus.addListener(ModCommands::registerCommands);
        eventBus.addListener(ModLootTables::onLootLoad);
        eventBus.addListener(ModPotions::registerPotionMixes);
    }
}
