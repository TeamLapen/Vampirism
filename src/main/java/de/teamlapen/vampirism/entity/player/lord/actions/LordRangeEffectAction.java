package de.teamlapen.vampirism.entity.player.lord.actions;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionResult;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.IVampirismPlayer;
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
    protected IActionResult activate(@NotNull T player, ActivationContext context) {
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
    public int getCooldown(T player) {
        return getEffectDuration(player);
    }

    protected int getEffectAmplifier(@NotNull T player) {
        return ((IVampirismPlayer) player.asEntity()).getVampAtts().lordLevel - 1;
    }

    @Override
    public @NotNull TagKey<IFaction<?>> factions() {
        return VampirismTags.Factions.ALL_FACTIONS;
    }
}
