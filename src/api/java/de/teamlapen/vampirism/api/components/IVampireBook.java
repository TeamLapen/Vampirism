package de.teamlapen.vampirism.api.components;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.List;

public interface IVampireBook {

    ResourceLocation id();

    ResourceLocation itemModel();

    ResourceLocation backgroundTexture();

    boolean is(TagKey<IVampireBook> tag, RegistryAccess registryAccess);

    MutableComponent title();

    MutableComponent author();

    List<MutableComponent> text();
}
