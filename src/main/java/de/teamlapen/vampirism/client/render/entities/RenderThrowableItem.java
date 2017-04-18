package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.vampirism.entity.EntityThrowableItem;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Renders the vampirism throwable entity item
 */
public class RenderThrowableItem extends RenderSnowball<EntityThrowableItem> {
    public RenderThrowableItem(RenderManager renderManagerIn, RenderItem itemRendererIn) {
        super(renderManagerIn, Items.SNOWBALL, itemRendererIn);
    }

    @Override
    public ItemStack getStackToRender(EntityThrowableItem entityIn) {
        ItemStack stack = entityIn.getItem();
        return ItemStackUtil.isEmpty(stack) ? new ItemStack(item) : stack;
    }
}
