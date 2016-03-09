package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillRegistry;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gui screen which displays the skills available to the players and allows him to unlock some
 */
public class GuiSkills extends GuiScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    private static final ResourceLocation defaultIcons = new ResourceLocation(REFERENCE.MODID, "textures/gui/skills.png");
    protected final int area_min_y = -112;
    protected final int skill_width = 24;
    private final List<SkillNode> skillNodes = new ArrayList<>();
    protected int display_width = 256;
    protected int display_height = 202;
    protected int area_min_x = 0;
    protected int area_max_x = 0;
    protected int area_max_y;
    protected int field_146563_h;
    protected int field_146564_i;
    protected float field_146570_r = 1.0F;
    protected double field_146569_s;
    protected double field_146568_t;
    protected double field_146567_u;
    protected double field_146566_v;
    protected double field_146565_w;
    protected double field_146573_x;
    private SkillHandler skillHandler;
    private boolean display;
    private int field_146554_D;

    public GuiSkills() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!display) {

            this.drawDefaultBackground();
            super.drawScreen(mouseX, mouseY, partialTicks);
            return;
        }
        if (Mouse.isButtonDown(0)) {
            int centerX = (this.width - this.display_width) / 2;
            int centerY = (this.height - this.display_height) / 2;
            int k = centerX + 8;
            int l = centerY + 17;

            if ((this.field_146554_D == 0 || this.field_146554_D == 1) && mouseX >= k && mouseX < k + 224 && mouseY >= l && mouseY < l + 155) {
                if (this.field_146554_D == 0) {
                    this.field_146554_D = 1;
                } else {
                    this.field_146567_u -= (double) ((float) (mouseX - this.field_146563_h) * this.field_146570_r);
                    this.field_146566_v -= (double) ((float) (mouseY - this.field_146564_i) * this.field_146570_r);
                    this.field_146565_w = this.field_146569_s = this.field_146567_u;
                    this.field_146573_x = this.field_146568_t = this.field_146566_v;
                }

                this.field_146563_h = mouseX;
                this.field_146564_i = mouseY;
            }
        } else {
            this.field_146554_D = 0;
        }

        int i1 = Mouse.getDWheel();
        float f3 = this.field_146570_r;

        if (i1 < 0) {
            this.field_146570_r += 0.25F;
        } else if (i1 > 0) {
            this.field_146570_r -= 0.25F;
        }

        this.field_146570_r = MathHelper.clamp_float(this.field_146570_r, 1.0F, 2.0F);

        if (this.field_146570_r != f3) {
            float f5 = f3 - this.field_146570_r;
            float f4 = f3 * (float) this.display_width;
            float f = f3 * (float) this.display_height;
            float f1 = this.field_146570_r * (float) this.display_width;
            float f2 = this.field_146570_r * (float) this.display_height;
            this.field_146567_u -= (double) ((f1 - f4) * 0.5F);
            this.field_146566_v -= (double) ((f2 - f) * 0.5F);
            this.field_146565_w = this.field_146569_s = this.field_146567_u;
            this.field_146573_x = this.field_146568_t = this.field_146566_v;
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

        this.drawDefaultBackground();
        this.drawSkills(mouseX, mouseY, partialTicks);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        this.drawTitle();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    @Override
    public void initGui() {
        IFactionPlayer factionPlayer = FactionPlayerHandler.get(mc.thePlayer).getCurrentFactionPlayer();
        if (factionPlayer != null) {
            display = true;
            skillHandler = (SkillHandler) factionPlayer.getSkillHandler();
            Integer[] info = ((SkillRegistry) VampirismAPI.skillRegistry()).getDisplayInfo(factionPlayer.getFaction());
            int w = info[0] * info[1] * skill_width * 2;
            area_max_x = w + 77;
            area_min_x = -w - 77;
            area_max_y = info[2] * skill_width * 2;
            skillNodes.clear();
            addToList(skillNodes, skillHandler.getRootNode());
        }
        this.buttonList.clear();
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 24, this.height / 2 + 74, 80, 20, I18n.format("gui.done")));
        if (display)
            this.buttonList.add(new GuiButton(2, (this.width - display_width) / 2 + 24, this.height / 2 + 74, 125, 20, I18n.format("text.vampirism.skill.resetall")));

    }

    public void updateScreen() {
        if (display) {
            this.field_146569_s = this.field_146567_u;
            this.field_146568_t = this.field_146566_v;
            double d0 = this.field_146565_w - this.field_146567_u;
            double d1 = this.field_146573_x - this.field_146566_v;

            if (d0 * d0 + d1 * d1 < 4.0D) {
                this.field_146567_u += d0;
                this.field_146566_v += d1;
            } else {
                this.field_146567_u += d0 * 0.85D;
                this.field_146566_v += d1 * 0.85D;
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            this.mc.displayGuiScreen(null);
        }
        if (button.id == 2) {
            skillHandler.disableAllSkills();
            skillHandler.enableRootSkill();
            //TODO make this "cost" something
        }
    }

    protected void drawTitle() {
        String title = I18n.format("text.vampirism.skills.gui_title");
        int i = (this.width - display_width) / 2;
        int j = (this.height - display_height) / 2;
        this.fontRendererObj.drawString(title, i + 15, j + 5, 4210752);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (ModKeys.getKeyCode(ModKeys.KEY.SKILL) == keyCode) {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        } else {
            super.keyTyped(typedChar, keyCode);
        }

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
        int offsetX = MathHelper.floor_double(this.field_146569_s + (this.field_146567_u - this.field_146569_s) * (double) partialTicks);
        int offsetY = MathHelper.floor_double(this.field_146568_t + (this.field_146566_v - this.field_146568_t) * (double) partialTicks);

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
        this.zLevel = 0.0F;
        GlStateManager.depthFunc(518);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) i1, (float) j1, -200.0F);
        GlStateManager.scale(1.0F / this.field_146570_r, 1.0F / this.field_146570_r, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        int k1 = offsetX + 288 >> 4;
        int l1 = offsetY + 288 >> 4;
        int i2 = (offsetX + 288) % 16;
        int j2 = (offsetY + 288) % 16;
        int k2 = 4;
        int l2 = 8;
        int i3 = 10;
        int j3 = 22;
        int k3 = 37;
        Random random = new Random();
        float f = 16.0F / this.field_146570_r;
        float f1 = 16.0F / this.field_146570_r;

        for (int l3 = 0; (float) l3 * f - (float) j2 < 155.0F; ++l3) {
            float f2 = 0.6F - (float) (l1 + l3) / 25.0F * 0.3F;
            GlStateManager.color(f2, f2, f2, 1.0F);

            for (int i4 = 0; (float) i4 * f1 - (float) i2 < 224.0F; ++i4) {
                random.setSeed((long) (this.mc.getSession().getPlayerID().hashCode() + k1 + i4 + (l1 + l3) * 16));
                int j4 = random.nextInt(1 + l1 + l3) + (l1 + l3) / 2;
                TextureAtlasSprite textureatlassprite = this.getTexture(Blocks.sand);

                if (j4 <= 37 && l1 + l3 != 35) {
                    if (j4 == 22) {
                        if (random.nextInt(2) == 0) {
                            textureatlassprite = this.getTexture(Blocks.diamond_ore);
                        } else {
                            textureatlassprite = this.getTexture(Blocks.redstone_ore);
                        }
                    } else if (j4 == 10) {
                        textureatlassprite = this.getTexture(Blocks.iron_ore);
                    } else if (j4 == 8) {
                        textureatlassprite = this.getTexture(Blocks.coal_ore);
                    } else if (j4 > 4) {
                        textureatlassprite = this.getTexture(ModBlocks.castleBlock);
                    } else if (j4 > 0) {
                        textureatlassprite = this.getTexture(Blocks.dirt);
                    }
                } else {
                    Block block = Blocks.bedrock;
                    textureatlassprite = this.getTexture(block);
                }

                this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                this.drawTexturedModalRect(i4 * 16 - i2, l3 * 16 - j2, textureatlassprite, 16, 16);
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        this.mc.getTextureManager().bindTexture(BACKGROUND);

        for (SkillNode node : skillNodes) {
            if (node.getParent() != null) {
                int xs = findHorizontalNodeCenter(node) - offsetX + 11;
                int ys = node.getElements()[0].getRenderRow() * skill_width * 2 - offsetY + 11;

                int xp = findHorizontalNodeCenter(node.getParent()) - offsetX + 11;
                int yp = node.getParent().getElements()[0].getRenderRow() * skill_width * 2 - offsetY + 11;

                int l4 = -16777216;


                this.drawHorizontalLine(xs, xp, yp, l4);
                this.drawVerticalLine(xs, ys - 11, yp, l4);
                if (ys > yp) {
                    //Currently always like this. The other option are here in case this changes at some point
                    this.drawTexturedModalRect(xs - 5, ys - 11 - 7, 96, 234, 11, 7);
                } else if (ys < yp) {
                    this.drawTexturedModalRect(xs - 5, ys + 11, 96, 241, 11, 7);
                } else if (xs > xp) {
                    this.drawTexturedModalRect(xs - 11 - 7, ys - 5, 114, 234, 7, 11);
                } else if (xs < xp) {
                    this.drawTexturedModalRect(xs + 11, ys - 5, 107, 234, 7, 11);
                }
            }
        }

        //TODO draw lines
        float mMouseX = (float) (mouseX - i1) * this.field_146570_r;
        float mMouseY = (float) (mouseY - j1) * this.field_146570_r;
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();

        ISkill selected = null;

        for (SkillNode node : skillNodes) {
            for (ISkill skill : node.getElements()) {
                int x = skill.getRenderColumn() * 2 * skill_width - offsetX;
                int y = skill.getRenderRow() * 2 * skill_width - offsetY;

                if (x >= -24 && y >= -24 && (float) x <= 224.0F * this.field_146570_r && (float) y <= 155.0F * this.field_146570_r) {
                    int unlockstate = skillHandler.isSkillEnabled(skill) ? 0 : skillHandler.isNodeEnabled(node) ? -1 : skillHandler.canSkillBeEnabled(skill) ? 1 : 2;

                    if (unlockstate == 0) {
                        float f5 = 0.75F;
                        GlStateManager.color(f5, f5, f5, 1.0F);
                    } else if (unlockstate == 1) {
                        float f6 = 1.0F;
                        GlStateManager.color(f6, f6, f6, 1.0F);
                    } else if (unlockstate == 2) {
                        float f7 = 0.3F;
                        GlStateManager.color(f7, f7, f7, 1.0F);
                    } else if (unlockstate == -1) {
                        float f8 = 0.2F;
                        GlStateManager.color(f8, f8, f8, 1.0F);
                    }

                    this.mc.getTextureManager().bindTexture(BACKGROUND);

                    GlStateManager.enableBlend(); // Forge: Specifically enable blend because it is needed here. And we fix Generic RenderItem's leakage of it.
                    this.drawTexturedModalRect(x - 2, y - 2, 0, 202, 26, 26);
                    GlStateManager.disableBlend(); //Forge: Cleanup states we set.

                    this.mc.getTextureManager().bindTexture(getIconLoc(skill));

                    GlStateManager.disableLighting(); //Forge: Make sure Lighting is disabled. Fixes MC-33065
                    GlStateManager.enableCull();
                    this.drawTexturedModalRect(x + 3, y + 3, skill.getMinU(), skill.getMinV(), 16, 16);
                    GlStateManager.blendFunc(770, 771);
                    GlStateManager.disableLighting();


                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                    if (mMouseX >= (float) x && mMouseX <= (float) (x + 22) && mMouseY >= (float) y && mMouseY <= (float) (y + 22)) {
                        selected = skill;
                    }
                }
            }
        }


        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexturedModalRect(k, l, 0, 0, this.display_width, this.display_height);
        this.zLevel = 0.0F;
        GlStateManager.depthFunc(515);
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (selected != null) {
            String name = selected.getUnlocalizedName();
            int m2MouseX = mouseX + 12;
            int m2MouseY = mouseY - 4;
            int j8 = Math.max(this.fontRendererObj.getStringWidth(name), 120);
            this.drawGradientRect(m2MouseX - 3, m2MouseY - 3, m2MouseX + j8 + 3, m2MouseY + 3 + 12, -1073741824, -1073741824);

            this.fontRendererObj.drawStringWithShadow(name, (float) m2MouseX, (float) m2MouseY, -8355712);
        }


        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
    }

    private int findHorizontalNodeCenter(SkillNode node) {
        int width = (node.getElements().length - 1) * skill_width * 2;
        return node.getElements()[0].getRenderColumn() * skill_width * 2 + width / 2;
    }

    private ResourceLocation getIconLoc(ISkill skill) {
        return skill.getIconLoc() == null ? defaultIcons : skill.getIconLoc();
    }

    private TextureAtlasSprite getTexture(Block block) {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(block.getDefaultState());
    }
}
