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
import de.teamlapen.faction.common.factions.skills.ClientSkillTreeData;
import de.teamlapen.faction.common.factions.skills.SkillHandler;
import de.teamlapen.faction.common.network.packets.server.ServerboundSimpleInputEvent;
import de.teamlapen.faction.common.network.packets.server.ServerboundUnlockSkillPacket;
import de.teamlapen.faction.common.world.inventory.InventoryHelper;
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
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * Gui screen which displays the skills available to the player and allows them to unlock some.
 * Inspired by Minecraft's new AchievementScreen but vertical
 * <p>
 * relevant classes {@link SkillsScreen} {@link SkillsTabComponent} {@link SkillNodeComponent}
 */
@NullMarked
public class SkillsScreen extends Screen {
    public static final int SCREEN_WIDTH = 252;
    public static final int SCREEN_HEIGHT = 219;
    private static final Identifier WINDOW_LOCATION = FIdentifier.mod("textures/gui/skills/window.png");
    private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
    private static final Component NO_TABS_LABEL = Component.translatable("gui.factionapi.skills.no_tab");
    private static final Component TITLE = Component.translatable("gui.factionapi.faction_menu.skill_screen");

    private final ISkillPlayer<?> factionPlayer;
    private final List<SkillsTabComponent> tabs = new ArrayList<>();
    @Nullable
    private final ILastScreenProvider backScreen;
    @Nullable
    private SkillsTabComponent selectedTab;

    private int guiLeft;
    private int guiTop;
    private boolean scrolling;
    @Nullable
    private Vec3 mousePos;
    private boolean clicked;

