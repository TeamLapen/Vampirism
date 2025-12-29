package de.teamlapen.vampirism.client.renderer.bloodvision.entries;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.ARGB;

public record OtherEntityEntry(EntityRenderState renderState) implements IEntityEntry {
    @Override
    public int color() {
        return ARGB.color(255,125,125,125);
    }
}
