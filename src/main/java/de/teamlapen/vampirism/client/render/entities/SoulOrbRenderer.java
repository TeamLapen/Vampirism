package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.SoulOrbEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class SoulOrbRenderer extends SpriteRenderer<SoulOrbEntity> {

    public SoulOrbRenderer(EntityRendererManager renderManager, ItemRenderer renderItem) {
        super(renderManager, Items.SNOWBALL, renderItem);
    }


    @Override
    public ItemStack getStackToRender(SoulOrbEntity entityIn) {
        ItemStack stack = entityIn.getSoulItemStack();
        return stack.isEmpty() ? new ItemStack(item) : stack;
    }

    @Override
    public boolean shouldRender(SoulOrbEntity livingEntity, ICamera camera, double camX, double camY, double camZ) {
        boolean flag = true;
        if (Minecraft.getInstance().player != null) {
            flag = !livingEntity.isInvisibleToPlayer(Minecraft.getInstance().player);
        }
        return flag && super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }
}
