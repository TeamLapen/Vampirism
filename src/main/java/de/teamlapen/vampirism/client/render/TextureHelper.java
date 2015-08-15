package de.teamlapen.vampirism.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to create vampire versions of textures by overlaying them with an overlay
 * 
 * @author Maxanier
 *
 */
@SideOnly(Side.CLIENT)
public class TextureHelper {

	/**
	 * Used instead of SimpleTexture for the vampire versions Does the image combining when loading the texture
	 * 
	 * @author Maxanier
	 *
	 */
	private static class VampireTexture extends AbstractTexture {
		protected final ResourceLocation textureLocation;
		protected final ResourceLocation overlayLocation;

		public VampireTexture(ResourceLocation loc, ResourceLocation overlay) {
			this.textureLocation = loc;
			this.overlayLocation = overlay;
		}

		@Override
		public void loadTexture(IResourceManager resManager) throws IOException {
			this.deleteGlTexture();
			InputStream inputstream = null;
			InputStream inputstreamOverlay = null;

			try {
				BufferedImage image = null;
				IResource iresource = null;
				if (this.textureLocation.getResourcePath().contains("skin")) {
					File f = getSkinFile(this.textureLocation);
					if (f.isFile()) {
						image = ImageIO.read(f);
					} else {
						Logger.w(TAG, "Did not find skin " + this.textureLocation + " which should be at " + f);
						iresource = resManager.getResource(AbstractClientPlayer.locationStevePng);
						inputstream = iresource.getInputStream();
						image = ImageIO.read(inputstream);
					}

				} else {
					iresource = resManager.getResource(this.textureLocation);
					inputstream = iresource.getInputStream();
					image = ImageIO.read(inputstream);

				}

				try {
					IResource iresource_overlay = resManager.getResource(this.overlayLocation);
					inputstreamOverlay = iresource_overlay.getInputStream();
					Image overlay = ImageIO.read(inputstreamOverlay);

					int w = image.getWidth();
					int h = image.getHeight();
					if (w != overlay.getWidth(null)) {
						overlay = overlay.getScaledInstance(w, -1, Image.SCALE_SMOOTH);
					}
					int oh=overlay.getHeight(null);
					if (h == oh||h==oh*2) {
						BufferedImage combined = new BufferedImage(w, oh, BufferedImage.TYPE_INT_ARGB);
						Graphics g = combined.getGraphics();
						g.drawImage(image, 0, 0, null);
						g.drawImage(overlay, 0, 0, null);
						image = combined;
					} else {
						Logger.w(TAG, "Overlay image does not have the same size/ratio as the original: " + overlayLocation + " for " + textureLocation);
					}

				} catch (Exception e) {
					Logger.e(TAG, e,"Failed to combine images " + overlayLocation + " and " + textureLocation);
				}

				boolean flag = false;
				boolean flag1 = false;

				if (iresource != null && iresource.hasMetadata()) {
					try {
						TextureMetadataSection texturemetadatasection = (TextureMetadataSection) iresource.getMetadata("texture");

						if (texturemetadatasection != null) {
							flag = texturemetadatasection.getTextureBlur();
							flag1 = texturemetadatasection.getTextureClamp();
						}
					} catch (RuntimeException runtimeexception) {
						Logger.e(TAG, "Failed reading metadata of: " + this.textureLocation, runtimeexception);
					}
				}

				TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), image, flag, flag1);
			} finally {
				if (inputstream != null) {
					inputstream.close();
				}

				if (inputstreamOverlay != null) {
					inputstreamOverlay.close();
				}
			}

		}

	}

	private static final String TAG = "TextureHelper";

	private final static ResourceLocation playerOverlay = new ResourceLocation(REFERENCE.MODID + ":textures/entity/playerOverlay.png");

	private final static Map<Class,ResourceLocation> overlays=new HashMap<Class, ResourceLocation>();

	static {
		overlays.put(EntityPlayer.class,playerOverlay);
	}
	/**
	 * Makes sure that the texture manager has a vampire version texture at the newLoc index
	 * 
	 * @param e
	 * @param old
	 *            Original/Vanilla texture location
	 * @param newLoc
	 *            New fake texture location
	 */
	public static void createVampireTexture(EntityLivingBase e, ResourceLocation old, ResourceLocation newLoc) {
		TextureManager manager = RenderManager.instance.renderEngine;
		if (manager.getTexture(newLoc) == null) {
			ResourceLocation overlay = getOverlay(e.getClass());
			ITextureObject texture = null;
			try {
				if (overlay!=null) {
					ITextureObject tex =  manager.getTexture(old);
					if(tex instanceof LayeredTexture){
						List l = ((LayeredTexture)tex).layeredTextureNames;
						l.add(overlay.toString());
						texture = new LayeredTexture(toStringArraySafe(l));
					}
					else{
						texture = new VampireTexture(old, overlay);
					}

				} else{
					texture = new SimpleTexture(old);
				}
			} catch (Exception e1) {
				Logger.e(TAG, "Failed to create overlayed texture object", e1);
				texture = manager.getTexture(old);
			}

			manager.loadTexture(newLoc, texture);
		}
	}

	/**
	 * Returns the resource location of the respective overlay
	 *
	 * @return Can be null if none is found
	 */
	private static ResourceLocation getOverlay(Class clazz) {
		if(clazz.equals(EntityCreature.class))return null;
		ResourceLocation loc=overlays.get(clazz);
		if(loc==null){
			return getOverlay(clazz.getSuperclass());
		}
		return loc;
	}


	public static void registerConvertedOverlay(Class<? extends EntityCreature> clazz,String loc){
		overlays.put(clazz,new ResourceLocation(loc));
	}
	/**
	 * Try's to get the real skin file which corresponds to the fake location
	 * 
	 * @param fakeLoc
	 * @return
	 */
	private static File getSkinFile(ResourceLocation fakeLoc) {
		String hash = fakeLoc.getResourcePath().replace("skins/", "");
		File dir = (File) Helper.Reflection.getPrivateFinalField(Minecraft.class, Minecraft.getMinecraft(), Helper.Obfuscation.getPosNames("Minecraft/fileAssets"));
		File sdir = new File(dir, "skins");
		File mdir = new File(sdir, hash.substring(0, 2));
		return new File(mdir, hash);
	}

	private static String[] toStringArraySafe(List list) {
		while (list.contains(null)) {
			list.remove(null);
		}
		return ((List<String>) list).toArray(new String[list.size()]);
	}

}
