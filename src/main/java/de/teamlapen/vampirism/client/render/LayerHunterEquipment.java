package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.client.model.BasicHunterModel;
import de.teamlapen.vampirism.client.model.HunterEquipmentModel;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;
import java.util.function.Predicate;


public class LayerHunterEquipment<T extends MobEntity, Q extends BasicHunterModel<T>> extends LayerRenderer<T, Q> {
    private final HunterEquipmentModel<T> equipmentModel = new HunterEquipmentModel<>();
    private final ResourceLocation textureExtra = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_extra.png");
    private final Predicate<T> predicateOnlyStake;
    private final Function<T, Integer> functionHat;

    /**
     * @param predicateOnlyStake True if axe should not be rendered
     * @param functionHat        Entity -> -1 to 4
     */
    public LayerHunterEquipment(IEntityRenderer<T, Q> entityRendererIn, Predicate<T> predicateOnlyStake, Function<T, Integer> functionHat) {
        super(entityRendererIn);
        this.predicateOnlyStake = predicateOnlyStake;
        this.functionHat = functionHat;
    }

    @Override
    public void render(T t, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
        if (!t.isInvisible()) {
            bindTexture(textureExtra);
            equipmentModel.setHat(functionHat.apply(t));
            equipmentModel.setWeapons(predicateOnlyStake.test(t));
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
