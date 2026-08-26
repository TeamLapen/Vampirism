package de.teamlapen.faction.api.factions.skills;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.neoforged.fml.common.asm.enumextension.ExtensionInfo;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;

/**
 * Places a skill segment on either side of another segment of the same row.
 */
public record SegmentPlacement(Type type, ResourceKey<ISkillSegment> segment) {

    public static final Codec<SegmentPlacement> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
            Type.CODEC.fieldOf("type").forGetter(SegmentPlacement::type),
            ResourceKey.codec(FactionRegistries.Keys.SKILL_SEGMENT).fieldOf("segment").forGetter(SegmentPlacement::segment)
    ).apply(instance, SegmentPlacement::new)));

    public static SegmentPlacement before(ResourceKey<ISkillSegment> segment) {
        return new SegmentPlacement(Type.BEFORE, segment);
    }

    public static SegmentPlacement after(ResourceKey<ISkillSegment> segment) {
        return new SegmentPlacement(Type.AFTER, segment);
    }

    public enum Type implements StringRepresentable, IExtensibleEnum {
        /**
         * Positions the segment left of the one it refers to.
         */
        BEFORE("before"),
        /**
         * Positions the segment right of the one it refers to.
         */
        AFTER("after");

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public static ExtensionInfo getExtensionInfo() {
            return ExtensionInfo.nonExtended(Type.class);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
