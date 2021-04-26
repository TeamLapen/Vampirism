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

    @Override
    protected final Iterable<ModelRenderer> getHeadParts() {
        Iterable<ModelRenderer> l =  getHeadModels();
        l.forEach(p->p.copyModelAngles(this.bipedHead));
        return l;    }

    @Override
    protected final Iterable<ModelRenderer> getBodyParts() {
        Iterable<ModelRenderer> b =  getBodyModels();
        b.forEach(p->p.copyModelAngles(this.bipedBody));
        Iterable<ModelRenderer> ll =  getLeftLegModels();
        ll.forEach(p->p.copyModelAngles(this.bipedLeftLeg));
        Iterable<ModelRenderer> rl =  getRightLegModels();
        rl.forEach(p->p.copyModelAngles(this.bipedRightLeg));
        return Iterables.concat(b,ll);
    }

    protected Iterable<ModelRenderer> getHeadModels(){
        return Collections.emptyList();
    }

    protected Iterable<ModelRenderer> getBodyModels(){
        return Collections.emptyList();
    }

    protected Iterable<ModelRenderer> getLeftLegModels(){
        return Collections.emptyList();
    }

    protected Iterable<ModelRenderer> getRightLegModels(){
        return Collections.emptyList();
    }




}
