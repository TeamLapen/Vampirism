package de.teamlapen.vampirism.modcompat.guide;

import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import net.neoforged.bus.api.Event;

import java.util.List;

public class VampirismGuideBookCategoriesEvent extends Event {

    public final List<CategoryAbstract> categories;

    public VampirismGuideBookCategoriesEvent(List<CategoryAbstract> categories) {
        this.categories = categories;
    }
}
