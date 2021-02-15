package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.skills.SkillNode;
import de.teamlapen.vampirism.player.skills.SkillTreeManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.*;

public class OblivionEffect extends VampirismEffect {

    public OblivionEffect(String name, EffectType effectType, int potionColor) {
        super(name, effectType, potionColor);
    }

    @Override
    public ITextComponent getDisplayName() {
        return super.getDisplayName();
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % (20 * amplifier) == 0;
    }

    @Override
    public void performEffect(@Nonnull LivingEntity entityLivingBaseIn, int amplifier) {
        if (!entityLivingBaseIn.getEntityWorld().isRemote) {
            if (entityLivingBaseIn instanceof PlayerEntity) {
                FactionPlayerHandler.getOpt(((PlayerEntity) entityLivingBaseIn)).map(FactionPlayerHandler::getCurrentFactionPlayer).flatMap(factionPlayer -> factionPlayer).ifPresent(factionPlayer -> {
                    ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
                    SkillNode rootNode = SkillTreeManager.getInstance().getSkillTree().getRootNodeForFaction(factionPlayer.getFaction().getID());
                    List<SkillNode> lastUnlockedNode = new ArrayList<>();
                    Queue<SkillNode> nodeQueue = new ArrayDeque<>();
                    nodeQueue.add(rootNode);

                    for (SkillNode skillNode = nodeQueue.poll(); skillNode != null; skillNode = nodeQueue.poll()) {
                        List<SkillNode> children = skillNode.getChildren();
                        if (children.isEmpty() || children.stream().flatMap(node -> Arrays.stream(node.getElements())).noneMatch(skillHandler::isSkillEnabled)) {
                            lastUnlockedNode.add(skillNode);
                        } else {
                            nodeQueue.addAll(children);
                        }
                    }

                    lastUnlockedNode.removeIf(SkillNode::isRoot);

                    if (!lastUnlockedNode.isEmpty()) {
                        SkillNode n = lastUnlockedNode.get(entityLivingBaseIn.getRNG().nextInt(lastUnlockedNode.size()));
                        for (ISkill element : n.getElements()) {
                            skillHandler.disableSkill(element);
                        }
                    } else {
                        entityLivingBaseIn.removePotionEffect(ModEffects.oblivion);
                    }
                });
            }
        }
    }

}
