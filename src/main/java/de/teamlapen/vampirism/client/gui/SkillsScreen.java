package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.player.skills.SkillManager;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gui screen which displays the skills available to the players and allows him to unlock some.
 * Inspired by Minecraft's old GuiAchievement
 */
@OnlyIn(Dist.CLIENT)
public class SkillsScreen extends Screen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/skills_window.png");
    private static final ResourceLocation defaultIcons = new ResourceLocation(REFERENCE.MODID, "textures/gui/skills.png");
    private final static int ICON_TEXTURE_WIDTH = 256;
    private final static int ICON_TEXTURE_HEIGHT = 80;
    private final int area_min_y = -77;
    private final int skill_width = 24;
    private final List<SkillNode> skillNodes = new ArrayList<>();
    private int display_width = 256;
    private int display_height = 202;
    private int area_min_x = 0;
    private int area_max_x = 0;
    private int area_max_y;
    private int field_146563_h;
    private int field_146564_i;
    private float zoomOut = 1.0F;
    private double weelmovement = 0D;
    private double displayX;
    private double displayY;
    private double displayXNew;
    private double displayYNew;
    private double field_146565_w;
    private double field_146573_x;
    private SkillHandler skillHandler;
    private boolean display;
    private ISkill selected;
    private int field_146554_D;

    public SkillsScreen() {
        super(new TranslationTextComponent("skillsscreen_title"));//TODO 1.14 name
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (!display) {
            super.render(mouseX, mouseY, partialTicks);
            return;
        }
        if (Minecraft.getInstance().mouseHelper.isLeftDown()) {
            int centerX = (this.width - this.display_width) / 2;
            int centerY = (this.height - this.display_height) / 2;
            int k = centerX + 8;
            int l = centerY + 17;

            if ((this.field_146554_D == 0 || this.field_146554_D == 1) && mouseX >= k && mouseX < k + 224 && mouseY >= l && mouseY < l + 155) {
                if (this.field_146554_D == 0) {
                    this.field_146554_D = 1;
                } else {
                    this.displayXNew -= (double) ((float) (mouseX - this.field_146563_h) * this.zoomOut);
                    this.displayYNew -= (double) ((float) (mouseY - this.field_146564_i) * this.zoomOut);
                    this.field_146565_w = this.displayX = this.displayXNew;
                    this.field_146573_x = this.displayY = this.displayYNew;
                }

                this.field_146563_h = mouseX;
                this.field_146564_i = mouseY;
            }
        } else {
            this.field_146554_D = 0;
        }

        float zoomOutOld = this.zoomOut;

        if (weelmovement < 0) {
            this.zoomOut += 0.25F;
        } else if (weelmovement > 0) {
            this.zoomOut -= 0.25F;
        }
        weelmovement = 0;

        this.zoomOut = MathHelper.clamp(this.zoomOut, 1.0F, 2.0F);

        if (this.zoomOut != zoomOutOld) {
            float f5 = zoomOutOld - this.zoomOut;
            float f4 = zoomOutOld * (float) this.display_width;
            float f = zoomOutOld * (float) this.display_height;
            float f1 = this.zoomOut * (float) this.display_width;
            float f2 = this.zoomOut * (float) this.display_height;
            this.displayXNew -= (double) ((f1 - f4) * 0.5F);
            this.displayYNew -= (double) ((f2 - f) * 0.5F);
            this.field_146565_w = this.displayX = this.displayXNew;
            this.field_146573_x = this.displayY = this.displayYNew;
        }

        if (this.field_146565_w < (double) area_min_x) {
            this.field_146565_w = (double) area_min_x;
        }

        if (this.field_146573_x < (double) area_min_y) {
            this.field_146573_x = (double) area_min_y;
        }

        if (this.field_146565_w >= (double) area_max_x) {
            this.field_146565_w = (double) (area_max_x - 1);
        }

        if (this.field_146573_x >= (double) area_max_y) {
            this.field_146573_x = (double) (area_max_y - 1);
        }

        this.renderBackground();
        this.drawSkills(mouseX, mouseY, partialTicks);
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        this.drawTitle();
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
        this.weelmovement += p_mouseScrolled_5_;
        return true;
    }

    @Override
    public void init() {
        IFactionPlayer factionPlayer = FactionPlayerHandler.get(minecraft.player).getCurrentFactionPlayer();
        if (factionPlayer != null) {
            display = true;
            skillHandler = (SkillHandler) factionPlayer.getSkillHandler();
            Integer[] info = ((SkillManager) VampirismAPI.skillManager()).getDisplayInfo(factionPlayer.getFaction());
            int w = info[0] * info[1] * skill_width * 2;
            area_max_x = w + 10 - display_width;
            area_min_x = -w - 10 - display_width;
            area_max_y = info[2] * skill_width * 2;
            this.displayX = displayXNew = field_146565_w = -100;
            this.displayY = displayYNew = field_146573_x = -10;
            skillNodes.clear();
            addToList(skillNodes, skillHandler.getRootNode());
        }
        this.buttons.clear();
        this.buttons.add(new Button(this.width / 2 + 24, this.height / 2 + 74, 80, 20, UtilLib.translate("gui.done"), (context) -> this.minecraft.displayGuiScreen((Screen) null)));
        if (display) {
            Button resetSkills = this.addButton(new Button((this.width - display_width) / 2 + 24, this.height / 2 + 74, 80, 20, UtilLib.translate("text.vampirism.skill.resetall"), (context) -> {
                boolean test = VampirismMod.inDev || VampirismMod.instance.getVersionInfo().getCurrentVersion().isTestVersion();
                ConfirmScreen resetGui = new ConfirmScreen((cxt) -> {
                    if (cxt) {
                        VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.RESETSKILL, ""));
                        Minecraft.getInstance().displayGuiScreen(this);
                    } else {
                        Minecraft.getInstance().displayGuiScreen(this);
                    }
                }, new TranslationTextComponent("gui.vampirism.reset_skills.title"), new TranslationTextComponent("gui.vampirism.reset_skills." + (test ? "desc_test" : "desc")));
                Minecraft.getInstance().displayGuiScreen(resetGui);
            }));

            if (factionPlayer.getLevel() < 2) {
                resetSkills.active = false;
            }
        }


    }

    public void updateScreen() {
        if (display) {
            this.displayX = this.displayXNew;
            this.displayY = this.displayYNew;
            double d0 = this.field_146565_w - this.displayXNew;
            double d1 = this.field_146573_x - this.displayYNew;

            if (d0 * d0 + d1 * d1 < 4.0D) {
                this.displayXNew += d0;
                this.displayYNew += d1;
            } else {
                this.displayXNew += d0 * 0.85D;
                this.displayYNew += d1 * 0.85D;
            }
        }
    }

    protected void drawTitle() {
        String title = I18n.format("text.vampirism.skills.gui_title");
        int x = (this.width - display_width) / 2;
        int y = (this.height - display_height) / 2;
        this.font.drawString(title, x + 15, y + 5, 0xFFFFFFFF);
        String points = I18n.format("text.vampirism.skills.points_left", skillHandler.getLeftSkillPoints());
        x = (this.width + display_width) / 2 - font.getStringWidth(points);
        this.font.drawString(points, x - 15, y + 5, 0xFFFFFFFF);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (ModKeys.getKeyBinding(ModKeys.KEY.SKILL).getKey().getKeyCode() == p_keyPressed_1_) {
            this.minecraft.displayGuiScreen(null);
            this.minecraft.setGameFocused(true);
            return true;
        } else {
            super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean retur = super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT && selected != null) {
            if (skillHandler.canSkillBeEnabled(selected) == ISkillHandler.Result.OK) {
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.UNLOCKSKILL, selected.getRegistryName().toString()));
                playSoundEffect(SoundEvents.ENTITY_PLAYER_LEVELUP, 0.7F);
                return true;
            } else {
                playSoundEffect(SoundEvents.BLOCK_NOTE_BLOCK_BASS, 0.5F);
                return true;
            }
        }
        return retur;
    }

    /**
     * Add the given node and all it's child nodes to the list
     *
     * @param list
     * @param root
     */
    private void addToList(List<SkillNode> list, SkillNode root) {
        list.add(root);
        for (SkillNode node : root.getChildren()) {
            addToList(list, node);
        }

    }

    private void drawSkills(int mouseX, int mouseY, float partialTicks) {
        int offsetX = MathHelper.floor(this.displayX + (this.displayXNew - this.displayX) * (double) partialTicks);
        int offsetY = MathHelper.floor(this.displayY + (this.displayYNew - this.displayY) * (double) partialTicks);

        if (offsetX < area_min_x) {
            offsetX = area_min_x;
        }

        if (offsetY < area_min_y) {
            offsetY = area_min_y;
        }

        if (offsetX >= area_max_x) {
            offsetX = area_max_x - 1;
        }

        if (offsetY >= area_max_y) {
            offsetY = area_max_y - 1;
        }
        int k = (this.width - this.display_width) / 2;
        int l = (this.height - this.display_height) / 2;
        int i1 = k + 16;
        int j1 = l + 17;
        this.blitOffset = 0;
        GlStateManager.depthFunc(518);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) i1, (float) j1, -200.0F);
        GlStateManager.scalef(1.0F / this.zoomOut, 1.0F / this.zoomOut, 1.0F);
        GlStateManager.enableTexture();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        int k1 = offsetX + 288 >> 4;
        int l1 = offsetY + 288 >> 4;
        int i2 = (offsetX + display_width * 2) % 16;
        int j2 = (offsetY + 288) % 16;
        Random random = new Random();
        float f = 16.0F / this.zoomOut;
        float f1 = 16.0F / this.zoomOut;

        //Render background block textures
        for (int y = 0; (float) y * f - (float) j2 < 155.0F; ++y) {
            float f2 = 0.6F - (float) (l1 + y) / 25.0F * 0.3F;
            GlStateManager.color4f(f2, f2, f2, 1.0F);

            for (int x = 0; (float) x * f1 - (float) i2 < 224.0F; ++x) {
                random.setSeed((long) (this.minecraft.getSession().getPlayerID().hashCode() + k1 + x + (l1 + y) * 16));
                int j4 = random.nextInt(1 + l1 + y) + (l1 + y) / 2;
                TextureAtlasSprite textureatlassprite = this.getTexture(Blocks.SAND);

                if (j4 <= 37 && l1 + y != 35) {
                    if (j4 == 22) {
                        if (random.nextInt(2) == 0) {
                            textureatlassprite = this.getTexture(Blocks.COAL_BLOCK);
                        } else {
                            textureatlassprite = this.getTexture(Blocks.REDSTONE_BLOCK);
                        }
                    } else if (j4 == 10) {
                        textureatlassprite = this.getTexture(ModBlocks.castle_block_dark_brick_bloody);
                    } else if (j4 == 8) {
                        textureatlassprite = this.getTexture(Blocks.STONE_BRICKS);
                    } else if (j4 > 4) {
                        textureatlassprite = this.getTexture(ModBlocks.castle_block_normal_brick);
                    } else if (j4 > 0) {
                        textureatlassprite = this.getTexture(Blocks.DIRT);

                    }
                } else {
                    Block block = Blocks.BEDROCK;
                    textureatlassprite = this.getTexture(block);
                }

                this.minecraft.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
                this.blit(x * 16 - i2, y * 16 - j2, this.blitOffset, 16, 16, textureatlassprite);
            }
        }

        //Draw lines/arrows
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(515);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);

        for (SkillNode node : skillNodes) {
            if (node.getParent() != null) {
                int xs = findHorizontalNodeCenter(node) - offsetX + 11;
                int ys = node.getElements()[0].getRenderRow() * skill_width - offsetY + 11;

                int xp = findHorizontalNodeCenter(node.getParent()) - offsetX + 11;
                int yp = node.getParent().getElements()[0].getRenderRow() * skill_width - offsetY + 11;

                int unlockstate = skillHandler.isNodeEnabled(node) ? 0 : skillHandler.isNodeEnabled(node.getParent()) ? 1 : -1;
                int color = 0xff000000;
                if (unlockstate == 0) {
                    color = 0xffa0a0a0;
                } else if (unlockstate == 1) {
                    color = 0xff009900;
                }


                this.hLine(xs, xp, yp, color);
                this.vLine(xs, ys - 11, yp, color);
                if (ys > yp) {
                    //Currently always like this. The other option are here in case this changes at some point
                    this.blit(xs - 5, ys - 11 - 7, 96, 234, 11, 7);
                } else if (ys < yp) {
                    this.blit(xs - 5, ys + 11, 96, 241, 11, 7);
                } else if (xs > xp) {
                    this.blit(xs - 11 - 7, ys - 5, 114, 234, 7, 11);
                } else if (xs < xp) {
                    this.blit(xs + 11, ys - 5, 107, 234, 7, 11);
                }
            }
        }

        float mMouseX = (float) (mouseX - i1) * this.zoomOut;
        float mMouseY = (float) (mouseY - j1) * this.zoomOut;
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();

        //Draw skills
        ISkill newselected = null;//Not sure if mouse clicks can occur while this is running, so don't set #selected to null here but use a extra variable to be sure
        for (SkillNode node : skillNodes) {
            ISkill[] elements = node.getElements();
            if (elements.length > 1) {
                int minX = elements[0].getRenderColumn() * skill_width - offsetX;
                int maxX = elements[elements.length - 1].getRenderColumn() * skill_width - offsetX;
                int y = elements[0].getRenderRow() * skill_width - offsetY;
                if (maxX >= -24 && y >= -24 && (float) minX <= 224.0F * this.zoomOut && (float) y <= 155.0F * this.zoomOut) {
                    GlStateManager.enableBlend();
                    this.fillGradient(minX - 1, y - 1, maxX + 23, y + 23, 0xFF9B9DA1, 0xFF9B9DA1);
                    GlStateManager.disableBlend();
                }

            }
            for (int i = 0; i < elements.length; i++) {
                ISkill skill = elements[i];
                int x = skill.getRenderColumn() * skill_width - offsetX;
                int y = skill.getRenderRow() * skill_width - offsetY;

                if (x >= -24 && y >= -24 && (float) x <= 224.0F * this.zoomOut && (float) y <= 155.0F * this.zoomOut) {
                    int unlockstate = skillHandler.isSkillEnabled(skill) ? 0 : skillHandler.isNodeEnabled(node) ? -1 : skillHandler.canSkillBeEnabled(skill) == ISkillHandler.Result.OK ? 1 : 2;

                    if (unlockstate == 0) {
                        float f5 = 1F;
                        GlStateManager.color4f(f5, f5, f5, 1.0F);
                    } else if (unlockstate == 1) {
                        float f6 = 0.6F;
                        GlStateManager.color4f(f6, f6, f6, 1.0F);
                    } else if (unlockstate == 2) {
                        float f7 = 0.3F;
                        GlStateManager.color4f(f7, f7, f7, 1.0F);
                    } else if (unlockstate == -1) {
                        float f8 = 0.2F;
                        GlStateManager.color4f(f8, f8, f8, 1.0F);
                    }

                    this.minecraft.getTextureManager().bindTexture(BACKGROUND);

                    GlStateManager.enableBlend();
                    this.blit(x - 2, y - 2, 0, 202, 26, 26);
                    GlStateManager.disableBlend();

                    this.minecraft.getTextureManager().bindTexture(getIconLoc(skill));

                    GlStateManager.disableLighting();
                    //GlStateManager.enableCull();
                    GlStateManager.enableBlend();
                    UtilLib.drawTexturedModalRect(this.blitOffset, x + 3, y + 3, skill.getMinU(), skill.getMinV(), 16, 16, ICON_TEXTURE_WIDTH, ICON_TEXTURE_HEIGHT);
                    //GlStateManager.blendFunc(770, 771);
                    GlStateManager.disableLighting();


                    GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

                    if (mMouseX >= (float) x && mMouseX <= (float) (x + 22) && mMouseY >= (float) y && mMouseY <= (float) (y + 22)) {
                        newselected = skill;
                    }

                    if (i + 1 < elements.length) {
                        this.drawCenteredString(font, "OR", x + skill_width + skill_width / 2, y + 1 + (skill_width - font.FONT_HEIGHT) / 2, 0xFFFFFF);
                    }
                }
            }
        }


        GlStateManager.disableDepthTest();
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();

        //Draw "window" and buttons
        int color = skillHandler.getPlayer().getFaction().getColor();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;
        GlStateManager.color4f(r / 255F, g / 255F, b / 255F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        this.blit(k, l, 0, 0, this.display_width, this.display_height);
        this.blitOffset = 0;
        GlStateManager.depthFunc(515);
        GlStateManager.disableDepthTest();
        GlStateManager.enableTexture();
        super.render(mouseX, mouseY, partialTicks);

        //Draw information for selected skill
        selected = newselected;
        if (selected != null) {
            int m2MouseX = mouseX + 12;
            int m2MouseY = mouseY - 4;

            String name = I18n.format(selected.getTranslationKey());
            ITextComponent desc = selected.getDescription();
            ISkillHandler.Result result = skillHandler.canSkillBeEnabled(selected);

            int width_name = Math.max(this.font.getStringWidth(name), 110);
            int height_desc = desc == null ? 0 : font.getWordWrappedHeight(desc.getString(), width_name);

            if (result == ISkillHandler.Result.ALREADY_ENABLED || result == ISkillHandler.Result.PARENT_NOT_ENABLED) {
                height_desc += 12;
            }
            this.fillGradient(m2MouseX - 3, m2MouseY - 3, m2MouseX + width_name + 3, m2MouseY + height_desc + 3 + 12, -1073741824, -1073741824);

            this.font.drawStringWithShadow(name, (float) m2MouseX, (float) m2MouseY, 0xff808080);
            if (desc != null)
                this.font.drawSplitString(desc.toString(), m2MouseX, m2MouseY + 12, width_name, 0xff505050);
            if (result == ISkillHandler.Result.ALREADY_ENABLED) {
                this.font.drawStringWithShadow(I18n.format("text.vampirism.skill.unlocked"), m2MouseX, m2MouseY + height_desc + 3, 0xFFFBAE00);
            } else if (result == ISkillHandler.Result.PARENT_NOT_ENABLED) {
                this.font.drawStringWithShadow(I18n.format("text.vampirism.skill.unlock_parent_first"), m2MouseX, m2MouseY + height_desc + 3, 0xFFA32228);
            }
        }


        GlStateManager.enableDepthTest();
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
    }

    private int findHorizontalNodeCenter(SkillNode node) {
        int width = (node.getElements().length - 1) * 2 * skill_width;
        return node.getElements()[0].getRenderColumn() * skill_width + width / 2;
    }

    private ResourceLocation getIconLoc(ISkill skill) {
        return skill.getIconLoc() == null ? defaultIcons : skill.getIconLoc();
    }


    private TextureAtlasSprite getTexture(BlockState blockstate) {
        return Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockstate);
    }

    private TextureAtlasSprite getTexture(Block block) {
        return getTexture(block.getDefaultState());
    }

    private void playSoundEffect(SoundEvent event, float pitch) {
        minecraft.getSoundHandler().play(SimpleSound.master(event, 1.0F));
    }

}
