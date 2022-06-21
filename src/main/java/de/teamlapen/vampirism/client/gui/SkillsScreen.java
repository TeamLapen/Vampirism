package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.CSimpleInputEvent;
import de.teamlapen.vampirism.network.CUnlockSkillPacket;
import de.teamlapen.vampirism.player.skills.ActionSkill;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.player.skills.SkillNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.List;
import java.util.*;

/**
 * Gui screen which displays the skills available to the players and allows him to unlock some.
 * Inspired by Minecraft's old GuiAchievement
 *
 * @deprecated unused
 */
@Deprecated
@OnlyIn(Dist.CLIENT)
public class SkillsScreen extends Screen { //TODO BREAKING remove
    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/skills_window.png");
    private final int area_min_y = -77;
    private final int skill_width = 24;
    private final List<SkillNode> skillNodes = new ArrayList<>();
    private final int display_width = 256;
    private final int display_height = 202;
    @Nullable
    private final Screen backScreen;
    private final Map<ISkill, List<ITextComponent>> skillToolTipsCache = new HashMap<>();
    private int area_min_x = 0;
    private int area_max_x = 0;
    private int area_max_y;
    private float zoomOut = 1.0F;
    private double displayX;
    private double displayY;
    private double displayXNew;
    private double displayYNew;
    private SkillHandler<?> skillHandler;
    private boolean display;
    private ISkill selected;
    private SkillNode selectedNode;
    private int displayXWidth;
    private int displayYHeight;
    @Nullable
    private ITextComponent lordTitle;
    private int lordLevel;
    private Button resetSkills;

    public SkillsScreen() {
        this(null);
    }

    public SkillsScreen(@Nullable Screen backScreen) {
        super(new TranslationTextComponent("screen.vampirism.skills"));
        this.width = display_width;
        this.height = display_height;
        this.backScreen = backScreen;
    }

