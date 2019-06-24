package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.SkillEvent;
import de.teamlapen.vampirism.entity.action.EntityActions;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.player.vampire.skills.VampireSkills;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;
import net.minecraftforge.registries.ObjectHolderRegistry;


/**
 * Handles registrations of all registrable things as well as a few additional
 * dependent things
 */
public class RegistryManager implements IInitListener {//TODO Mod Loading process @maxanier

    /**
     * Delegate for some client side registrations
     */
    @OnlyIn(Dist.CLIENT)
    private static de.teamlapen.vampirism.client.core.RegistryManagerClient registryManagerClient;

    @OnlyIn(Dist.CLIENT)
    public static void setupClientRegistryManager() {

        registryManagerClient = new de.teamlapen.vampirism.client.core.RegistryManagerClient();
        MinecraftForge.EVENT_BUS.register(registryManagerClient);
    }

    @OnlyIn(Dist.CLIENT)
    public static de.teamlapen.vampirism.client.core.RegistryManagerClient getRegistryManagerClient() {

        return registryManagerClient;
    }

    @SubscribeEvent
    public void onBuildRegistries(RegistryEvent.NewRegistry event) {

        VampirismRegistries.init();
    }

    @Override
    public void onInitStep(Step step, ModLifecycleEvent event) {

        switch (step) {
            case COMMON_SETUP:
                ModFluids.registerFluids();
                ModEntities.registerConvertibles();
                ModEntities.registerCustomExtendedCreatures();
                ModItems.registerCraftingRecipes();
                ModItems.registerBloodConversionRates();
                ModVillages.init();
                ModAdvancements.registerAdvancements();
                ModParticles.init();

                break;
            case LOAD_COMPLETE:
                if (ModPotions.checkNightVision()) {
                    ModPotions.fixNightVisionPotionTypes();
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
    public void onRegisterEntityActions(RegistryEvent.Register<IEntityAction> event) {
        EntityActions.registerDefaultActions(event.getRegistry());
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
    public void onRegisterEnchantments(RegistryEvent.Register<Enchantment> event) {

        ModEnchantments.registerEnchantments(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterEntities(RegistryEvent.Register<EntityType<?>> event) {

        ModEntities.registerEntities(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {

        ModItems.registerItems(event.getRegistry());
        ModBlocks.registerItemBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        ModTiles.registerTiles(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterPotions(RegistryEvent.Register<Potion> event) {

        ModPotions.registerPotions(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterProfessions(RegistryEvent.Register<VillagerRegistry.VillagerProfession> event) {
        ModVillages.registerProfessions(event.getRegistry());
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
    public void onSkillNodeCreated(SkillEvent.CreatedNode event) {

        if (event.getNode().isRoot()) {
            if (event.getNode().getFaction().equals(VReference.HUNTER_FACTION)) {
                HunterSkills.buildSkillTree(event.getNode());
            } else if (event.getNode().getFaction().equals(VReference.VAMPIRE_FACTION)) {
                VampireSkills.buildSkillTree(event.getNode());
            }
        }
    }
}
