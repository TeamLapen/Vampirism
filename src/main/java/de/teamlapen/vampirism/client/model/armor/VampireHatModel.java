package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class VampireHatModel extends VampirismArmorModel {

    private static final String BASE = "base";
    private static final String TOP = "top";

    private static VampireHatModel instance;

    public static VampireHatModel getAdjustedInstance(HumanoidModel<?> wearerModel) {
        if (instance == null) {
            instance = new VampireHatModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModEntitiesRender.CLOTHING_HAT));
        }
        instance.copyFromHumanoid(wearerModel);
        return instance;
    }

    public final @NotNull ModelPart base;
    public final @NotNull ModelPart top;

    public static @NotNull LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition part = mesh.getRoot();
        CubeDeformation def = new CubeDeformation(0.25f);
        PartDefinition base = part.addOrReplaceChild(BASE, CubeListBuilder.create().texOffs(16, 0).addBox(-4.5f, -8.4f, -3.5f, 7, 0.4f, 7f, def), PartPose.ZERO);
        base.addOrReplaceChild(TOP, CubeListBuilder.create().addBox(-0.6f, -14.5f, -3, 4, 7, 4, def), PartPose.rotation(-0.22217304763960307F, 0.0F, -0.27750734440919567F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    public VampireHatModel(@NotNull ModelPart part) {
        super();
        base = part.getChild(BASE);
        top = base.getChild(TOP);
    }


    @Override
    protected @NotNull Iterable<ModelPart> getHeadModels() {
        return ImmutableList.of(this.base);
    }
}
