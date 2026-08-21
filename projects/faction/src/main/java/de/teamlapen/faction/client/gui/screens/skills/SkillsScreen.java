package de.teamlapen.faction.client.gui.screens.skills;

import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.faction.client.gui.screens.ILastScreenProvider;
import de.teamlapen.faction.common.core.FactionEffects;
import de.teamlapen.faction.common.core.FactionItems;
import de.teamlapen.faction.common.core.FactionSounds;
import de.teamlapen.faction.common.factions.skills.SkillTreeGraphs;
import de.teamlapen.faction.common.network.packets.server.ServerboundForgetSkillPacket;
import de.teamlapen.faction.common.network.packets.server.ServerboundSimpleInputEvent;
import de.teamlapen.faction.common.network.packets.server.ServerboundUnlockSkillPacket;
import de.teamlapen.faction.common.world.inventory.InventoryHelper;
import de.teamlapen.faction.common.world.items.OblivionPotionItem;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gui screen which displays the skills available to the player and allows them to unlock some.
 * Inspired by Minecraft's new AchievementScreen but vertical
 * <p>
 * relevant classes {@link SkillsScreen} {@link SkillsTabComponent} {@link SkillSegmentComponent}
 */
@NullMarked
public class SkillsScreen extends Screen {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final int SCREEN_WIDTH = 252;
    public static final int SCREEN_HEIGHT = 219;

    private static final Identifier WINDOW_LOCATION = FIdentifier.mod("textures/gui/skills/window.png");
    private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
    private static final Component NO_TABS_LABEL = Component.translatable("gui.factionapi.skills.no_tab");
    private static final Component TITLE = Component.translatable("gui.factionapi.faction_menu.skill_screen");

    private static final int UNLOCK_HOLD_TICKS = 8;
    private static final int RESET_HOLD_TICKS = 16;

    private final ISkillPlayer<?> factionPlayer;
    private final List<SkillsTabComponent> tabs = new ArrayList<>();
    @Nullable
    private final ILastScreenProvider backScreen;
    @Nullable
    private SkillsTabComponent selectedTab;

    private int guiLeft;
    private int guiTop;
    private boolean scrolling;

    private Holding holdingMouse = Holding.NONE;
    private @Nullable Holder<? extends ISkill<?>> heldSkill;
    private int holdingTicks;
    private double lastMouseX;
    private double lastMouseY;

    private int oblivionPortions = 0;
    private int forgetCost = 0;

    public SkillsScreen(ISkillPlayer<?> factionPlayer, @Nullable ILastScreenProvider backScreen) {
        super(GameNarrator.NO_TITLE);
        this.factionPlayer = factionPlayer;
        this.backScreen = backScreen;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected List<Holder<ISkillTree>> getOrderedTrees(ISkillHandler<?> skillHandler) {

        var allTrees = new ArrayList<>(skillHandler.unlockedSkillTrees());
        var allTreeKeys = allTrees.stream().flatMap(x -> x.unwrapKey().stream()).collect(Collectors.toSet());
        var sortedTrees = new ArrayList<Holder<ISkillTree>>();

        while (!allTrees.isEmpty()) {
            var newTrees = allTrees.stream().filter(x -> x.value().orderAfter().isEmpty() || x.value().orderAfter().stream().allMatch(y -> sortedTrees.stream().anyMatch(z -> z.is(y)) || !allTreeKeys.contains(y))).toList();
            if (newTrees.isEmpty()) {
                LOGGER.warn("Could not order skill trees: {}", allTrees.stream().map(Holder::getRegisteredName).collect(Collectors.joining(", ")) );
                sortedTrees.addAll(allTrees);
                break;
            }
            sortedTrees.addAll(newTrees);
            allTrees.removeAll(newTrees);
        }

        return sortedTrees;
    }

    @Override
    protected void init() {
        tabs.clear();
        guiLeft = (width - SCREEN_WIDTH) / 2;
        guiTop = (height - SCREEN_HEIGHT) / 2;

        var graph = SkillTreeGraphs.get(factionPlayer.asEntity().level());
        var skillHandler = factionPlayer.getSkillHandler();

        int index = 0;
        for (Holder<ISkillTree> unlockedSkillTree : getOrderedTrees(skillHandler)) {
            var tree = graph.tree(unlockedSkillTree);
            if (tree.isEmpty()) {
                LOGGER.warn("No skill segments for tree {}", unlockedSkillTree.getRegisteredName());
                continue;
            }
            tabs.add(new SkillsTabComponent(minecraft, index++, unlockedSkillTree, skillHandler, graph, tree.get()));
        }

        if (!tabs.isEmpty()) {
            selectedTab = tabs.get(selectedTab == null ? 0 : selectedTab.getIndex());
        }

        if (backScreen != null) {
            addRenderableWidget(new ExtendedButton(guiLeft + 4, guiTop + 194, 80, 20, Component.translatable("gui.back"), (context) -> {
                backScreen.returnToLastScreen();
            }));
        }
        addRenderableWidget(new ExtendedButton(guiLeft + 168, guiTop + 194, 80, 20, Component.translatable("gui.done"), (context) -> {
            minecraft.setScreen(null);
        }));
        boolean test = !FMLEnvironment.isProduction();

        //server syncs after the screen is closed
        @Nullable
        Button resetSkills = addRenderableWidget(new ExtendedButton(guiLeft + 85, guiTop + 194, 80, 20, Component.translatable("gui.factionapi.skills.resetall"), (context) -> {
            FactionsMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.RESET_SKILLS));
            InventoryHelper.removeItemFromInventory(factionPlayer.asEntity().getInventory(), new ItemStack(FactionItems.OBLIVION_POTION.get())); //server syncs after the screen is closed
            if ((factionPlayer.getLevel() < 2 || minecraft.player.getInventory().countItem(FactionItems.OBLIVION_POTION.get()) <= 1) && !test) {
                context.active = false;
            }
        }));
        if ((factionPlayer.getLevel() < 2 || minecraft.player.getInventory().countItem(FactionItems.OBLIVION_POTION.get()) <= 0) && !test) {
            resetSkills.active = false;
            resetSkills.setTooltip(Tooltip.create(Component.translatable("gui.factionapi.skills.reset_consume")));
        } else {
            resetSkills.setTooltip(Tooltip.create(Component.translatable("gui.factionapi.skills.reset_req")));
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        extractInside(graphics, mouseX, mouseY, guiLeft, guiTop);
        extractWindow(graphics, mouseX, mouseY, guiLeft, guiTop);
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        extractTooltip(graphics, mouseX, mouseY, guiLeft, guiTop);
    }

