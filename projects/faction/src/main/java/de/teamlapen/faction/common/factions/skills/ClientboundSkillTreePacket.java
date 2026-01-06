package de.teamlapen.faction.common.factions.skills;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkillNode;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.List;

public record ClientboundSkillTreePacket(List<ConfigHolder> skillTrees) implements CustomPacketPayload {

    public static final Type<ClientboundSkillTreePacket> TYPE = new Type<>(FIdentifier.mod("skill_tree_config"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSkillTreePacket> CODEC = StreamCodec.composite(
            ConfigHolder.CODEC.apply(ByteBufCodecs.list()), ClientboundSkillTreePacket::skillTrees,
            ClientboundSkillTreePacket::new
    );

    public static ClientboundSkillTreePacket of(List<SkillTreeConfiguration> configurations) {
        return new ClientboundSkillTreePacket(configurations.stream().map(x -> new ConfigHolder(x.skillTree().unwrapKey().get(), x.root().unwrapKey().get(), x.children().stream().map(ClientboundSkillTreePacket::children).toList())).toList());
    }

    private static NodeHolder children(SkillTreeConfiguration.SkillTreeNodeConfiguration node) {
        return new NodeHolder(node.node().unwrapKey().get(), node.children().stream().map(ClientboundSkillTreePacket::children).toList());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record ConfigHolder(ResourceKey<ISkillTree> skillTree, ResourceKey<ISkillNode> root, List<NodeHolder> children) {
        public static final StreamCodec<RegistryFriendlyByteBuf, ConfigHolder> CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(FactionRegistries.Keys.SKILL_TREE), ConfigHolder::skillTree,
                ResourceKey.streamCodec(FactionRegistries.Keys.SKILL_NODE), ConfigHolder::root,
                NodeHolder.CODEC.apply(ByteBufCodecs.list()), ConfigHolder::children,
                ConfigHolder::new
        );

        public SkillTreeConfiguration toConfiguration(Registry<ISkillTree> treeRegistry, Registry<ISkillNode> nodeRegistry) {
            return new SkillTreeConfiguration(treeRegistry.getOrThrow(skillTree), nodeRegistry.getOrThrow(root), children.stream().map(x -> x.toConfiguration(nodeRegistry)).toList());
        }
    }

    public record NodeHolder(ResourceKey<ISkillNode> node, List<NodeHolder> children) {
        public static final StreamCodec<RegistryFriendlyByteBuf, NodeHolder> CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(FactionRegistries.Keys.SKILL_NODE), NodeHolder::node,
                NeoForgeStreamCodecs.lazy(() -> NodeHolder.CODEC).apply(ByteBufCodecs.list()), NodeHolder::children,
                NodeHolder::new
        );

        public SkillTreeConfiguration.SkillTreeNodeConfiguration toConfiguration(Registry<ISkillNode> registry) {
            return new SkillTreeConfiguration.SkillTreeNodeConfiguration(registry.getOrThrow(node), children.stream().map(x -> x.toConfiguration(registry)).toList());
        }
    }

}
