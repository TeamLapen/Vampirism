package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.entity.action.EntityActions;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.player.vampire.skills.VampireSkills;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.stats.StatType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;
import net.minecraftforge.registries.ObjectHolderRegistry;


/**
 * Handles registrations of all registrable things as well as a few additional
 * dependent things
 */
public class RegistryManager implements IInitListener {


    @SubscribeEvent
    public void onBuildRegistries(RegistryEvent.NewRegistry event) {

        ModRegistries.init();
    }

    @Override
    public void onInitStep(Step step, ModLifecycleEvent event) {

        switch (step) {
            case COMMON_SETUP:
                ModBiomes.addBiome();
                ModEntities.registerConvertibles();
                ModEntities.registerSpawns();
                ModEntities.registerCustomExtendedCreatures();
                ModItems.registerCraftingRecipes();
                ModAdvancements.registerAdvancements();
                break;
            case LOAD_COMPLETE:
                if (ModEffects.checkNightVision()) {
                    ModEffects.fixNightVisionEffecTypes();
                }
                break;
            default:
                break;
        }
    }


    @SubscribeEvent
    public void onRegisterActions(RegistryEvent.Register<IAction> event) {

        VampireActions.registerDefaultActions(event.getRegistry());
        HunterActions.registerDefaultActions(event.getRegistry());
        ObjectHolderRegistry.applyObjectHolders();
    }

    @SubscribeEvent
    public void onRegisterBiomes(RegistryEvent.Register<Biome> event) {

        ModBiomes.registerBiomes(event.getRegistry());
        ModBiomes.registerFeatures();
    }

    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent.Register<Block> event) {

        ModBlocks.registerBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterContainer(RegistryEvent.Register<ContainerType<?>> event) {
        ModContainer.registerContainer(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterEnchantments(RegistryEvent.Register<Enchantment> event) {

        ModEnchantments.registerEnchantments(event.getRegistry());
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
        ModWorldFeatures.registerFeatures(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterFluids(RegistryEvent.Register<Fluid> event) {
        ModFluids.registerFluids(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {

        ModItems.registerItems(event.getRegistry());
        ModBlocks.registerItemBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterParticles(RegistryEvent.Register<ParticleType<?>> event) {
        ModParticles.registerParticles(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterPotions(RegistryEvent.Register<Effect> event) {

        ModEffects.registerEffects(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterProfessions(RegistryEvent.Register<VillagerProfession> event) {
        ModEntities.registerProfessions(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterRecipeSerializer(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        ModRecipes.registerSerializer(event.getRegistry());
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
    public void onRegisterTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        ModTiles.registerTiles(event.getRegistry());
    }
}
