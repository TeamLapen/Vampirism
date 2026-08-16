package de.teamlapen.faction.common.factions.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillSegment;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;

import java.util.Arrays;
import java.util.List;

public record SkillSegment(Holder<ISkillTree> tree, List<Holder<? extends ISkill<?>>> skills, List<ResourceKey<ISkillSegment>> parents, List<ResourceKey<ISkillSegment>> lockingSegments, int priority) implements ISkillSegment {

    public static final Codec<ISkillSegment> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
            RegistryFixedCodec.create(FactionRegistries.Keys.SKILL_TREE).fieldOf("tree").forGetter(ISkillSegment::tree),
            ExtraCodecs.nonEmptyList(ISkill.CODEC.listOf()).fieldOf("skills").forGetter(ISkillSegment::skills),
            Codec.lazyInitialized(() -> ResourceKey.codec(FactionRegistries.Keys.SKILL_SEGMENT)).listOf().optionalFieldOf("parents", List.of()).forGetter(ISkillSegment::parents),
            Codec.lazyInitialized(() -> ResourceKey.codec(FactionRegistries.Keys.SKILL_SEGMENT)).listOf().optionalFieldOf("locking_segments", List.of()).forGetter(ISkillSegment::lockingSegments),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(ISkillSegment::priority)
    ).apply(instance, SkillSegment::new)));

    public static class Builder {

        private final ResourceKey<ISkillTree> tree;
        private final ResourceKey<ISkillSegment> key;
        private final List<Holder<? extends ISkill<?>>> skills;
        private List<ResourceKey<ISkillSegment>> parents = List.of();
        private List<ResourceKey<ISkillSegment>> lockingSegments = List.of();
        private int priority = 0;

        @SafeVarargs
        public Builder(ResourceKey<ISkillTree> tree, ResourceKey<ISkillSegment> key, Holder<? extends ISkill<?>>... skills) {
            this.tree = tree;
            this.key = key;
            this.skills = Arrays.asList(skills);
        }

        @SafeVarargs
        public static Builder of(ResourceKey<ISkillTree> tree, ResourceKey<ISkillSegment> key, Holder<? extends ISkill<?>>... skills) {
            return new Builder(tree, key, skills);
        }

        @SafeVarargs
        public final Builder parents(ResourceKey<ISkillSegment>... parents) {
            this.parents = Arrays.asList(parents);
            return this;
        }

        @SafeVarargs
        public final Builder lockingSegments(ResourceKey<ISkillSegment>... lockingSegments) {
            this.lockingSegments = Arrays.asList(lockingSegments);
            return this;
        }

        public final Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public void register(BootstrapContext<ISkillSegment> context) {
            context.register(key, new SkillSegment(context.lookup(FactionRegistries.Keys.SKILL_TREE).getOrThrow(tree), skills, parents, lockingSegments, priority));
        }
    }
}
