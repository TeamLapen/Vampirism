package de.teamlapen.vampirism.common.util.wings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IWingsEntity;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.StringRepresentable;

import java.util.*;
import java.util.function.Function;

public record Wings(String name, UUID userId, Set<IWingsEntity.Texture> textures) {
    public static final Codec<Wings> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("name").forGetter(Wings::name),
            UUIDUtil.STRING_CODEC.fieldOf("userId").forGetter(Wings::userId),
            StringRepresentable.fromEnum(IWingsEntity.Texture::values).listOf().xmap((Function<? super List<IWingsEntity.Texture>, Set<IWingsEntity.Texture>>) HashSet::new, ArrayList::new).fieldOf("textures").forGetter(Wings::textures)
    ).apply(inst, Wings::new));
}
