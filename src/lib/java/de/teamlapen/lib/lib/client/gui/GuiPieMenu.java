package de.teamlapen.lib.lib.client.gui;


import de.teamlapen.lib.LIBREFERENCE;
import de.teamlapen.lib.lib.util.UtilLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeIngameGui;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;


/**
 * Radial/pie menu which can be used as screen overlay to select stuff The angles (in radiant) used in this class are used to describe the direction from the screen center. 0/2Pi shows right, Pi/2 up
 * (!negative Y), Pi left ... (similar to the visualization of complex numbers.
 *
 * @author maxanier
 */
@OnlyIn(Dist.CLIENT)
public abstract class GuiPieMenu<T> extends Screen { //TODO 1.14 pie menu sometimes get rendered with 1F alpha !?
    private final static ResourceLocation backgroundTex = new ResourceLocation(LIBREFERENCE.MODID, "textures/gui/pie_menu_bg.png");
    private final static ResourceLocation centerTex = new ResourceLocation(LIBREFERENCE.MODID, "textures/gui/pie_menu_center.png");
    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
    protected final ArrayList<T> elements;

    protected final float bgred;
    protected final float bgblue;
    protected final float bggreen;
    protected final float bgalpha;
    /**
     * Size of the background image
     */
    private final int BGS = 300;
    /**
     * Radius of the ring in the middle
     */
    private final int RR = 60;
    /**
     * Size of the images for the center
     */
    private final int CS = 100;
    private int selectedElement = -1;
    private int elementCount;
    /**
     * Angle between each element in rad
     */
    private double radDiff;

