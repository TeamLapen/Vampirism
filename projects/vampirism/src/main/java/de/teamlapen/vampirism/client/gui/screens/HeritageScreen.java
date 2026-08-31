package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.faction.client.gui.screens.ILastScreenProvider;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundHeritagePacket;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundRequestHeritagePacket;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HeritageScreen extends Screen {

    private static final int SCREEN_WIDTH = 252;
    private static final int SCREEN_HEIGHT = 219;
    private static final int CONTENT_WIDTH = SCREEN_WIDTH - 18;
    private static final int CONTENT_HEIGHT = SCREEN_HEIGHT - 46;
    private static final int CONTENT_TOP_PADDING = 20;
    private static final int NODE_WIDTH = 94;
    private static final int NODE_HEIGHT = 20;
    private static final int NODE_GAP = 18;
    private static final int ROW_HEIGHT = 52;
    private static final double MIN_ZOOM = 0.25;
    private static final double MAX_ZOOM = 2;

    private static final Identifier WINDOW_LOCATION = FIdentifier.mod("textures/gui/skills/window.png");
    private static final Identifier BACKGROUND = FIdentifier.mod("textures/gui/skills/backgrounds/level.png");
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
    private @Nullable Node hoveredNode;

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
            for (Node node : this.nodes) {
                this.drawNode(graphics, node);
            }
            pose.popMatrix();
        }
        graphics.disableScissor();
    }

    private void drawBackground(GuiGraphicsExtractor graphics) {
        for (int x = -(int) ((CONTENT_WIDTH / 2f + this.centerX) / 16 / this.zoom) - 1; x <= (int) ((CONTENT_WIDTH / 2f - this.centerX) / 16 / this.zoom); ++x) {
            for (int y = -(int) ((CONTENT_TOP_PADDING + this.centerY) / 16 / this.zoom) - 1; y <= (int) ((CONTENT_HEIGHT - this.centerY) / 16 / this.zoom); ++y) {
                GuiRenderer.blit(graphics, BACKGROUND, 16 * x, 16 * y, 16, 16, 16, 16);
            }
        }
    }

    private void drawConnections(GuiGraphicsExtractor graphics) {
        for (Node node : this.nodes) {
            if (node.parent == null) {
                continue;
            }

            int branchY = node.parent.y + NODE_HEIGHT + (node.y - node.parent.y - NODE_HEIGHT) / 2;
            graphics.verticalLine(node.parent.x, node.parent.y + NODE_HEIGHT, branchY, 0xff3d111f);
            graphics.horizontalLine(Math.min(node.parent.x, node.x), Math.max(node.parent.x, node.x), branchY, 0xff3d111f);
            graphics.verticalLine(node.x, branchY, node.y, 0xff3d111f);
            graphics.verticalLine(node.parent.x, node.parent.y + NODE_HEIGHT, branchY, 0xff9d314a);
            graphics.horizontalLine(Math.min(node.parent.x, node.x), Math.max(node.parent.x, node.x), branchY, 0xff9d314a);
            graphics.verticalLine(node.x, branchY, node.y, 0xff9d314a);
        }
    }

    private void drawNode(GuiGraphicsExtractor graphics, Node node) {
        int left = node.x - NODE_WIDTH / 2;
        int right = left + NODE_WIDTH;
        boolean currentPlayer = this.minecraft.player != null && node.playerId != null && node.playerId.equals(this.minecraft.player.getUUID());
        boolean hovered = node == this.hoveredNode;
        int border = hovered ? 0xfff4c2d0 : currentPlayer ? 0xffc14b75 : 0xff6c2038;
        int background = currentPlayer ? 0xff551b32 : 0xff30101f;

        graphics.fill(left - 1, node.y - 1, right + 1, node.y + NODE_HEIGHT + 1, border);
        graphics.fill(left, node.y, right, node.y + NODE_HEIGHT, background);
        graphics.centeredText(this.font, Component.literal(this.abbreviate(node.name)), node.x, node.y + 6, 0xffffffff);
    }

    private String abbreviate(String name) {
        int width = NODE_WIDTH - 8;
        if (this.font.width(name) <= width) {
            return name;
        }
        return this.font.plainSubstrByWidth(name, width - this.font.width("...")) + "...";
    }

    @Nullable
    private Node getNodeAt(double mouseX, double mouseY) {
        if (!this.isMouseOverContent(mouseX, mouseY) || this.nodes.isEmpty()) {
            return null;
        }

        double scaledX = (mouseX - (this.guiLeft + 9 + CONTENT_WIDTH / 2d) - this.centerX) / this.zoom;
        double scaledY = (mouseY - (this.guiTop + 18 + CONTENT_TOP_PADDING) - this.centerY) / this.zoom;
        for (Node node : this.nodes) {
            if (scaledX >= node.x - NODE_WIDTH / 2d && scaledX < node.x + NODE_WIDTH / 2d && scaledY >= node.y && scaledY < node.y + NODE_HEIGHT) {
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
                .forEach(member -> staticNodesById.put(member.id(), new Node(null, member.id(), member.name(), null, member.parentId())));

        Map<UUID, Node> playerNodesById = new HashMap<>();
        this.heritage.members().stream()
                .sorted(Comparator.comparing(ClientboundHeritagePacket.Member::playerName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(ClientboundHeritagePacket.Member::playerId))
                .forEach(member -> playerNodesById.put(member.playerId(), new Node(member.playerId(), null, member.playerName(), member.parentPlayerId(), member.parentNpcId())));

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
            Node founder = new Node(null, null, this.heritage.founderName(), null, null);
            for (Node root : roots) {
                root.parent = founder;
                founder.children.add(root);
            }
            graphicalNodes.add(founder);
            roots = List.of(founder);
        }

        int cursor = 0;
        for (Node root : roots) {
            cursor = this.layout(root, 0, cursor);
        }
        this.nodes = List.copyOf(graphicalNodes);
        if (this.nodes.isEmpty()) {
            this.centerX = 0;
            this.centerY = 0;
            return;
        }

        double treeCenter = (this.nodes.stream().mapToInt(node -> node.x - NODE_WIDTH / 2).min().orElse(0)
                + this.nodes.stream().mapToInt(node -> node.x + NODE_WIDTH / 2).max().orElse(0)) / 2d;
        for (Node node : this.nodes) {
            node.x -= (int) treeCenter;
        }
        this.minX = this.nodes.stream().mapToInt(node -> node.x - NODE_WIDTH / 2).min().orElse(0);
        this.maxX = this.nodes.stream().mapToInt(node -> node.x + NODE_WIDTH / 2).max().orElse(0);
        this.maxY = this.nodes.stream().mapToInt(node -> node.y + NODE_HEIGHT).max().orElse(0);
        this.center(0, 0);
    }

    private int layout(Node node, int depth, int cursor) {
        node.y = depth * ROW_HEIGHT;
        if (node.children.isEmpty()) {
            node.x = cursor + NODE_WIDTH / 2;
            return cursor + NODE_WIDTH + NODE_GAP;
        }

        int firstChildLeft = cursor;
        for (Node child : node.children) {
            cursor = this.layout(child, depth + 1, cursor);
        }
        node.x = (firstChildLeft + cursor - NODE_GAP) / 2;
        return cursor;
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
        private final List<Node> children = new ArrayList<>();
        private @Nullable Node parent;
        private int x;
        private int y;

        private Node(@Nullable UUID playerId, @Nullable String staticId, String name, @Nullable UUID parentPlayerId, @Nullable String parentStaticId) {
            this.playerId = playerId;
            this.staticId = staticId;
            this.name = name;
            this.parentPlayerId = parentPlayerId;
            this.parentStaticId = parentStaticId;
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
}
