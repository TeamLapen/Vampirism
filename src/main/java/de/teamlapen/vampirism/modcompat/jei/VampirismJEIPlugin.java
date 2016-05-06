package de.teamlapen.vampirism.modcompat.jei;

import de.teamlapen.vampirism.core.ModBlocks;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Plugin for Just Enough Items
 */
@JEIPlugin
public class VampirismJEIPlugin extends BlankModPlugin {

    @Override
    public void register(@Nonnull IModRegistry registry) {
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();

        jeiHelpers.getItemBlacklist().addItemToBlacklist(new ItemStack(ModBlocks.fluidBlood));

        //TODO add recipe handler for hunter table
    }
}