    public GuiPieMenu(int backgroundColor, ITextComponent title) {
        super(title);
        this.passEvents = true;
        this.bgred = (backgroundColor >> 16 & 255) / 255.0F;
        this.bgblue = (backgroundColor >> 8 & 255) / 255.0F;
        this.bggreen = (backgroundColor & 255) / 255.0F;
        this.bgalpha = (backgroundColor >> 24 & 255) / 255.0F;
        this.elements = new ArrayList<>();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void init() {
        this.onGuiInit();
        this.elementCount = elements.size();
        radDiff = 2D * Math.PI / elementCount;// gap in rad
        GLFW.glfwSetInputMode(minecraft.mainWindow.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
        ForgeIngameGui.renderCrosshairs = false;
    }

    @Override
    public boolean keyReleased(int key, int scancode, int modifiers) {
        if (getMenuKeyBinding().matchesKey(key, scancode)) {
            onClose();
            if (selectedElement >= 0) {
                this.onElementSelected(elements.get(selectedElement));
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
        return false;
    }

    @Override
    public void removed() {
        super.removed();
        GLFW.glfwSetInputMode(Minecraft.getInstance().mainWindow.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        ForgeIngameGui.renderCrosshairs = true;
    }

    @Override
    public void onClose() {
        super.onClose();
        ForgeIngameGui.renderCrosshairs = true;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        // Calculate center and radius of the skill cycle
        int cX = this.width / 2;
        int cY = this.height / 2;
        double radius = this.height / 4;

        drawBackground(cX, cY);
        // Check if the mouse is in bounds and whether its in the center or not
        double mouseRad = updateMouse(mouseX, mouseY, cX, cY, radius / 2);
        boolean center = (mouseX - cX) * (mouseX - cX) + (mouseY - cY) * (mouseY - cY) < (radius / 4) * (radius / 4);
        if (center)
            selectedElement = -1;
        // Draw each skill
        for (int i = 0; i < elementCount; i++) {
            T element = elements.get(i);

            // Check if the mouse cursor is in the area of this element
            double rad = radDiff * i;
            boolean selected = false;
            if (!center && mouseRad > rad - radDiff / 2D && mouseRad < (rad + (radDiff / 2D))) {
                selected = true;
            } else if (!center && rad == 0 && mouseRad > 2D * Math.PI - radDiff / 2D) {
                selected = true;
            }
            int x = (int) (cX + Math.cos(rad) * radius) - 16 / 2;
            int y = (int) (cY - Math.sin(rad) * radius) - 16 / 2;

            // Draw box and, if selected, highlight
            float[] col = this.getColor(element);
            GL11.glColor4f(col[0], col[1], col[2], 0.5F);
            this.minecraft.getTextureManager().bindTexture(WIDGETS);
            blit(x - 2, y - 2, 1, 1, 20, 20);
            if (selected) {
                blit(x - 3, y - 3, 1, 23, 22, 22);
            }
            if (selected) {
                selectedElement = i;
                drawSelectedCenter(cX, cY, rad);
            }
            // Draw Icon
            GL11.glColor4f(1F, 1F, 1F, 1F);
            this.minecraft.getTextureManager().bindTexture(getIconLoc(element));
            UtilLib.drawTexturedModalRect(blitOffset, x, y, 0, 0, 16, 16, 16, 16);

            this.afterIconDraw(element, x, y);

        }
        if (selectedElement == -1) {
            this.drawUnselectedCenter(cX, cY);
        } else {
            String name = UtilLib.translate(getUnlocalizedName(elements.get(selectedElement)));
            int tx = cX - minecraft.fontRenderer.getStringWidth(name) / 2;
            int ty = this.height / 7;
            minecraft.fontRenderer.drawStringWithShadow(name, tx, ty, 16777215);
        }
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();
        this.minecraft.player.movementInput.tick(this.minecraft.player.shouldRenderSneaking(), this.minecraft.player.isSpectator());
    }

    protected void afterIconDraw(T element, int x, int y) {

    }

    /**
     * Draws a line between the given coordinates
     */
    protected void drawLine(double x1, double y1, double x2, double y2) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(0F, 0F, 0F, 1F);
        GL11.glLineWidth(2F);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(x1, y1, this.blitOffset);
        GL11.glVertex3d(x2, y2, this.blitOffset);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }

    /**
     * This method is called to retrieve the color for the elements border
     *
     * @param s
     * @return RGBA array
     */
    @Nonnull
    protected float[] getColor(T s) {
        return new float[]{1F, 1F, 1F, 1F};
    }

    /**
     * @param item
     * @return the location of the icon map where the icon for the given item is in
     */
    protected abstract ResourceLocation getIconLoc(T item);

    /**

     * @return the menu key binding set in the game settings
     */
    protected abstract KeyBinding getMenuKeyBinding();

    protected abstract String getUnlocalizedName(T item);

    protected void onElementSelected(T id) {

    }

    protected abstract void onGuiInit();

    /**
     * Draws the background cicle image as well as border lines between the different segments
     *
     * @param cX CenterX
     * @param cY CenterY
     */
    private void drawBackground(float cX, float cY) {
        // Calculate the scale which has to be applied for the image to fit
        float scale = (this.height / 2F + 16 + 16) / BGS;

        GL11.glPushMatrix();
        GL11.glTranslatef(cX, cY, this.blitOffset);
        GL11.glScalef(scale, scale, 1);

        // Draw the cicle image
        this.minecraft.getTextureManager().bindTexture(backgroundTex);
        GL11.glColor4f(this.bgred, this.bgblue, this.bggreen, this.bgalpha);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(1F, 1F);
        GL11.glVertex3f(BGS / 2, BGS / 2, this.blitOffset);
        GL11.glTexCoord2f(1F, 0F);
        GL11.glVertex3f(BGS / 2, -BGS / 2, this.blitOffset);
        GL11.glTexCoord2f(0F, 0F);
        GL11.glVertex3f(-BGS / 2, -BGS / 2, this.blitOffset);
        GL11.glTexCoord2f(0F, 1F);
        GL11.glVertex3f(-BGS / 2, BGS / 2, this.blitOffset);
        GL11.glEnd();

        // Draw the lines
        if (elementCount > 1) {
            for (int i = 0; i < elementCount; i++) {
                double rad = i * radDiff + radDiff / 2;
                double cos = Math.cos(rad);
                double sin = Math.sin(rad);
                this.drawLine(cos * RR, sin * RR, +cos * BGS / 2, sin * BGS / 2);
            }
        }
        GL11.glPopMatrix();

    }

    /**
     * Draws a circle with an arrow at the given coords
     *
     * @param cX
     * @param cY
     * @param rad The direction the arrow should point in radiant
     */
    private void drawSelectedCenter(double cX, double cY, double rad) {

        // Caluculate rotation and scale
        double deg = Math.toDegrees(-rad);
        float scale = (this.height) / 4F / CS;

        GL11.glPushMatrix();
        // Move origin to center, scale and rotate
        GL11.glTranslated(cX, cY, this.blitOffset);
        GL11.glScalef(scale, scale, 1);
        GL11.glRotated(deg, 0, 0, 1);

        // Draw
        this.minecraft.getTextureManager().bindTexture(centerTex);
        GL11.glColor4f(this.bgred, this.bgblue, this.bggreen, this.bgalpha);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.5F, 1F);
        GL11.glVertex3d(CS / 2, CS / 2, this.blitOffset);
        GL11.glTexCoord2f(0.5F, 0F);
        GL11.glVertex3d(CS / 2, -CS / 2, this.blitOffset);
        GL11.glTexCoord2f(0F, 0F);
        GL11.glVertex3d(-CS / 2, -CS / 2, this.blitOffset);
        GL11.glTexCoord2f(0F, 1F);
        GL11.glVertex3d(-CS / 2, CS / 2, this.blitOffset);
        GL11.glEnd();

        GL11.glPopMatrix();
    }

    private void drawUnselectedCenter(double cX, double cY) {

        float scale = (this.height) / 4F / CS;

        GL11.glPushMatrix();
        // Move origin to center, scale and rotate
        GL11.glTranslated(cX, cY, this.blitOffset);
        GL11.glScalef(scale, scale, 1);

        // Draw
        this.minecraft.getTextureManager().bindTexture(centerTex);
        GL11.glColor4f(this.bgred, this.bgblue, this.bggreen, this.bgalpha);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(1F, 1F);
        GL11.glVertex3d(CS / 2, CS / 2, this.blitOffset);
        GL11.glTexCoord2f(1F, 0F);
        GL11.glVertex3d(CS / 2, -CS / 2, this.blitOffset);
        GL11.glTexCoord2f(0.5F, 0F);
        GL11.glVertex3d(-CS / 2, -CS / 2, this.blitOffset);
        GL11.glTexCoord2f(0.5F, 1F);
        GL11.glVertex3d(-CS / 2, CS / 2, this.blitOffset);
        GL11.glEnd();

        GL11.glPopMatrix();
    }


    /**
     * Calculates the absolute mouse coordinates from the scaled ones and sets the cursor accordingly
     *
     * @param x
     * @param y
     */
    private void setAbsoluteMouse(double x, double y) {
        x = x * this.minecraft.mainWindow.getFramebufferWidth() / this.width;
        y = -(y + 1 - height) * this.minecraft.mainWindow.getFramebufferHeight() / height;
        GLFW.glfwSetCursorPos(this.minecraft.mainWindow.getHandle(), x, y);
    }

    /**
     * Checks if the mouse if the mouse cursor is to far from the center and moves it back if necessary
     *
     * @param x  MouseX
     * @param y  MouseY
     * @param cX CenterX
     * @param cY CenterY
     * @param r  Allowed distance
     * @return Angle/Direction of the mouse pointer as seen from the center
     */
    private double updateMouse(int x, int y, int cX, int cY, double r) {
        int dx = (x - cX);
        int dy = (y - cY);
        double rad = (Math.atan2(dy, -dx) + Math.PI);

        if (Math.abs(dx) > Math.abs(Math.cos(rad) * r) + 8 || Math.abs(dy) > Math.abs(Math.sin(rad) * r) + 8) {
            setAbsoluteMouse(dx / 1.5 + cX + 4, cY - dy / 1.5);
        }
        return rad;
    }
}