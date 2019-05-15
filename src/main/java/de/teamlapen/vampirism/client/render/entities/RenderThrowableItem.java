package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.EntityThrowableItem;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSprite;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renders the vampirism throwable entity item
 */
@OnlyIn(Dist.CLIENT)
public class RenderThrowableItem extends RenderSprite<EntityThrowableItem> {
    public RenderThrowableItem(RenderManager renderManagerIn, ItemRenderer itemRendererIn) {
        super(renderManagerIn, Items.SNOWBALL, itemRendererIn);
    }

    @Override
    public ItemStack getStackToRender(EntityThrowableItem entityIn) {
        ItemStack stack = entityIn.getItem();
        return stack.isEmpty() ? new ItemStack(item) : stack;
    }
}
