package de.teamlapen.vampirism.misc.extension.client;

import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;

public interface ILevelRenderer {

    SubmitNodeStorage vampirism$submitNodeStorage();

    EntityRenderDispatcher vampirism$entityRenderDispatcher();
}
