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
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
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
        return !vampire.isGettingSundamage(vampire.getRepresentingEntity().level)
                && !ModItems.UMBRELLA.get().equals(vampire.getRepresentingEntity().getMainHandItem().getItem())
                && vampire.isGettingGarlicDamage(vampire.getRepresentingEntity().level) == EnumStrength.NONE
                && !vampire.getActionHandler().isActionActive(VampireActions.VAMPIRE_RAGE.get())
                && !vampire.getRepresentingPlayer().isInWater()
                && (VampirismConfig.SERVER.batModeInEnd.get() || !(vampire.getRepresentingPlayer().getCommandSenderWorld().dimension() == World.END))
                && !VampirismConfig.SERVER.batDimensionBlacklist.get().contains(vampire.getRepresentingPlayer().getCommandSenderWorld().dimension().location().toString())
                && (vampire.getRepresentingEntity().getVehicle() == null);
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
            player.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 20, 100, false, false));
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
        if (vampire.isGettingSundamage(vampire.getRepresentingEntity().level) && !vampire.isRemote()) {
            vampire.getRepresentingPlayer().sendMessage(new TranslationTextComponent("text.vampirism.cant_fly_day"), Util.NIL_UUID);
            return true;
        } else if (ModItems.UMBRELLA.get().equals(vampire.getRepresentingEntity().getMainHandItem().getItem()) && !vampire.isRemote()) {
            vampire.getRepresentingPlayer().sendMessage(new TranslationTextComponent("text.vampirism.cant_fly_umbrella"), Util.NIL_UUID);
            return true;
        } else if (vampire.isGettingGarlicDamage(vampire.getRepresentingEntity().level) != EnumStrength.NONE && !vampire.isRemote()) {
            vampire.getRepresentingEntity().sendMessage(new TranslationTextComponent("text.vampirism.cant_fly_garlic"), Util.NIL_UUID);
            return true;
        } else if (VampirismConfig.SERVER.batDimensionBlacklist.get().contains(vampire.getRepresentingPlayer().getCommandSenderWorld().dimension().location().toString()) || !VampirismConfig.SERVER.batModeInEnd.get() && vampire.getRepresentingPlayer().getCommandSenderWorld().dimension() == World.END) {
            vampire.getRepresentingPlayer().sendMessage(new TranslationTextComponent("text.vampirism.cant_fly_dimension"), Util.NIL_UUID);
            return true;
        } else {
            float exhaustion = VampirismConfig.BALANCE.vaBatExhaustion.get().floatValue();
            if(exhaustion>0)vampire.addExhaustion(exhaustion);
            return vampire.getRepresentingPlayer().isInWater();
        }
    }

    /**
     * Set's flightspeed capability
     */
    private void setFlightSpeed(PlayerEntity player, float speed) {
        player.abilities.flyingSpeed = speed;
    }

    private void setModifier(PlayerEntity player, boolean enabled) {
        if (enabled) {

            ModifiableAttributeInstance armorAttributeInst = player.getAttribute(Attributes.ARMOR);

            if (armorAttributeInst.getModifier(armorModifierUUID) == null) {
                armorAttributeInst.addPermanentModifier(new AttributeModifier(armorModifierUUID, "Bat Armor Disabled", -1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
            ModifiableAttributeInstance armorToughnessAttributeInst = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (armorToughnessAttributeInst.getModifier(armorToughnessModifierUUID) == null) {
                armorToughnessAttributeInst.addPermanentModifier(new AttributeModifier(armorToughnessModifierUUID, "Bat Armor Disabled", -1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }

            player.abilities.mayfly = true;
            player.abilities.flying = true;
            setFlightSpeed(player, VampirismConfig.BALANCE.vaBatFlightSpeed.get().floatValue());
        } else {
            // Health modifier
            ModifiableAttributeInstance armorAttributeInst = player.getAttribute(Attributes.ARMOR);
            AttributeModifier m = armorAttributeInst.getModifier(armorModifierUUID);
            if (m != null) {
                armorAttributeInst.removeModifier(m);
            }
            ModifiableAttributeInstance armorToughnessAttributeInst = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            AttributeModifier m2 = armorToughnessAttributeInst.getModifier(armorToughnessModifierUUID);
            if (m2 != null) {
                armorToughnessAttributeInst.removeModifier(m2);
            }
            boolean spectator = player.isSpectator();
            boolean creative = player.isCreative();
            player.abilities.mayfly = spectator || creative;
            player.abilities.flying = spectator;

            setFlightSpeed(player, 0.05F);
        }
        player.onUpdateAbilities();

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
        player.refreshDimensions();
        if (bat)
            player.setPos(player.getX(), player.getY() + (PLAYER_HEIGHT - BAT_SIZE.height), player.getZ());
    }

    @Override
    public boolean showHudDuration(PlayerEntity player) {
        return true;
    }

}
