package de.teamlapen.vampirism.player.skills;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillManager;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.core.VampirismRegistries;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Returns a list of values relevant for rendering.
     * See {@link SkillRegistry#createDisplayInfo(SkillNode)} for content info
     *
     * @param faction
     * @return
     */
    @SideOnly(Side.CLIENT)
    public Integer[] getDisplayInfo(IPlayableFaction faction) {
        return skillNodeSizeMap.get(faction);
    }

    @Override
    public SkillNode getRootSkillNode(IPlayableFaction faction) {
        if (rootNodes.get(faction.getKey()) == null) {
            rootNodes.put();
            VampirismRegistries.SKILLS.getEntries()
        }
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
