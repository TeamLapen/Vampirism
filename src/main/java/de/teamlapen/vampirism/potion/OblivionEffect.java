package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.vampirism.player.skills.SkillNode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
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
        return duration % getTickDuration(amplifier) == 0;
    }

    private int getTickDuration(int amplifier) {
        return (1000 / (amplifier +1));
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }

    @Override
    public void performEffect(@Nonnull LivingEntity entityLivingBaseIn, int amplifier) {
        if (!entityLivingBaseIn.getEntityWorld().isRemote) {
            if (entityLivingBaseIn instanceof PlayerEntity) {
                entityLivingBaseIn.addPotionEffect(new EffectInstance(Effects.NAUSEA,getTickDuration(amplifier),5,false,false,false, null));
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
