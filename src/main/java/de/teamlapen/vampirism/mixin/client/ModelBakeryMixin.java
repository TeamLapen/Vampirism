package de.teamlapen.vampirism.mixin.client;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

import static net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin {

    @Inject(method = "lambda$static$2(Ljava/util/HashSet;)V", at = @At("RETURN"))
    private static void addCoffin(HashSet<RenderMaterial> set, CallbackInfo ci){
        Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map((dye) -> {
            return new RenderMaterial(BLOCK_ATLAS, new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_" + dye.getName()));
        }).forEach(set::add);
    }
}
