package de.teamlapen.vampirism.modcompat.guide;

import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.entry.EntryResourceLocation;
import amerifrance.guideapi.gui.BaseScreen;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

/**
 * Simple bullet point text entry
 */
public class EntryText extends EntryResourceLocation {
    public EntryText(List<IPage> pageList, String unlocEntryName) {
        super(pageList, unlocEntryName, new ResourceLocation(REFERENCE.MODID, "textures/item/vampire_fang.png"));
    }

    @OnlyIn(Dist.CLIENT)

    @Override
    public void drawExtras(Book book, CategoryAbstract category, int entryX, int entryY, int entryWidth, int entryHeight, int mouseX, int mouseY, BaseScreen guiBase, FontRenderer fontRendererObj) {
        super.drawExtras(book, category, entryX, entryY, entryWidth, entryHeight, mouseX, mouseY, guiBase, fontRendererObj);
    }
}
