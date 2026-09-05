package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.faction.client.gui.screens.ILastScreenProvider;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundHeritagePacket;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundRequestHeritagePacket;
import de.teamlapen.vampirism.misc.extension.client.IGuiGraphicsExtractor;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HeritageScreen extends Screen {

    private static final int SCREEN_WIDTH = 252;
    private static final int SCREEN_HEIGHT = 219;
    private static final int CONTENT_WIDTH = SCREEN_WIDTH - 18;
    private static final int CONTENT_HEIGHT = SCREEN_HEIGHT - 46;
    private static final int CONTENT_TOP_PADDING = 20;
    private static final int NODE_SIZE = 26;
    private static final int NODE_GAP = 10;
    private static final int ROW_HEIGHT = 60;
    private static final int LOST_HISTORY_HEIGHT = 32;
    private static final double MIN_ZOOM = 0.25;
    private static final double MAX_ZOOM = 2;

    private static final Identifier WINDOW_LOCATION = FIdentifier.mod("textures/gui/skills/window.png");
    private static final List<PageTexture> PAGE_TEXTURES = List.of(
            new PageTexture(VIdentifier.mod("textures/gui/sprites/container/heritage_screen/page_clean.png"), 85),
            new PageTexture(VIdentifier.mod("textures/gui/sprites/container/heritage_screen/page_stain_small.png"), 10),
            new PageTexture(VIdentifier.mod("textures/gui/sprites/container/heritage_screen/page_stain_big.png"), 2),
            new PageTexture(VIdentifier.mod("textures/gui/sprites/container/heritage_screen/page_hole.png"), 1),
            new PageTexture(VIdentifier.mod("textures/gui/sprites/container/heritage_screen/page_stain_lines.png"), 1)
    );
    private static final int PAGE_TEXTURE_WEIGHT = PAGE_TEXTURES.stream().mapToInt(PageTexture::weight).sum();
    private static final Identifier LOST_HISTORY_TEXTURE = VIdentifier.mod("textures/gui/sprites/container/heritage_screen/rip.png");
    private static final List<Identifier> HERITAGE_FRAMES = List.of(
            VIdentifier.mod("container/heritage_screen/heritage_frame_dark_oak"),
            VIdentifier.mod("container/heritage_screen/heritage_frame_oak"),
            VIdentifier.mod("container/heritage_screen/heritage_frame_spruce")
    );
    private static final RandomSource RANDOM = RandomSource.create();
    private static final Component TITLE = Component.translatable("gui.vampirism.heritage.title");

    private final ILastScreenProvider backScreen;
    private @Nullable ClientboundHeritagePacket heritage;
    private List<Node> nodes = List.of();
    private int guiLeft;
    private int guiTop;
    private double minX;
    private double maxX;
    private double maxY;
    private double centerX;
    private double centerY;
    private double zoom = 1;
    private List<Node> lostHistoryNodes = List.of();
    private @Nullable Node hoveredNode;
    private final Map<UUID, PlayerSkin> remotePlayerSkins = new HashMap<>();
    private final Set<UUID> requestedPlayerSkins = new HashSet<>();

    public HeritageScreen(ILastScreenProvider backScreen) {
        super(GameNarrator.NO_TITLE);
        this.backScreen = backScreen;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - SCREEN_WIDTH) / 2;
        this.guiTop = (this.height - SCREEN_HEIGHT) / 2;

        this.addRenderableWidget(new ExtendedButton(this.guiLeft + 4, this.guiTop + 194, 80, 20, Component.translatable("gui.back"), button -> this.backScreen.returnToLastScreen()));
        this.addRenderableWidget(new ExtendedButton(this.guiLeft + 168, this.guiTop + 194, 80, 20, Component.translatable("gui.done"), button -> this.minecraft.setScreen(null)));

        if (this.heritage == null) {
            VampirismMod.proxy.sendToServer(new ServerboundRequestHeritagePacket());
        }
    }

    public void setHeritage(ClientboundHeritagePacket heritage) {
        this.heritage = heritage;
        this.rebuildTree();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        this.extractContents(graphics, mouseX, mouseY);
        GuiRenderer.blit(graphics, WINDOW_LOCATION, this.guiLeft, this.guiTop, SCREEN_WIDTH, SCREEN_HEIGHT);
        graphics.text(this.font, TITLE, this.guiLeft + 8, this.guiTop + 6, 0xff000000, false);
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);

        if (this.hoveredNode != null) {
            graphics.setTooltipForNextFrame(this.font, Component.literal(this.hoveredNode.name), mouseX, mouseY);
        }
    }

    private void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int contentX = this.guiLeft + 9;
        int contentY = this.guiTop + 18;
        this.hoveredNode = this.getNodeAt(mouseX, mouseY);

        graphics.enableScissor(contentX, contentY, contentX + CONTENT_WIDTH, contentY + CONTENT_HEIGHT);
        var pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(contentX, contentY);
        pose.translate((float) (CONTENT_WIDTH / 2d + this.centerX), (float) (CONTENT_TOP_PADDING + this.centerY));
        pose.scale((float) this.zoom, (float) this.zoom);
        this.drawBackground(graphics);
        if (this.heritage == null) {
            pose.popMatrix();
            graphics.centeredText(this.font, Component.translatable("gui.vampirism.heritage.loading"), contentX + CONTENT_WIDTH / 2, contentY + CONTENT_HEIGHT / 2 - this.font.lineHeight / 2, 0xffffffff);
        } else if (this.nodes.isEmpty()) {
            pose.popMatrix();
            graphics.centeredText(this.font, Component.translatable("gui.vampirism.heritage.empty"), contentX + CONTENT_WIDTH / 2, contentY + CONTENT_HEIGHT / 2 - this.font.lineHeight / 2, 0xffffffff);
        } else {
            this.drawConnections(graphics);
            this.drawLostHistorySections(graphics);
            for (Node node : this.nodes) {
                this.drawNode(graphics, node);
            }
            pose.popMatrix();
        }
        graphics.disableScissor();
    }

    private void drawBackground(GuiGraphicsExtractor graphics) {
        for (int x = -(int) ((CONTENT_WIDTH / 2f + this.centerX) / 64 / this.zoom) - 1; x <= (int) ((CONTENT_WIDTH / 2f - this.centerX) / 64 / this.zoom); ++x) {
            for (int y = -(int) ((CONTENT_TOP_PADDING + this.centerY) / 64 / this.zoom) - 1; y <= (int) ((CONTENT_HEIGHT - this.centerY) / 64 / this.zoom); ++y) {
                Identifier pageTexture = this.getPageTexture(x, y);
                GuiRenderer.blit(graphics, pageTexture, 64 * x, 64 * y, 64, 64, 64, 64);
            }
        }
    }

    private Identifier getPageTexture(int x, int y) {
        int selection = RandomSource.create(Mth.getSeed(new BlockPos(x, y, 0))).nextInt(PAGE_TEXTURE_WEIGHT);
        for (PageTexture pageTexture : PAGE_TEXTURES) {
            selection -= pageTexture.weight();
            if (selection < 0) {
                return pageTexture.texture();
            }
        }
        throw new IllegalStateException("Page texture weights must sum to a positive value");
    }

    private void drawLostHistorySections(GuiGraphicsExtractor graphics) {
        int firstTileX = -(int) ((CONTENT_WIDTH / 2f + this.centerX) / 32 / this.zoom) - 1;
        int lastTileX = (int) ((CONTENT_WIDTH / 2f - this.centerX) / 32 / this.zoom);
        for (Node node : this.lostHistoryNodes) {
            int y = node.y;
            for (int x = firstTileX; x <= lastTileX; ++x) {
                for (int tileY = 0; tileY < LOST_HISTORY_HEIGHT; tileY += 32) {
                    int tileHeight = Math.min(32, LOST_HISTORY_HEIGHT - tileY);
                    graphics.blit(RenderPipelines.GUI_TEXTURED, LOST_HISTORY_TEXTURE, 32 * x, y + tileY, 0, 0, 32, tileHeight, 32, 32);
                }
            }
        }
    }

    private void drawConnections(GuiGraphicsExtractor graphics) {
        for (Node node : this.nodes) {
            if (node.parent == null) {
                continue;
            }

            Node parent = node.parent;
            int parentBottom = parent.y + (parent.lostHistoryNode ? LOST_HISTORY_HEIGHT : NODE_SIZE) - 1;
            if (node.lostHistoryNode) {
                graphics.verticalLine(parent.x, parentBottom, node.y, 0xff9d314a);
                continue;
            }

            int branchY = parent.lostHistoryNode
                    ? parent.y + LOST_HISTORY_HEIGHT + getConnectionLength()
                    : parent.y + NODE_SIZE + (node.y - parent.y - NODE_SIZE) / 2;
            graphics.verticalLine(parent.x, parentBottom, branchY, 0xff9d314a);
            graphics.horizontalLine(Math.min(parent.x, node.x), Math.max(parent.x, node.x), branchY, 0xff9d314a);
            graphics.verticalLine(node.x, branchY, node.y, 0xff9d314a);
        }
    }

    private void drawNode(GuiGraphicsExtractor graphics, Node node) {
        if (node.lostHistoryNode) {
            return;
        }
        int left = node.x - NODE_SIZE / 2;
        @Nullable Identifier skinTexture = this.getSkinTexture(node);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, node.frame, left, node.y, NODE_SIZE, NODE_SIZE);
        if (skinTexture == null) {
            ((IGuiGraphicsExtractor) graphics).vampirism$centeredText(this.font, Component.literal("?"), node.x, node.y + 9, 0xff000000, false);
        } else {
            this.drawFace(graphics, skinTexture, left + 5, node.y + 5);
        }
    }

    private void drawFace(GuiGraphicsExtractor graphics, Identifier skinTexture, int x, int y) {
        var pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        pose.scale(2, 2);
        graphics.blit(RenderPipelines.GUI_TEXTURED, skinTexture, 0, 0, 8, 8, 8, 8, 64, 64);
        graphics.blit(RenderPipelines.GUI_TEXTURED, skinTexture, 0, 0, 40, 8, 8, 8, 64, 64);
        pose.popMatrix();
    }

    @Nullable
    private Identifier getSkinTexture(Node node) {
        if (node.playerId != null) {
            if (this.minecraft.getConnection() != null) {
                PlayerInfo playerInfo = this.minecraft.getConnection().getPlayerInfo(node.playerId);
                if (playerInfo != null) {
                    return playerInfo.getSkin().body().texturePath();
                }
            }
            PlayerSkin playerSkin = this.remotePlayerSkins.get(node.playerId);
            if (playerSkin != null) {
                return playerSkin.body().texturePath();
            }
            this.requestPlayerSkin(node);
            return null;
        }
        if (node.staticId != null) {
            PlayerSkin supporterSkin = VampirismModClient.services().playerSkinHelper().getSkins().get(node.staticId);
            if (supporterSkin != null) {
                return supporterSkin.body().texturePath();
            }
        }
        return null;
    }

    private void requestPlayerSkin(Node node) {
        if (node.playerId == null || !this.requestedPlayerSkins.add(node.playerId)) {
            return;
        }
        this.minecraft.playerSkinRenderCache().lookup(ResolvableProfile.createUnresolved(node.name)).thenAccept(profile ->
                this.minecraft.execute(() -> profile.ifPresent(resolved -> this.remotePlayerSkins.put(node.playerId, resolved.playerSkin())))
        );
    }

    @Nullable
    private Node getNodeAt(double mouseX, double mouseY) {
        if (!this.isMouseOverContent(mouseX, mouseY) || this.nodes.isEmpty()) {
            return null;
        }

        double scaledX = (mouseX - (this.guiLeft + 9 + CONTENT_WIDTH / 2d) - this.centerX) / this.zoom;
        double scaledY = (mouseY - (this.guiTop + 18 + CONTENT_TOP_PADDING) - this.centerY) / this.zoom;
        for (Node node : this.nodes) {
            if (node.lostHistoryNode) {
                continue;
            }
            if (scaledX >= node.x - NODE_SIZE / 2d && scaledX < node.x + NODE_SIZE / 2d && scaledY >= node.y && scaledY < node.y + NODE_SIZE) {
                return node;
            }
        }
        return null;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (event.button() == 0 && this.isMouseOverContent(event.x(), event.y())) {
            this.center(this.centerX + dragX, this.centerY + dragY);
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!this.isMouseOverContent(mouseX, mouseY) || this.nodes.isEmpty()) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }

        double scaledX = (mouseX - (this.guiLeft + 9 + CONTENT_WIDTH / 2d) - this.centerX) / this.zoom;
        double scaledY = (mouseY - (this.guiTop + 18 + CONTENT_TOP_PADDING) - this.centerY) / this.zoom;
        this.zoom = Mth.clamp(this.zoom + (scrollX + scrollY) * 0.1, MIN_ZOOM, MAX_ZOOM);
        this.center(
                mouseX - (this.guiLeft + 9 + CONTENT_WIDTH / 2d) - scaledX * this.zoom,
                mouseY - (this.guiTop + 18 + CONTENT_TOP_PADDING) - scaledY * this.zoom
        );
        return true;
    }

    private boolean isMouseOverContent(double mouseX, double mouseY) {
        return mouseX >= this.guiLeft + 9 && mouseX < this.guiLeft + 9 + CONTENT_WIDTH
                && mouseY >= this.guiTop + 18 && mouseY < this.guiTop + 18 + CONTENT_HEIGHT;
    }

    private void rebuildTree() {
        Map<String, Node> staticNodesById = new HashMap<>();
        this.heritage.staticMembers().stream()
                .sorted(Comparator.comparing(ClientboundHeritagePacket.StaticMember::name, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(ClientboundHeritagePacket.StaticMember::id))
                .forEach(member -> staticNodesById.put(member.id(), new Node(null, member.id(), member.name(), null, member.parentId(), member.lostHistory())));

        Map<UUID, Node> playerNodesById = new HashMap<>();
        this.heritage.members().stream()
                .sorted(Comparator.comparing(ClientboundHeritagePacket.Member::playerName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(ClientboundHeritagePacket.Member::playerId))
                .forEach(member -> playerNodesById.put(member.playerId(), new Node(member.playerId(), null, member.playerName(), member.parentPlayerId(), member.parentNpcId(), false)));

        List<Node> roots = new ArrayList<>();
        for (Node node : staticNodesById.values()) {
            node.parent = staticNodesById.get(node.parentStaticId);
            if (node.parent == null) {
                roots.add(node);
            } else {
                node.parent.children.add(node);
            }
        }
        for (Node node : playerNodesById.values()) {
            node.parent = playerNodesById.get(node.parentPlayerId);
            if (node.parent == null) {
                node.parent = staticNodesById.get(node.parentStaticId);
            }
            if (node.parent == null) {
                roots.add(node);
            } else {
                node.parent.children.add(node);
            }
        }
        roots.sort(Node.ORDER);
        for (Node node : staticNodesById.values()) {
            node.children.sort(Node.ORDER);
        }
        for (Node node : playerNodesById.values()) {
            node.children.sort(Node.ORDER);
        }

        List<Node> graphicalNodes = new ArrayList<>(staticNodesById.values());
        graphicalNodes.addAll(playerNodesById.values());
        if (this.heritage.founderName() != null && !roots.isEmpty()) {
            Node founder = new Node(null, null, this.heritage.founderName(), null, null, false);
            for (Node root : roots) {
                root.parent = founder;
                founder.children.add(root);
            }
            graphicalNodes.add(founder);
            roots = List.of(founder);
        }

        List<Node> lostHistoryNodes = new ArrayList<>();
        for (Node root : roots) {
            this.insertLostHistoryNodes(root, lostHistoryNodes);
        }
        graphicalNodes.addAll(lostHistoryNodes);
        this.lostHistoryNodes = List.copyOf(lostHistoryNodes);

        int cursor = 0;
        for (Node root : roots) {
            cursor = this.layout(root, cursor);
        }
        this.nodes = List.copyOf(graphicalNodes);
        if (this.nodes.isEmpty()) {
            this.centerX = 0;
            this.centerY = 0;
            return;
        }

        double treeCenter = (this.nodes.stream().mapToInt(node -> node.x - NODE_SIZE / 2).min().orElse(0)
                + this.nodes.stream().mapToInt(node -> node.x + NODE_SIZE / 2).max().orElse(0)) / 2d;
        for (Node node : this.nodes) {
            node.x -= (int) treeCenter;
        }
        this.minX = this.nodes.stream().mapToInt(node -> node.x - NODE_SIZE / 2).min().orElse(0);
        this.maxX = this.nodes.stream().mapToInt(node -> node.x + NODE_SIZE / 2).max().orElse(0);
        this.maxY = this.nodes.stream().mapToInt(node -> node.y + NODE_SIZE).max().orElse(0);
        this.center(0, 0);
    }

    private void insertLostHistoryNodes(Node node, List<Node> lostHistoryNodes) {
        if (node.lostHistory) {
            Node lostHistoryNode = Node.lostHistory();
            lostHistoryNode.parent = node;
            lostHistoryNode.children.addAll(node.children);
            for (Node child : lostHistoryNode.children) {
                child.parent = lostHistoryNode;
            }
            node.children.clear();
            node.children.add(lostHistoryNode);
            lostHistoryNodes.add(lostHistoryNode);
        }
        for (Node child : node.children) {
            this.insertLostHistoryNodes(child, lostHistoryNodes);
        }
    }

    private int layout(Node node, int cursor) {
        if (node.parent == null) {
            node.y = 0;
        } else if (node.lostHistoryNode) {
            node.y = node.parent.y + NODE_SIZE + getConnectionLength();
        } else if (node.parent.lostHistoryNode) {
            node.y = node.parent.y + LOST_HISTORY_HEIGHT + 2 * getConnectionLength();
        } else {
            node.y = node.parent.y + ROW_HEIGHT;
        }
        if (node.children.isEmpty()) {
            node.x = cursor + NODE_SIZE / 2;
            return cursor + NODE_SIZE + NODE_GAP;
        }

        int firstChildLeft = cursor;
        for (Node child : node.children) {
            cursor = this.layout(child, cursor);
        }
        node.x = (firstChildLeft + cursor - NODE_GAP) / 2;
        return cursor;
    }

    private static int getConnectionLength() {
        return (ROW_HEIGHT - NODE_SIZE) / 2;
    }

    private void center(double x, double y) {
        if (this.nodes.isEmpty()) {
            this.centerX = 0;
            this.centerY = 0;
            return;
        }

        double minCenterX = CONTENT_WIDTH / 2d - this.maxX * this.zoom;
        double maxCenterX = -CONTENT_WIDTH / 2d - this.minX * this.zoom;
        this.centerX = minCenterX > maxCenterX ? 0 : Mth.clamp(x, minCenterX, maxCenterX);

        double minCenterY = CONTENT_HEIGHT - CONTENT_TOP_PADDING - this.maxY * this.zoom;
        this.centerY = minCenterY > 0 ? 0 : Mth.clamp(y, minCenterY, 0);
    }

    private static final class Node {
        private static final Comparator<Node> ORDER = Comparator.comparing(Node::name, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Node::key);

        private final @Nullable UUID playerId;
        private final @Nullable String staticId;
        private final String name;
        private final @Nullable UUID parentPlayerId;
        private final @Nullable String parentStaticId;
        private final boolean lostHistory;
        private final boolean lostHistoryNode;
        private final List<Node> children = new ArrayList<>();
        private final Identifier frame = HERITAGE_FRAMES.get(RANDOM.nextInt(HERITAGE_FRAMES.size()));
        private @Nullable Node parent;
        private int x;
        private int y;

        private Node(@Nullable UUID playerId, @Nullable String staticId, String name, @Nullable UUID parentPlayerId, @Nullable String parentStaticId, boolean lostHistory) {
            this(playerId, staticId, name, parentPlayerId, parentStaticId, lostHistory, false);
        }

        private Node(@Nullable UUID playerId, @Nullable String staticId, String name, @Nullable UUID parentPlayerId, @Nullable String parentStaticId, boolean lostHistory, boolean lostHistoryNode) {
            this.playerId = playerId;
            this.staticId = staticId;
            this.name = name;
            this.parentPlayerId = parentPlayerId;
            this.parentStaticId = parentStaticId;
            this.lostHistory = lostHistory;
            this.lostHistoryNode = lostHistoryNode;
        }

        private static Node lostHistory() {
            return new Node(null, null, "", null, null, false, true);
        }

        private String name() {
            return this.name;
        }

        private String key() {
            if (this.playerId != null) {
                return "player:" + this.playerId;
            }
            if (this.staticId != null) {
                return "static:" + this.staticId;
            }
            return "founder:" + this.name;
        }

    }

    private record PageTexture(Identifier texture, int weight) {
    }
}
