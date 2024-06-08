package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.advancements.critereon.VampireActionCriterionTrigger;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModAttachments;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
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
        Player player = vampire.asEntity();
        setModifier(player, true);
        updatePlayer((VampirePlayer) vampire, true);
        if (player instanceof ServerPlayer) {
            ModAdvancements.TRIGGER_VAMPIRE_ACTION.get().trigger((ServerPlayer) player, VampireActionCriterionTrigger.Action.BAT);
        }
        return true;
    }

    @Override
    public boolean canBeUsedBy(@NotNull IVampirePlayer vampire) {
        Player player = vampire.asEntity();
        return !vampire.isGettingSundamage(player.level())
                && ModItems.UMBRELLA.asItem() != player.getMainHandItem().getItem()
                && vampire.isGettingGarlicDamage(player.level()) == EnumStrength.NONE
                && !vampire.getActionHandler().isActionActive(VampireActions.VAMPIRE_RAGE)
                && !player.isInWater()
                && !VampirismConfig.SERVER.batDimensionBlacklist.get().contains(player.getCommandSenderWorld().dimension().location().toString())
                && (player.getVehicle() == null);
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
        Player player = vampire.asEntity();
        setModifier(player, false);
        if (!player.onGround()) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 100, false, false));
        }
        //player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20, 0, false, false));
        updatePlayer((VampirePlayer) vampire, false);
        player.removeData(ModAttachments.VAMPIRE_BAT);
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
        Player player = vampire.asEntity();
        if (vampire.isGettingSundamage(player.level()) && !vampire.isRemote()) {
            player.sendSystemMessage(Component.translatable("text.vampirism.cant_fly_day"));
            return true;
        } else if (ModItems.UMBRELLA.get() == player.getMainHandItem().getItem() && !vampire.isRemote()) {
            player.sendSystemMessage(Component.translatable("text.vampirism.cant_fly_umbrella"));
            return true;
        } else if (vampire.isGettingGarlicDamage(player.level()) != EnumStrength.NONE && !vampire.isRemote()) {
            player.sendSystemMessage(Component.translatable("text.vampirism.cant_fly_garlic"));
            return true;
        } else if (VampirismConfig.SERVER.batDimensionBlacklist.get().contains(player.getCommandSenderWorld().dimension().location().toString())) {
            player.sendSystemMessage(Component.translatable("text.vampirism.cant_fly_dimension"));
            return true;
        } else {
            float exhaustion = VampirismConfig.BALANCE.vaBatExhaustion.get().floatValue();
            if (exhaustion > 0) vampire.addExhaustion(exhaustion);
            return player.isInWater();
        }
    }

    /**
     * Set's flightspeed capability
     */
    private void setFlightSpeed(@NotNull Player player, float speed) {
        player.getAbilities().setFlyingSpeed(speed);
    }

    private void setModifier(@NotNull Player player, boolean enabled) {
        if (enabled) {

            AttributeInstance armorAttributeInst = player.getAttribute(Attributes.ARMOR);

            if (armorAttributeInst.getModifier(armorModifierUUID) == null) {
                armorAttributeInst.addPermanentModifier(new AttributeModifier(armorModifierUUID, "Bat Armor Disabled", -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
            AttributeInstance armorToughnessAttributeInst = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (armorToughnessAttributeInst.getModifier(armorToughnessModifierUUID) == null) {
                armorToughnessAttributeInst.addPermanentModifier(new AttributeModifier(armorToughnessModifierUUID, "Bat Armor Disabled", -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }

            player.getAbilities().mayfly = true;
            player.getAbilities().flying = true;
            setFlightSpeed(player, VampirismConfig.BALANCE.vaBatFlightSpeed.get().floatValue());
        } else {
            // Health modifier
            player.getAttribute(Attributes.ARMOR).removeModifier(armorModifierUUID);
            player.getAttribute(Attributes.ARMOR_TOUGHNESS).removeModifier(armorToughnessModifierUUID);

            boolean spectator = player.isSpectator();
            boolean creative = player.isCreative();
            player.getAbilities().mayfly = spectator || creative;
            player.getAbilities().flying = spectator;

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
        if (bat) {
            player.setPos(player.getX(), player.getY() + (PLAYER_HEIGHT - BAT_SIZE.height()), player.getZ());
        }
    }

    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }

}
