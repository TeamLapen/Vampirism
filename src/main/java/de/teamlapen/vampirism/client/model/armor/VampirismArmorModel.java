package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.Iterables;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;

@OnlyIn(Dist.CLIENT)
public class VampirismArmorModel extends BipedModel<LivingEntity> {
    protected VampirismArmorModel(int textureWidthIn, int textureHeightIn) {
        super(0, 0, textureWidthIn, textureHeightIn);
    }

    protected Iterable<ModelRenderer> getBodyModels() {
        return Collections.emptyList();
    }

    @Override
    protected final Iterable<ModelRenderer> bodyParts() {
        Iterable<ModelRenderer> b = getBodyModels();
        b.forEach(p -> p.copyFrom(this.body));
        Iterable<ModelRenderer> ll = getLeftLegModels();
        ll.forEach(p -> p.copyFrom(this.leftLeg));
        Iterable<ModelRenderer> rl = getRightLegModels();
        rl.forEach(p -> p.copyFrom(this.rightLeg));
        return Iterables.concat(b, ll, rl);
    }

    protected Iterable<ModelRenderer> getHeadModels() {
        return Collections.emptyList();
    }

    @Override
    protected final Iterable<ModelRenderer> headParts() {
        Iterable<ModelRenderer> l = getHeadModels();
        l.forEach(p -> p.copyFrom(this.head));
        return l;
    }

    protected Iterable<ModelRenderer> getLeftLegModels() {
        return Collections.emptyList();
    }

    protected Iterable<ModelRenderer> getRightLegModels() {
        return Collections.emptyList();
    }


}
