package de.teamlapen.vampirism.guide;

import amerifrance.guideapi.api.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.abstraction.EntryAbstract;
import amerifrance.guideapi.api.base.Book;
import amerifrance.guideapi.api.base.PageBase;
import amerifrance.guideapi.api.util.TextHelper;
import amerifrance.guideapi.gui.GuiBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Book page containing a table and an optional headline
 * @author Maxanier
 *
 */
public class PageTable extends PageBase {
	private List<String[]> lines;
	/**
	 * Max char count in one cell for each column
	 */
	private int[] width;
	private String headline;
	private PageTable(List<String[]> lines,int[] width,String headline){
		this.lines=lines;
		this.width=width;
		this.headline=headline;
	}

	@Override
    @SideOnly(Side.CLIENT)
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRenderer) {
        fontRenderer.setUnicodeFlag(true);
        int charWidth=fontRenderer.getCharWidth(' ');
        int y=guiTop + 12;
        int x=guiLeft + 39;
        if(headline!=null){
        	fontRenderer.drawString(TextHelper.BOLD+headline, x, y, 0);
        	y+=fontRenderer.FONT_HEIGHT;
        }
		drawLine(x, y + fontRenderer.FONT_HEIGHT, x + (guiBase.xSize * 3F / 5F), y + fontRenderer.FONT_HEIGHT, guiBase.publicZLevel);
		for(String[] l:lines){
        	x=guiLeft + 39;
        	for(int i=0;i<l.length;i++){
        		int mw=width[i]*charWidth;
        		int aw=fontRenderer.getStringWidth(l[i]);
        		int dw=(mw-aw)/2;
        		fontRenderer.drawString(l[i], x+dw, y, 0);
        		x+=mw;
        	}
        	y+=fontRenderer.FONT_HEIGHT;
        	
        }
       
        fontRenderer.setUnicodeFlag(false);
    }

	public static class Builder{
		int columns;
		List<String[]> lines;
		String headline;
		public Builder(int columns){
			this.columns=columns;
			lines=new ArrayList<String[]>();
		}
		
		public Builder setHeadline(String s){
			headline=s;
			return this;
		}
		
		public Builder addLine(Object...objects){
			if(objects.length!=columns){
				throw new IllegalArgumentException("Every added line as to contain one String for every column");
			}
			String[] l=new String[objects.length];
			for(int i=0;i<objects.length;i++){
				l[i]=String.valueOf(objects[i]);
			}
			lines.add(l);
			return this;
		}
		
		public Builder addUnlocLine(String...strings){
			String[] loc=new String[strings.length];
			for(int i=0;i<strings.length;i++){
				loc[i]=StatCollector.translateToLocal(strings[i]);
			}
			return addLine((Object[])loc);
		}
		
		public PageTable build(){
			int[] width=new int[columns];
			for(int i=0;i<columns;i++){
				int max=0;
				for(String[] s:lines){
					int w=s[i].length();
					if(w>max)max=w;
				}
				width[i]=max;
			}
			return new PageTable(lines,width,headline);
		}
		
		
	}
	
	/**
	 * {@link GuiBase#drawRect(int, int, int, int, int)} does not work for me
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	protected void drawLine(double x1, double y1, double x2, double y2,float publicZLevel) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(0F, 0F, 0F, 1F);
		GL11.glLineWidth(2F);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(x1, y1, publicZLevel);
		GL11.glVertex3d(x2, y2, publicZLevel);
		GL11.glEnd();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glPopMatrix();
	}
}
