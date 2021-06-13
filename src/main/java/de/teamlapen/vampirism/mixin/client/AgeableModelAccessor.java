package de.teamlapen.vampirism.mixin.client;

import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AgeableModel.class)
public interface AgeableModelAccessor {

    @Invoker("getBodyParts")
    Iterable<ModelRenderer> getBodyParts_vampirism();
}
