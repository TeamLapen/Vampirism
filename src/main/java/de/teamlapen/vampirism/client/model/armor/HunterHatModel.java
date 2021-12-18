package de.teamlapen.vampirism.client.model.armor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class HunterHatModel extends VampirismArmorModel {
    public static final HunterHatModel hat0 = new HunterHatModel(0);
    public static final HunterHatModel hat1 = new HunterHatModel(1);
    private ModelRenderer hatTop;
    private ModelRenderer hatRim;


    public HunterHatModel(int type) {
        super(64, 64);
        if (type == 1) {
            hatTop = new ModelRenderer(this, 0, 31);
            hatTop.addBox(-4F, -14F, -4F, 8, 5, 8);
            hatTop.setPos(super.head.x, super.head.y, super.head.z);
            hatTop.setTexSize(128, 64);
            hatTop.mirror = true;

            hatRim = new ModelRenderer(this, 0, 35);
            hatRim.addBox(-6F, -9F, -6F, 12, 1, 12);
            hatRim.setPos(super.head.x, super.head.y, super.head.z);
            hatRim.setTexSize(128, 64);
            hatRim.mirror = true;
        } else if (type == 0) {
            hatTop = new ModelRenderer(this, 0, 31);
            hatTop.addBox(-4F, -12F, -4F, 8, 3, 8);
            hatTop.setPos(super.head.x, super.head.y, super.head.z);
            hatTop.setTexSize(128, 64);
            hatTop.mirror = true;

            hatRim = new ModelRenderer(this, 0, 31);
            hatRim.addBox(-8F, -9F, -8F, 16, 1, 16);
            hatRim.setPos(super.head.x, super.head.y, super.head.z);
            hatRim.setTexSize(128, 64);
            hatRim.mirror = true;
        }
        getHeadModels().forEach(this.head::addChild);

    }

    @Override
    public void setAllVisible(boolean invisible) {
        super.setAllVisible(false);
        hatRim.visible = true;
        hatTop.visible = true;
    }

    @Override
    protected Iterable<ModelRenderer> getHeadModels() {
        return ImmutableList.of(hatTop, hatRim);
    }
}