    public SkillsScreen(ISkillPlayer<?> factionPlayer, @Nullable ILastScreenProvider backScreen) {
        super(GameNarrator.NO_TITLE);
        this.factionPlayer = factionPlayer;
        this.backScreen = backScreen;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        this.tabs.clear();
        this.guiLeft = (this.width - SCREEN_WIDTH) / 2;
        this.guiTop = (this.height - SCREEN_HEIGHT) / 2;

        int index = 0;
        SkillHandler<?> skillHandler = (SkillHandler<?>) this.factionPlayer.getSkillHandler();
        for (Holder<ISkillTree> unlockedSkillTree : skillHandler.unlockedSkillTrees()) {
            this.tabs.add(new SkillsTabComponent(this.minecraft, this, index++, unlockedSkillTree, this.factionPlayer.getSkillHandler(), ((ClientSkillTreeData) skillHandler.getTreeData())));
        }

        if (!this.tabs.isEmpty()) {
            this.selectedTab = this.tabs.get(this.selectedTab == null ? 0 : this.selectedTab.getIndex());
        }

        if (this.backScreen != null) {
            this.addRenderableWidget(new ExtendedButton(guiLeft + 4, guiTop + 194, 80, 20, Component.translatable("gui.back"), (context) -> {
                this.backScreen.returnToLastScreen();
            }));
        }
        this.addRenderableWidget(new ExtendedButton(guiLeft + 168, guiTop + 194, 80, 20, Component.translatable("gui.done"), (context) -> {
            this.minecraft.setScreen(null);
        }));
        boolean test = !FMLEnvironment.isProduction();

        //server syncs after the screen is closed
        @Nullable
        Button resetSkills = this.addRenderableWidget(new ExtendedButton(guiLeft + 85, guiTop + 194, 80, 20, Component.translatable("gui.factionapi.skills.resetall"), (context) -> {
            FactionsMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.RESET_SKILLS));
            InventoryHelper.removeItemFromInventory(this.factionPlayer.asEntity().getInventory(), new ItemStack(FactionItems.OBLIVION_POTION.get())); //server syncs after the screen is closed
            if ((this.factionPlayer.getLevel() < 2 || this.minecraft.player.getInventory().countItem(FactionItems.OBLIVION_POTION.get()) <= 1) && !test) {
                context.active = false;
            }
        }));
        if ((this.factionPlayer.getLevel() < 2 || this.minecraft.player.getInventory().countItem(FactionItems.OBLIVION_POTION.get()) <= 0) && !test) {
            resetSkills.active = false;
            resetSkills.setTooltip(Tooltip.create(Component.translatable("gui.factionapi.skills.reset_consume")));
        } else {
            resetSkills.setTooltip(Tooltip.create(Component.translatable("gui.factionapi.skills.reset_req")));
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        this.extractInside(graphics, mouseX, mouseY, guiLeft, guiTop);
        this.extractWindow(graphics, mouseX, mouseY, guiLeft, guiTop);
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        this.extractTooltip(graphics, mouseX, mouseY, guiLeft, guiTop);
    }

    public void extractInside(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y) {
        var pose = graphics.pose();
        if (this.selectedTab != null) {
            this.selectedTab.extractContents(graphics, x + 9, y + 18, mouseX - 9 - guiLeft, mouseY - 18 - guiTop);
        } else {
            pose.pushMatrix();
            pose.translate(x + 9, y + 18);
            graphics.fill(0, 0, SCREEN_WIDTH - 18, SCREEN_HEIGHT - 27, -16777216);
            int i = 117;
            graphics.centeredText(this.font, NO_TABS_LABEL, i, 56 - 9 / 2, -1);
            graphics.centeredText(this.font, VERY_SAD_LABEL, i, 113 - 9, -1);
            pose.popMatrix();
        }
    }

    public void extractWindow(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int x, int y) {
        GuiRenderer.blit(graphics, WINDOW_LOCATION, x, y, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (this.tabs.size() > 1) {

            for (SkillsTabComponent skillTab : this.tabs) {
                skillTab.extractTab(graphics, x, y, mouseX, mouseY, skillTab == this.selectedTab);
            }

            for (SkillsTabComponent skillTab : this.tabs) {
                skillTab.drawIcon(graphics, x, y);
            }
        }
        if (this.selectedTab != null) {
            Component remainingPoints = this.selectedTab.getRemainingPointsText();
            graphics.text(this.font, remainingPoints, x + 240 - this.font.width(remainingPoints), y + 6, 0xff000000, false);
        }
        graphics.text(this.font, TITLE, x + 8, y + 6, 0xff000000, false);
    }

    public void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int guiLeft, int guiTop) {
        if (this.minecraft.player.getEffect(FactionEffects.OBLIVION) != null) return;
        if (this.selectedTab != null) {
            var pose = graphics.pose();
            pose.pushMatrix();
            pose.translate((float) (guiLeft + 9), (float) (guiTop + 18));
            this.selectedTab.drawTooltips(graphics, mouseX - guiLeft - 9, mouseY - guiTop - 18);
            pose.popMatrix();
        }

        if (this.tabs.size() > 1) {
            for (SkillsTabComponent tabScreen : this.tabs) {
                if (tabScreen.isMouseOverTabItem(guiLeft, guiTop, mouseX, mouseY)) {
                    graphics.setTooltipForNextFrame(this.minecraft.font, tabScreen.getTitle(), mouseX, mouseY);
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
            this.clicked = true;
            this.mousePos = new Vec3(event.x(), event.y(), 0);
            for (SkillsTabComponent tab : this.tabs) {
                if (tab != this.selectedTab && tab.isMouseOverTabItem(this.guiLeft, this.guiTop, event.x(), event.y())) {
                    this.selectedTab = tab;
                    break;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        if (this.selectedTab != null && this.minecraft.player.getEffect(FactionEffects.OBLIVION) == null && this.isMouseOverContent(pMouseX, pMouseY)) {
            return this.selectedTab.mouseScrolled(pMouseX - 9 - guiLeft, pMouseY - 18 - guiTop, pScrollX, pScrollY);
        }
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }

    private boolean isMouseOverContent(double pMouseX, double pMouseY) {
        return pMouseX > guiLeft + 8 && pMouseX <= guiLeft + 9 + SkillsTabComponent.SCREEN_WIDTH && pMouseY > guiTop + 17 && pMouseY <= guiTop + 18 + SkillsTabComponent.SCREEN_HEIGHT;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            if (this.clicked) {
                if (!this.scrolling || (this.mousePos != null && this.mousePos.distanceTo(new Vec3(event.x(), event.y(), 0)) < 5)) {
                    unlockSkill(event.x(), event.y());
                }
            }
            this.clicked = false;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double xDragged, double yDragged) {
        this.scrolling = true;
        if (this.selectedTab != null && this.minecraft.player.getEffect(FactionEffects.OBLIVION) == null && isMouseOverContent(event.x(), event.y())) {
            this.selectedTab.mouseDragged(event.x(), event.y(), event.button(), xDragged, yDragged);
        }
        return super.mouseDragged(event, xDragged, yDragged);
    }


    private void unlockSkill(double mouseX, double mouseY) {
        Holder<ISkill<?>> selected = selectedTab != null ? selectedTab.getSelected((int) (mouseX - guiLeft - 9), (int) (mouseY - guiTop - 18)) : null;
        if (selected != null) {
            if (this.factionPlayer.getSkillHandler().canSkillBeEnabled(selected, this.selectedTab.getSkillTree()) == ISkillHandler.Result.OK) {
                FactionsMod.proxy.sendToServer(new ServerboundUnlockSkillPacket(selected, this.selectedTab.getSkillTree()));
                playSoundEffect(FactionSounds.UNLOCK_SKILLS.get(), 0.7F);
            } else {
                playSoundEffect(SoundEvents.NOTE_BLOCK_BASS.value(), 0.5F);
            }
        }
    }

    private void playSoundEffect(@NotNull SoundEvent event, float pitch) {
        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(event, pitch));
    }
}
