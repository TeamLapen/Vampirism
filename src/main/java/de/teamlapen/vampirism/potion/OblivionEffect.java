package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.player.skills.SkillNode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.Optional;

public class OblivionEffect extends VampirismEffect {

    public OblivionEffect(String name, EffectType effectType, int potionColor) {
        super(name, effectType, potionColor);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return super.getDisplayName();
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int a = (100 / (amplifier + 1));
        return duration % a == 0;
    }

    @Override
    public void performEffect(@Nonnull LivingEntity entityLivingBaseIn, int amplifier) {
        if (!entityLivingBaseIn.getEntityWorld().isRemote) {
            if (entityLivingBaseIn instanceof PlayerEntity) {
                FactionPlayerHandler.getOpt(((PlayerEntity) entityLivingBaseIn)).map(FactionPlayerHandler::getCurrentFactionPlayer).flatMap(factionPlayer -> factionPlayer).ifPresent(factionPlayer -> {
                    ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
                    Optional<SkillNode> nodeOPT = ((SkillHandler<?>) skillHandler).anyLastNode();
                    if (nodeOPT.isPresent()) {
                        for (ISkill element : nodeOPT.get().getElements()) {
                            skillHandler.disableSkill(element);
                        }
                    } else {
                        entityLivingBaseIn.removePotionEffect(ModEffects.oblivion);
                        ((PlayerEntity) entityLivingBaseIn).sendStatusMessage(new TranslationTextComponent("text.vampirism.skill.skills_reset"), true);
                    }
                });
            }
        }
    }

}
