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

    @Accessor("midLoadSoundPlayed")
    void setMidLoadSoundPlayer(boolean value);

    @Accessor("startSoundPlayed")
    void setStartSoundPlayed(boolean value);

    @Invoker("getShotPitch")
    static float getShotPitches(RandomSource p_220024_, int w){
        throw new IllegalStateException("Mixin not applied");
    }

}
