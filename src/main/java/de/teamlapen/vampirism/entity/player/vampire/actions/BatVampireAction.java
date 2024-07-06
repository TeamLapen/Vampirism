package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.advancements.critereon.VampireActionCriterionTrigger;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.entity.player.actions.IActionResult;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModAttachments;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class BatVampireAction extends DefaultVampireAction implements ILastingAction<IVampirePlayer> {

    public final static float BAT_EYE_HEIGHT = 0.85F * 0.6f;
    public static final EntityDimensions BAT_SIZE = EntityDimensions.fixed(0.8f, 0.6f);

    private static final float PLAYER_WIDTH = 0.6F;
    private static final float PLAYER_HEIGHT = 1.8F;

    public BatVampireAction() {
        super();
    }

    @Override
    public IActionResult activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        Player player = vampire.asEntity();
        setModifier(player, true);
        updatePlayer((VampirePlayer) vampire, true);
        if (player instanceof ServerPlayer) {
            ModAdvancements.TRIGGER_VAMPIRE_ACTION.get().trigger((ServerPlayer) player, VampireActionCriterionTrigger.Action.BAT);
        }
        return IActionResult.SUCCESS;
    }

    @Override
    public @NotNull IActionResult canBeUsedBy(@NotNull IVampirePlayer vampire) {
        Player player = vampire.asEntity();
        if (vampire.isGettingSundamage(player.level())) {
            return IActionResult.fail(Component.translatable("text.vampirism.action.bat.in_sun"));
        } else if (ModItems.UMBRELLA.get() == player.getMainHandItem().getItem()) {
            return IActionResult.fail(Component.translatable("text.vampirism.action.bat.has_umbrella"));
        } else if (vampire.isGettingGarlicDamage(player.level()) != EnumStrength.NONE) {
            return IActionResult.fail(Component.translatable("text.vampirism.action.bat.effected_by_garlic"));
        } else if (VampirismConfig.SERVER.batDimensionBlacklist.get().contains(player.getCommandSenderWorld().dimension().location().toString())) {
            return IActionResult.fail(Component.translatable("text.vampirism.action.bat.dimension"));
        } else if (vampire.getActionHandler().isActionActive(VampireActions.VAMPIRE_RAGE)) {
            return IActionResult.fail(Component.translatable("text.vampirism.action.other_action", Component.translatable(Util.makeDescriptionId("action", VampireActions.VAMPIRE_RAGE.getId()))));
        } else if (player.isInWater()) {
            return IActionResult.fail(Component.translatable("text.vampirism.action.bat.in_water"));
        } else if (player.getVehicle() != null) {
            return IActionResult.fail(Component.translatable("text.vampirism.action.bat.in_vehicle"));
        } else {
            return IActionResult.SUCCESS;
        }
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
        ResourceLocation key = ModRegistries.ACTIONS.getKey(this);
        AttributeInstance fly = vampire.asEntity().getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        if (fly != null && !fly.hasModifier(key)) {
            fly.addPermanentModifier(new AttributeModifier(key, 1, AttributeModifier.Operation.ADD_VALUE));
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
        ResourceLocation key = ModRegistries.ACTIONS.getKey(this);
        if (key == null) {
            return;
        }
        if (enabled) {
            AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
            if (armor != null && !armor.hasModifier(key)) {
                armor.addPermanentModifier(new AttributeModifier(key, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
            AttributeInstance armorToughness = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (armorToughness != null && !armorToughness.hasModifier(key)) {
                armorToughness.addPermanentModifier(new AttributeModifier(key, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
            AttributeInstance fly = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
            if (fly != null && !fly.hasModifier(key)) {
                fly.addPermanentModifier(new AttributeModifier(key, 1, AttributeModifier.Operation.ADD_VALUE));
            }

            setFlightSpeed(player, VampirismConfig.BALANCE.vaBatFlightSpeed.get().floatValue());
        } else {
            Objects.requireNonNull(player.getAttribute(Attributes.ARMOR)).removeModifier(key);
            Objects.requireNonNull(player.getAttribute(Attributes.ARMOR_TOUGHNESS)).removeModifier(key);
            Objects.requireNonNull(player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT)).removeModifier(key);

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
