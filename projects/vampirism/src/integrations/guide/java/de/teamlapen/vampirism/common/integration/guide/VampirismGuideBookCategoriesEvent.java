package de.teamlapen.vampirism.common.integration.guide;

import de.maxanier.guideapi.api.category.CategoryBase;
import net.neoforged.bus.api.Event;

import java.util.List;

public class VampirismGuideBookCategoriesEvent extends Event {

    public final List<CategoryBase> categories;

    public VampirismGuideBookCategoriesEvent(List<CategoryBase> categories) {
        this.categories = categories;
    }
}
