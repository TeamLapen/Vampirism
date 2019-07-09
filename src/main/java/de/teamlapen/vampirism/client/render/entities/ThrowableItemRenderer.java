package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.ItemThrowableEntity;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Renders the vampirism throwable entity item
 */
@OnlyIn(Dist.CLIENT)
public class ThrowableItemRenderer extends SpriteRenderer<ItemThrowableEntity> {
    public ThrowableItemRenderer(EntityRendererManager renderManagerIn, ItemRenderer itemRendererIn) {
        super(renderManagerIn, Items.SNOWBALL, itemRendererIn);
    }

    @Override
    public ItemStack getStackToRender(ItemThrowableEntity entityIn) {
        ItemStack stack = entityIn.getItem();
        return stack.isEmpty() ? new ItemStack(item) : stack;
    }
}
