package de.teamlapen.vampirism.modcompat.guide;

import de.teamlapen.lib.lib.util.IModCompat;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;


public class GuideAPICompat implements IModCompat {
    @Override
    public String getModID() {
        return "guideapi";
    }

    @Override
    public void loadConfigs(Configuration config, ConfigCategory category) {

    }

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                GuideBook.initBook();
                break;
            case INIT:
                GameRegistry.addShapelessRecipe(GuideBook.bookStack.copy(), new ItemStack(Items.BOOK), new ItemStack(ModItems.vampireFang));
                GameRegistry.addShapelessRecipe(GuideBook.bookStack.copy(), new ItemStack(Items.BOOK), new ItemStack(ModItems.humanHeart));
                break;
            case POST_INIT:
                if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                    GuideBook.buildCategories();
                }
                break;
            default:
                break;

        }
    }
}
