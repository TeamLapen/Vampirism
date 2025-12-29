package de.teamlapen.vampirism.common.integration.guide;

import de.maxanier.guideapi.api.IPage;
import de.maxanier.guideapi.entry.EntryResourceLocation;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Simple bullet point text entry
 */
public class EntryText extends EntryResourceLocation {
    public EntryText(List<IPage> pageList, Component name) {
        super(pageList, name, VResourceLocation.mod("textures/item/vampire_fang.png"));
    }

}
