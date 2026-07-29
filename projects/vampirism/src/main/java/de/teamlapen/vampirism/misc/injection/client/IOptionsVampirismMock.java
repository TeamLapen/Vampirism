package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.IOptions;
import net.minecraft.client.OptionInstance;

@Deprecated
public interface IOptionsVampirismMock extends IOptions {

    @Override
    default OptionInstance<Boolean> vampirism$invertedSunBlindness() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
