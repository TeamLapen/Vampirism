package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.ILevelRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;

@Deprecated
public interface ILevelRendererVampirismMock extends ILevelRenderer {

    @Override
    default SubmitNodeStorage vampirism$submitNodeStorage() {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default EntityRenderDispatcher vampirism$entityRenderDispatcher() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
