package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.EntitySoulOrb;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderSoulOrb extends RenderSnowball<EntitySoulOrb> {

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
        if (Minecraft.getMinecraft().player != null) {
            flag = !livingEntity.isInvisibleToPlayer(Minecraft.getMinecraft().player);
        }
        return flag && super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }
}
