package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;


@OnlyIn(Dist.CLIENT)
public class HunterHatModel extends VampirismArmorModel {

    private static final String HAT_TOP = "hat_top";
    private static final String HAT_RIM = "hat_rim";

    public static @NotNull LayerDefinition createLayer(float p_170683_, int type) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        if (type == 1) {
            part.addOrReplaceChild(HAT_TOP, CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -14.0F, -4.0F, 8.0F, 5.0F, 8.0F), PartPose.offset(0, p_170683_, 0));
            part.addOrReplaceChild(HAT_RIM, CubeListBuilder.create().texOffs(0, 13).addBox(-6.0F, -9.0F, -6.0F, 12.0F, 1.0F, 12.0F), PartPose.offset(0, p_170683_, 0));
        } else {
            part.addOrReplaceChild(HAT_TOP, CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 3.0F, 8.0F), PartPose.offset(0.0F, p_170683_, 0.0F));
            part.addOrReplaceChild(HAT_RIM, CubeListBuilder.create().texOffs(0, 17).addBox(-8.0F, -9.0F, -8.0F, 16.0F, 1.0F, 16.0F), PartPose.offset(0.0F, p_170683_, 0.0F));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    private static HunterHatModel hat0;
    private static HunterHatModel hat1;
    private final @NotNull ModelPart hatTop;
    private final @NotNull ModelPart hatRim;

    public static HunterHatModel getAdjustedInstance0(HumanoidModel<?> wearerModel) {
        if (hat0 == null) {
            hat0 = new HunterHatModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEntitiesRender.HUNTER_HAT0));
        }
        hat0.copyFromHumanoid(wearerModel);
        return hat0;
    }

    public static HunterHatModel getAdjustedInstance1(HumanoidModel<?> wearerModel) {
        if (hat1 == null) {
            hat1 = new HunterHatModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEntitiesRender.HUNTER_HAT1));
        }
        hat1.copyFromHumanoid(wearerModel);
        return hat1;
    }


    public HunterHatModel(@NotNull ModelPart part) {
        super();
        this.hatTop = part.getChild(HAT_TOP);
        this.hatRim = part.getChild(HAT_RIM);
    }


    @Override
    protected @NotNull Iterable<ModelPart> getHeadModels() {
        return ImmutableList.of(hatTop, hatRim);
    }
}
