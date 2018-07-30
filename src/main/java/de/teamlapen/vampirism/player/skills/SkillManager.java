package de.teamlapen.vampirism.player.skills;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillManager;
import de.teamlapen.vampirism.api.entity.player.skills.SkillEvent;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.core.VampirismRegistries;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * 1.12
 *
 * @author maxanier
 */
public class SkillManager implements ISkillManager {


    private final Map<ResourceLocation, SkillNode> rootNodes = new HashMap<>();

    /**
     * Stores values relevant for rendering the skill menu. Only filled on client
     */
    private final Map<ResourceLocation, Integer[]> skillNodeSizeMap = new HashMap<>();

    public void buildSkillTrees() {
        for (IPlayableFaction f : VampirismAPI.factionRegistry().getPlayableFactions()) {
            buildSkillTree(f);
        }
    }

    @Override
    public SkillNode createSkillNode(@Nonnull SkillNode parent, ISkill... skills) {
        Objects.requireNonNull(parent);
        List<ISkill> skillList = Arrays.asList(skills);
        if (skillList.contains(null)) throw new IllegalArgumentException("Can't use null skill");
        assert skills.length > 0;
        ISkill[] moddedSkills = fireAddSkillsEvent(parent.getFaction(), skillList);
        if (moddedSkills.length == 0) {
            throw new IllegalArgumentException("Cannot remove all skills from a skill node");
        }

        SkillNode node = new SkillNode(parent, moddedSkills);
        MinecraftForge.EVENT_BUS.post(new SkillEvent.CreatedNode(node.getFaction(), node));
        return node;
    }

    /**
     * Returns a list of values relevant for rendering.
     * See {@link SkillManager#createDisplayInfo(SkillNode)} for content info
     *
     * @param faction
     * @return
     */
    @SideOnly(Side.CLIENT)
    public Integer[] getDisplayInfo(IPlayableFaction faction) {
        if (!skillNodeSizeMap.containsKey(faction.getKey())) {
            skillNodeSizeMap.put(faction.getKey(), createDisplayInfo(getRootSkillNode(faction)));
        }
        return skillNodeSizeMap.get(faction.getKey());
    }

    @Override
    public SkillNode getRootSkillNode(IPlayableFaction faction) {
        return rootNodes.get(faction.getKey());
    }

    @Override
    public List<ISkill> getSkillsForFaction(IPlayableFaction faction) {
        List<ISkill> list = Lists.newArrayList(VampirismRegistries.SKILLS.getValues());
        list.removeIf(skill -> !faction.equals(skill.getFaction()));
        return list;
    }

    /**
     * For debug purpose only.
     * Prints the skills of the given faction to the given sender
     *
     * @param faction
     * @param sender
     */
    public void printSkills(IPlayableFaction faction, ICommandSender sender) {
        for (ISkill s : getSkillsForFaction(faction)) {
            sender.sendMessage(new TextComponentString("ID: " + VampirismRegistries.SKILLS.getKey(s) + " Skill: " + s));
        }
    }

    private void buildSkillTree(IPlayableFaction faction) {
        ISkill root = getRootSkill(faction);
        SkillNode rootNode = new SkillNode(faction, root);
        rootNodes.put(faction.getKey(), rootNode);
        MinecraftForge.EVENT_BUS.post(new SkillEvent.CreatedNode(faction, rootNode));
        setRenderPos(rootNode, 0);
    }

    /**
     * Counts the amount of end points in this tree
     *
     * @param start
     * @return
     */
    private int calculateEndPoints(SkillNode start) {
        if (start.getChildren().size() == 0) {
            return 1;
        }
        int count = 0;
        for (SkillNode node : start.getChildren()) {
            count += calculateEndPoints(node);
        }
        return count;
    }

    private int calculateMaxSkillDepth(SkillNode start) {
        int max = start.getDepth();
        for (SkillNode node : start.getChildren()) {
            int n = calculateMaxSkillDepth(node);
            if (n > max) max = n;
        }
        return max;
    }

    private int calculateMaxSkillsPerNode(SkillNode start) {
        int max = start.getElements().length;
        for (SkillNode node : start.getChildren()) {
            int n = calculateMaxSkillsPerNode(node);
            if (n > max) max = n;
        }
        return max;
    }

    private Integer[] createDisplayInfo(SkillNode root) {
        Integer[] info = new Integer[3];
        info[0] = calculateEndPoints(root);
        info[1] = calculateMaxSkillsPerNode(root);
        info[2] = calculateMaxSkillDepth(root) + 1;
        return info;
    }

    private ISkill[] fireAddSkillsEvent(IPlayableFaction faction, List<ISkill> skills) {
        SkillEvent.AddSkills event = new SkillEvent.AddSkills(faction, skills);
        MinecraftForge.EVENT_BUS.post(event);
        event.getSkills().remove(null);
        return event.getSkills().toArray(new ISkill[event.getSkills().size()]);
    }

    /**
     * Get the root skill of the faction (Registered with the same key as the faction itself.
     * If none ist found, prints a warning and returns a dummy one
     *
     * @param faction
     * @return
     */
    private @Nonnull
    ISkill getRootSkill(IPlayableFaction faction) {
        ISkill skill = VampirismRegistries.SKILLS.getValue(faction.getKey());
        if (skill == null) {
            VampirismMod.log.bigWarning("SkillManager", "No root skill exists for faction %s", faction.getKey());
            throw new IllegalStateException("You need to register a root skill for your faction " + faction.getKey());
        }
        return skill;
    }

    /**
     * Calculate and set the render position for the given node using the given column as center
     *
     * @param base
     * @param column
     */
    private void setRenderPos(SkillNode base, int column) {
        int left = -(base.getElements().length * 2 - 1) / 2;
        for (ISkill skill : base.getElements()) {
            skill.setRenderPos(base.getDepth() * 2, column + left);
            left += 2;
        }
        int[] widths = new int[base.getChildren().size()];
        int total = 0;
        for (int i = 0; i < widths.length; i++) {
            SkillNode node = base.getChildren().get(i);
            widths[i] = calculateMaxSkillsPerNode(node) * calculateEndPoints(node) * 2;
            total += widths[i];
        }
        left = -(total) / 2;
        for (int i = 0; i < widths.length; i++) {
            setRenderPos(base.getChildren().get(i), column + left + widths[i] / 2);
            left += widths[i];
        }
    }
}
