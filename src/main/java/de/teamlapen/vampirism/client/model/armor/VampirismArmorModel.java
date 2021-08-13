package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.Iterables;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;

@OnlyIn(Dist.CLIENT)
public class VampirismArmorModel extends HumanoidModel<LivingEntity> {
    protected VampirismArmorModel(ModelPart part) {
        super(part);
    }

    public static MeshDefinition createMesh() {
        return HumanoidModel.createMesh(CubeDeformation.NONE, 0);
    }

    protected Iterable<ModelPart> getBodyModels() {
        return Collections.emptyList();
    }

    @Override
    protected final Iterable<ModelPart> bodyParts() {
        Iterable<ModelPart> b = getBodyModels();
        b.forEach(p -> p.copyFrom(this.body));
        Iterable<ModelPart> ll = getLeftLegModels();
        ll.forEach(p -> p.copyFrom(this.leftLeg));
        Iterable<ModelPart> rl = getRightLegModels();
        rl.forEach(p -> p.copyFrom(this.rightLeg));
        return Iterables.concat(b, ll, rl);
    }

    protected Iterable<ModelPart> getHeadModels() {
        return Collections.emptyList();
    }

    @Override
    protected final Iterable<ModelPart> headParts() {
        Iterable<ModelPart> l = getHeadModels();
        l.forEach(p -> p.copyFrom(this.head));
        return l;
    }

    protected Iterable<ModelPart> getLeftLegModels() {
        return Collections.emptyList();
    }

    protected Iterable<ModelPart> getRightLegModels() {
        return Collections.emptyList();
    }


}
