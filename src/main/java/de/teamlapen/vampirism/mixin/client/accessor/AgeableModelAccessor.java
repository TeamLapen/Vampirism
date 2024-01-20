package de.teamlapen.vampirism.mixin.client.accessor;

import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AgeableListModel.class)
public interface AgeableModelAccessor {

    @Invoker("bodyParts")
    Iterable<ModelPart> invoke_bodyParts();
}
