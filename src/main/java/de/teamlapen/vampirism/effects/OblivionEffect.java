package de.teamlapen.vampirism.effects;

import de.teamlapen.lib.lib.util.LogUtil;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillHandler;
import de.teamlapen.vampirism.entity.player.skills.SkillNode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class OblivionEffect extends VampirismEffect {

    private static final Logger LOGGER = LogManager.getLogger(FactionPlayerHandler.class);

    public OblivionEffect(MobEffectCategory effectType, int potionColor) {
        super(effectType, potionColor);
    }

    @Override
    public @NotNull List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entityLivingBaseIn, int amplifier) {
        if (!entityLivingBaseIn.getCommandSenderWorld().isClientSide) {
            if (entityLivingBaseIn instanceof Player) {
                entityLivingBaseIn.addEffect(new MobEffectInstance(MobEffects.CONFUSION, getTickDuration(amplifier), 5, false, false, false, null, Optional.empty()));
                FactionPlayerHandler.getOpt(((Player) entityLivingBaseIn)).map(FactionPlayerHandler::getCurrentFactionPlayer).flatMap(factionPlayer -> factionPlayer).ifPresent(factionPlayer -> {
                    ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
                    Optional<SkillNode> nodeOPT = ((SkillHandler<?>) skillHandler).anyLastNode();
                    if (nodeOPT.isPresent()) {
                        //noinspection rawtypes
                        for (ISkill element : nodeOPT.get().getElements()) {
                            //noinspection unchecked
                            skillHandler.disableSkill(element);
                        }
                    } else {
                        entityLivingBaseIn.removeEffect(ModEffects.OBLIVION.get());
                        ((Player) entityLivingBaseIn).displayClientMessage(Component.translatable("text.vampirism.skill.skills_reset"), true);
                        LOGGER.debug(LogUtil.FACTION, "Skills were reset for {}", entityLivingBaseIn.getName().getString());
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
