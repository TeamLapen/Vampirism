package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.UUID;


public class BatVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    private static final float BAT_HEIGHT = 0.8F;
    public final static float BAT_EYE_HEIGHT = 0.85F * BAT_HEIGHT;
    private static final float BAT_WIDTH = 0.6F;

    /**
     * Set the player's entity size to the bat size if it isn't already
     *
     * @param player
     */
    public static void updatePlayerBatSize(EntityPlayer player) {
        float width = BAT_WIDTH;
        float height = BAT_HEIGHT;
        if (player.isSneaking()) {
            height = BAT_HEIGHT - 0.15F;
        }
        if (player.isPlayerSleeping()) {
            height = 0.2F;
            width = 0.2F;
        }
        if (player.width != width || player.height != height) {
            AxisAlignedBB axisalignedbb = player.getEntityBoundingBox();
            axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double) width, axisalignedbb.minY + (double) height, axisalignedbb.minZ + (double) width);

            if (!player.worldObj.collidesWithAnyBlock(axisalignedbb)) {
                if (!VampirePlayer.get(player).setEntitySize(width, height)) return;
            }
        }
    }
    public final UUID healthModifierUUID = UUID.fromString("4392fccb-4bfd-4290-b2e6-5cc91429053c");
    private final float PLAYER_WIDTH = 0.6F;
    private final float PLAYER_HEIGHT = 1.8F;

    public BatVampireAction() {
        super(null);
    }

    @Override
    public boolean canBeUsedBy(IVampirePlayer vampire) {
        return !vampire.isGettingSundamage() && !vampire.getActionHandler().isActionActive(VampireActions.rageAction);
    }

    @Override
    public int getCooldown() {
        return 1;
    }

    @Override
    public int getDuration(int level) {
        return Integer.MAX_VALUE - 1;
    }

    @Override
    public int getMinU() {
        return 64;
    }

    @Override
    public int getMinV() {
        return 0;
    }

    @Override
    public String getUnlocalizedName() {
        return "action.vampirism.vampire.bat_skill";
    }

    @Override
    public boolean isEnabled() {
        return Balance.vpa.BAT_ENABLED;
    }

    @Override
    public boolean onActivated(IVampirePlayer vampire) {
        EntityPlayer player = vampire.getRepresentingPlayer();
        float oldMax = player.getMaxHealth();
        float oldHealth = player.getHealth();
        setModifier(player, true);
        float newMax = player.getMaxHealth();
        float mult = newMax / oldMax;
        float newHealth = mult * oldHealth;
        if (newHealth < 1) newHealth = 1;
        player.setHealth(newHealth);
        setPlayerBat(player, true);
        ((VampirePlayer) vampire).getSpecialAttributes().bat=true;
        return true;
    }

    @Override
    public void onActivatedClient(IVampirePlayer vampire) {
        setPlayerBat(vampire.getRepresentingPlayer(), true);
        ((VampirePlayer) vampire).getSpecialAttributes().bat=true;
    }

    @Override
    public void onDeactivated(IVampirePlayer vampire) {
        EntityPlayer player = vampire.getRepresentingPlayer();
        float oldMax = player.getMaxHealth();
        float oldHealth = player.getHealth();
        setModifier(player, false);
        float newMax = player.getMaxHealth();
        float mult = newMax / oldMax;
        float newHealth = mult * oldHealth;
        player.setHealth(newHealth);
        if (!player.onGround) {
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20, 100));
        }
        player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 60, 100));
        setPlayerBat(player, false);
        ((VampirePlayer) vampire).getSpecialAttributes().bat=false;
    }

    @Override
    public void onReActivated(IVampirePlayer vampire) {
        setModifier(vampire.getRepresentingPlayer(), true);
        setPlayerBat(vampire.getRepresentingPlayer(), true);
        ((VampirePlayer) vampire).getSpecialAttributes().bat=true;
    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        if (vampire.isGettingSundamage() && !vampire.isRemote()) {
            vampire.getRepresentingPlayer().addChatMessage(new TextComponentTranslation("text.vampirism.cant_fly_day"));
            return true;
        }
        return false;
    }

    private void setModifier(EntityPlayer player, boolean enabled) {
        if (enabled) {

            IAttributeInstance health = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            if (health.getModifier(healthModifierUUID) == null) {
                health.applyModifier(new AttributeModifier(healthModifierUUID, "Bat Health Reduction", -0.9, 2).setSaved(false));
            }

            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = true;
            player.sendPlayerAbilities();
        } else {

            // Health modifier
            IAttributeInstance health = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            AttributeModifier m = health.getModifier(healthModifierUUID);
            if (m != null) {
                health.removeModifier(m);
            }

            if (!player.capabilities.isCreativeMode) {
                player.capabilities.allowFlying = false;
            }
            player.capabilities.isFlying = false;
            player.sendPlayerAbilities();
        }

    }

    /**
     * Adjust the players size and eye height to fit to the bat model
     *
     * @param player
     * @param bat
     */
    private void setPlayerBat(EntityPlayer player, boolean bat) {
        if (bat) updatePlayerBatSize(player);
        if (bat) player.setPosition(player.posX, player.posY + (PLAYER_HEIGHT - BAT_HEIGHT), player.posZ);
        player.eyeHeight = (bat ? BAT_EYE_HEIGHT : player.getDefaultEyeHeight());
    }

}
