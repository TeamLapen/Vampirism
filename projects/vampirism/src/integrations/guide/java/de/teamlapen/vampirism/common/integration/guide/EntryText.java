package de.teamlapen.vampirism.common.integration.guide;

import de.maxanier.guideapi.api.entry.EntryItemStack;
import de.maxanier.guideapi.api.pages.IPage;
import de.teamlapen.vampirism.common.core.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Simple bullet point text entry
 */
public class EntryText extends EntryItemStack {
    public EntryText(List<IPage> pageList, Component name) {
        super(pageList, name, new ItemStack(ModItems.VAMPIRE_FANG.get()));
    }

}
