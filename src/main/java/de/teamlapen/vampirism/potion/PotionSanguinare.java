package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;


public class PotionSanguinare extends VampirismPotion {
    public PotionSanguinare(ResourceLocation location, boolean badEffect, int potionColor) {
        super(location, badEffect, potionColor);
        setIconIndex(7, 1).setPotionName("potion.vampirism.sanguinare").registerPotionAttributeModifier(SharedMonsterAttributes.attackDamage, "22663B89-116E-49DC-9B6B-9971489B5BE5", 2.0D, 0);
    }

    /**
     * @param entity
     * @param player Whether to use the player effect duration or the mob duration
     */
    public static void addRandom(EntityLivingBase entity, boolean player) {
        int avgDuration = 20 * (player ? Balance.vp.SANGUINARE_AVG_DURATION : Balance.mobProps.SANGUINARE_AVG_DURATION);
        int duration = (int) ((entity.getRNG().nextFloat() + 0.5F) * avgDuration);
        entity.addPotionEffect(new PotionSanguinareEffect(ModPotions.sanguinare.id, duration));

    }

    @Override
    public boolean isReady(int duration, int p_76397_2_) {
        return duration == 1;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int p_76394_2_) {

        if (entity instanceof EntityCreature) {
            ExtendedCreature creature = ExtendedCreature.get((EntityCreature) entity);
            creature.makeVampire();
        }
        if (entity instanceof EntityPlayer) {
            VampirePlayer.get((EntityPlayer) entity).onSanguinareFinished();
        }
    }

//    @SideOnly(Side.CLIENT)
//    @Override
//    public boolean shouldRenderInvText(PotionEffect effect) {
//        return false;
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
////        https://github.com/MinecraftForge/MinecraftForge/issues/2473
//        String s1 = I18n.format(getName());
//
//        mc.fontRendererObj.drawStringWithShadow(s1, (float) (x + 10 + 18), (float) (y + 6), 16777215);
//        String s = "Unknown";
//        mc.fontRendererObj.drawStringWithShadow(s, (float) (y + 10 + 18), (float) (y + 6 + 10), 8355711);
//
//    }
}
