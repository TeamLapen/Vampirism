package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.model.BasicHunterModel;
import de.teamlapen.vampirism.client.render.entities.HunterEquipmentModel;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Render weapons for hunter entities
 */
@OnlyIn(Dist.CLIENT)
public class HunterEquipmentLayer<T extends MobEntity, Q extends BasicHunterModel<T>> extends LayerRenderer<T, Q> {

    private final HunterEquipmentModel<T> equipmentModel = new HunterEquipmentModel<>();
    private final ResourceLocation textureExtra = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_extra.png");
    private final Predicate<T> predicateOnlyStake;
    private final Function<T, Integer> functionHat;

    public HunterEquipmentLayer(IEntityRenderer<T, Q> entityRendererIn, Predicate<T> predicateOnlyStake, Function<T, Integer> functionHat) {
        super(entityRendererIn);
        this.predicateOnlyStake = predicateOnlyStake;
        this.functionHat = functionHat;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entityIn.isInvisible()) {
            equipmentModel.setHat(functionHat.apply(entityIn));
            equipmentModel.setWeapons(predicateOnlyStake.test(entityIn));

            renderCopyCutoutModel(this.getEntityModel(), this.equipmentModel, textureExtra, matrixStackIn, bufferIn, packedLightIn, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1, 1, 1);
        }
    }
}
