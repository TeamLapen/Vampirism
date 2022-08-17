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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class BatVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public final static float BAT_EYE_HEIGHT = 0.85F * 0.6f;
    public static final EntityDimensions BAT_SIZE = EntityDimensions.fixed(0.8f, 0.6f);

    private static final float PLAYER_WIDTH = 0.6F;
    private static final float PLAYER_HEIGHT = 1.8F;

    private final UUID armorModifierUUID = UUID.fromString("4392fccb-4bfd-4290-b2e6-5cc91439053c");
    private final UUID armorToughnessModifierUUID = UUID.fromString("6d3df16d-85e4-4b99-b2fc-301818697a6d");

    public BatVampireAction() {
        super();
    }

    @Override
    public boolean activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        Player player = vampire.getRepresentingPlayer();
        setModifier(player, true);
        updatePlayer((VampirePlayer) vampire, true);
        if (player instanceof ServerPlayer) {
            ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger((ServerPlayer) player, VampireActionTrigger.Action.BAT);
        }
        return true;
    }

    @Override
    public boolean canBeUsedBy(@NotNull IVampirePlayer vampire) {
        return !vampire.isGettingSundamage(vampire.getRepresentingEntity().level)
                && !ModItems.UMBRELLA.equals(vampire.getRepresentingEntity().getMainHandItem().getItem())
                && vampire.isGettingGarlicDamage(vampire.getRepresentingEntity().level) == EnumStrength.NONE
                && !vampire.getActionHandler().isActionActive(VampireActions.VAMPIRE_RAGE.get())
                && !vampire.getRepresentingPlayer().isInWater()
                && (VampirismConfig.SERVER.batModeInEnd.get() || !(vampire.getRepresentingPlayer().getCommandSenderWorld().dimension() == Level.END))
                && !VampirismConfig.SERVER.batDimensionBlacklist.get().contains(vampire.getRepresentingPlayer().getCommandSenderWorld().dimension().location().toString())
                && (vampire.getRepresentingEntity().getVehicle() == null);
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return VampirismConfig.BALANCE.vaBatCooldown.get() * 20 + 1;
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return Mth.clamp(VampirismConfig.BALANCE.vaBatDuration.get(), 10, Integer.MAX_VALUE / 20 - 1) * 20;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaBatEnabled.get();
    }

    @Override
    public void onActivatedClient(@NotNull IVampirePlayer vampire) {
        if (!((VampirePlayer) vampire).getSpecialAttributes().bat) {
            updatePlayer((VampirePlayer) vampire, true);
        }
    }

    @Override
    public void onDeactivated(@NotNull IVampirePlayer vampire) {
        Player player = vampire.getRepresentingPlayer();
        setModifier(player, false);
        if (!player.isOnGround()) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 100, false, false));
        }
        //player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20, 0, false, false));
        updatePlayer((VampirePlayer) vampire, false);
    }

    @Override
    public void onReActivated(@NotNull IVampirePlayer vampire) {
        setModifier(vampire.getRepresentingPlayer(), true);
        if (!((VampirePlayer) vampire).getSpecialAttributes().bat) {
            updatePlayer((VampirePlayer) vampire, true);
        }
    }

    @Override
    public boolean onUpdate(@NotNull IVampirePlayer vampire) {
        if (vampire.isGettingSundamage(vampire.getRepresentingEntity().level) && !vampire.isRemote()) {
            vampire.getRepresentingPlayer().sendSystemMessage(Component.translatable("text.vampirism.cant_fly_day"));
            return true;
        } else if (ModItems.UMBRELLA.get() == vampire.getRepresentingEntity().getMainHandItem().getItem() && !vampire.isRemote()) {
            vampire.getRepresentingPlayer().sendSystemMessage(Component.translatable("text.vampirism.cant_fly_umbrella"));
            return true;
        } else if (vampire.isGettingGarlicDamage(vampire.getRepresentingEntity().level) != EnumStrength.NONE && !vampire.isRemote()) {
            vampire.getRepresentingEntity().sendSystemMessage(Component.translatable("text.vampirism.cant_fly_garlic"));
            return true;
        } else if (VampirismConfig.SERVER.batDimensionBlacklist.get().contains(vampire.getRepresentingPlayer().getCommandSenderWorld().dimension().location().toString()) || !VampirismConfig.SERVER.batModeInEnd.get() && vampire.getRepresentingPlayer().getCommandSenderWorld().dimension() == Level.END) {
            vampire.getRepresentingPlayer().sendSystemMessage(Component.translatable("text.vampirism.cant_fly_dimension"));
            return true;
        } else {
            float exhaustion = VampirismConfig.BALANCE.vaBatExhaustion.get().floatValue();
            if (exhaustion > 0) vampire.addExhaustion(exhaustion);
            return vampire.getRepresentingPlayer().isInWater();
        }
    }

    /**
     * Set's flightspeed capability
     */
    private void setFlightSpeed(@NotNull Player player, float speed) {
        player.getAbilities().flyingSpeed = speed;
    }

    private void setModifier(@NotNull Player player, boolean enabled) {
        if (enabled) {

            AttributeInstance armorAttributeInst = player.getAttribute(Attributes.ARMOR);

            if (armorAttributeInst.getModifier(armorModifierUUID) == null) {
                armorAttributeInst.addPermanentModifier(new AttributeModifier(armorModifierUUID, "Bat Armor Disabled", -1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }
            AttributeInstance armorToughnessAttributeInst = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (armorToughnessAttributeInst.getModifier(armorToughnessModifierUUID) == null) {
                armorToughnessAttributeInst.addPermanentModifier(new AttributeModifier(armorToughnessModifierUUID, "Bat Armor Disabled", -1, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }

            player.getAbilities().mayfly = true;
            player.getAbilities().flying = true;
            setFlightSpeed(player, VampirismConfig.BALANCE.vaBatFlightSpeed.get().floatValue());
        } else {
            // Health modifier
            AttributeInstance armorAttributeInst = player.getAttribute(Attributes.ARMOR);
            AttributeModifier m = armorAttributeInst.getModifier(armorModifierUUID);
            if (m != null) {
                armorAttributeInst.removeModifier(m);
            }
            AttributeInstance armorToughnessAttributeInst = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            AttributeModifier m2 = armorToughnessAttributeInst.getModifier(armorToughnessModifierUUID);
            if (m2 != null) {
                armorToughnessAttributeInst.removeModifier(m2);
            }

            if (!player.getAbilities().instabuild) {
                player.getAbilities().mayfly = false;
            }
            player.getAbilities().flying = false;
            setFlightSpeed(player, 0.05F);
        }
        player.onUpdateAbilities();

    }

    /**
     * Adjust the players size and eye height to fit to the bat model
     */
    private void updatePlayer(@NotNull VampirePlayer vampire, boolean bat) {
        Player player = vampire.getRepresentingPlayer();
        vampire.getSpecialAttributes().bat = bat;
        player.setForcedPose(bat ? Pose.STANDING : null);
        //Eye height is set in {@link ModPlayerEventHandler} on {@link EyeHeight} event
        //Entity size is hacked in via {@link ASMHooks}
        player.refreshDimensions();
        if (bat)
            player.setPos(player.getX(), player.getY() + (PLAYER_HEIGHT - BAT_SIZE.height), player.getZ());
    }

    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }

}
