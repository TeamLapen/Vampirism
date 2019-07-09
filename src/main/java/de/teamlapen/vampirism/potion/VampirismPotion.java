package de.teamlapen.vampirism.potion;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Base class for Vampirism's potions
 */
public class VampirismPotion extends Effect {

    private static final ResourceLocation ICONS = new ResourceLocation(REFERENCE.MODID, "textures/gui/potions.png");
    @OnlyIn(Dist.CLIENT)
    private static final int ICON_TEXTURE_WIDTH = 144;
    @OnlyIn(Dist.CLIENT)
    private static final int ICON_TEXTURE_HEIGHT = 36;

    public VampirismPotion(String name, boolean badEffect, int potionColor) {
        super(badEffect, potionColor);
        this.setRegistryName(REFERENCE.MODID, name);
    }


    @Override
    public boolean hasStatusIcon() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderHUDEffect(EffectInstance effect, AbstractGui gui, int x, int y, float z, float alpha) {
        int index = getStatusIconIndex();
        if (index >= 0) {
            Minecraft.getInstance().getTextureManager().bindTexture(ICONS);
            UtilLib.drawTexturedModalRect(0, x + 3, y + 3, index % 8 * 18, index / 8 * 18, 18, 18, ICON_TEXTURE_WIDTH, ICON_TEXTURE_HEIGHT);

        }

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderInventoryEffect(EffectInstance effect, AbstractGui gui, int x, int y, float z) {
        int index = getStatusIconIndex();
        if (index >= 0) {
            Minecraft.getInstance().getTextureManager().bindTexture(ICONS);
            UtilLib.drawTexturedModalRect(0, x + 6, y + 7, index % 8 * 18, index / 8 * 18, 18, 18, ICON_TEXTURE_WIDTH, ICON_TEXTURE_HEIGHT);
        }
    }

    @Override
    public VampirismPotion setIconIndex(int p_76399_1_, int p_76399_2_) {
        super.setIconIndex(p_76399_1_, p_76399_2_);
        return this;
    }
}
