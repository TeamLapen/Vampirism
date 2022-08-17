package de.teamlapen.vampirism.client.render.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.HunterEquipmentModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Render weapons for hunter entities
 */
@OnlyIn(Dist.CLIENT)
public class HunterEquipmentLayer<T extends Mob, Q extends EntityModel<T>> extends RenderLayer<T, Q> {

    private final @NotNull HunterEquipmentModel<T> equipmentModel;
    private final ResourceLocation textureExtra = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_extra.png");
    private final Function<T, HunterEquipmentModel.StakeType> predicateStake;
    private final Function<T, HunterEquipmentModel.HatType> functionHat;

    /**
     * @param predicateStake entity -> Type of equipment that should be rendered
     */
    public HunterEquipmentLayer(@NotNull RenderLayerParent<T, Q> entityRendererIn, @NotNull EntityModelSet modelSet, Function<T, HunterEquipmentModel.StakeType> predicateStake, Function<T, HunterEquipmentModel.HatType> functionHat) {
        super(entityRendererIn);
        equipmentModel = new HunterEquipmentModel<>(modelSet.bakeLayer(ModEntitiesRender.HUNTER_EQUIPMENT));
        this.predicateStake = predicateStake;
        this.functionHat = functionHat;
    }

    @Override
    public void render(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, @NotNull T entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entityIn.isInvisible()) {
            equipmentModel.setHat(functionHat.apply(entityIn));
            equipmentModel.setWeapons(predicateStake.apply(entityIn));

            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.equipmentModel, textureExtra, matrixStackIn, bufferIn, packedLightIn, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1, 1, 1);
        }
    }
}
