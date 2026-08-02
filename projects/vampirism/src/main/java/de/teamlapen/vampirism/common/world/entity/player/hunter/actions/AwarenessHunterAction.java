package de.teamlapen.vampirism.common.world.entity.player.hunter.actions;

import de.teamlapen.faction.api.factions.IFactionPredicate;
import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.faction.api.factions.actions.ILastingAction;
import de.teamlapen.faction.common.factions.FactionPredicate;
import de.teamlapen.faction.common.factions.FactionPredicates;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class AwarenessHunterAction extends DefaultHunterAction implements ILastingAction<IHunterPlayer> {

    private final Predicate<Entity> vampirePredicate = IFactionPredicate.builder(ModFactions.HUNTER).targetFaction(VampirismTags.Factions.IS_VAMPIRE).ignoreDisguise().build().forEntity();

    public AwarenessHunterAction() {
        super();
    }

    @Override
    public IActionResult canBeUsedBy(IHunterPlayer player) {
        return IActionResult.otherAction(player.getActionHandler(), HunterActions.DISGUISE_HUNTER);
    }

    @Override
    public int getCooldown(IHunterPlayer player) {
        return ModConfig.balance().haAwarenessCooldown.get();
    }

    @Override
    public int getDuration(IHunterPlayer player) {
        return ModConfig.balance().haAwarenessDuration.get();
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.balance().haAwarenessEnabled.get();
    }

    @Override
    public void onActivatedClient(IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    public void onDeactivated(IHunterPlayer player) {
        ((HunterPlayer) player).getSpecialAttributes().nearbyVampire(0);
    }

    @Override
    public void onReActivated(IHunterPlayer player) {
        onUpdate(player);
    }

    @Override
    protected IActionResult activate(IHunterPlayer player, ActivationContext context) {
        return IActionResult.SUCCESS;
    }

    @Override
    public boolean onUpdate(IHunterPlayer player) {
        if (player.asEntity().level().isClientSide() && player.asEntity().tickCount % 8 == 0) {
            double dist = nearbyVampire(player);
            double p = 0;
            if (dist != Double.MAX_VALUE) {
                p = 1f - (dist / (float) ModConfig.balance().haAwarenessRadius.get());
            }
            ((HunterPlayer) player).getSpecialAttributes().nearbyVampire(p);
        }
        return false;
    }

    private double nearbyVampire(IHunterPlayer hunter) {
        int r = ModConfig.balance().haAwarenessRadius.get();
        Player player = hunter.asEntity();
        var area = new AABB(player.getX() - r, player.getY() - r + 1, player.getZ() - r, player.getX() + r, player.getY() + r + 1, player.getZ() + r);
        return player.level().getEntities(player, area, vampirePredicate).stream().map(x -> x.distanceTo(player)).sorted().findFirst().orElse(Float.MAX_VALUE);
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }

    @Override
    public boolean showHudDuration(Player player) {
        return true;
    }

}