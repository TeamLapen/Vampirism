package de.teamlapen.vampirism.client.renderer.entity.layers;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.model.HunterEquipmentModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Render weapons for hunter entities
 */
@OnlyIn(Dist.CLIENT)
public class HunterEquipmentLayer<T extends Mob, Q extends EntityModel<T>> extends RenderLayer<T, Q> {

    private final @NotNull HunterEquipmentModel<T> equipmentModel;
    private final ResourceLocation textureExtra = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_extra.png");
    private final Function<T, HunterEquipmentModel.StakeType> predicateStake;
    private final Function<T, HunterEquipmentModel.HatType> functionHat;
    private final @NotNull Supplier<Optional<ModelPart>> hatPart;

    /**
     * @param predicateStake entity -> Type of equipment that should be rendered
     */
    public HunterEquipmentLayer(@NotNull RenderLayerParent<T, Q> entityRendererIn, @NotNull EntityModelSet modelSet, Function<T, HunterEquipmentModel.StakeType> predicateStake, Function<T, HunterEquipmentModel.HatType> functionHat, @NotNull Supplier<@NotNull Optional<ModelPart>> hatPart) {
        super(entityRendererIn);
        this.equipmentModel = new HunterEquipmentModel<>(modelSet.bakeLayer(ModEntitiesRender.HUNTER_EQUIPMENT));
        this.predicateStake = predicateStake;
        this.functionHat = functionHat;
        this.hatPart = hatPart;
    }

    public HunterEquipmentLayer(@NotNull RenderLayerParent<T, Q> entityRendererIn, @NotNull EntityModelSet modelSet, Function<T, HunterEquipmentModel.StakeType> predicateStake, Function<T, HunterEquipmentModel.HatType> functionHat, @Nullable ModelPart hatPart) {
        this(entityRendererIn, modelSet, predicateStake, functionHat, Suppliers.memoize(() -> Optional.ofNullable(hatPart)));
    }

    @Override
    public void render(@NotNull PoseStack matrixStackIn, @NotNull MultiBufferSource bufferIn, int packedLightIn, @NotNull T entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entityIn.isInvisible()) {
            equipmentModel.setHat(this.hatPart.get().map(a -> a.visible).orElse(true) ? functionHat.apply(entityIn) : HunterEquipmentModel.HatType.NONE);
            equipmentModel.setWeapons(predicateStake.apply(entityIn));

            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.equipmentModel, textureExtra, matrixStackIn, bufferIn, packedLightIn, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1, 1, 1);
        }
    }
}
