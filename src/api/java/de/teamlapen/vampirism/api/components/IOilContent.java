package de.teamlapen.vampirism.api.components;

import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.core.Holder;

public interface IOilContent {

    Holder<IOil> oil();

    IOilContent withOil(Holder<IOil> oil);
}
