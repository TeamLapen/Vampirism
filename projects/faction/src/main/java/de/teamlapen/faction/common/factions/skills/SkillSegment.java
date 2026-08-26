package de.teamlapen.faction.common.factions.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillSegment;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.api.factions.skills.SegmentPlacement;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record SkillSegment(Holder<ISkillTree> tree, List<Holder<? extends ISkill<?>>> skills, List<ResourceKey<ISkillSegment>> parents, List<ResourceKey<ISkillSegment>> lockingSegments, Optional<SegmentPlacement> placement) implements ISkillSegment {

    public static final Codec<ISkillSegment> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
            RegistryFixedCodec.create(FactionRegistries.Keys.SKILL_TREE).fieldOf("tree").forGetter(ISkillSegment::tree),
            ExtraCodecs.nonEmptyList(ISkill.CODEC.listOf()).fieldOf("skills").forGetter(ISkillSegment::skills),
            Codec.lazyInitialized(() -> ResourceKey.codec(FactionRegistries.Keys.SKILL_SEGMENT)).listOf().optionalFieldOf("parents", List.of()).forGetter(ISkillSegment::parents),
            Codec.lazyInitialized(() -> ResourceKey.codec(FactionRegistries.Keys.SKILL_SEGMENT)).listOf().optionalFieldOf("locking_segments", List.of()).forGetter(ISkillSegment::lockingSegments),
            SegmentPlacement.CODEC.optionalFieldOf("placement").forGetter(ISkillSegment::placement)
    ).apply(instance, SkillSegment::new)));

    public static class Builder {

        private final ResourceKey<ISkillTree> tree;
        private final ResourceKey<ISkillSegment> key;
        private final List<Holder<? extends ISkill<?>>> skills;
        private List<ResourceKey<ISkillSegment>> parents = List.of();
        private List<ResourceKey<ISkillSegment>> lockingSegments = List.of();
        private Optional<SegmentPlacement> placement = Optional.empty();

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

        public final Builder before(ResourceKey<ISkillSegment> segment) {
            return placement(SegmentPlacement.before(segment));
        }

        public final Builder after(ResourceKey<ISkillSegment> segment) {
            return placement(SegmentPlacement.after(segment));
        }

        public final Builder placement(SegmentPlacement placement) {
            this.placement = Optional.of(placement);
            return this;
        }

        public void register(BootstrapContext<ISkillSegment> context) {
            context.register(key, new SkillSegment(context.lookup(FactionRegistries.Keys.SKILL_TREE).getOrThrow(tree), skills, parents, lockingSegments, placement));
        }
    }
}
