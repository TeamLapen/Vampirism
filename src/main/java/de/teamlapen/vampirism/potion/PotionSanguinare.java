package de.teamlapen.vampirism.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class PotionSanguinare extends VampirismPotion {
    public PotionSanguinare(ResourceLocation location, boolean badEffect, int potionColor) {
        super(location, badEffect, potionColor);
        setIconIndex(7, 1).setPotionName("potion.vampirism.sanguinare").registerPotionAttributeModifier(SharedMonsterAttributes.attackDamage, "22663B89-116E-49DC-9B6B-9971489B5BE5", 2.0D, 0);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldRenderInvText(PotionEffect effect) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
//        https://github.com/MinecraftForge/MinecraftForge/issues/2473
        String s1 = I18n.format(getName());

        mc.fontRendererObj.drawStringWithShadow(s1, (float) (x + 10 + 18), (float) (y + 6), 16777215);
        String s = "Unknown";
        mc.fontRendererObj.drawStringWithShadow(s, (float) (y + 10 + 18), (float) (y + 6 + 10), 8355711);

    }
}
