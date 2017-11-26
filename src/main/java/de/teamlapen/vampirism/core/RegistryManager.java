package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.SkillEvent;
import de.teamlapen.vampirism.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.player.vampire.skills.VampireSkills;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.ObjectHolderRegistry;


/**
 * Handles registrations of all registrable things as well as a few additional dependent things
 */
public class RegistryManager implements IInitListener {


    /**
     * Delegate for some client side registrations
     */
    @SideOnly(Side.CLIENT)
    private static de.teamlapen.vampirism.client.core.RegistryManagerClient registryManagerClient;

    @SideOnly(Side.CLIENT)
    public static void setupClientRegistryManager() {
        registryManagerClient = new de.teamlapen.vampirism.client.core.RegistryManagerClient();
        MinecraftForge.EVENT_BUS.register(registryManagerClient);
    }

    @SideOnly(Side.CLIENT)
    public static de.teamlapen.vampirism.client.core.RegistryManagerClient getRegistryManagerClient() {
        return registryManagerClient;
    }

    @SubscribeEvent
    public void onBuildRegistries(RegistryEvent.NewRegistry event) {
        VampirismRegistries.init();
    }

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        switch (step) {
            case INIT:
                ModBlocks.registerCraftingRecipes();
                ModItems.registerCraftingRecipes();
                ModVillages.init();
                ModAdvancements.registerAdvancements();
                ModParticles.init();
                break;
            case PRE_INIT:
                ModFluids.registerFluids();
                ModEntities.registerConvertibles();
                ModEntities.registerCustomExtendedCreatures();
                break;
            case POST_INIT:
                ModPotions.checkNightVision();
            default:
                break;
        }
    }

    @SubscribeEvent
    public void onMissinMappingsPotion(RegistryEvent.MissingMappings<SoundEvent> event) {
        for (RegistryEvent.MissingMappings.Mapping<SoundEvent> m : event.getMappings()) {
            m.ignore();
        }
    }

    @SubscribeEvent
    public void onMissingMappingsBlock(RegistryEvent.MissingMappings<Block> event) {
        for (RegistryEvent.MissingMappings.Mapping<Block> m : event.getMappings()) {
            ModBlocks.fixMapping(m);
        }
    }

    @SubscribeEvent
    public void onMissingMappingsItem(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> m : event.getMappings()) {
            if (!ModItems.fixMapping(m)) {
                ModBlocks.fixMappingItemBlock(m);
            }
        }
    }

    @SubscribeEvent
    public void onMissingMappingsPotion(RegistryEvent.MissingMappings<Potion> event) {
        for (RegistryEvent.MissingMappings.Mapping<Potion> m : event.getMappings()) {
            ModPotions.fixMapping(m);
        }
    }

    @SubscribeEvent
    public void onRegisterActions(RegistryEvent.Register<IAction> event) {
        VampirismMod.log.t("Registering Actions");
        VampireActions.registerDefaultActions(event.getRegistry());
        HunterActions.registerDefaultActions(event.getRegistry());
        ObjectHolderRegistry.INSTANCE.applyObjectHolders();
    }

    @SubscribeEvent
    public void onRegisterBiomes(RegistryEvent.Register<Biome> event) {
        ModBiomes.registerBiomes(event.getRegistry());
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
    public void onRegisterEntities(RegistryEvent.Register<EntityEntry> event) {
        ModEntities.registerEntities(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
        ModItems.registerItems(event.getRegistry());
        ModBlocks.registerItemBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterPotions(RegistryEvent.Register<Potion> event) {
        ModPotions.registerPotions(event.getRegistry());
    }

    @SubscribeEvent
    public void onRegisterSkills(RegistryEvent.Register<ISkill> event) {
        VampirismMod.log.t("Registering Skills");
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
