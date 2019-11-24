package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.advancements.VampireActionTrigger;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.EndDimension;

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
    public static void updatePlayerBatSize(PlayerEntity player) {
        float width = BAT_WIDTH;
        float height = BAT_HEIGHT;
        if (player.isSneaking()) {
            height = BAT_HEIGHT - 0.15F;
        }
        if (player.isSleeping()) {
            height = 0.2F;
            width = 0.2F;
        }
        if (player.getWidth() != width || player.getHeight() != height) {
            AxisAlignedBB axisalignedbb = player.getBoundingBox();
            axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double) width, axisalignedbb.minY + (double) height, axisalignedbb.minZ + (double) width);

            if (!player.getEntityWorld().checkBlockCollision(axisalignedbb)) {
                if (!VampirePlayer.get(player).setEntitySize(width, height)) return;
            }
        }
    }

    public final UUID healthModifierUUID = UUID.fromString("4392fccb-4bfd-4290-b2e6-5cc91429053c");
    private final float PLAYER_WIDTH = 0.6F;
    private final float PLAYER_HEIGHT = 1.8F;

    public BatVampireAction() {
        super();
    }

    @Override
    public boolean activate(IVampirePlayer vampire) {
        PlayerEntity player = vampire.getRepresentingPlayer();
        float oldMax = player.getMaxHealth();
        float oldHealth = player.getHealth();
        setModifier(player, true);
        float newMax = player.getMaxHealth();
        float mult = newMax / oldMax;
        float newHealth = mult * oldHealth;
        if (newHealth < 1) newHealth = 1;
        player.setHealth(newHealth);
        setPlayerBat(player, true);
        ((VampirePlayer) vampire).getSpecialAttributes().bat = true;
        if (player instanceof ServerPlayerEntity) {
            ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger((ServerPlayerEntity) player, VampireActionTrigger.Action.BAT);
        }
        return true;
    }

    @Override
    public boolean canBeUsedBy(IVampirePlayer vampire) {
        return !vampire.isGettingSundamage(vampire.getRepresentingEntity().world) && !ModItems.umbrella.equals(vampire.getRepresentingEntity().getHeldItemMainhand().getItem()) && vampire.isGettingGarlicDamage(vampire.getRepresentingEntity().world) == EnumStrength.NONE && !vampire.getActionHandler().isActionActive(VampireActions.vampire_rage) && !vampire.getRepresentingPlayer().isInWater() && (VampirismConfig.SERVER.batModeInEnd.get() || !(vampire.getRepresentingPlayer().getEntityWorld().dimension instanceof EndDimension));
    }

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.vaBatCooldown.get() * 20 + 1;
    }

    @Override
    public int getDuration(int level) {
        return MathHelper.clamp(VampirismConfig.BALANCE.vaBatDuration.get(), 10, Integer.MAX_VALUE / 20 - 1) * 20;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaBatEnabled.get();
    }

    @Override
    public void onActivatedClient(IVampirePlayer vampire) {
        setPlayerBat(vampire.getRepresentingPlayer(), true);
        ((VampirePlayer) vampire).getSpecialAttributes().bat = true;
    }

    @Override
    public void onDeactivated(IVampirePlayer vampire) {
        PlayerEntity player = vampire.getRepresentingPlayer();
        float oldMax = player.getMaxHealth();
        float oldHealth = player.getHealth();
        setModifier(player, false);
        float newMax = player.getMaxHealth();
        float mult = newMax / oldMax;
        float newHealth = mult * oldHealth;
        player.setHealth(newHealth);
        if (!player.onGround) {
            player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 20, 100, false, false));
        }
        //player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20, 0, false, false));
        setPlayerBat(player, false);
        ((VampirePlayer) vampire).getSpecialAttributes().bat = false;
    }

    @Override
    public void onReActivated(IVampirePlayer vampire) {
        setModifier(vampire.getRepresentingPlayer(), true);
        setPlayerBat(vampire.getRepresentingPlayer(), true);
        ((VampirePlayer) vampire).getSpecialAttributes().bat = true;
    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        if (vampire.isGettingSundamage(vampire.getRepresentingEntity().world) && !vampire.isRemote()) {
            vampire.getRepresentingPlayer().sendMessage(new TranslationTextComponent("text.vampirism.cant_fly_day"));
            return true;
        } else if (ModItems.umbrella.equals(vampire.getRepresentingEntity().getHeldItemMainhand().getItem()) && !vampire.isRemote()) {
            vampire.getRepresentingPlayer().sendMessage(new TranslationTextComponent("text.vampirism.cant_fly_umbrella"));
            return true;
        } else if (vampire.isGettingGarlicDamage(vampire.getRepresentingEntity().world) != EnumStrength.NONE && !vampire.isRemote()) {
            vampire.getRepresentingEntity().sendMessage(new TranslationTextComponent("text.vampirism.cant_fly_garlic"));
            return true;
        } else if (!VampirismConfig.SERVER.batModeInEnd.get() && vampire.getRepresentingPlayer().getEntityWorld().dimension instanceof EndDimension) {
            vampire.getRepresentingPlayer().sendMessage(new TranslationTextComponent("text.vampirism.cant_fly_end"));
            return true;
        } else return vampire.getRepresentingPlayer().isInWater();
    }

    /**
     * Set's flightspeed capability
     */
    private void setFlightSpeed(PlayerEntity player, float speed) {
        player.abilities.flySpeed = speed;
    }

    private void setModifier(PlayerEntity player, boolean enabled) {
        if (enabled) {

            IAttributeInstance health = player.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
            if (health.getModifier(healthModifierUUID) == null) {
                health.applyModifier(new AttributeModifier(healthModifierUUID, "Bat Health Reduction", -VampirismConfig.BALANCE.vaBatHealthReduction.get(), AttributeModifier.Operation.MULTIPLY_TOTAL).setSaved(false));
            }

            player.abilities.allowFlying = true;
            player.abilities.isFlying = true;
            setFlightSpeed(player, (float) 0.03);
            player.sendPlayerAbilities();
        } else {

            // Health modifier
            IAttributeInstance health = player.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
            AttributeModifier m = health.getModifier(healthModifierUUID);
            if (m != null) {
                health.removeModifier(m);
            }

            if (!player.abilities.isCreativeMode) {
                player.abilities.allowFlying = false;
            }
            player.abilities.isFlying = false;
            setFlightSpeed(player, 0.05F);
            player.sendPlayerAbilities();
        }

    }

    /**
     * Adjust the players size and eye height to fit to the bat model
     *
     * @param player
     * @param bat
     */
    private void setPlayerBat(PlayerEntity player, boolean bat) {
        if (bat) updatePlayerBatSize(player);
        if (bat) player.setPosition(player.posX, player.posY + (PLAYER_HEIGHT - BAT_HEIGHT), player.posZ);
        player.eyeHeight = (bat ? BAT_EYE_HEIGHT : player.getStandingEyeHeight(player.getPose(), player.size));
    }

}
