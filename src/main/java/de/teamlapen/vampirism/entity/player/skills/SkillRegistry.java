package de.teamlapen.vampirism.entity.player.skills;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillRegistry;
import de.teamlapen.vampirism.api.entity.player.skills.SkillNode;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ISkillRegistry implementation
 */
public class SkillRegistry implements ISkillRegistry {
    private final Map<IPlayableFaction, SkillNode> rootNodes = new HashMap<>();
    private final Map<IPlayableFaction, BiMap<String, ISkill>> skillMap = new HashMap<>();
    /**
     * Stores values relevant for rendering the skill menu. Only filled on client
     */
    private final Map<IPlayableFaction, Integer[]> skillNodeSizeMap = new HashMap<>();

    /**
     * For debug purpose only.
     * Adds all available skill ids to the list
     *
     * @param faction
     * @param list
     */
    public void addSkills(IPlayableFaction faction, List list) {
        for (String s : skillMap.get(faction).keySet()) {
            list.add(s);
        }
    }

//    /**
//     * Calculate and set the render position for the given node using the given column as center
//     * @param base
//     * @param column
//     */
//    private void setRenderPos(SkillNode base,int column){
//        VampirismMod.log.t("Node %s at %d",base,column);
//        int left=-base.getElements().length/2;
//        VampirismMod.log.t("Left1 %d",left);
//        for(ISkill skill:base.getElements()){
//            skill.setRenderPos(base.getDepth(),column+left);
//            VampirismMod.log.t("Skill %s at %d %d",skill,base.getDepth(),column+left);
//            left++;
//        }
//        VampirismMod.log.t("Right %d",left);
//        int[] widths=new int[base.getChildren().size()];
//        int total=0;
//        for(int i=0;i<widths.length;i++){
//            SkillNode node=base.getChildren().get(i);
//            widths[i]=calculateMaxSkillsPerNode(node)*calculateEndPoints(node);
//            total+=widths[i];
//        }
//        VampirismMod.log.t("Width %d",total);
//        left=-(total)/2;
//        VampirismMod.log.t("Left2 %d",left);
//        for(int i=0;i<widths.length;i++){
//            setRenderPos(base.getChildren().get(i),column+left+widths[i]/2);
//            left+=widths[i];
//        }
//    }

    public void finish() {
        for (final IPlayableFaction faction : VampirismAPI.factionRegistry().getPlayableFactions()) {
            SkillNode rootNode = rootNodes.get(faction);
            if (rootNode == null) {
                rootNode = new SkillNode(faction, new DefaultSkill() {


                    @Override
                    public String getID() {
                        return faction.getKey() + "_dummy";
                    }

                    @Override
                    public int getMinU() {
                        return 0;
                    }

                    @Override
                    public int getMinV() {
                        return 0;
                    }

                    @Override
                    public String getUnlocalizedName() {
                        return faction.getUnlocalizedName();
                    }
                });
                rootNodes.put(faction, rootNode);
            }
            if (!skillMap.containsKey(faction)) {
                skillMap.put(faction, HashBiMap.<String, ISkill>create());
            }
            if (FMLCommonHandler.instance().getSide().isClient()) {
                skillNodeSizeMap.put(faction, createDisplayInfo(rootNode));
                setRenderPos(rootNode, 0);
            }

        }
    }

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
    public String getID(IPlayableFaction faction, ISkill skill) {
        return skillMap.get(faction).inverse().get(skill);
    }

    @Override
    public SkillNode getRootSkillNode(IPlayableFaction faction) {
        return rootNodes.get(faction);
    }

    @Override
    public ISkill getSkill(IPlayableFaction faction, String id) {
        return skillMap.get(faction).get(id);
    }

    /**
     * For debug purpose only.
     * Prints the skills of the given faction to the given sender
     *
     * @param faction
     * @param sender
     */
    public void printSkills(IPlayableFaction faction, ICommandSender sender) {
        for (Map.Entry e : skillMap.get(faction).entrySet()) {
            sender.addChatMessage(new ChatComponentText("ID: " + e.getKey() + " Skill: " + e.getValue()));
        }
    }

    @Override
    public void registerNode(SkillNode node) {
        BiMap<String, ISkill> map = skillMap.get(node.getFaction());
        if (map == null) {
            map = HashBiMap.create();
            skillMap.put(node.getFaction(), map);
        }
        ISkill[] skills = node.getElements();
        for (ISkill skill : skills) {
            if (map.put(skill.getID(), skill) != null) {
                throw new IllegalArgumentException("There already is a skill registered for " + skill.getID() + " ");
            }
        }
    }

//    private int calculateAddColumns(SkillNode start){
//        int count=start.getElements().length-1;
//        if(start.getChildren().size()==0){
//            return count;
//        }
//        count+=start.getChildren().size()-1;
//        for(SkillNode node:start.getChildren()){
//            count+=calculateAddColumns(node);
//        }
//        count-=start.getElements().length;
//        return count;
//    }

    @Override
    public SkillNode setRootSkill(IPlayableFaction faction, ISkill skill) {
        SkillNode s = new SkillNode(faction, skill);
        rootNodes.put(faction, s);
        return s;
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
            if(n>max)max = n;
        }
        return max;
    }

    private Integer[] createDisplayInfo(SkillNode root) {
        Integer[] info = new Integer[3];
        info[0] = calculateEndPoints(root);
        info[1] = calculateMaxSkillsPerNode(root);
        info[2] = calculateMaxSkillDepth(root)+1;
        return info;
    }

    /**
     * Calculate and set the render position for the given node using the given column as center
     *
     * @param base
     * @param column
     */
    private void setRenderPos(SkillNode base, int column) {
        int left = -(base.getElements().length * 2 -1) / 2;
        for (ISkill skill : base.getElements()) {
            skill.setRenderPos(base.getDepth() * 2, column + left);
            left+=2;
        }
        int[] widths = new int[base.getChildren().size()];
        int total = 0;
        for (int i = 0; i < widths.length; i++) {
            SkillNode node = base.getChildren().get(i);
            widths[i] = calculateMaxSkillsPerNode(node) * calculateEndPoints(node)*2;
            total += widths[i];
        }
        left = -(total) / 2;
        for (int i = 0; i < widths.length; i++) {
            setRenderPos(base.getChildren().get(i), column + left + widths[i] / 2);
            left += widths[i];
        }
    }
}
