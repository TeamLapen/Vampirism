package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.Difficulty;

import java.util.Optional;


public class InfectAction extends DefaultVampireAction {

    @Override
    public int getCooldown() {
        return 10;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean canBeUsedBy(IVampirePlayer player) {
        if (player.getRepresentingPlayer().level.getDifficulty() == Difficulty.PEACEFUL) return false;
        if(player.isRemote()){
            Entity target = VampirismMod.proxy.getMouseOverEntity();
            if(target != null && !( (target instanceof PlayerEntity || target instanceof VillagerEntity) && UtilLib.canReallySee((LivingEntity) target, player.getRepresentingPlayer(), false)) && (player.getRepresentingPlayer().distanceTo(target) <= (player.getRepresentingPlayer().getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue()/2f) + 1)){
                return deriveBiteableEntry(target).map(b->b.canBeInfected(player)).orElse(false);
            }
            return false;
        }
        return true;
    }

    @Override
    protected boolean activate(IVampirePlayer vampire, ActivationContext context) {
        PlayerEntity player = vampire.getRepresentingPlayer();
        Entity creature = context.targetEntity().filter(LivingEntity.class::isInstance).filter(target -> {
            if ((target instanceof PlayerEntity || target instanceof VillagerEntity) && UtilLib.canReallySee((LivingEntity) target, player, false)) {
                return false;
            }
            if (player.distanceTo(target) <= (player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue()/2f) + 1) {
                return deriveBiteableEntry(target).map(e -> e.tryInfect(vampire)).orElse(false);
            }
            return false;
        }).orElse(null);

        if (creature != null) {
            player.awardStat(ModStats.infected_creatures);
            player.level.playSound(null, creature.getX(), creature.getY() + 1.5d, creature.getZ(), ModSounds.PLAYER_BITE.get(), SoundCategory.PLAYERS, 1, 1);
        } else {
            player.level.playSound(null, vampire.getRepresentingPlayer().getX(), vampire.getRepresentingPlayer().getY() + 1.5d, vampire.getRepresentingPlayer().getZ(), SoundEvents.NOTE_BLOCK_BANJO, SoundCategory.PLAYERS, 1, 1);
        }
        return creature != null;
    }

    private Optional<? extends IBiteableEntity> deriveBiteableEntry(Entity target){
        if (target instanceof IBiteableEntity) {
            return Optional.of((IBiteableEntity) target);
        } else if (target instanceof CreatureEntity) {
            return ExtendedCreature.getSafe(target).resolve();
        } else if (target instanceof PlayerEntity) {
            return VampirePlayer.getOpt((PlayerEntity) target).resolve();
        }
        return Optional.empty();
    }
}
