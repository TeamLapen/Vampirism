package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PotionBrewing.class)
public interface PotionBrewingAccessor {

    @Invoker("addMix(Lnet/minecraft/world/item/alchemy/Potion;Lnet/minecraft/world/item/Item;Lnet/minecraft/world/item/alchemy/Potion;)V")
    static void addMix(Potion pPotionEntry, Item pPotionIngredient, Potion pPotionResult) {
        throw new IllegalStateException("Mixin failed to apply");
    }
}
