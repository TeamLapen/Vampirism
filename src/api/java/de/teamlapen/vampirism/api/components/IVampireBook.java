package de.teamlapen.vampirism.api.components;

import de.teamlapen.vampirism.api.general.IBookBackground;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.List;

public interface IVampireBook {

    ResourceLocation id();

    Component author();

    ResourceLocation backgroundId();

    boolean is(TagKey<IVampireBook> tag, RegistryAccess registryAccess);

    boolean isEmpty();

    MutableComponent title();

    List<MutableComponent> contents();

    IBookBackground background();
}
