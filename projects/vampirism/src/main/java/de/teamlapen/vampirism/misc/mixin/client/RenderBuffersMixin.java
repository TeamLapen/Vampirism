package de.teamlapen.vampirism.misc.mixin.client;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import de.teamlapen.vampirism.client.core.ModRenderPipelines;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBuffers.class)
public class RenderBuffersMixin {

    @Shadow
    private static void put(Object2ObjectLinkedOpenHashMap<RenderType, ByteBufferBuilder> map, RenderType type) {
        throw new AssertionError();
    }

    /**
     * Buffers are drawn in insertion order and the crumbling overlay looks weird over the enchantment glint, so it has
     * to be inserted below the latter one. {@link net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent} can
     * only append, hence the injection.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "lambda$new$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;armorEntityGlint()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private void addItemCrumblingBeforeGlint(Object2ObjectLinkedOpenHashMap map, CallbackInfo ci) {
        put(map, ModRenderPipelines.itemCrumbling());
    }
}
