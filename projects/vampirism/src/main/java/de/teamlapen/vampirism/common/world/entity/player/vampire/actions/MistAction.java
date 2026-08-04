package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.tags.ModActionTags;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class MistAction extends FormAction {
    @Override
    public boolean isEnabled() {
        return ModConfig.balance().vaMistEnabled.getAsBoolean();
    }

    @Override
    protected IActionResult activateServer(IVampirePlayer player, ActivationContext context) {
        updatePlayer((VampirePlayer) player, true);
        updateAttributes(player.asEntity(), true);
        return IActionResult.SUCCESS;
    }

    @Override
    public @Nullable TagKey<IAction<?>> mutualExclusiveActionTag() {
        return ModActionTags.VAMPIRE_FORM_ACTIONS;
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return ModConfig.balance().vaMistCooldown.getAsInt();
    }

    @Override
    public int getDuration(IVampirePlayer player) {
        return ModConfig.balance().vaMistDuration.getAsInt();
    }

    @Override
    public void onActivatedClient(IVampirePlayer player) {
        updatePlayer((VampirePlayer) player, true);
    }

    @Override
    public void onDeactivated(IVampirePlayer player) {
        updatePlayer((VampirePlayer) player, false);
        updateAttributes(player.asEntity(), false);
    }

    @Override
    public void onReActivatedServer(IVampirePlayer player) {
        updatePlayer((VampirePlayer) player, true);
        updateAttributes(player.asEntity(), true);
    }

    private void updatePlayer(VampirePlayer vampire, boolean mist) {
        Player player = vampire.asEntity();

        vampire.getSkillProperties().mist = mist;
        player.setForcedPose(mist ? Pose.STANDING : null);
        player.refreshDimensions();
    }

    private void updateAttributes(Player player, boolean enabled) {
        Identifier key = ModRegistries.ACTIONS.getKey(this);
        if (key == null) {
            return;
        }

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            if (enabled) {
                speed.addOrReplacePermanentModifier(new AttributeModifier(key, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            } else {
                speed.removeModifier(key);
            }
        }
        AttributeInstance stepHeight = player.getAttribute(Attributes.STEP_HEIGHT);
        if (stepHeight != null) {
            if (enabled) {
                stepHeight.addOrReplacePermanentModifier(new AttributeModifier(key, 0.4, AttributeModifier.Operation.ADD_VALUE));
            } else {
                stepHeight.removeModifier(key);
            }
        }
        AttributeInstance gravity = player.getAttribute(Attributes.GRAVITY);
        if (gravity != null) {
            if (enabled) {
                gravity.addOrReplacePermanentModifier(new AttributeModifier(key, -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            } else {
                gravity.removeModifier(key);
            }
        }

    }
}
