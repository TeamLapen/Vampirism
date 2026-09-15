package de.teamlapen.faction.misc.mixin.client;

import de.teamlapen.faction.client.FactionsClientMod;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {

    @Inject(method = "itemBar", at = @At("TAIL"))
    private void factions$renderItemBars(ItemStack itemStack, int x, int y, CallbackInfo ci) {
        FactionsClientMod.services().itemBars().render((GuiGraphicsExtractor) (Object) this, itemStack, x, y);
    }
}
