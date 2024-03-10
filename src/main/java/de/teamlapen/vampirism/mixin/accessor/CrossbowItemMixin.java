package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CrossbowItem.class)
public interface CrossbowItemMixin {


    @NotNull
    @Invoker("getChargedProjectiles")
    static List<ItemStack> getChargedProjectiles(ItemStack p_40942_) {
        throw new IllegalStateException("Mixin not applied");
    }

    @Invoker("getShotPitches")
    static float @NotNull [] getShotPitches(RandomSource p_220024_){
        throw new IllegalStateException("Mixin not applied");
    }

    @Invoker("onCrossbowShot")
    static void onCrossbowShot(Level p_40906_, LivingEntity p_40907_, ItemStack p_40908_){
        throw new IllegalStateException("Mixin not applied");
    }

    @Invoker("getArrow")
    static AbstractArrow getArrow(Level p_40915_, LivingEntity p_40916_, ItemStack p_40917_, ItemStack p_40918_) {
        throw new IllegalStateException("Mixin not applied");
    }

    @Invoker("addChargedProjectile")
    static void addChargedProjectile(ItemStack p_40929_, ItemStack p_40930_){
        throw new IllegalStateException("Mixin not applied");
    }
}
