package de.teamlapen.vampirism.player.skills;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A node for the skill tree. Can contain multiple skills which only can be activated exclusively to each other.
 */
public class SkillNode {
    private final static Logger LOGGER = LogManager.getLogger();
    private final SkillNode parent;
    private final List<SkillNode> children;
    private final ISkill[] elements;
    private final int depth;
    private final IPlayableFaction faction;
    private final ResourceLocation id;

    private SkillNode(ResourceLocation id, IPlayableFaction faction, SkillNode parent, int depth, ISkill... elements) {
        this.id = id;
        this.parent = parent;
        this.faction = faction;
        this.depth = depth;
        this.children = new ArrayList<>();
        this.elements = elements;
    }


    /**
     * For root skill node
     */
    public SkillNode(IPlayableFaction faction, ISkill element) {
        this(faction.getID(), faction, null, 0, element);

    }

    /**
     * For child nodes
     *
     * @param elements One or more xor skills
     */
    public SkillNode(ResourceLocation id, SkillNode parent, ISkill... elements) {
        this(id, parent.getFaction(), parent, parent.depth + 1, elements);
        parent.children.add(this);
    }

    /**
     * @param skill
     * @return If the given skill is an element of this node
     */
    public boolean containsSkill(ISkill skill) {
        return ArrayUtils.contains(elements, skill);
    }

    public List<SkillNode> getChildren() {
        return children;
    }

    public Builder getCopy() {
        return new Builder(parent.id, null, Arrays.asList(elements));
    }

    public int getDepth() {
        return depth;
    }

    public ISkill[] getElements() {
        return elements;
    }

    public IPlayableFaction getFaction() {
        return faction;
    }

    public ResourceLocation getId() {
        return id;
    }

    public SkillNode getParent() {
        return parent;
    }

    public boolean isRoot() {
        return parent == null;
    }

    @Override
    public String toString() {
        return "SkillNode{" +
                "faction=" + faction +
                ", depth=" + depth +
                ", elements=" + Arrays.toString(elements) +
                '}';
    }

    public static class Builder {
        public static Builder deserialize(JsonObject json, JsonDeserializationContext context) {
            if (json.has("remove") && JSONUtils.getBoolean(json, "remove")) return null;
            ResourceLocation parent = json.has("parent") ? new ResourceLocation(JSONUtils.getString(json, "parent")) : null;
            ResourceLocation merge = json.has("merge") ? new ResourceLocation(JSONUtils.getString(json, "merge")) : null;
            JsonArray skills = JSONUtils.getJsonArray(json, "skills", new JsonArray());
            List<ISkill> skillList = new ArrayList<>();
            for (int i = 0; i < skills.size(); i++) {
                ResourceLocation id = new ResourceLocation(JSONUtils.getString(skills.get(i), "skill"));
                ISkill s = ModRegistries.SKILLS.getValue(id);
                if (s == null) {
                    throw new IllegalArgumentException("Skill " + id + " is not registered");
                }
                skillList.add(s);
            }
            return new Builder(parent, merge, skillList);
        }

        public static Builder readFrom(PacketBuffer buf) {
            ResourceLocation parent = buf.readBoolean() ? buf.readResourceLocation() : null;
            ResourceLocation merge = buf.readBoolean() ? buf.readResourceLocation() : null;

            List<ISkill> skillList = new ArrayList<>();
            int count = buf.readVarInt();
            for (int i = 0; i < count; i++) {
                ResourceLocation id = buf.readResourceLocation();
                ISkill s = ModRegistries.SKILLS.getValue(id);
                if (s == null) {
                    LOGGER.warn("Skill {} is not registered", id);
                }
                skillList.add(s);
            }


            return new Builder(parent, merge, skillList);
        }

        public final ResourceLocation parentId;
        public final List<ISkill> skills;
        public final ResourceLocation mergeId;

        private Builder(ResourceLocation parentId, ResourceLocation mergeId, List<ISkill> skills) {
            this.mergeId = mergeId;
            this.parentId = parentId;
            this.skills = skills;
        }

        public boolean checkSkillFaction(IPlayableFaction faction) {
            for (ISkill s : skills) {
                if (!faction.getID().equals(s.getFaction().getID())) {
                    return false;
                }
            }
            return true;
        }

        public JsonObject serialize() {

            JsonObject jsonobject = new JsonObject();
            if (this.parentId != null) {
                jsonobject.addProperty("parent", this.parentId.toString());
            } else if (mergeId != null) {
                jsonobject.addProperty("merge", mergeId.toString());
            }

            JsonArray skillIds = new JsonArray();

            for (ISkill s : skills) {
                skillIds.add(s.getRegistryName().toString());
            }

            jsonobject.add("skills", skillIds);
            return jsonobject;
        }

        @Override
        public String toString() {
            return "SkillNode.Builder{parent=" + parentId + ",merge=" + mergeId + "skills" + skills.toString() + "}";
        }

        public void writeTo(PacketBuffer buf) {
            if (this.parentId == null) {
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeResourceLocation(this.parentId);
            }

            if (this.mergeId == null) { //Probably never happens, but meh
                buf.writeBoolean(false);
            } else {
                buf.writeBoolean(true);
                buf.writeResourceLocation(this.mergeId);
            }

            buf.writeVarInt(this.skills.size());
            for (ISkill s : skills) {
                buf.writeResourceLocation(s.getRegistryName());
            }

        }
    }
}
