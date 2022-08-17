package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.render.layers.VampireEntityLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;


@OnlyIn(Dist.CLIENT)
public class ConvertedVillagerRenderer extends VillagerRenderer {

    private static final ResourceLocation overlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/villager_overlay.png");

    public ConvertedVillagerRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context);
        this.addLayer(new VampireEntityLayer<>(this, overlay, false));
    }
}
