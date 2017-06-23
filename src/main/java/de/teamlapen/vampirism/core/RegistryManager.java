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
            default:
                break;
        }
    }

    @SubscribeEvent
    public void onRegisterBiomes(RegistryEvent<Biome> event) {
        ModBiomes.registerBiomes();
    }

    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent<Block> event) {
        ModBlocks.registerBlocks();
    }

    @SubscribeEvent
    public void onRegisterEnchantments(RegistryEvent<Enchantment> event) {
        ModEnchantments.registerEnchantments();
    }

    @SubscribeEvent
    public void onRegisterEntities(RegistryEvent<EntityEntry> event) {
        //ModEntities.registerEntities(); moved to pre-init again due to Forge complaining
    }

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent<Item> event) {
        ModItems.registerItems();
    }

    @SubscribeEvent
    public void onRegisterPotions(RegistryEvent<Potion> event) {
        ModPotions.registerPotions();
    }

    @SubscribeEvent
    public void onRegisterSounds(RegistryEvent<SoundEvent> event) {
        ModSounds.registerSounds();
    }


}
