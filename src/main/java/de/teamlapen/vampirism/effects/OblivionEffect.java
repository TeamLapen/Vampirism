package de.teamlapen.vampirism.effects;

import de.teamlapen.lib.lib.util.LogUtil;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import de.teamlapen.vampirism.misc.VampirismLogger;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.EffectCure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

public class OblivionEffect<T extends IFactionPlayer<T> & ISkillPlayer<T>> extends VampirismEffect {

    private static final Logger LOGGER = LogManager.getLogger();

    public OblivionEffect() {
        super(MobEffectCategory.NEUTRAL, 0x4E9331);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entityLivingBaseIn, int amplifier) {
        if (!entityLivingBaseIn.getCommandSenderWorld().isClientSide) {
            if (entityLivingBaseIn instanceof Player player) {
                entityLivingBaseIn.addEffect(new MobEffectInstance(MobEffects.CONFUSION, getTickDuration(amplifier), 5, false, false, false, null));
                return FactionPlayerHandler.get(player).<T>getSkillHandler().map(handler ->
                {
                    Optional<ISkillNode> nodeOPT = ((SkillHandler<?>) handler).anyLastNode();
                    if (nodeOPT.isPresent()) {
                        for (Holder<ISkill<?>> element : nodeOPT.get().skills()) {
                            //noinspection unchecked
                            handler.disableSkill((Holder<ISkill<T>>) (Object) element);
                            player.awardStat(ModStats.SKILL_FORGOTTEN.get().get(element.value()));
                        }
                        return true;
                    } else {
                        ((Player) entityLivingBaseIn).displayClientMessage(Component.translatable("text.vampirism.skill.skills_reset"), true);
                        LOGGER.debug(LogUtil.FACTION, "Skills were reset for {}", entityLivingBaseIn.getName().getString());
                        VampirismLogger.info(VampirismLogger.SKILLS, "Skills were reset for {}", entityLivingBaseIn.getName().getString());
                        return false;
                    }
                }).orElse(true);
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % getTickDuration(amplifier) == 0;
    }

    private int getTickDuration(int amplifier) {
        return (1000 / (amplifier + 1));
    }

}
