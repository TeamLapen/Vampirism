package de.teamlapen.vampirism.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.teamlapen.vampirism.items.crossbow.HunterCrossbowItem;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.CrossbowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @WrapOperation(method = "renderArmWithItem", constant = @Constant(classValue = CrossbowItem.class))
    private boolean yourHandlerMethod(Object obj, Operation<Boolean> original) {
        if (obj instanceof HunterCrossbowItem) {
            return false;
        }
        return original.call(obj);
    }
}
