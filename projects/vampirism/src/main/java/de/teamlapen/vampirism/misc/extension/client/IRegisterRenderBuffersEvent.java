package de.teamlapen.vampirism.misc.extension.client;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.renderer.rendertype.RenderType;

import java.util.SequencedMap;

public interface IRegisterRenderBuffersEvent {

    SequencedMap<RenderType, ByteBufferBuilder> vampirism$renderBuffers();
}
