package de.teamlapen.vampirism.client.gui.skills;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.player.skills.SkillNode;
import de.teamlapen.vampirism.player.skills.SkillTreeManager;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SkillsScreen extends Screen {
    private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation(REFERENCE.MODID, "textures/gui/skills/window.png");
    private static final ITextComponent VERY_SAD_LABEL = new TranslationTextComponent("advancements.sad_label");
    private static final ITextComponent NO_ADVANCEMENTS_LABEL = new TranslationTextComponent("advancements.empty");
    private static final ITextComponent TITLE = new TranslationTextComponent("gui.vampirism.vampirism_menu.skill_screen");
    private static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");
    public static final int SCREEN_WIDTH = 252;
    public static final int SCREEN_HEIGHT = 219;
    private final IFactionPlayer<?> factionPlayer;
    @Nonnull
    private final List<SkillTabScreen> tabs = new ArrayList<>();
    @Nullable
    private SkillTabScreen selectedTab;
    private Screen backScreen;
    private Button resetSkills;

    private int guiLeft;
    private int guiTop;

    public SkillsScreen(IFactionPlayer<?> factionPlayer, Screen backScreen) {
        super(NarratorChatListener.NO_TITLE);
        this.factionPlayer = factionPlayer;
        this.backScreen = backScreen;
    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - SCREEN_WIDTH) / 2;
        this.guiTop = (this.height - SCREEN_HEIGHT) / 2;

        if (factionPlayer != null) {
            SkillNode rootNode = SkillTreeManager.getInstance().getSkillTree().getRootNodeForFaction(this.factionPlayer.getFaction().getID());
            tabs.add(new SkillTabScreen(this.minecraft, this, 0, new ItemStack(ModItems.vampire_book), rootNode, factionPlayer.getSkillHandler()));
        }

        if (this.tabs.size() > 0) {
            this.selectedTab = this.tabs.get(0);
        }

        if (this.backScreen != null) {
            this.addButton(new Button(guiLeft + 4,guiTop + 194, 80, 20, new TranslationTextComponent("gui.back"), (context) -> {
                this.minecraft.setScreen(this.backScreen);
            }));
        }
        this.addButton(new Button(guiLeft + 168,guiTop + 194, 80, 20, new TranslationTextComponent("gui.done"), (context) -> {
            this.minecraft.setScreen(null);
        }));
        FactionPlayerHandler.getOpt(minecraft.player).ifPresent(fph -> {
            fph.getCurrentFactionPlayer().ifPresent(factionPlayer -> {

                boolean test = VampirismMod.inDev || VampirismMod.instance.getVersionInfo().getCurrentVersion().isTestVersion();

                resetSkills = this.addButton(new Button(guiLeft + 85, guiTop + 194, 80, 20, new TranslationTextComponent("text.vampirism.skill.resetall"), (context) -> {
                    VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.RESETSKILL, ""));
                    InventoryHelper.removeItemFromInventory(factionPlayer.getRepresentingPlayer().inventory, new ItemStack(ModItems.oblivion_potion)); //server syncs after the screen is closed
                    if ((factionPlayer.getLevel() < 2 || minecraft.player.inventory.countItem(ModItems.oblivion_potion) <= 1) && !test) {
                        context.active = false;
                    }
                }, (button, stack, mouseX, mouseY) -> {
                    if (button.active) {
                        SkillsScreen.this.renderTooltip(stack, new TranslationTextComponent("text.vampirism.skills.reset_consume", ModItems.oblivion_potion.getDescription()), mouseX, mouseY);
                    } else {
                        SkillsScreen.this.renderTooltip(stack, new TranslationTextComponent("text.vampirism.skills.reset_req", ModItems.oblivion_potion.getDescription()), mouseX, mouseY);
                    }
                }));
                if ((factionPlayer.getLevel() < 2 || minecraft.player.inventory.countItem(ModItems.oblivion_potion) <= 0) && !test) {
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
        if (selectedTab != null) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)(x + 9), (float)(y + 18), 0.0F);
            this.selectedTab.drawContents(stack);
            RenderSystem.popMatrix();
            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
        } else {
            fill(stack, x + 9, y + 18, x + 9 + SCREEN_WIDTH - 18, y + 18 + SCREEN_HEIGHT - 27, -16777216);
            int i = x + 9 + 117;
            drawCenteredString(stack, this.font, NO_ADVANCEMENTS_LABEL, i, y + 18 + 56 - 9 / 2, -1);
            drawCenteredString(stack, this.font, VERY_SAD_LABEL, i, y + 18 + 113 - 9, -1);
        }
    }

    public void renderWindow(MatrixStack stack, int mouseX, int mouseY, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        this.minecraft.getTextureManager().bind(WINDOW_LOCATION);
        this.blit(stack, x, y, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (this.tabs.size() > 1) {
            this.minecraft.getTextureManager().bind(TABS_LOCATION);

            for (SkillTabScreen skillTab : this.tabs) {
                skillTab.drawTab(stack, x, y, skillTab == this.selectedTab);
            }

            RenderSystem.enableRescaleNormal();
            RenderSystem.defaultBlendFunc();

            for (SkillTabScreen skillTab : this.tabs) {
                skillTab.drawIcon(x, y, this.itemRenderer);
            }

            RenderSystem.disableBlend();
        }
        if (this.selectedTab != null) {
            TextComponent remainingPoints = new TranslationTextComponent("text.vampirism.skills.points_left",String.valueOf(this.selectedTab.getRemainingPoints()));
            this.font.draw(stack, remainingPoints, x+240 - this.font.width(remainingPoints), y+6, 4210752);
        }
        this.font.draw(stack, TITLE, (float) (x + 8), (float) (y + 6), 4210752);
    }

    public void renderTooltip(MatrixStack stack, int mouseX, int mouseY, int guiLeft, int guiTop) {
        if (this.minecraft.player.getEffect(ModEffects.oblivion) != null) return;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.selectedTab != null) {
            RenderSystem.pushMatrix();
            RenderSystem.enableDepthTest();
            RenderSystem.translatef((float)(guiLeft + 9), (float)(guiTop + 18), 400.0F);
            this.selectedTab.drawTooltips(stack, mouseX, mouseY , guiLeft , guiTop);
            RenderSystem.disableDepthTest();
            RenderSystem.popMatrix();
        }

        if (this.tabs.size() > 1) {
            for(SkillTabScreen tabScreen : this.tabs) {
                if (tabScreen == selectedTab && tabScreen.isMouseOver(guiLeft, guiTop, (double)mouseX, (double)mouseY)) {
                    this.renderTooltip(stack, tabScreen.getTitle(), mouseX, mouseY);
                }
            }
        }
    }

    private boolean scrolling;
    private boolean clicked;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (scrolling) {
            scrolling = false;
        }
        if (button == 0) {
            clicked = true;
            for (SkillTabScreen tab : this.tabs) {
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
        if (this.selectedTab != null && this.minecraft.player.getEffect(ModEffects.oblivion) == null) {
            return this.selectedTab.mouseScrolled(mouseX, mouseY, amount);
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (!scrolling && clicked) {
                unlockSkill(mouseX, mouseY);
            }
            clicked = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double xDragged, double yDragged) {
        this.scrolling = true;
        if (this.selectedTab != null && this.minecraft.player.getEffect(ModEffects.oblivion) == null) {
            this.selectedTab.mouseDragged(mouseX, mouseY, mouseButton, xDragged, yDragged);
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, xDragged, yDragged);
    }

    private void unlockSkill(double mouseX, double mouseY) {
        ISkill selected = selectedTab != null ? selectedTab.getSelected(mouseX, mouseY, guiLeft, guiTop) : null;
        if (selected != null && canUnlockSkill(selected)) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.UNLOCKSKILL, selected.getRegistryName().toString()));
            playSoundEffect(SoundEvents.PLAYER_LEVELUP, 0.7F);
        } else {
            playSoundEffect(SoundEvents.NOTE_BLOCK_BASS, 0.5F);
        }
    }

    private boolean canUnlockSkill(ISkill skill) {
        return this.factionPlayer.getSkillHandler().canSkillBeEnabled(skill) == ISkillHandler.Result.OK;
    }

    private void playSoundEffect(SoundEvent event, float pitch) {
        this.minecraft.getSoundManager().play(SimpleSound.forUI(event, 1.0F));
    }
}
