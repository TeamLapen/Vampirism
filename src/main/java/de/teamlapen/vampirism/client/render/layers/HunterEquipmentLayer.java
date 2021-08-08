package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.render.entities.HunterEquipmentModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.Mob;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

/**
 * Render weapons for hunter entities
 */
@OnlyIn(Dist.CLIENT)
public class HunterEquipmentLayer<T extends Mob, Q extends EntityModel<T>> extends RenderLayer<T, Q> {

    private final HunterEquipmentModel<T> equipmentModel = new HunterEquipmentModel<>();
    private final ResourceLocation textureExtra = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_extra.png");
    private final Function<T, HunterEquipmentModel.StakeType> predicateStake;
    private final Function<T, Integer> functionHat;

    /**
     * @param predicateStake entity -> Type of equipment that should be rendered
     * @param functionHat    entity -> -2 to 4
     */
    public HunterEquipmentLayer(RenderLayerParent<T, Q> entityRendererIn, Function<T, HunterEquipmentModel.StakeType> predicateStake, Function<T, Integer> functionHat) {
        super(entityRendererIn);
        this.predicateStake = predicateStake;
        this.functionHat = functionHat;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entityIn.isInvisible()) {
            equipmentModel.setHat(functionHat.apply(entityIn));
            equipmentModel.setWeapons(predicateStake.apply(entityIn));

            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.equipmentModel, textureExtra, matrixStackIn, bufferIn, packedLightIn, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1, 1, 1);
        }
    }
}
