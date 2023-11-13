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
    private static final String HAT_BASE = "hat_top";

    public static @NotNull LayerDefinition createHat0Layer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        PartDefinition base = part.addOrReplaceChild(HAT_BASE, CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -9.0F, -6.0F, 12.0F, 1.0F, 12.0F), PartPose.ZERO);
        PartDefinition top = base.addOrReplaceChild(HAT_TOP, CubeListBuilder.create().texOffs(0, 13).addBox(-4.0F, -14.0F, -4.0F, 8.0F, 5.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static @NotNull LayerDefinition createHat1Layer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        PartDefinition base = part.addOrReplaceChild(HAT_BASE, CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -9.0F, -8.0F, 16.0F, 1.0F, 16.0F), PartPose.ZERO);
        PartDefinition top = base.addOrReplaceChild(HAT_TOP, CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, -12.0F, -4.0F, 8.0F, 3.0F, 8.0F), PartPose.ZERO);
        return LayerDefinition.create(mesh, 64, 64);
    }

    private static HunterHatModel hat0;
    private static HunterHatModel hat1;
    private final @NotNull ModelPart hatBase;

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
        this.hatBase = part.getChild(HAT_BASE);
    }


    @Override
    protected @NotNull Iterable<ModelPart> getHeadModels() {
        return ImmutableList.of(hatBase);
    }
}
