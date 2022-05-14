package de.teamlapen.vampirism.player.skills;


import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SkillTree {
    private final static Logger LOGGER = LogManager.getLogger();

    private final Map<ResourceLocation, SkillNode> rootNodes = new HashMap<>();
    /**
     * Stores values relevant for rendering the skill menu. Only filled on client
     */
    private final Map<ResourceLocation, Integer[]> skillNodeSizeMap = new HashMap<>();
    private Map<ResourceLocation, SkillNode> allNodes = new HashMap<>();

    public Map<ResourceLocation, SkillNode.Builder> getCopy() {
        Map<ResourceLocation, SkillNode.Builder> map = new HashMap<>();
        for (SkillNode root : rootNodes.values()) {
            addChildrenCopyToMap(root, map);
        }
        return map;
    }

    /**
     * Returns a list of values relevant for rendering.
     * See {@link SkillTree#createDisplayInfo(SkillNode)} for content info
     */
    @OnlyIn(Dist.CLIENT)
    public Integer[] getDisplayInfo(ResourceLocation faction) {
        if (!skillNodeSizeMap.containsKey(faction)) {
            skillNodeSizeMap.put(faction, createDisplayInfo(getRootNodeForFaction(faction)));
        }
        return skillNodeSizeMap.get(faction);
    }

    @Nullable
    public SkillNode getNodeFromId(ResourceLocation id) {
        return allNodes.get(id);
    }

    @Nonnull
    public SkillNode getRootNodeForFaction(ResourceLocation id) {
        if (!rootNodes.containsKey(id))
            throw new IllegalStateException("Faction " + id + " does not have a root skill");
        return rootNodes.get(id);
    }

    @Nonnull
    public SkillNode getRootNodeForFaction(ResourceLocation id, ISkillType type) {
        ResourceLocation skillId = type.createIdForFaction(id);
        if (!rootNodes.containsKey(skillId))
            throw new IllegalStateException("Faction " + skillId + " does not have a root skill");
        return rootNodes.get(skillId);
    }

    /**
     * @deprecated use {@link #getTreeWidth(SkillNode)}
     */
    @Deprecated //TODO BREAKING remove
    public int getTreeWidth(ResourceLocation id) {
        return getTreeWidth(getRootNodeForFaction(id));
    }

    public static int getTreeWidth(SkillNode node) {
        int max = node.getElements().length * 26 + ((node.getElements().length-1) * 10);

        int children = node.getChildren().size() > 0 ?(node.getChildren().size()-1) * 30:0;
        for (SkillNode child : node.getChildren()) {
            children += getTreeWidth(child);
        }

        return Math.max(max, children);
    }

    public static int getTreeHeight(SkillNode node) {
        int max = 26;
        for (SkillNode child : node.getChildren()) {
            max = Math.max(max, getTreeHeight(child) + 60);
        }
        return max;
    }

    /**
     * Initialized with root nodes so skill before the actual tree is received
     */
    public void initRootSkills() {
        //Built root nodes
        rootNodes.clear();
        for (IPlayableFaction faction : VampirismAPI.factionRegistry().getPlayableFactions()) {
            for (ISkillType value : VampirismAPI.skillManager().getSkillTypes()) {
                if (value.isForFaction(faction)) {
                    SkillNode rootNode = new SkillNode(faction, ((SkillManager) VampirismAPI.skillManager()).getRootSkill(faction, value), value);
                    rootNodes.put(rootNode.getId(), rootNode);
                }
            }
        }
    }

    public void loadNodes(Map<ResourceLocation, SkillNode.Builder> nodes) {

        Map<ResourceLocation, SkillNode> builtNodes = new HashMap<>();
        //Built root nodes
        rootNodes.clear();
        for (IPlayableFaction faction : VampirismAPI.factionRegistry().getPlayableFactions()) {
            for (ISkillType value : VampirismAPI.skillManager().getSkillTypes()) {
                if (value.isForFaction(faction)) {
                    SkillNode rootNode = new SkillNode(faction, ((SkillManager) VampirismAPI.skillManager()).getRootSkill(faction, value), value);
                    builtNodes.put(rootNode.getId(), rootNode);
                    rootNodes.put(rootNode.getId(), rootNode);
                }
            }
        }

        //Merge
        Iterator<Map.Entry<ResourceLocation, SkillNode.Builder>> it = nodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ResourceLocation, SkillNode.Builder> entry = it.next();
            SkillNode.Builder builder = entry.getValue();
            if (builder.mergeId != null) {
                SkillNode.Builder b = nodes.get(builder.mergeId);
                if (b == null) {
                    LOGGER.error("Could not load skill node {} because merge target {} couldn't be found", entry.getKey(), builder.mergeId);
                } else {
                    b.skills.addAll(builder.skills);
                }
                it.remove();
            }
        }

        while (!nodes.isEmpty()) {
            boolean flag = false;
            Iterator<Map.Entry<ResourceLocation, SkillNode.Builder>> iterator = nodes.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ResourceLocation, SkillNode.Builder> entry = iterator.next();
                ResourceLocation id = entry.getKey();
                SkillNode.Builder builder = entry.getValue();

                if (builder.parentId != null) {
                    SkillNode parent = builtNodes.get(builder.parentId);
                    if (parent != null) {
                        if (!builder.checkSkillFaction(parent.getFaction())) {
                            LOGGER.error("Cannot create skill node {} because skills do not match the derived faction {}", id, parent.getFaction());
                        } else {
                            builtNodes.put(id, builder.build(id, parent));
                        }
                        iterator.remove();
                        flag = true;
                    }
                }
            }
            if (!flag) {//Went though all builders but could not create a single one. So we can stop here
                for (Map.Entry<ResourceLocation, SkillNode.Builder> entry : nodes.entrySet()) {
                    LOGGER.error("Could not load skill node (probably parent invalid) {}: {}", entry.getKey(), entry.getValue());
                }
                break;
            }
        }
        this.allNodes = builtNodes;
        LOGGER.info("Loaded {} skill nodes", builtNodes.size() - rootNodes.size());
    }

    @OnlyIn(Dist.CLIENT)
    public void updateRenderInfo() {
        for (SkillNode n : rootNodes.values()) {
            setRenderPos(n, 0);
        }
    }

    private void addChildrenCopyToMap(SkillNode n, Map<ResourceLocation, SkillNode.Builder> map) {
        for (SkillNode c : n.getChildren()) {
            map.put(c.getId(), c.getCopy());
            addChildrenCopyToMap(c, map);
        }
    }

    /**
     * Counts the amount of end points in this tree
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
     */
    @OnlyIn(Dist.CLIENT)
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
