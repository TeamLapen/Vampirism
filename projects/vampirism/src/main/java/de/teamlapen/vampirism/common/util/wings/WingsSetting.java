package de.teamlapen.vampirism.common.util.wings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IWingsEntity;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.StringRepresentable;

import java.util.*;

public record WingsSetting(IWingsEntity.Texture texture, List<Player> players) {

    public static final Codec<WingsSetting> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            StringRepresentable.fromEnum(IWingsEntity.Texture::values).fieldOf("texture").forGetter(WingsSetting::texture),
            Player.CODEC.listOf().fieldOf("players").forGetter(WingsSetting::players)
    ).apply(inst, WingsSetting::new));

    public record Player(UUID id, String name) {
        public static final Codec<Player> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(Player::id),
                Codec.STRING.fieldOf("name").forGetter(Player::name)
        ).apply(inst, Player::new));
    }
}
