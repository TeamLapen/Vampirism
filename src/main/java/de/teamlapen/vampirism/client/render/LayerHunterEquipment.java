package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.client.model.HunterEquipmentModel;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;


public class LayerHunterEquipment<T extends MobEntity, Q extends BipedModel<T>> extends LayerRenderer<T, Q> {
    private final HunterEquipmentModel<T> equipmentModel;
    private final ResourceLocation textureExtra = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_extra.png");
    private final Function<T, HunterEquipmentModel.StakeType> predicateStake;
    private final Function<T, Integer> functionHat;


    /**
     * @param predicateStake True if axe should not be rendered
     * @param functionHat    Entity -> -2 to 4
     */
    public LayerHunterEquipment(IEntityRenderer<T, Q> entityRendererIn, Function<T, HunterEquipmentModel.StakeType> predicateStake, Function<T, Integer> functionHat) {
        this(entityRendererIn, new HunterEquipmentModel<>(), predicateStake, functionHat);
    }

    public LayerHunterEquipment(IEntityRenderer<T, Q> entityRendererIn, HunterEquipmentModel<T> equipmentModel, Function<T, HunterEquipmentModel.StakeType> predicateStake, Function<T, Integer> functionHat) {
        super(entityRendererIn);
        this.predicateStake = predicateStake;
        this.functionHat = functionHat;
        this.equipmentModel = equipmentModel;
    }

    @Override
    public void render(T t, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
        if (!t.isInvisible()) {
            bindTexture(textureExtra);
            equipmentModel.setHat(functionHat.apply(t));
            equipmentModel.setWeapons(predicateStake.apply(t));
            this.getEntityModel().setModelAttributes(equipmentModel);
            this.equipmentModel.setLivingAnimations(t, p_212842_2_, p_212842_3_, p_212842_4_);
            this.equipmentModel.render(t, p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
