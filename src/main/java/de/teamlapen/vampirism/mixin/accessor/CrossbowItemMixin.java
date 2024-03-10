package de.teamlapen.vampirism.mixin.accessor;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CrossbowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CrossbowItem.class)
public interface CrossbowItemMixin {


    @Invoker("getShotPitch")
    static float getShotPitches(RandomSource p_220024_, int w){
        throw new IllegalStateException("Mixin not applied");
    }

}
