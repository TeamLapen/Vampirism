package de.teamlapen.vampirism.guide;

import amerifrance.guideapi.api.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.abstraction.EntryAbstract;
import amerifrance.guideapi.api.base.Book;
import amerifrance.guideapi.api.base.PageBase;
import amerifrance.guideapi.api.util.GuiHelper;
import amerifrance.guideapi.gui.GuiBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Max on 01.07.2015.
 */
public class PageScaledLocImage extends PageBase {

	private boolean drawAtTop;
	private ResourceLocation image;
	private String locText;
	private float scale;

	public PageScaledLocImage(boolean drawAtTop, ResourceLocation image, String locText, float scale) {
		this.drawAtTop = drawAtTop;
		this.image = image;
		this.locText = locText;
		this.scale = scale;
	}

	@Override public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRenderer) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(image);
		int ySize= (int) (guiBase.xSize/scale);
		if (drawAtTop) {
			GuiHelper.drawSizedIconWithoutColor(guiLeft + 50, guiTop + 12, guiBase.xSize, ySize, 0);

			fontRenderer.setUnicodeFlag(true);
			fontRenderer.drawSplitString(locText, guiLeft + 39, guiTop + 112, 3 * guiBase.xSize / 5, 0);
			fontRenderer.setUnicodeFlag(false);
		} else {
			GuiHelper.drawSizedIconWithoutColor(guiLeft + 50, guiTop + 60, guiBase.xSize, ySize, 0);

			fontRenderer.setUnicodeFlag(true);
			fontRenderer.drawSplitString(locText, guiLeft + 39, guiTop + 12, 3 * guiBase.xSize / 5, 0);
			fontRenderer.setUnicodeFlag(false);
		}
	}

	public PageScaledLocImage() {
		super();
	}

	@Override public int hashCode() {
		return super.hashCode();
	}
}
