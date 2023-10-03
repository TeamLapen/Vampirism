package de.teamlapen.vampirism.client.gui.screens.skills;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.network.ServerboundSimpleInputEvent;
import de.teamlapen.vampirism.network.ServerboundUnlockSkillPacket;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.ArrayList;
import java.util.List;

/**
 * Gui screen which displays the skills available to the player and allows them to unlock some.
 * Inspired by Minecraft's new AchievementScreen but vertical
 * <p>
 * relevant classes {@link SkillsScreen} {@link SkillsTabScreen} {@link SkillNodeScreen}
 */
@NonnullDefault
public class SkillsScreen extends Screen {
    public static final int SCREEN_WIDTH = 252;
    public static final int SCREEN_HEIGHT = 219;
    private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation(REFERENCE.MODID, "textures/gui/skills/window.png");
    private static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");
    private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
    private static final Component NO_TABS_LABEL = Component.translatable("gui.vampirism.skill_screen.no_tab");
    private static final Component TITLE = Component.translatable("gui.vampirism.vampirism_menu.skill_screen");

    @Nullable
    private final IFactionPlayer<?> factionPlayer;
    private final List<SkillsTabScreen> tabs = new ArrayList<>();
    @Nullable
    private final Screen backScreen;
    @Nullable
    private SkillsTabScreen selectedTab;
    @Nullable
    private Button resetSkills;

    private int guiLeft;
    private int guiTop;
    private boolean scrolling;
    @Nullable
    private Vec3 mousePos;
    private boolean clicked;

