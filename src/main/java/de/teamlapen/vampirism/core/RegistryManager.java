package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.entity.IVampirismBoat;
import de.teamlapen.vampirism.entity.action.EntityActions;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.entity.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.lord.actions.LordActions;
import de.teamlapen.vampirism.entity.player.lord.skills.LordSkills;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.misc.VampirismDispenseBoatBehavior;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Handles registrations of all registrable things as well as a few additional
 * dependent things
 */
@ApiStatus.Internal
public class RegistryManager implements IInitListener {

    public RegistryManager() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    public static void setupRegistries(@NotNull IEventBus modbus) {
        ModRegistries.init(modbus);
        ModAttributes.register(modbus);
        ModBiomes.register(modbus);
        ModBlocks.register(modbus);
        ModContainer.register(modbus);
        ModEffects.register(modbus);
        ModEnchantments.register(modbus);
        ModEntities.register(modbus);
        ModFeatures.register(modbus);
        ModFluids.register(modbus);
        ModItems.register(modbus);
        ModLoot.register(modbus);
        ModParticles.register(modbus);
        ModPotions.register(modbus);
        ModRecipes.register(modbus);
        ModRefinements.register(modbus);
        ModRefinementSets.register(modbus);
        ModSounds.register(modbus);
        ModTasks.register(modbus);
        ModTiles.register(modbus);
        ModAi.register(modbus);
        ModVillage.register(modbus);
        VampireActions.register(modbus);
        HunterActions.register(modbus);
        EntityActions.register(modbus);
        MinionTasks.register(modbus);
        LordActions.register(modbus);
        LordSkills.register(modbus);
        VampireSkills.register(modbus);
        HunterSkills.register(modbus);
        ModCommands.register(modbus);
        ModOils.register(modbus);

        VampirismFeatures.register(modbus);
    }

    @SubscribeEvent
    public void onBuildRegistries(NewRegistryEvent event) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEntities::onModifyEntityTypeAttributes);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEntities::onRegisterEntityTypeAttributes);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModEntities::onRegisterSpawns);
    }

    @Override
    public void onInitStep(@NotNull Step step, @NotNull ParallelDispatchEvent event) {
        switch (step) {
            case COMMON_SETUP:
                ModEntities.registerCustomExtendedCreatures();
                ModItems.registerCraftingRecipes();
                ModAdvancements.registerAdvancementTrigger();
                event.enqueueWork(() -> {
                    ModPotions.registerPotionMixes();
                    ModStats.registerCustomStats();
                    ModVillage.villagerTradeSetup();
                });
                ModTiles.registerTileExtensionsUnsafe();
                DispenserBlock.registerBehavior(ModItems.DARK_SPRUCE_BOAT.get(), new VampirismDispenseBoatBehavior(IVampirismBoat.BoatType.DARK_SPRUCE));
                DispenserBlock.registerBehavior(ModItems.CURSED_SPRUCE_BOAT.get(), new VampirismDispenseBoatBehavior(IVampirismBoat.BoatType.CURSED_SPRUCE));
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
    public void onNewDatapackRegistries(@NotNull DataPackRegistryEvent.NewRegistry event) {
        ModRegistries.registerDatapackRegistries(event);
    }

    @SubscribeEvent
    public void onRegisterEffects(@NotNull RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.MOB_EFFECTS)) {
            //noinspection ConstantConditions,unchecked
            ModEffects.replaceEffects((IForgeRegistry<MobEffect>) (Object) event.getForgeRegistry()); //TODO 1.19 check
        }
    }
}
