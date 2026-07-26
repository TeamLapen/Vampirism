package de.teamlapen.vampirism.misc.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.teamlapen.vampirism.client.core.ModAtlases;
import de.teamlapen.vampirism.client.core.ModEntityRenderStates;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.FlameFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.SpriteId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlameFeatureRenderer.class)
public class FlameFeatureRendererMixin {

    @WrapOperation(method = "renderFlame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/sprite/AtlasManager;get(Lnet/minecraft/client/resources/model/sprite/SpriteId;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"))
    private TextureAtlasSprite useSunFire(AtlasManager atlasManager, SpriteId sprite, Operation<TextureAtlasSprite> original, @Local(argsOnly = true, name = "state") EntityRenderState state) {
        if (state.getRenderDataOrDefault(ModEntityRenderStates.VAMPIRE_BURNING_IN_SUN, false)) {
            sprite = ModelBakery.FIRE_0.equals(sprite) ? ModAtlases.SUN_FIRE_0 : ModAtlases.SUN_FIRE_1;
        }

        return original.call(atlasManager, sprite);
    }
}