    public void extractInside(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y) {
        var pose = graphics.pose();
        if (selectedTab != null) {
            selectedTab.extractContents(graphics, x + 9, y + 18, mouseX - 9 - guiLeft, mouseY - 18 - guiTop);
        } else {
            pose.pushMatrix();
            pose.translate(x + 9, y + 18);
            graphics.fill(0, 0, SCREEN_WIDTH - 18, SCREEN_HEIGHT - 27, -16777216);
            int i = 117;
            graphics.centeredText(font, NO_TABS_LABEL, i, 56 - 9 / 2, -1);
            graphics.centeredText(font, VERY_SAD_LABEL, i, 113 - 9, -1);
            pose.popMatrix();
        }
    }

    public void extractWindow(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y) {
        GuiRenderer.blit(graphics, WINDOW_LOCATION, x, y, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (tabs.size() > 1) {
            for (SkillsTabComponent skillTab : tabs) {
                skillTab.extractTab(graphics, x, y, mouseX, mouseY, skillTab == selectedTab);
            }

            for (SkillsTabComponent skillTab : tabs) {
                skillTab.drawIcon(graphics, x, y);
            }
        }
        if (selectedTab != null) {
            Component remainingPoints = selectedTab.getRemainingPointsText();
            graphics.text(font, remainingPoints, x + 240 - font.width(remainingPoints), y + 6, 0xff000000, false);
        }
        graphics.text(font, TITLE, x + 8, y + 6, 0xff000000, false);
    }

    public void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int guiLeft, int guiTop) {
        if (minecraft.player.getEffect(FactionEffects.OBLIVION) != null) return;
        if (selectedTab != null) {
            var pose = graphics.pose();
            pose.pushMatrix();
            pose.translate((float) (guiLeft + 9), (float) (guiTop + 18));
            selectedTab.drawTooltips(graphics, mouseX - guiLeft - 9, mouseY - guiTop - 18, heldSkill, (float) holdingTicks / (holdingMouse == Holding.LEFT ? UNLOCK_HOLD_TICKS : getTotalResetDuration()), holdingMouse);
            pose.popMatrix();
        }

        if (tabs.size() > 1) {
            for (SkillsTabComponent tabScreen : tabs) {
                if (tabScreen.isMouseOverTabItem(guiLeft, guiTop, mouseX, mouseY)) {
                    graphics.setTooltipForNextFrame(minecraft.font, tabScreen.getTitle(), mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (scrolling) {
            scrolling = false;
        }

        if (event.button() == 0) {
            holdingMouse = Holding.LEFT;
        }
        if (event.button() == 1) {
            holdingMouse = Holding.RIGHT;
        }

        for (SkillsTabComponent tab : tabs) {
            if (tab != selectedTab && tab.isMouseOverTabItem(guiLeft, guiTop, event.x(), event.y())) {
                selectedTab = tab;
                break;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        if (selectedTab != null && minecraft.player.getEffect(FactionEffects.OBLIVION) == null && isMouseOverContent(pMouseX, pMouseY)) {
            return selectedTab.mouseScrolled(pMouseX - 9 - guiLeft, pMouseY - 18 - guiTop, pScrollX, pScrollY);
        }
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }

    private boolean isMouseOverContent(double pMouseX, double pMouseY) {
        return pMouseX > guiLeft + 8 && pMouseX <= guiLeft + 9 + SkillsTabComponent.SCREEN_WIDTH && pMouseY > guiTop + 17 && pMouseY <= guiTop + 18 + SkillsTabComponent.SCREEN_HEIGHT;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        cancelHolding();

        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double xDragged, double yDragged) {
        scrolling = true;
        if (selectedTab != null && minecraft.player.getEffect(FactionEffects.OBLIVION) == null && isMouseOverContent(event.x(), event.y())) {
            selectedTab.mouseDragged(event.x(), event.y(), event.button(), xDragged, yDragged);
        }
        return super.mouseDragged(event, xDragged, yDragged);
    }

    @Override
    public void tick() {
        super.tick();

        if (minecraft.player != null) {
            oblivionPortions = OblivionPotionItem.countPortions(minecraft.player);
        }

        if (holdingMouse == Holding.NONE || selectedTab == null || minecraft.player == null || minecraft.player.getEffect(FactionEffects.OBLIVION) != null || !isMouseOverContent(lastMouseX, lastMouseY)) {
            cancelHolding();
            return;
        }

        var hovered = getSkillMouseOver(lastMouseX, lastMouseY);
        if (hovered == null
                || holdingMouse == Holding.LEFT && factionPlayer.getSkillHandler().canSkillBeEnabled(hovered, selectedTab.getSkillTree()) != ISkillHandler.Result.OK
                || holdingMouse == Holding.RIGHT && !canForget(hovered)) {
            cancelHolding();
            return;
        }

        if (!hovered.equals(heldSkill)) {
            heldSkill = hovered;
            holdingTicks = 0;
        }

        if (holdingMouse == Holding.LEFT && holdingTicks >= UNLOCK_HOLD_TICKS) {
            unlockSkill(hovered);
            cancelHolding();
        }
        if (holdingMouse == Holding.RIGHT && holdingTicks >= getTotalResetDuration()) {
            resetSkill(hovered);
            cancelHolding();
        }

        holdingTicks++;
    }

    private void cancelHolding() {
        heldSkill = null;
        holdingTicks = 0;
        holdingMouse = Holding.NONE;
    }

    private boolean canForget(Holder<? extends ISkill<?>> skill) {
        forgetCost = forgetCascade(skill).size();
        return forgetCost > 0 && (minecraft.player != null && minecraft.player.isCreative() || oblivionPortions >= forgetCost);
    }

    private List<Holder<? extends ISkill<?>>> forgetCascade(Holder<? extends ISkill<?>> skill) {
        if (minecraft.level == null || selectedTab == null) {
            return List.of();
        }

        return SkillTreeGraphs.get(minecraft.level).forgetCascade(selectedTab.getSkillTree(), skill, factionPlayer.getSkillHandler()::isSkillEnabled);
    }

    private void unlockSkill(Holder<? extends ISkill<?>> skill) {
        if (selectedTab == null) return;

        if (factionPlayer.getSkillHandler().canSkillBeEnabled(skill, selectedTab.getSkillTree()) == ISkillHandler.Result.OK) {
            //noinspection unchecked
            FactionsMod.proxy.sendToServer(new ServerboundUnlockSkillPacket((Holder<ISkill<?>>) skill, selectedTab.getSkillTree()));
            playSoundEffect(FactionSounds.UNLOCK_SKILLS.get(), 0.7F);
        } else {
            playSoundEffect(SoundEvents.NOTE_BLOCK_BASS.value(), 0.5F);
        }
    }

    private void resetSkill(Holder<? extends ISkill<?>> skill) {
        if (selectedTab == null) return;

        //noinspection unchecked
        FactionsMod.proxy.sendToServer(new ServerboundForgetSkillPacket((Holder<ISkill<?>>) skill, selectedTab.getSkillTree()));
    }

    private int getTotalResetDuration() {
        return (int) (RESET_HOLD_TICKS * (2 - 1 / (Math.pow(2, forgetCost - 1))));
    }

    private @Nullable Holder<? extends ISkill<?>> getSkillMouseOver(double mouseX, double mouseY) {
        return selectedTab != null ? selectedTab.getSelected((int) (mouseX - guiLeft - 9), (int) (mouseY - guiTop - 18)) : null;
    }

    private void playSoundEffect(SoundEvent event, float pitch) {
        minecraft.getSoundManager().play(SimpleSoundInstance.forUI(event, pitch));
    }

    public enum Holding {
        LEFT,
        RIGHT,
        NONE
    }
}
