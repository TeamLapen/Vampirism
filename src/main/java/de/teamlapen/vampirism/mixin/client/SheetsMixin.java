package de.teamlapen.vampirism.mixin.client;

import de.teamlapen.vampirism.client.core.ModBlocksRender;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Sheets.class)
public class SheetsMixin {

    @Inject(method = "getAllMaterials(Ljava/util/function/Consumer;)V", at = @At("RETURN"))
    private static void load(Consumer<Material> p_228775_0_, CallbackInfo ci) {
        for (Material material : ModBlocksRender.COFFIN_TEXTURES) {
            p_228775_0_.accept(material);
        }
    }
}
