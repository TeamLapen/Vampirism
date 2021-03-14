package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.client.render.layers.HunterEquipmentLayer;
import de.teamlapen.vampirism.client.render.layers.PlayerBodyOverlayLayer;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.IntFunction;

/**
 * There are differently looking level 0 hunters.
 * Hunter as of level 1 look all the same, but have different weapons
 */
@OnlyIn(Dist.CLIENT)
public class HunterMinionRenderer extends DualBipedRenderer<HunterMinionEntity, PlayerModel<HunterMinionEntity>> {
    private final Pair<ResourceLocation, Boolean>[] textures;

    public HunterMinionRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PlayerModel<>(0.5f, false), new PlayerModel<>(0.5f, true), 0.5F);
        IResourceManager rm = Minecraft.getInstance().getResourceManager();
        Collection<ResourceLocation> allTexs = new ArrayList<>(rm.getAllResourceLocations("textures/entity/hunter", s -> s.endsWith(".png")));
        allTexs.addAll(rm.getAllResourceLocations("textures/entity/minion/hunter", s -> s.endsWith(".png")));
        textures = allTexs.stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())).map(r -> {
            boolean b = r.getPath().endsWith("slim.png");
            return Pair.of(r, b);
        }).toArray((IntFunction<Pair<ResourceLocation, Boolean>[]>) Pair[]::new);
        this.addLayer(new PlayerBodyOverlayLayer<>(this));
        this.addLayer(new HunterEquipmentLayer<>(this, hunterMinionEntity -> hunterMinionEntity.getItemStackFromSlot(EquipmentSlotType.MAINHAND).isEmpty() ? HunterEquipmentModel.StakeType.FULL : HunterEquipmentModel.StakeType.NONE, HunterMinionEntity::getHatType));
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5f), new BipedModel<>(1f)));
    }

    @Override
    protected Pair<ResourceLocation, Boolean> determineTextureAndModel(HunterMinionEntity entity) {
        return textures[entity.getHunterType() % textures.length];
    }

    @Override
    protected void renderSelected(HunterMinionEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        if (entityIn.isSwingingArms()) {
            this.entityModel.rightArmPose = BipedModel.ArmPose.CROSSBOW_HOLD;
        } else {
            this.entityModel.rightArmPose = BipedModel.ArmPose.ITEM;
        }
        super.renderSelected(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    public int getTextureLength() {
        return this.textures.length;
    }

    @Override
    protected void preRenderCallback(HunterMinionEntity entityIn, MatrixStack matrixStackIn, float partialTickTime) {
        float s = entityIn.getScale();
        //float off = (1 - s) * 1.95f;
        matrixStackIn.scale(s, s, s);
        //matrixStackIn.translate(0,off,0f);
    }

}