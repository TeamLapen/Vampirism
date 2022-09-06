package de.teamlapen.vampirism.client.gui.screens.actions;

import com.mojang.blaze3d.vertex.PoseStack;

public interface DrawCallback<T> {
    void accept(T objectToBeDrawn, PoseStack poseStack, int positionX, int positionY, int size, boolean renderTransparent);
}
