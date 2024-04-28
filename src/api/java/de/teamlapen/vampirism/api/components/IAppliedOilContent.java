package de.teamlapen.vampirism.api.components;

import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import net.minecraft.core.Holder;

public interface IAppliedOilContent {

    Holder<IApplicableOil> oil();

    int duration();
}
