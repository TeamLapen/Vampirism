package de.teamlapen.vampirism.client.renderer.bloodvision.entries;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.ARGB;

public record BloodEntityEntry(EntityRenderState renderState, float bloodPercentage) implements IEntityEntry {
    @Override
    public int color() {
        return ARGB.color((int) (155 * bloodPercentage) + 50, (int) (100 * (1-bloodPercentage)), (int) (100 * (1-bloodPercentage)));
    }
}
