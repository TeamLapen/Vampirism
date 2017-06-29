package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
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

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        switch (step) {
            case INIT:
                ModBlocks.registerCraftingRecipes();
                ModItems.registerCraftingRecipes();
                ModVillages.init();
                Achievements.registerAchievement();
                ModParticles.init();
                break;
            case PRE_INIT:
                ModFluids.registerFluids();
                ModEntities.registerEntities();
                ModEntities.registerSpawns();
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
        //ModEntities.registerEntities(); moved to pre-init again due to Forge complaining TODO
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
    public void onRegisterSounds(RegistryEvent.Register<SoundEvent> event) {
        ModSounds.registerSounds(event.getRegistry());
    }

}
