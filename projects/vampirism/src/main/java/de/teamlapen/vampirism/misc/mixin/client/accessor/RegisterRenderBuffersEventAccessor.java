package de.teamlapen.vampirism.misc.mixin.client.accessor;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import de.teamlapen.vampirism.misc.extension.client.IRegisterRenderBuffersEvent;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.SequencedMap;

@Mixin(RegisterRenderBuffersEvent.class)
public interface RegisterRenderBuffersEventAccessor extends IRegisterRenderBuffersEvent {

    @Accessor("renderBuffers")
    @Override
    SequencedMap<RenderType, ByteBufferBuilder> vampirism$renderBuffers();
}
