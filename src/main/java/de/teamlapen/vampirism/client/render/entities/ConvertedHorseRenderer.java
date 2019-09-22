package de.teamlapen.vampirism.client.render.entities;

import com.google.common.collect.Maps;
import de.teamlapen.vampirism.client.render.LayerVampireEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.LeatherHorseArmorLayer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;


@OnlyIn(Dist.CLIENT)
public final class ConvertedHorseRenderer extends AbstractHorseRenderer<HorseEntity, HorseModel<HorseEntity>> {
    private static final Map<String, ResourceLocation> LAYERED_LOCATION_CACHE = Maps.newHashMap();
    private final ResourceLocation overlay = new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/horse_overlay.png");

    public ConvertedHorseRenderer(EntityRendererManager p_i47205_1_) {
        super(p_i47205_1_, new HorseModel<>(0.0F), 1.1F);
        this.addLayer(new LeatherHorseArmorLayer(this));
        this.addLayer(new LayerVampireEntity<>(this, overlay, false));

    }

    @Override
    protected ResourceLocation getEntityTexture(HorseEntity p_110775_1_) {
        String lvt_2_1_ = p_110775_1_.getHorseTexture();
        ResourceLocation lvt_3_1_ = LAYERED_LOCATION_CACHE.get(lvt_2_1_);
        if (lvt_3_1_ == null) {
            lvt_3_1_ = new ResourceLocation(lvt_2_1_);
            Minecraft.getInstance().getTextureManager().loadTexture(lvt_3_1_, new LayeredTexture(p_110775_1_.getVariantTexturePaths()));
            LAYERED_LOCATION_CACHE.put(lvt_2_1_, lvt_3_1_);
        }

        return lvt_3_1_;
    }
}
