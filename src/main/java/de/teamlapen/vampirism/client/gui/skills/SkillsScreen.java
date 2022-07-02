package de.teamlapen.vampirism.client.gui.skills;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
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
import de.teamlapen.vampirism.network.CSimpleInputEvent;
import de.teamlapen.vampirism.network.CUnlockSkillPacket;
import de.teamlapen.vampirism.player.skills.SkillNode;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.system.NonnullDefault;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * Gui screen which displays the skills available to the player and allows them to unlock some.
 * Inspired by Minecraft's new AchievementScreen but vertical
 * <p>
 * relevant classes {@link SkillsScreen} {@link SkillsTabScreen} {@link SkillNodeScreen}
 */
@NonnullDefault
@ParametersAreNonnullByDefault
public class SkillsScreen extends Screen {
    public static final int SCREEN_WIDTH = 252;
    public static final int SCREEN_HEIGHT = 219;
    private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation(REFERENCE.MODID, "textures/gui/skills/window.png");
    private static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");
    private static final ITextComponent VERY_SAD_LABEL = new TranslationTextComponent("advancements.sad_label");
    private static final ITextComponent NO_TABS_LABEL = new TranslationTextComponent("gui.vampirism.skill_screen.no_tab");
    private static final ITextComponent TITLE = new TranslationTextComponent("gui.vampirism.vampirism_menu.skill_screen");

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
    private Vector3d mousePos;
    private boolean clicked;

    public SkillsScreen(@Nullable IFactionPlayer<?> factionPlayer, @Nullable Screen backScreen) {
        super(NarratorChatListener.NO_TITLE);
        this.factionPlayer = factionPlayer;
        this.backScreen = backScreen;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        assert this.minecraft!=null;
        this.tabs.clear();
        this.guiLeft = (this.width - SCREEN_WIDTH) / 2;
        this.guiTop = (this.height - SCREEN_HEIGHT) / 2;

        if (this.factionPlayer != null) {
            SkillNode rootNode = VampirismMod.proxy.getSkillTree(true).getRootNodeForFaction(this.factionPlayer.getFaction().getID(), SkillType.LEVEL);
            this.tabs.add(new SkillsTabScreen(this.minecraft, this, 0, new ItemStack(ModItems.VAMPIRE_BOOK.get()), rootNode, this.factionPlayer.getSkillHandler()));
            if (this.factionPlayer.getFaction().hasLordSkills() && FactionPlayerHandler.getOpt(factionPlayer.getRepresentingPlayer()).map(a -> a.getLordLevel() > 0).orElse(false)) {
                rootNode = VampirismMod.proxy.getSkillTree(true).getRootNodeForFaction(this.factionPlayer.getFaction().getID(), SkillType.LORD);
                this.tabs.add(new SkillsTabScreen(this.minecraft, this, 1, new ItemStack(ModItems.VAMPIRE_MINION_BINDING.get()), rootNode, this.factionPlayer.getSkillHandler()));
            }
        }

        if (this.tabs.size() > 0) {
            this.selectedTab = this.tabs.get(this.selectedTab == null ?0: this.selectedTab.getIndex());
        }

        if (this.backScreen != null) {
            this.addButton(new Button(guiLeft + 4, guiTop + 194, 80, 20, new TranslationTextComponent("gui.back"), (context) -> {
                this.minecraft.setScreen(this.backScreen);
            }));
        }
        this.addButton(new Button(guiLeft + 168, guiTop + 194, 80, 20, new TranslationTextComponent("gui.done"), (context) -> {
            this.minecraft.setScreen(null);
        }));
        FactionPlayerHandler.getOpt(minecraft.player).ifPresent(fph -> {
            fph.getCurrentFactionPlayer().ifPresent(factionPlayer -> {

                boolean test = VampirismMod.inDev || VampirismMod.instance.getVersionInfo().getCurrentVersion().isTestVersion();

                resetSkills = this.addButton(new Button(guiLeft + 85, guiTop + 194, 80, 20, new TranslationTextComponent("text.vampirism.skill.resetall"), (context) -> {
                    VampirismMod.dispatcher.sendToServer(new CSimpleInputEvent(CSimpleInputEvent.Type.RESET_SKILLS));
                    InventoryHelper.removeItemFromInventory(factionPlayer.getRepresentingPlayer().inventory, new ItemStack(ModItems.OBLIVION_POTION.get())); //server syncs after the screen is closed
                    if ((factionPlayer.getLevel() < 2 || minecraft.player.inventory.countItem(ModItems.OBLIVION_POTION.get()) <= 1) && !test) {
                        context.active = false;
                    }
                }, (button, stack, mouseX, mouseY) -> {
                    if (button.active) {
                        SkillsScreen.this.renderTooltip(stack, new TranslationTextComponent("text.vampirism.skills.reset_consume", ModItems.OBLIVION_POTION.get().getDescription()), mouseX, mouseY);
                    } else {
                        SkillsScreen.this.renderTooltip(stack, new TranslationTextComponent("text.vampirism.skills.reset_req", ModItems.OBLIVION_POTION.get().getDescription()), mouseX, mouseY);
                    }
                }));
                if ((factionPlayer.getLevel() < 2 || minecraft.player.inventory.countItem(ModItems.OBLIVION_POTION.get()) <= 0) && !test) {
                    resetSkills.active = false;
                }
            });

        });
    }

