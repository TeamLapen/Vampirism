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
import de.teamlapen.vampirism.util.SharedMonsterAttributes;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.UUID;


public class BatVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public final static float BAT_EYE_HEIGHT = 0.85F * 0.6f;
    public static final EntitySize BAT_SIZE = EntitySize.fixed(0.8f, 0.6f);


    private final UUID armorModifierUUID = UUID.fromString("4392fccb-4bfd-4290-b2e6-5cc91439053c");
    private final UUID armorToughnessModifierUUID = UUID.fromString("6d3df16d-85e4-4b99-b2fc-301818697a6d");
    private final float PLAYER_WIDTH = 0.6F;
    private final float PLAYER_HEIGHT = 1.8F;

    public BatVampireAction() {
        super();
    }

    @Override
    public boolean activate(IVampirePlayer vampire) {
        PlayerEntity player = vampire.getRepresentingPlayer();
        setModifier(player, true);
        updatePlayer((VampirePlayer) vampire, true);
        if (player instanceof ServerPlayerEntity) {
            ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger((ServerPlayerEntity) player, VampireActionTrigger.Action.BAT);
        }
        return true;
    }

    @Override
    public boolean canBeUsedBy(IVampirePlayer vampire) {
        return !vampire.isGettingSundamage(vampire.getRepresentingEntity().world) && !ModItems.umbrella.equals(vampire.getRepresentingEntity().getHeldItemMainhand().getItem()) && vampire.isGettingGarlicDamage(vampire.getRepresentingEntity().world) == EnumStrength.NONE && !vampire.getActionHandler().isActionActive(VampireActions.vampire_rage) && !vampire.getRepresentingPlayer().isInWater() && (VampirismConfig.SERVER.batModeInEnd.get() || !(vampire.getRepresentingPlayer().getEntityWorld().getDimensionKey() == World.THE_END));
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
        if (!((VampirePlayer) vampire).getSpecialAttributes().bat) {
            updatePlayer((VampirePlayer) vampire, true);
        }
    }

    @Override
    public void onDeactivated(IVampirePlayer vampire) {
        PlayerEntity player = vampire.getRepresentingPlayer();
        setModifier(player, false);
        if (!player.isOnGround()) {
            player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 20, 100, false, false));
        }
        //player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20, 0, false, false));
        updatePlayer((VampirePlayer) vampire, false);
    }

    @Override
    public void onReActivated(IVampirePlayer vampire) {
        setModifier(vampire.getRepresentingPlayer(), true);
        if (!((VampirePlayer) vampire).getSpecialAttributes().bat) {
            updatePlayer((VampirePlayer) vampire, true);
        }
    }

    @Override
    public boolean onUpdate(IVampirePlayer vampire) {
        if (vampire.isGettingSundamage(vampire.getRepresentingEntity().world) && !vampire.isRemote()) {
            vampire.getRepresentingPlayer().sendMessage(new TranslationTextComponent("text.vampirism.cant_fly_day"), Util.DUMMY_UUID);
            return true;
        } else if (ModItems.umbrella.equals(vampire.getRepresentingEntity().getHeldItemMainhand().getItem()) && !vampire.isRemote()) {
            vampire.getRepresentingPlayer().sendMessage(new TranslationTextComponent("text.vampirism.cant_fly_umbrella"), Util.DUMMY_UUID);
            return true;
        } else if (vampire.isGettingGarlicDamage(vampire.getRepresentingEntity().world) != EnumStrength.NONE && !vampire.isRemote()) {
            vampire.getRepresentingEntity().sendMessage(new TranslationTextComponent("text.vampirism.cant_fly_garlic"), Util.DUMMY_UUID);
            return true;
        } else if (!VampirismConfig.SERVER.batModeInEnd.get() && vampire.getRepresentingPlayer().getEntityWorld().getDimensionKey() == World.THE_END) {
            vampire.getRepresentingPlayer().sendMessage(new TranslationTextComponent("text.vampirism.cant_fly_end"), Util.DUMMY_UUID);
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

            ModifiableAttributeInstance armorAttributeInst = player.getAttribute(SharedMonsterAttributes.ARMOR);
            if (armorAttributeInst.getModifier(armorModifierUUID) == null) {
                armorAttributeInst.applyPersistentModifier(new AttributeModifier(armorModifierUUID, "Bat Armor Disabled", -1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
            ModifiableAttributeInstance armorToughnessAttributeInst = player.getAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS);
            if (armorToughnessAttributeInst.getModifier(armorToughnessModifierUUID) == null) {
                armorToughnessAttributeInst.applyPersistentModifier(new AttributeModifier(armorToughnessModifierUUID, "Bat Armor Disabled", -1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }

            player.abilities.allowFlying = true;
            player.abilities.isFlying = true;
            setFlightSpeed(player, (float) 0.03);
            player.sendPlayerAbilities();
        } else {

            // Health modifier
            ModifiableAttributeInstance armorAttributeInst = player.getAttribute(SharedMonsterAttributes.ARMOR);
            AttributeModifier m = armorAttributeInst.getModifier(armorModifierUUID);
            if (m != null) {
                armorAttributeInst.removeModifier(m);
            }
            ModifiableAttributeInstance armorToughnessAttributeInst = player.getAttribute(SharedMonsterAttributes.ARMOR);
            AttributeModifier m2 = armorToughnessAttributeInst.getModifier(armorModifierUUID);
            if (m2 != null) {
                armorToughnessAttributeInst.removeModifier(m2);
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
     * @param bat
     */
    private void updatePlayer(VampirePlayer vampire, boolean bat) {
        PlayerEntity player = vampire.getRepresentingPlayer();
        vampire.getSpecialAttributes().bat = bat;
        player.setForcedPose(bat ? Pose.STANDING : null);
        //Eye height is set in {@link ModPlayerEventHandler} on {@link EyeHeight} event
        //Entity size is hacked in via {@link ASMHooks}
        player.recalculateSize();
        if (bat)
            player.setPosition(player.getPosX(), player.getPosY() + (PLAYER_HEIGHT - BAT_SIZE.height), player.getPosZ());
    }

}
