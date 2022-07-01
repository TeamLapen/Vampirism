package de.teamlapen.vampirism.mixin.client;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin {

    @Inject(method = "lambda$static$2(Ljava/util/HashSet;)V", at = @At("RETURN"))
    private static void addCoffin(HashSet<RenderMaterial> set, CallbackInfo ci){
        Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map((dye) -> {
            return new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_" + dye.getName()));
        }).forEach(set::add);
    }
}
