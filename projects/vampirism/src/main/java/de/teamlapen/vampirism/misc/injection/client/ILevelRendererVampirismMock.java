package de.teamlapen.vampirism.misc.injection.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.misc.extension.client.ILevelRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.state.LevelRenderState;

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
