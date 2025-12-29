package de.teamlapen.vampirism.misc.mixin.accessor;

import de.teamlapen.vampirism.misc.extension.IFoodData;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FoodData.class)
public interface FoodDataAccessor extends IFoodData {

    @Override
    @Accessor("exhaustionLevel")
    float getExhaustionLevel();

    @Override
    @Accessor("exhaustionLevel")
    void setExhaustionLevel(float exhaustionLevel);
}
