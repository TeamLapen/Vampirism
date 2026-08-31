package de.teamlapen.vampirism.data.reloadlistener.heritage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Datapack-provided fixed members and lore for a named NPC's cosmetic heritage.
 */
public record HeritageDefinition(String npc, String anchor, List<Component> lore, List<Member> members) {
    public static final Codec<HeritageDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("npc").forGetter(HeritageDefinition::npc),
            Codec.STRING.fieldOf("anchor").forGetter(HeritageDefinition::anchor),
            ComponentSerialization.CODEC.listOf().optionalFieldOf("lore", List.of()).forGetter(HeritageDefinition::lore),
            Member.CODEC.listOf().fieldOf("members").forGetter(HeritageDefinition::members)
    ).apply(instance, HeritageDefinition::new));

    public HeritageDefinition {
        lore = List.copyOf(lore);
        members = List.copyOf(members);
        if (npc.isBlank()) {
            throw new IllegalArgumentException("Named NPC key cannot be blank");
        }
        Set<String> memberIds = new HashSet<>();
        for (Member member : members) {
            if (!memberIds.add(member.id())) {
                throw new IllegalArgumentException("Duplicate member id " + member.id());
            }
        }
        if (!memberIds.contains(anchor)) {
            throw new IllegalArgumentException("Anchor " + anchor + " is not a defined member");
        }
        for (Member member : members) {
            member.parent().ifPresent(parent -> {
                if (!memberIds.contains(parent)) {
                    throw new IllegalArgumentException("Member " + member.id() + " references unknown parent " + parent);
                }
            });
            ensureAcyclic(member, members);
        }
    }

    private static void ensureAcyclic(Member member, List<Member> members) {
        Set<String> visited = new HashSet<>();
        Member current = member;
        while (current.parent().isPresent()) {
            if (!visited.add(current.id())) {
                throw new IllegalArgumentException("Cycle found at member " + current.id());
            }
            String parent = current.parent().orElseThrow();
            current = members.stream().filter(candidate -> candidate.id().equals(parent)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown parent " + parent));
        }
    }

    public record Member(String id, Component name, Optional<String> parent) {
        public static final Codec<Member> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(Member::id),
                ComponentSerialization.CODEC.fieldOf("name").forGetter(Member::name),
                Codec.STRING.optionalFieldOf("parent").forGetter(Member::parent)
        ).apply(instance, Member::new));

        public Member {
            if (id.isBlank()) {
                throw new IllegalArgumentException("Member id cannot be blank");
            }
        }
    }
}
