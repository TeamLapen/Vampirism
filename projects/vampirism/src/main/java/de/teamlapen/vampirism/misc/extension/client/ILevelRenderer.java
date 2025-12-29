package de.teamlapen.vampirism.misc.extension.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.state.LevelRenderState;

public interface ILevelRenderer {

    SubmitNodeStorage vampirism$submitNodeStorage();

    EntityRenderDispatcher vampirism$entityRenderDispatcher();
}
