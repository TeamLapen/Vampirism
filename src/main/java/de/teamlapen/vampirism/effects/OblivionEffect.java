package de.teamlapen.vampirism.effects;

import de.teamlapen.lib.lib.util.LogUtil;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.misc.VampirismLogger;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.player.skills.SkillNode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class OblivionEffect extends VampirismEffect {

    private static final Logger LOGGER = LogManager.getLogger();

    public OblivionEffect(EffectType effectType, int potionColor) {
        super(effectType, potionColor);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }

    @Override
    public void applyEffectTick(@Nonnull LivingEntity entityLivingBaseIn, int amplifier) {
        if (!entityLivingBaseIn.getCommandSenderWorld().isClientSide) {
            if (entityLivingBaseIn instanceof PlayerEntity) {
                entityLivingBaseIn.addEffect(new EffectInstance(Effects.CONFUSION, getTickDuration(amplifier), 5, false, false, false, null));
                FactionPlayerHandler.getOpt(((PlayerEntity) entityLivingBaseIn)).map(FactionPlayerHandler::getCurrentFactionPlayer).flatMap(factionPlayer -> factionPlayer).ifPresent(factionPlayer -> {
                    ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
                    Optional<SkillNode> nodeOPT = ((SkillHandler<?>) skillHandler).anyLastNode();
                    if (nodeOPT.isPresent()) {
                        for (ISkill element : nodeOPT.get().getElements()) {
                            skillHandler.disableSkill(element);
                        }
                    } else {
                        entityLivingBaseIn.removeEffect(ModEffects.OBLIVION.get());
                        ((PlayerEntity) entityLivingBaseIn).displayClientMessage(new TranslationTextComponent("text.vampirism.skill.skills_reset"), true);
                        LOGGER.debug(LogUtil.FACTION, "Skills were reset for {}", entityLivingBaseIn.getName().getString());
                        VampirismLogger.info(VampirismLogger.SKILLS, "Skills were reset for {}", entityLivingBaseIn.getName().getString());
                    }
                });
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % getTickDuration(amplifier) == 0;
    }

    private int getTickDuration(int amplifier) {
        return (1000 / (amplifier + 1));
    }

}
