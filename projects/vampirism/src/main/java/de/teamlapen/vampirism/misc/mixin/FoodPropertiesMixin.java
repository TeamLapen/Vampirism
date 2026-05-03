package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.faction.common.factions.minions.MinionEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {

    @Inject(method = "onConsume", at = @At("RETURN"))
    private void affectMinion(Level level, LivingEntity user, ItemStack stack, Consumable consumable, CallbackInfo ci) {
        if (user instanceof MinionEntity<?> minion) {
            minion.eat(level, stack, (FoodProperties) (Object) this);
        }
    }
}