    public SkillsScreen(@Nullable IFactionPlayer<?> factionPlayer, @Nullable Screen backScreen) {
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
        assert this.minecraft != null;
        this.tabs.clear();
        this.guiLeft = (this.width - SCREEN_WIDTH) / 2;
        this.guiTop = (this.height - SCREEN_HEIGHT) / 2;

        if (this.factionPlayer != null) {
            SkillNode rootNode = VampirismMod.proxy.getSkillTree(true).getRootNodeForFaction(this.factionPlayer.getFaction().getID());
            this.tabs.add(new SkillsTabScreen(this.minecraft, this, 0, new ItemStack(ModItems.VAMPIRE_BOOK.get()), rootNode, this.factionPlayer.getSkillHandler(), Component.translatable("text.vampirism.skills.level")));
            if (this.factionPlayer.getFaction().hasLordSkills() && FactionPlayerHandler.getOpt(factionPlayer.getRepresentingPlayer()).map(a -> a.getLordLevel() > 0).orElse(false)) {
                rootNode = VampirismMod.proxy.getSkillTree(true).getRootNodeForFaction(this.factionPlayer.getFaction().getID(), SkillType.LORD);
                this.tabs.add(new SkillsTabScreen(this.minecraft, this, 1, new ItemStack(ModItems.VAMPIRE_MINION_BINDING.get()), rootNode, this.factionPlayer.getSkillHandler(), Component.translatable("text.vampirism.skills.lord")));
            }
        }

        if (this.tabs.size() > 0) {
            this.selectedTab = this.tabs.get(this.selectedTab == null ? 0 : this.selectedTab.getIndex());
        }

        if (this.backScreen != null) {
            this.addRenderableWidget(new ExtendedButton(guiLeft + 4, guiTop + 194, 80, 20, Component.translatable("gui.back"), (context) -> {
                this.minecraft.setScreen(this.backScreen);
            }));
        }
        this.addRenderableWidget(new ExtendedButton(guiLeft + 168, guiTop + 194, 80, 20, Component.translatable("gui.done"), (context) -> {
            this.minecraft.setScreen(null);
        }));
        FactionPlayerHandler.getOpt(minecraft.player).ifPresent(fph -> {
            fph.getCurrentFactionPlayer().ifPresent(factionPlayer -> {

                boolean test = VampirismMod.inDev || REFERENCE.VERSION.isTestVersion();

                resetSkills = this.addRenderableWidget(new ExtendedButton(guiLeft + 85, guiTop + 194, 80, 20, Component.translatable("text.vampirism.skill.resetall"), (context) -> {
                    VampirismMod.dispatcher.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Type.RESET_SKILLS));
                    InventoryHelper.removeItemFromInventory(factionPlayer.getRepresentingPlayer().getInventory(), new ItemStack(ModItems.OBLIVION_POTION.get())); //server syncs after the screen is closed
                    if ((factionPlayer.getLevel() < 2 || minecraft.player.getInventory().countItem(ModItems.OBLIVION_POTION.get()) <= 1) && !test) {
                        context.active = false;
                    }
                }));
                if ((factionPlayer.getLevel() < 2 || minecraft.player.getInventory().countItem(ModItems.OBLIVION_POTION.get()) <= 0) && !test) {
                    resetSkills.active = false;
                    resetSkills.setTooltip(Tooltip.create(Component.translatable("text.vampirism.skills.reset_consume", ModItems.OBLIVION_POTION.get().getDescription())));
                } else {
                    resetSkills.setTooltip(Tooltip.create(Component.translatable("text.vampirism.skills.reset_req", ModItems.OBLIVION_POTION.get().getDescription())));
                }
            });

        });
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);

        this.renderInside(graphics, mouseX, mouseY, guiLeft, guiTop);
        this.renderWindow(graphics, mouseX, mouseY, guiLeft, guiTop);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY, guiLeft, guiTop);
    }

    public void renderInside(@NotNull GuiGraphics graphics, int mouseX, int mouseY, int x, int y) {
        PoseStack pose = graphics.pose();
        if (this.selectedTab != null) {
            this.selectedTab.drawContents(graphics, x + 9, y + 18);
        } else {
            pose.pushPose();
            pose.translate(x + 9, y + 18, 0);
            graphics.fill(0, 0, SCREEN_WIDTH - 18, SCREEN_HEIGHT - 27, -16777216);
            int i = 117;
            graphics.drawCenteredString(this.font, NO_TABS_LABEL, i, 56 - 9 / 2, -1);
            graphics.drawCenteredString(this.font, VERY_SAD_LABEL, i, 113 - 9, -1);
            pose.popPose();
        }
    }

    public void renderWindow(@NotNull GuiGraphics graphics, int mouseX, int mouseY, int x, int y) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(WINDOW_LOCATION, x, y, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (this.tabs.size() > 1) {

            for (SkillsTabScreen skillTab : this.tabs) {
                skillTab.drawTab(graphics, x, y, skillTab == this.selectedTab);
            }

            for (SkillsTabScreen skillTab : this.tabs) {
                skillTab.drawIcon(graphics, x, y);
            }
        }
        if (this.selectedTab != null) {
            Component remainingPoints = Component.translatable("text.vampirism.skills.points_left", String.valueOf(this.selectedTab.getRemainingPoints()));
            graphics.drawString(this.font, remainingPoints, x + 240 - this.font.width(remainingPoints), y + 6, 4210752, false);
        }
        graphics.drawString(this.font, TITLE, x + 8, y + 6, 4210752, false);
    }

    public void renderTooltip(@NotNull GuiGraphics graphics, int mouseX, int mouseY, int guiLeft, int guiTop) {
        if (this.minecraft.player.getEffect(ModEffects.OBLIVION.get()) != null) return;
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.selectedTab != null) {
            PoseStack pose = graphics.pose();
            pose.pushPose();
            pose.translate((float) (guiLeft + 9), (float) (guiTop + 18), 400.0F);
            this.selectedTab.drawTooltips(graphics, mouseX - guiLeft - 9, mouseY - guiTop - 18);
            pose.popPose();
        }

        if (this.tabs.size() > 1) {
            for (SkillsTabScreen tabScreen : this.tabs) {
                if (tabScreen.isMouseOver(guiLeft, guiTop, mouseX, mouseY)) {
                    graphics.renderTooltip(this.minecraft.font, tabScreen.getTitle(), mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (scrolling) {
            scrolling = false;
        }
        if (button == 0) {
            this.clicked = true;
            this.mousePos = new Vec3(mouseX, mouseY, 0);
            for (SkillsTabScreen tab : this.tabs) {
                if (tab != this.selectedTab && tab.isMouseOver(this.guiLeft, this.guiTop, mouseX, mouseY)) {
                    this.selectedTab = tab;
                    break;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.selectedTab != null && this.minecraft.player.getEffect(ModEffects.OBLIVION.get()) == null) {
            return this.selectedTab.mouseScrolled(mouseX, mouseY, amount);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (this.clicked) {
                if (!this.scrolling || (this.mousePos != null && this.mousePos.distanceTo(new Vec3(mouseX, mouseY, 0)) < 5)) {
                    unlockSkill(mouseX, mouseY);
                }
            }
            this.clicked = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double xDragged, double yDragged) {
        this.scrolling = true;
        if (this.selectedTab != null && this.minecraft.player.getEffect(ModEffects.OBLIVION.get()) == null) {
            this.selectedTab.mouseDragged(mouseX, mouseY, mouseButton, xDragged, yDragged);
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, xDragged, yDragged);
    }

    private void unlockSkill(double mouseX, double mouseY) {
        ISkill<?> selected = selectedTab != null ? selectedTab.getSelected((int) (mouseX - guiLeft - 9), (int) (mouseY - guiTop - 18)) : null;
        if (selected != null) {
            if (canUnlockSkill(selected)) {
                VampirismMod.dispatcher.sendToServer(new ServerboundUnlockSkillPacket(RegUtil.id(selected)));
                playSoundEffect(SoundEvents.PLAYER_LEVELUP, 0.7F);
            } else {
                playSoundEffect(SoundEvents.NOTE_BLOCK_BASS.get(), 0.5F);
            }
        }
    }

    private boolean canUnlockSkill(@NotNull ISkill skill) {
        if (this.factionPlayer == null) return false;
        return this.factionPlayer.getSkillHandler().canSkillBeEnabled(skill) == ISkillHandler.Result.OK;
    }

    private void playSoundEffect(@NotNull SoundEvent event, float pitch) {
        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(event, 1.0F));
    }
}
