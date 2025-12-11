package de.teamlapen.vampirism.common.entity.player.lord.actions;

import de.teamlapen.factions.api.tags.FactionTags;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.common.actions.DefaultAction;
import de.teamlapen.factions.api.actions.IActionResult;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class LordRangeEffectAction<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends DefaultAction<T> {

    private final Holder<MobEffect> effect;

    public LordRangeEffectAction(Holder<MobEffect> effect) {
        this.effect = effect;
    }

    @Override
    protected @NotNull IActionResult activate(@NotNull T player, @NotNull ActivationContext context) {
        int lordLevel = FactionPlayerHandler.get(player.asEntity()).getLordLevel();
        List<LivingEntity> entitiesOfClass = player.asEntity().level().getEntitiesOfClass(LivingEntity.class, new AABB(player.asEntity().blockPosition()).inflate(10, 10, 10), e -> IFaction.is(player.getFaction(), VampirismAPI.factionRegistry().getFaction(e)));
        for (LivingEntity entity : entitiesOfClass) {
            if (entity instanceof Player && FactionPlayerHandler.get(((Player) entity)).getLordLevel() >= lordLevel) {
                continue;
            }
            entity.addEffect(new MobEffectInstance(effect, getEffectDuration(player), getEffectAmplifier(player)));
        }
        if (entitiesOfClass.isEmpty()) {
            return IActionResult.fail(Component.translatable("text.vampirism.action.lord_range.no_target"));
        } else {
            return IActionResult.SUCCESS;
        }
    }

    protected abstract int getEffectDuration(T player);

    @Override
    public int getCooldown(@NotNull T player) {
        return getEffectDuration(player);
    }

    protected int getEffectAmplifier(@NotNull T player) {
        return FactionPlayerHandler.get(player.asEntity()).getLordLevel() - 1;
    }

    @Override
    public @NotNull TagKey<IFaction<?>> factions() {
        return FactionTags.ALL_FACTIONS;
    }
}
