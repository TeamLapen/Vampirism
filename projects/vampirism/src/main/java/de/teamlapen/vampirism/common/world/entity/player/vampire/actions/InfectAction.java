package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.world.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.core.ModStats;
import de.teamlapen.vampirism.common.util.UtilLib;
import de.teamlapen.vampirism.common.world.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;


public class InfectAction extends DefaultVampireAction {

    @Override
    public int getCooldown(IVampirePlayer player) {
        return 10;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    protected IActionResult activateServer(IVampirePlayer vampire, ActivationContext context) {
        Player player = vampire.asEntity();
        Entity creature = context.targetEntity().orElse(null);
        if (creature instanceof LivingEntity target) {
            if ((creature instanceof Player || creature instanceof AbstractVillager) &&  UtilLib.canReallySee(target, player, false)) {
                return IActionResult.fail(Component.translatable("message.vampirism.action.infect.sees_you"));
            }
            if(!deriveBiteableEntry(target).map(b -> b.tryInfect(vampire)).orElse(false)) {
                return IActionResult.fail(Component.translatable("message.vampirism.action.infect.can_not_infect"));
            }

            player.awardStat(ModStats.INFECTED_CREATURES.get());
            player.level().playSound(null, creature.getX(), creature.getY() + 1.5d, creature.getZ(), ModSounds.VAMPIRE_BITE.get(), SoundSource.PLAYERS, 1, 1);

            return IActionResult.SUCCESS;
        }
        return IActionResult.fail(Component.translatable("message.vampirism.action.infect.no_target"));
    }


    @Override
    public IActionResult canBeUsedBy(IVampirePlayer player) {
        if (player.asEntity().level().getDifficulty() == Difficulty.PEACEFUL) {
            return IActionResult.fail(Component.translatable("message.vampirism.action.infect.peaceful"));
        }
        if (player.isRemote()) {
            Entity target = VampirismMod.proxy.getMouseOverEntity();
            if (target instanceof LivingEntity living) {
                if (UtilLib.canReallySee(living, player.asEntity(), false)) {
                    return IActionResult.fail(Component.translatable("message.vampirism.action.infect.sees_you"));
                }
                if(!deriveBiteableEntry(target).map(b -> b.canBeInfected(player)).orElse(false)) {
                    return IActionResult.fail(Component.translatable("message.vampirism.action.infect.can_not_infect"));
                }
            } else {
                return IActionResult.fail(Component.translatable("message.vampirism.action.infect.no_target"));
            }
        }
        return IActionResult.SUCCESS;
    }

    private Optional<? extends IBiteableEntity> deriveBiteableEntry(Entity target) {
        return switch (target) {
            case IBiteableEntity iBiteableEntity -> Optional.of(iBiteableEntity);
            case PathfinderMob pathfinderMob -> ExtendedCreature.getSafe(target);
            case Player player -> Optional.of(VampirePlayer.get(player));
            default -> Optional.empty();
        };
    }
}