    @Override
    public void render(@Nonnull MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);

        this.renderInside(stack, mouseX, mouseY, guiLeft, guiTop);
        this.renderWindow(stack, mouseX, mouseY, guiLeft, guiTop);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltip(stack, mouseX, mouseY, guiLeft, guiTop);
    }

    public void renderInside(MatrixStack stack, int mouseX, int mouseY, int x, int y) {
        if (this.selectedTab != null) {
            stack.pushPose();
            stack.translate((float) (x + 9), (float) (y + 18), 0.0F);
            this.selectedTab.drawContents(stack);
            stack.popPose();
            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
        } else {
            stack.pushPose();
            stack.translate(x + 9,y + 18,0);
            fill(stack, 0, 0, SCREEN_WIDTH - 18, SCREEN_HEIGHT - 27, -16777216);
            int i = 117;
            drawCenteredString(stack, this.font, NO_TABS_LABEL, i, 56 - 9 / 2, -1);
            drawCenteredString(stack, this.font, VERY_SAD_LABEL, i, 113 - 9, -1);
            stack.popPose();
        }
    }

    public void renderWindow(MatrixStack stack, int mouseX, int mouseY, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        this.minecraft.getTextureManager().bind(WINDOW_LOCATION);
        this.blit(stack, x, y, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (this.tabs.size() > 1) {
            this.minecraft.getTextureManager().bind(TABS_LOCATION);

            for (SkillsTabScreen skillTab : this.tabs) {
                skillTab.drawTab(stack, x, y, skillTab == this.selectedTab);
            }

            RenderSystem.enableRescaleNormal();
            RenderSystem.defaultBlendFunc();

            for (SkillsTabScreen skillTab : this.tabs) {
                skillTab.drawIcon(x, y, this.itemRenderer);
            }

            RenderSystem.disableBlend();
        }
        if (this.selectedTab != null) {
            TextComponent remainingPoints = new TranslationTextComponent("text.vampirism.skills.points_left", String.valueOf(this.selectedTab.getRemainingPoints()));
            this.font.draw(stack, remainingPoints, x + 240 - this.font.width(remainingPoints), y + 6, 4210752);
        }
        this.font.draw(stack, TITLE, (float) (x + 8), (float) (y + 6), 4210752);
    }

    public void renderTooltip(MatrixStack stack, int mouseX, int mouseY, int guiLeft, int guiTop) {
        if (this.minecraft.player.getEffect(ModEffects.OBLIVION.get()) != null) return;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.selectedTab != null) {
            stack.pushPose();
            RenderSystem.enableDepthTest();
            stack.translate((float) (guiLeft + 9), (float) (guiTop + 18), 400.0F);
            this.selectedTab.drawTooltips(stack, mouseX - guiLeft - 9, mouseY - guiTop - 18);
            RenderSystem.disableDepthTest();
            stack.popPose();
        }

        if (this.tabs.size() > 1) {
            for (SkillsTabScreen tabScreen : this.tabs) {
                if (tabScreen == selectedTab && tabScreen.isMouseOver(guiLeft, guiTop, mouseX, mouseY)) {
                    this.renderTooltip(stack, tabScreen.getTitle(), mouseX, mouseY);
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
            this.mousePos = new Vector3d( mouseX, mouseY, 0);
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
                if (!this.scrolling || (this.mousePos != null && this.mousePos.distanceTo(new Vector3d(mouseX, mouseY, 0)) < 5)) {
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
        ISkill selected = selectedTab != null ? selectedTab.getSelected(mouseX, mouseY, guiLeft, guiTop) : null;
        if (selected != null ) {
            if (canUnlockSkill(selected)) {
                VampirismMod.dispatcher.sendToServer(new CUnlockSkillPacket(selected.getRegistryName()));
                playSoundEffect(SoundEvents.PLAYER_LEVELUP, 0.7F);
            } else {
                playSoundEffect(SoundEvents.NOTE_BLOCK_BASS, 0.5F);
            }
        }
    }

    private boolean canUnlockSkill(ISkill skill) {
        if (this.factionPlayer == null) return false;
        return this.factionPlayer.getSkillHandler().canSkillBeEnabled(skill) == ISkillHandler.Result.OK;
    }

    private void playSoundEffect(SoundEvent event, float pitch) {
        this.minecraft.getSoundManager().play(SimpleSound.forUI(event, 1.0F));
    }
}
