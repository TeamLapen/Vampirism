package de.teamlapen.vampirism.client.renderer.bloodvision.entries;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

public interface IEntityEntry {

    EntityRenderState renderState();

    int color();
}
