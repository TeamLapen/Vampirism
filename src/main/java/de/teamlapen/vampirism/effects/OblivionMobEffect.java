package de.teamlapen.vampirism.effects;

import de.teamlapen.lib.lib.util.LogUtil;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import de.teamlapen.vampirism.misc.VampirismLogger;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OblivionMobEffect<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends VampirismMobEffect {

    private static final Logger LOGGER = LogManager.getLogger();

    public OblivionMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) return true;

        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, getTickDuration(amplifier), 5, false, false, false, null));
        return FactionPlayerHandler.get(player).<T>getSkillHandler().map(handler -> {
            var nodeOPT = ((SkillHandler<?>) handler).anyLastNode();
            if (nodeOPT.isPresent()) {
                for (Holder<ISkill<?>> element : nodeOPT.get().getValue().skills()) {
                    //noinspection unchecked
                    handler.disableSkill((Holder<ISkill<T>>) (Object) element, nodeOPT.get().getKey());
                    player.awardStat(ModStats.SKILL_FORGOTTEN.get().get(element.value()));
                }
                return true;
            } else {
                ((Player) entity).displayClientMessage(Component.translatable("text.vampirism.skill.skills_reset"), true);
                LOGGER.debug(LogUtil.FACTION, "Skills were reset for {}", entity.getName().getString());
                VampirismLogger.info(VampirismLogger.SKILLS, "Skills were reset for {}", entity.getName().getString());
                return false;
            }
        }).orElse(true);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % getTickDuration(amplifier) == 0;
    }

    private int getTickDuration(int amplifier) {
        return (1000 / (amplifier + 1));
    }
}
