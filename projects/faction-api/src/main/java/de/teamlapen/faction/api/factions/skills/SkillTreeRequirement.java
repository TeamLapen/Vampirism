package de.teamlapen.faction.api.factions.skills;

import com.mojang.datafixers.util.Either;
import de.teamlapen.faction.api.FactionRegistries;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public record SkillTreeRequirement(Either<ResourceKey<ISkillTree>, TagKey<ISkillTree>> skillTree) {

    public static final StreamCodec<ByteBuf, SkillTreeRequirement> STREAM_CODEC = ByteBufCodecs.either(ResourceKey.streamCodec(FactionRegistries.Keys.SKILL_TREE), TagKey.streamCodec(FactionRegistries.Keys.SKILL_TREE)).map(SkillTreeRequirement::new, SkillTreeRequirement::skillTree);

    public SkillTreeRequirement(TagKey<ISkillTree> tagKey) {
        this(Either.right(tagKey));
    }

    public SkillTreeRequirement(ResourceKey<ISkillTree> key) {
        this(Either.left(key));
    }

    public boolean is(Holder<ISkillTree> treeHolder) {
        return skillTree.map(treeHolder::is, treeHolder::is);
    }
}
