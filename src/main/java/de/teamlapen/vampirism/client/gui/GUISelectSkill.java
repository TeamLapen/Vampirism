package de.teamlapen.vampirism.client.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.KeyInputEventHandler;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.entity.player.skills.FakeSkill;
import de.teamlapen.vampirism.entity.player.skills.ISkill;
import de.teamlapen.vampirism.entity.player.skills.Skills;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * Used to select/activate/deactivate skills. The angles (in radiant) used in this class are used to describe the direction from the screen center. 0/2Pi shows right, Pi/2 up (!negative Y), Pi left
 * ... (similar to the visualization of complex numbers.
 * 
 * @author maxanier
 *
 */
@SideOnly(Side.CLIENT)
public class GUISelectSkill extends GuiScreen {

	private final static ResourceLocation backgroundTex = new ResourceLocation(REFERENCE.MODID + ":textures/gui/skill-bg.png");
	private final static ResourceLocation centerTex = new ResourceLocation(REFERENCE.MODID + ":textures/gui/skill-center.png");
	private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
	private ArrayList<ISkill> skills;
	private int skillCount;
	private VampirePlayer player;
	private int selectedSkill = -1;
	/**
	 * Icon width/height
	 */
	private final int IS = 16;
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
	/**
	 * Angle between each skill in rad
	 */
	private double radDiff;

	public GUISelectSkill() {
		this.allowUserInput = true;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	/**
	 * Draws the background cicle image as well as border lines between the different segments
	 * 
	 * @param cX
	 *            CenterX
	 * @param cY
	 *            CenterY
	 */
	private void drawBackground(float cX, float cY) {
		// Calculate the scale which has to be applied for the image to fit
		float scale = (this.height / 2F + IS + IS) / BGS;

		GL11.glPushMatrix();
		GL11.glTranslatef(cX, cY, this.zLevel);
		GL11.glScalef(scale, scale, 1);

		// Draw the cicle image
		this.mc.getTextureManager().bindTexture(backgroundTex);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(1F, 1F);
		GL11.glVertex3f(BGS / 2, BGS / 2, this.zLevel);
		GL11.glTexCoord2f(1F, 0F);
		GL11.glVertex3f(BGS / 2, -BGS / 2, this.zLevel);
		GL11.glTexCoord2f(0F, 0F);
		GL11.glVertex3f(-BGS / 2, -BGS / 2, this.zLevel);
		GL11.glTexCoord2f(0F, 1F);
		GL11.glVertex3f(-BGS / 2, BGS / 2, this.zLevel);
		GL11.glEnd();

		// Draw the lines
		if (skillCount > 1) {
			for (int i = 0; i < skillCount; i++) {
				double rad = i * radDiff + radDiff / 2;
				double cos = Math.cos(rad);
				double sin = Math.sin(rad);
				this.drawLine(cos * RR, sin * RR, +cos * BGS / 2, sin * BGS / 2);
			}
		}
		GL11.glPopMatrix();

	}

	/**
	 * Draws a line between the given coordinates
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	protected void drawLine(double x1, double y1, double x2, double y2) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(0F, 0F, 0F, 1F);
		GL11.glLineWidth(2F);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(x1, y1, this.zLevel);
		GL11.glVertex3d(x2, y2, this.zLevel);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopMatrix();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.mc.mcProfiler.startSection("vampirismSkills");
		// Calculate center and radius of the skill cycle
		int cX = this.width / 2;
		int cY = this.height / 2;
		double radius = this.height / 4;

		drawBackground(cX, cY);
		// Check if the mouse is in bounds and whether its in the center or not
		double mouseRad = updateMouse(mouseX, mouseY, cX, cY, radius / 2);
		boolean center = (mouseX - cX) * (mouseX - cX) + (mouseY - cY) * (mouseY - cY) < (radius / 4) * (radius / 4);
		if (center)
			selectedSkill = -1;
		// Draw each skill
		for (int i = 0; i < skillCount; i++) {
			ISkill s = skills.get(i);

			// Check if the mouse cursor is in the area of this skill
			double rad = radDiff * i;
			boolean selected = false;
			if (!center && mouseRad > rad - radDiff / 2D && mouseRad < (rad + (radDiff / 2D))) {
				selected = true;
			} else if (!center && rad == 0 && mouseRad > 2D * Math.PI - radDiff / 2D) {
				selected = true;
			}
			int x = (int) (cX + Math.cos(rad) * radius) - IS / 2;
			int y = (int) (cY - Math.sin(rad) * radius) - IS / 2;

			// Draw box and, if selected, highlight
			this.mc.getTextureManager().bindTexture(WIDGETS);
			drawTexturedModalRect(x - 2, y - 2, 1, 1, 20, 20);
			if (selected) {
				drawTexturedModalRect(x - 3, y - 3, 1, 23, 22, 22);
				selectedSkill = i;
				drawSelectedCenter(cX, cY, rad);

			}
			// Draw Icon
			this.mc.getTextureManager().bindTexture(s.getIconLoc());
			this.drawTexturedModalRect(x, y, s.getMinU(), s.getMinV(), IS, IS);

		}
		if (selectedSkill == -1) {
			this.drawUnselectedCenter(cX, cY);
		}
		this.mc.mcProfiler.endSection();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void initGui() {
		player = VampirePlayer.get(this.mc.thePlayer);
		skills = Skills.getAvailableSkills(player.getLevel());
		skills.add(new FakeSkill());
		skillCount = skills.size();
		radDiff = 2D * Math.PI / skillCount;// gap in rad
		// Disable cursor
		try {
			Mouse.setNativeCursor(new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null));
		} catch (LWJGLException e) {
			Logger.e("GuiSelectSkill", "Failed to set empty cursor", e);
		}
		GuiIngameForge.renderCrosshairs = false;
	}

	@Override
	public void onGuiClosed() {
		GuiIngameForge.renderCrosshairs = true;
		// Enable cursor
		try {
			Mouse.setNativeCursor(null);
		} catch (LWJGLException e) {
			Logger.e("GuiSelectSkill", "Could not reset cursor", e);
		}
	}

	/**
	 * Calculates the absolute mouse coordinates from the scaled ones and sets the cursor accordingly
	 * 
	 * @param x
	 * @param y
	 */
	private void setAbsoluteMouse(double x, double y) {
		x = x * this.mc.displayWidth / this.width;
		y = -(y + 1 - height) * this.mc.displayHeight / height;
		Mouse.setCursorPosition((int) x, (int) y);
	}

	/**
	 * Checks if the mouse if the mouse cursor is to far from the center and moves it back if necessary
	 * 
	 * @param x
	 *            MouseX
	 * @param y
	 *            MouseY
	 * @param cX
	 *            CenterX
	 * @param cY
	 *            CenterY
	 * @param r
	 *            Allowed distance
	 * @return Angle/Direction of the mouse pointer as seen from the center
	 */
	private double updateMouse(int x, int y, int cX, int cY, double r) {
		int dx = (x - cX);
		int dy = (y - cY);
		double rad = (Math.atan2(dy, -dx) + Math.PI);

		if (Math.abs(dx) > Math.abs(Math.cos(rad) * r) + 8 || Math.abs(dy) > Math.abs(Math.sin(rad) * r) + 8) {
			setAbsoluteMouse(dx / 1.5 + cX + 4, dy / 1.5 + cY);
		}
		return rad;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		this.mc.thePlayer.movementInput.updatePlayerMoveState();
		if (!KeyInputEventHandler.isKeyDown(KeyInputEventHandler.SKILL.getKeyCode())) {
			if (selectedSkill >= 0) {
				int id = skills.get(selectedSkill).getId();
				if (id >= 0) {
					VampirismMod.modChannel.sendToServer(new InputEventPacket(InputEventPacket.TOGGLESKILL, "" + id));
				}
			}

			this.mc.displayGuiScreen(null);
		}
	}

	private void drawSelectedCenter(double cX, double cY, double rad) {

		// Caluculate rotation and scale
		double deg = Math.toDegrees(-rad);
		float scale = (this.height) / 4F / CS;

		GL11.glPushMatrix();
		// Move origin to center, scale and rotate
		GL11.glTranslated(cX, cY, this.zLevel);
		GL11.glScalef(scale, scale, 1);
		GL11.glRotated(deg, 0, 0, 1);

		// Draw
		this.mc.getTextureManager().bindTexture(centerTex);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0.5F, 1F);
		GL11.glVertex3d(CS / 2, CS / 2, this.zLevel);
		GL11.glTexCoord2f(0.5F, 0F);
		GL11.glVertex3d(CS / 2, -CS / 2, this.zLevel);
		GL11.glTexCoord2f(0F, 0F);
		GL11.glVertex3d(-CS / 2, -CS / 2, this.zLevel);
		GL11.glTexCoord2f(0F, 1F);
		GL11.glVertex3d(-CS / 2, CS / 2, this.zLevel);
		GL11.glEnd();

		GL11.glPopMatrix();
	}

	private void drawUnselectedCenter(double cX, double cY) {

		float scale = (this.height) / 4F / CS;

		GL11.glPushMatrix();
		// Move origin to center, scale and rotate
		GL11.glTranslated(cX, cY, this.zLevel);
		GL11.glScalef(scale, scale, 1);

		// Draw
		this.mc.getTextureManager().bindTexture(centerTex);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(1F, 1F);
		GL11.glVertex3d(CS / 2, CS / 2, this.zLevel);
		GL11.glTexCoord2f(1F, 0F);
		GL11.glVertex3d(CS / 2, -CS / 2, this.zLevel);
		GL11.glTexCoord2f(0.5F, 0F);
		GL11.glVertex3d(-CS / 2, -CS / 2, this.zLevel);
		GL11.glTexCoord2f(0.5F, 1F);
		GL11.glVertex3d(-CS / 2, CS / 2, this.zLevel);
		GL11.glEnd();

		GL11.glPopMatrix();
	}

}
