package de.teamlapen.vampirism.modcompat.guide;

import de.maxanier.guideapi.api.IPage;
import de.maxanier.guideapi.entry.EntryResourceLocation;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * Simple bullet point text entry
 */
public class EntryText extends EntryResourceLocation {
    public EntryText(List<IPage> pageList, String unlocEntryName) {
        super(pageList, unlocEntryName, new ResourceLocation(REFERENCE.MODID, "textures/item/vampire_fang.png"));
    }

}
