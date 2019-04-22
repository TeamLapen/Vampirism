package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.EntitySoulOrb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSprite;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class RenderSoulOrb extends RenderSprite<EntitySoulOrb> {

    public RenderSoulOrb(RenderManager renderManager, RenderItem renderItem) {
        super(renderManager, Items.SNOWBALL, renderItem);
    }


    @Override
    public ItemStack getStackToRender(EntitySoulOrb entityIn) {
        ItemStack stack = entityIn.getSoulItemStack();
        return stack.isEmpty() ? new ItemStack(item) : stack;
    }

    @Override
    public boolean shouldRender(EntitySoulOrb livingEntity, ICamera camera, double camX, double camY, double camZ) {
        boolean flag = true;
        if (Minecraft.getInstance().player != null) {
            flag = !livingEntity.isInvisibleToPlayer(Minecraft.getInstance().player);
        }
        return flag && super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }
}