    @Override
    public void init() {
        int guiLeft = (this.width - display_width) / 2;
        int guiTop = (this.height - display_height) / 2;
        if (this.backScreen != null) {
            this.addButton(new Button(guiLeft + 5, guiTop + 175, 80, 20, new TranslationTextComponent("gui.back"), (context) -> {
                this.minecraft.setScreen(this.backScreen);
            }));
        }
        this.addButton(new Button(guiLeft + 171, guiTop + 175, 80, 20, new TranslationTextComponent("gui.done"), (context) -> {
            this.minecraft.setScreen(null);
        }));
        FactionPlayerHandler.getOpt(minecraft.player).ifPresent(fph -> {
            lordTitle = fph.getLordTitle();
            lordLevel = fph.getLordLevel();
            fph.getCurrentFactionPlayer().ifPresent(factionPlayer -> {
                IPlayableFaction<?> faction = factionPlayer.getFaction();
                display = true;
                skillHandler = (SkillHandler<?>) factionPlayer.getSkillHandler();
                Integer[] info = VampirismMod.proxy.getSkillTree(true).getDisplayInfo(faction.getID());
                int w = info[0] * info[1] * skill_width * 2;
                area_max_x = w + 10 - display_width;
                area_min_x = -w - 10 - display_width;
                area_max_y = info[2] * skill_width * 2;
                this.displayX = displayXNew = -100;
                this.displayY = displayYNew = -10;
                skillNodes.clear();
                SkillNode root = VampirismMod.proxy.getSkillTree(true).getRootNodeForFaction(faction.getID());
                addToList(skillNodes, root);

                boolean test = VampirismMod.inDev || VampirismMod.instance.getVersionInfo().getCurrentVersion().isTestVersion();

                resetSkills = this.addButton(new Button(guiLeft + 88, guiTop + 175, 80, 20, new TranslationTextComponent("text.vampirism.skill.resetall"), (context) -> {
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

        this.displayXWidth = this.skillNodes.stream().flatMap(node -> Arrays.stream(node.getElements())).mapToInt(ISkill::getRenderColumn).max().orElse(0) * 25;
        this.displayYHeight = this.skillNodes.stream().flatMap(node -> Arrays.stream(node.getElements())).mapToInt(ISkill::getRenderRow).max().orElse(0) * 20;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (p_keyPressed_1_ == 256 || ModKeys.getKeyBinding(ModKeys.KEY.SKILL).getKey().getValue() == p_keyPressed_1_) {
            this.minecraft.setScreen(null);
            this.minecraft.setWindowActive(true);
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
            this.unlockSkill();
            return true;
        }
        return retur;
    }

    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        displayY -= p_mouseDragged_8_;
        displayX -= p_mouseDragged_6_;
        checkDisplay();
        return true;
    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
        zoomOut += p_mouseScrolled_5_ > 0 ? -0.25 : 0.25;
        zoomOut = MathHelper.clamp(this.zoomOut, 1.0F, 2.0F);
        checkDisplay();
        return true;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (!display) {
            super.render(stack, mouseX, mouseY, partialTicks);
            return;
        }

        this.renderBackground(stack);


        this.drawSkills(stack, mouseX, mouseY, partialTicks);

        this.drawDisableText(stack);

        this.drawTitle(stack);
    }

    public void resetToolTipCache() {
        skillToolTipsCache.clear();
    }

    @Override
    public void tick() {
        if (!this.minecraft.player.isAlive()) {
            this.minecraft.player.closeContainer();
        }
    }

    protected void drawTitle(MatrixStack stack) {
        ITextComponent title;
        if (lordTitle != null) {
            title = lordTitle.copy().append(" (" + lordLevel + ")");
        } else {
            title = new TranslationTextComponent("text.vampirism.skills.gui_title");
        }
        int x = (this.width - display_width) / 2;
        int y = (this.height - display_height) / 2;
        this.font.drawShadow(stack, title.getVisualOrderText(), x + 15, y + 5, 0xFFFFFFFF);
        IFormattableTextComponent points = new TranslationTextComponent("text.vampirism.skills.points_left", skillHandler.getLeftSkillPoints());
        if (this.minecraft.player.getEffect(ModEffects.OBLIVION.get()) != null) {
            points.withStyle(TextFormatting.DARK_RED);
        }
        x = (this.width + display_width) / 2 - this.font.width(points);
//        this.font.drawText(stack, points, x - 15, y + 5, 0xFFFFFFFF);
        this.font.drawShadow(stack, points.getVisualOrderText(), x - 15, y + 5, 0xFFFFFFFF);
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

    private boolean canUnlockSkill() {
        return skillHandler.canSkillBeEnabled(selected) == ISkillHandler.Result.OK;
    }

    private void checkDisplay() {
        displayY = MathHelper.clamp(displayY, -20 / zoomOut, (this.displayYHeight - 20) / zoomOut);
        displayX = MathHelper.clamp(displayX, (-400 - displayXWidth) / zoomOut + (zoomOut - 2.0F) * (-1) * 250, (-400 + displayXWidth) / zoomOut + (zoomOut - 2.0F) * (-1) * 250);
        displayXNew = displayX;
        displayYNew = displayY;
    }

    private void drawDisableText(MatrixStack mStack) {
        if (this.minecraft.player.getEffect(ModEffects.OBLIVION.get()) == null) return;
        int tooltipX = (this.width - this.display_width) / 2 + 19 + 3;
        int tooltipY = (this.height - this.display_height) / 2 + 4 + 19;
        int tooltipTextWidth = this.display_width - 19 - 19 - 6;
        int tooltipHeight = 17;
        int backgroundColor = 0xF0aa0808;//0xF0550404;;
        int borderColorStart = 0x505f0c0c;
        int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
        int zLevel = this.getBlitOffset();

        mStack.pushPose();
        Matrix4f mat = mStack.last().pose();
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
        GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

        IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        mStack.translate(0.0D, 0.0D, zLevel);

        ITextComponent f = new TranslationTextComponent("text.vampirism.skill.unlock_unavailable").withStyle(TextFormatting.WHITE);

        IReorderingProcessor s = LanguageMap.getInstance().getVisualOrder(f);


        font.drawInBatch(s, (float) tooltipX + (tooltipTextWidth / 2) - this.font.width(f) / 2, (float) tooltipY + (tooltipHeight / 2) - 3, -1, true, mat, renderType, false, 0, 15728880);

        renderType.endBatch();
        mStack.popPose();
    }

    private void drawSkills(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
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

        //Limit render area
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        double scale = minecraft.getWindow().getGuiScale();
        GL11.glScissor((int) (k * scale), (int) (l * scale),
                (int) (display_width * scale), (int) (display_height * scale));

        this.setBlitOffset(0);
        RenderSystem.depthFunc(518);
        stack.pushPose();
        stack.translate(i1, j1, -200);
        stack.scale(1 / this.zoomOut, 1 / this.zoomOut, 1);
        RenderSystem.enableTexture();
        RenderSystem.disableLighting();
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableColorMaterial();
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
            RenderSystem.color4f(f2, f2, f2, 1.0F);

            for (int x = 0; (float) x * f1 - (float) i2 < 224.0F; ++x) {
                random.setSeed(this.minecraft.getUser().getUuid().hashCode() + k1 + x + (l1 + y) * 16);
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
                        textureatlassprite = this.getTexture(ModBlocks.CASTLE_BLOCK_DARK_BRICK_BLOODY.get());
                    } else if (j4 == 8) {
                        textureatlassprite = this.getTexture(Blocks.STONE_BRICKS);
                    } else if (j4 > 4) {
                        textureatlassprite = this.getTexture(ModBlocks.CASTLE_BLOCK_NORMAL_BRICK.get());
                    } else if (j4 > 0) {
                        textureatlassprite = this.getTexture(Blocks.DIRT);

                    }
                } else {
                    Block block = Blocks.BEDROCK;
                    textureatlassprite = this.getTexture(block);
                }

                this.minecraft.getTextureManager().bind(AtlasTexture.LOCATION_BLOCKS);
                blit(stack, x * 16 - i2, y * 16 - j2, this.getBlitOffset(), 16, 16, textureatlassprite);
            }
        }

        //Draw lines/arrows
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        this.minecraft.getTextureManager().bind(BACKGROUND);

        for (SkillNode node : skillNodes) {
            if (node.getParent() != null) {
                int xs = findHorizontalNodeCenter(node) - offsetX + 11;
                int ys = node.getElements()[0].getRenderRow() * skill_width - offsetY + 11;

                int xp = findHorizontalNodeCenter(node.getParent()) - offsetX + 11;
                int yp = node.getParent().getElements()[0].getRenderRow() * skill_width - offsetY + 11;

                int color = 0xff000000;
                if (skillHandler.isNodeEnabled(node)) {
                    color = 0xffa0a0a0;
                } else if (skillHandler.isSkillNodeLocked(node)) {
                    color = 0xff990000;
                } else if (skillHandler.isNodeEnabled(node.getParent())) {
                    color = 0xff009900;
                }
                this.hLine(stack, xs, xp, yp, color);
                this.vLine(stack, xs, ys - 11, yp, color);
                if (ys > yp) {
                    //Currently always like this. The other option are here in case this changes at some point
                    this.blit(stack, xs - 5, ys - 11 - 7, 96, 234, 11, 7);
                } else if (ys < yp) {
                    this.blit(stack, xs - 5, ys + 11, 96, 241, 11, 7);
                } else if (xs > xp) {
                    this.blit(stack, xs - 11 - 7, ys - 5, 114, 234, 7, 11);
                } else if (xs < xp) {
                    this.blit(stack, xs + 11, ys - 5, 107, 234, 7, 11);
                }
            }
        }

        float mMouseX = (float) (mouseX - i1) * this.zoomOut;
        float mMouseY = (float) (mouseY - j1) * this.zoomOut;
        RenderHelper.turnBackOn(); //enableStandardGUIItemLighting
        RenderSystem.disableLighting();
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableColorMaterial();

        //Draw skills
        ISkill newSelected = null;//Not sure if mouse clicks can occur while this is running, so don't set #selected to null here but use a extra variable to be sure
        SkillNode newSelectedNode = null;
        for (SkillNode node : skillNodes) {
            ISkill[] elements = node.getElements();
            if (elements.length > 1) {
                int minX = elements[0].getRenderColumn() * skill_width - offsetX;
                int maxX = elements[elements.length - 1].getRenderColumn() * skill_width - offsetX;
                int y = elements[0].getRenderRow() * skill_width - offsetY;
                if (maxX >= -skill_width && y >= -skill_width && (float) minX <= 224.0F * this.zoomOut && (float) y <= 155.0F * this.zoomOut) {
                    RenderSystem.enableBlend();
                    this.fillGradient(stack, minX - 1, y - 1, maxX + 23, y + 23, 0xFF9B9DA1, 0xFF9B9DA1);
                    RenderSystem.disableBlend();
                }

            }
            for (int i = 0; i < elements.length; i++) {
                ISkill skill = elements[i];
                int x = skill.getRenderColumn() * skill_width - offsetX;
                int y = skill.getRenderRow() * skill_width - offsetY;

                if (x >= -skill_width && y >= -skill_width && (float) x <= 224.0F * this.zoomOut && (float) y <= 155.0F * this.zoomOut) {

                    if (skillHandler.isSkillEnabled(skill)) {
                        float f5 = 1F;
                        RenderSystem.color4f(f5, f5, f5, 1.0F);
                    } else if (skillHandler.canSkillBeEnabled(skill) == ISkillHandler.Result.OK) {
                        float f6 = 0.6F;
                        RenderSystem.color4f(f6, f6, f6, 1.0F);
                    } else if (skillHandler.isNodeEnabled(node)) {
                        float f8 = 0.2F;
                        RenderSystem.color4f(f8, f8, f8, 1.0F);
                    } else {
                        float f7 = 0.3F;
                        RenderSystem.color4f(f7, f7, f7, 1.0F);
                    }

                    this.minecraft.getTextureManager().bind(BACKGROUND);

                    RenderSystem.enableBlend();
                    this.blit(stack, x - 2, y - 2, node.getLockingNodes().length == 0 ? 0 : 26, 202, 26, 26);
                    RenderSystem.disableBlend();

                    this.minecraft.getTextureManager().bind(getIconLoc(skill));

                    RenderSystem.disableLighting();
                    //GlStateManager.enableCull();
                    RenderSystem.enableBlend();
                    UtilLib.drawTexturedModalRect(stack.last().pose(), this.getBlitOffset(), x + 3, y + 3, 0, 0, 16, 16, 16, 16);
                    //GlStateManager.blendFunc(770, 771);
                    RenderSystem.disableLighting();


                    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

                    if (mMouseX >= (float) x && mMouseX <= (float) (x + 22) && mMouseY >= (float) y && mMouseY <= (float) (y + 22)) {
                        newSelected = skill;
                        newSelectedNode = node;
                    }

                    if (i + 1 < elements.length) {
                        drawCenteredString(stack, this.font, "OR", x + skill_width + skill_width / 2, y + 1 + (skill_width - this.font.lineHeight) / 2, 0xFFFFFF);
                    }
                }
            }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        stack.popPose();

        //Draw "window" and buttons
        Color color = skillHandler.getPlayer().getFaction().getColor();
        RenderSystem.color4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1.0F);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        this.blit(stack, k, l, 0, 0, this.display_width, this.display_height);
        this.setBlitOffset(0);
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
        super.render(stack, mouseX, mouseY, partialTicks);

        //Don't render skill tooltip when hovering over button
        for (Widget button : this.buttons) {
            if (button.isHovered()) {
                newSelected = null;
                newSelectedNode = null;
            }
        }
        //Draw information for selected skill
        selected = newSelected;
        selectedNode = newSelectedNode;
        if (selected != null) {
            stack.pushPose();
            stack.translate(0, 0, 1); //Render tooltips in front of buttons

            List<ITextComponent> tooltips = skillToolTipsCache.computeIfAbsent(selected, (skill) -> new ArrayList<>());

            if (tooltips.isEmpty()) {
                ITextComponent name = selected.getName().plainCopy().withStyle(TextFormatting.GRAY);
                ITextComponent desc = selected.getDescription();

                tooltips.add(name);
                if (desc != null) {
                    tooltips.add(desc.copy().withStyle(TextFormatting.DARK_GRAY));
                }

                ISkillHandler.Result result = skillHandler.canSkillBeEnabled(selected);

                List<ISkill> lockingSkills = null;
                TextFormatting lockingColor = TextFormatting.BLACK;
                if (selectedNode.getLockingNodes().length != 0) {
                    lockingSkills = skillHandler.getLockingSkills(selectedNode);
                    lockingColor = result == ISkillHandler.Result.ALREADY_ENABLED ? TextFormatting.DARK_GRAY : lockingSkills.stream().anyMatch(skill -> skillHandler.isSkillEnabled(skill)) ? TextFormatting.DARK_RED : TextFormatting.YELLOW;
                }
                if (lockingSkills != null) {
                    tooltips.add(new TranslationTextComponent("text.vampirism.skill.excluding").withStyle(lockingColor));
                    for (ISkill lockingSkill : lockingSkills) {
                        tooltips.add(new StringTextComponent("  ").append(lockingSkill.getName().copy().withStyle(lockingColor)));
                    }
                }

                if (result == ISkillHandler.Result.ALREADY_ENABLED) {
                    tooltips.add(new TranslationTextComponent("text.vampirism.skill.unlocked").withStyle(TextFormatting.GOLD));
                } else if (result == ISkillHandler.Result.PARENT_NOT_ENABLED) {
                    tooltips.add(new TranslationTextComponent("text.vampirism.skill.unlock_parent_first").withStyle(TextFormatting.DARK_RED));
                }
            }
            int width_name = Math.max(this.font.width(tooltips.get(0)), 110);

            GuiUtils.drawHoveringText(stack, tooltips, mouseX, mouseY, width, height, width_name, -1073741824, -1073741824, -1073741824, this.font);

            stack.popPose();
        }


        RenderSystem.enableDepthTest();
        RenderSystem.enableLighting();
        RenderHelper.turnOff();

    }

    private int findHorizontalNodeCenter(SkillNode node) {
        int width = (node.getElements().length - 1) * 2 * skill_width;
        return node.getElements()[0].getRenderColumn() * skill_width + width / 2;
    }

    private ResourceLocation getIconLoc(ISkill skill) {
        if (skill instanceof ActionSkill) {
            return new ResourceLocation(((ActionSkill) skill).getActionID().getNamespace(), "textures/actions/" + ((ActionSkill) skill).getActionID().getPath() + ".png");
        } else {
            return new ResourceLocation(skill.getRegistryName().getNamespace(), "textures/skills/" + skill.getRegistryName().getPath() + ".png");
        }
    }


    private TextureAtlasSprite getTexture(BlockState blockstate) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(blockstate);
    }

    private TextureAtlasSprite getTexture(Block block) {
        return getTexture(block.defaultBlockState());
    }

    private void playSoundEffect(SoundEvent event, float pitch) {
        minecraft.getSoundManager().play(SimpleSound.forUI(event, 1.0F));
    }

    private void unlockSkill() {
        if (canUnlockSkill()) {
            VampirismMod.dispatcher.sendToServer(new CUnlockSkillPacket(selected.getRegistryName()));
            playSoundEffect(SoundEvents.PLAYER_LEVELUP, 0.7F);
        } else {
            playSoundEffect(SoundEvents.NOTE_BLOCK_BASS, 0.5F);
        }
    }

}
