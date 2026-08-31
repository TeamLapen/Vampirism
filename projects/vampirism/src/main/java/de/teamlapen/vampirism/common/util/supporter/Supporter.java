package de.teamlapen.vampirism.common.util.supporter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.Factions;
import de.teamlapen.vampirism.api.world.items.components.IVampireBook;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record Supporter(Identifier faction, Component name, String player, Map<String, String> appearance, Optional<Holder<IVampireBook>> book, Optional<Heritage> heritage) {

    public static final Supporter FALLBACK = new Supporter(Factions.Keys.NEUTRAL, Component.empty(), "", Map.of(), Optional.empty(), Optional.empty());

    public static final MapCodec<Supporter> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Identifier.CODEC.fieldOf("faction").forGetter(Supporter::faction),
            ComponentSerialization.CODEC.fieldOf("name").forGetter(Supporter::name),
            Codec.STRING.fieldOf("player").forGetter(Supporter::player),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("appearance").forGetter(Supporter::appearance),
            IVampireBook.HOLDER_CODEC.optionalFieldOf("book").orElse(Optional.empty()).forGetter(Supporter::book),
            Heritage.CODEC.optionalFieldOf("heritage").orElse(Optional.empty()).forGetter(Supporter::heritage)
    ).apply(inst, Supporter::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Supporter> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, Supporter::faction,
            ComponentSerialization.STREAM_CODEC, Supporter::name,
            ByteBufCodecs.STRING_UTF8, Supporter::player,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8), Supporter::appearance,
            ByteBufCodecs.optional(IVampireBook.HOLDER_STREAM_CODEC), Supporter::book,
            ByteBufCodecs.optional(Heritage.STREAM_CODEC), Supporter::heritage,
            Supporter::new
    );

    public static Supporter defaultSupplier(IAttachmentHolder attachment) {
        return FALLBACK;
    }

    /**
     * Predefined lineage information for a supporter. The supporter is the implicit root; members without a parent are
     * direct descendants of that root.
     */
    public record Heritage(List<Component> lore, List<Member> members) {
        public static final Codec<Heritage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ComponentSerialization.CODEC.listOf().optionalFieldOf("lore", List.of()).forGetter(Heritage::lore),
                Member.CODEC.listOf().optionalFieldOf("members", List.of()).forGetter(Heritage::members)
        ).apply(instance, Heritage::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Heritage> STREAM_CODEC = StreamCodec.composite(
                ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)), Heritage::lore,
                Member.STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)), Heritage::members,
                Heritage::new
        );

        public Heritage {
            lore = List.copyOf(lore);
            members = List.copyOf(members);

            Map<String, Member> membersById = new HashMap<>();
            for (Member member : members) {
                if (membersById.putIfAbsent(member.id(), member) != null) {
                    throw new IllegalArgumentException("Duplicate heritage member id " + member.id());
                }
            }
            for (Member member : members) {
                member.parent().ifPresent(parent -> {
                    if (!membersById.containsKey(parent)) {
                        throw new IllegalArgumentException("Heritage member " + member.id() + " references unknown parent " + parent);
                    }
                });
                ensureAcyclic(member, membersById);
            }
        }

        private static void ensureAcyclic(Member member, Map<String, Member> membersById) {
            Set<String> visited = new HashSet<>();
            Member current = member;
            while (current.parent().isPresent()) {
                if (!visited.add(current.id())) {
                    throw new IllegalArgumentException("Heritage cycle found at member " + current.id());
                }
                current = membersById.get(current.parent().orElseThrow());
            }
        }
    }

    public record Member(String id, Component name, Optional<String> parent) {
        public static final Codec<Member> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(Member::id),
                ComponentSerialization.CODEC.fieldOf("name").forGetter(Member::name),
                Codec.STRING.optionalFieldOf("parent").forGetter(Member::parent)
        ).apply(instance, Member::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Member> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, Member::id,
                ComponentSerialization.STREAM_CODEC, Member::name,
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), Member::parent,
                Member::new
        );

        public Member {
            if (id.isBlank()) {
                throw new IllegalArgumentException("Heritage member id cannot be blank");
            }
        }
    }
}
