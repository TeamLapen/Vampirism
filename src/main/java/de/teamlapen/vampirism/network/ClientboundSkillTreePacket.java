package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.entity.player.skills.SkillTreeConfiguration;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClientboundSkillTreePacket(List<ConfigHolder> skillTrees) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "skill_tree_config");

    public static final Codec<ClientboundSkillTreePacket> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    ConfigHolder.CODEC.listOf().fieldOf("skill_trees").forGetter(l -> l.skillTrees)
            ).apply(inst, ClientboundSkillTreePacket::new)
    );

    public static ClientboundSkillTreePacket of(List<SkillTreeConfiguration> configurations) {
        return new ClientboundSkillTreePacket(configurations.stream().map(x -> new ConfigHolder(x.skillTree().unwrapKey().get(), x.root().unwrapKey().get(), x.children().stream().map(ClientboundSkillTreePacket::children).toList())).toList());
    }

    private static NodeHolder children(SkillTreeConfiguration.SkillTreeNodeConfiguration node) {
        return new NodeHolder(node.node().unwrapKey().get(), node.children().stream().map(ClientboundSkillTreePacket::children).toList());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public record ConfigHolder(ResourceKey<ISkillTree> skillTree, ResourceKey<ISkillNode> root, List<NodeHolder> children) {
        public static final Codec<ConfigHolder> CODEC = RecordCodecBuilder.create(inst
                        -> inst.group(
                        ResourceKey.codec(VampirismRegistries.SKILL_TREE_ID).fieldOf("skill_tree").forGetter(x -> x.skillTree),
                        ResourceKey.codec(VampirismRegistries.SKILL_NODE_ID).fieldOf("root").forGetter(x -> x.root),
                        NodeHolder.CODEC.listOf().fieldOf("children").forGetter(ConfigHolder::children)
                ).apply(inst, ConfigHolder::new)
        );

        public SkillTreeConfiguration toConfiguration(Registry<ISkillTree> treeRegistry, Registry<ISkillNode> nodeRegistry) {
            return new SkillTreeConfiguration(treeRegistry.getHolderOrThrow(skillTree), nodeRegistry.getHolderOrThrow(root), children.stream().map(x -> x.toConfiguration(nodeRegistry)).toList());
        }
    }

    public record NodeHolder(ResourceKey<ISkillNode> node, List<NodeHolder> children) {
        public static final Codec<NodeHolder> CODEC = RecordCodecBuilder.create(inst
                        -> inst.group(
                        ResourceKey.codec(VampirismRegistries.SKILL_NODE_ID).fieldOf("node").forGetter(x -> x.node),
                        ExtraCodecs.lazyInitializedCodec(() -> NodeHolder.CODEC).listOf().fieldOf("children").forGetter(NodeHolder::children)
                ).apply(inst, NodeHolder::new)
        );

        public SkillTreeConfiguration.SkillTreeNodeConfiguration toConfiguration(Registry<ISkillNode> registry) {
            return new SkillTreeConfiguration.SkillTreeNodeConfiguration(registry.getHolderOrThrow(node), children.stream().map(x -> x.toConfiguration(registry)).toList());
        }
    }

}
