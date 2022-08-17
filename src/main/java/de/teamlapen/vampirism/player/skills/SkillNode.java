package de.teamlapen.vampirism.player.skills;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillType;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A node for the skill tree. Can contain multiple skills which only can be activated exclusively to each other.
 */
public class SkillNode {
    private final static Logger LOGGER = LogManager.getLogger();
    private final SkillNode parent;
    private final @NotNull List<SkillNode> children;
    private final ISkill<?>[] elements;
    private final ResourceLocation[] lockingNodes;
    private final int depth;
    private final IPlayableFaction<?> faction;
    private final ResourceLocation id;
    private final boolean hidden = false;

    private SkillNode(ResourceLocation id, IPlayableFaction<?> faction, SkillNode parent, int depth, ISkill<?>[] elements, ResourceLocation... lockingNodes) {
        this.id = id;
        this.parent = parent;
        this.faction = faction;
        this.depth = depth;
        this.children = new ArrayList<>();
        this.elements = elements;
        this.lockingNodes = lockingNodes;
    }


    /**
     * For root skill node
     */
    public SkillNode(@NotNull IPlayableFaction<?> faction, ISkill<?> element, @NotNull ISkillType type) {
        this(type.createIdForFaction(faction.getID()), faction, null, 0, new ISkill[]{element});

    }

    /**
     * For child nodes
     *
     * @param elements One or more xor skills
     */
    public SkillNode(ResourceLocation id, @NotNull SkillNode parent, ISkill<?>[] elements, ResourceLocation... lockingNodes) {
        this(id, parent.getFaction(), parent, parent.depth + 1, elements, lockingNodes);
        parent.children.add(this);
    }

    /**
     * @return If the given skill is an element of this node
     */
    public boolean containsSkill(ISkill<?> skill) {
        return ArrayUtils.contains(elements, skill);
    }

    public List<SkillNode> getChildren() {
        return children;
    }

    public @NotNull Builder getCopy() {
        return new Builder(parent.id, null, Arrays.asList(elements), Arrays.asList(lockingNodes));
    }

    public int getDepth() {
        return depth;
    }

    public ISkill<?>[] getElements() {
        return elements;
    }

    public IPlayableFaction<?> getFaction() {
        return faction;
    }

    public ResourceLocation getId() {
        return id;
    }

    public boolean isHidden() {
        return hidden;
    }

    public ResourceLocation[] getLockingNodes() {
        return lockingNodes;
    }

    public SkillNode getParent() {
        return parent;
    }

    public boolean isRoot() {
        return parent == null;
    }

    @Override
    public @NotNull String toString() {
        return "SkillNode{" +
                "faction=" + faction +
                ", depth=" + depth +
                ", elements=" + Arrays.toString(elements) +
                '}';
    }

    public static class Builder {
        public static @Nullable Builder deserialize(@NotNull JsonObject json, @SuppressWarnings("unused") JsonDeserializationContext context) {
            if (json.has("remove") && GsonHelper.getAsBoolean(json, "remove")) return null;
            ResourceLocation parent = json.has("parent") ? new ResourceLocation(GsonHelper.getAsString(json, "parent")) : null;
            ResourceLocation merge = json.has("merge") ? new ResourceLocation(GsonHelper.getAsString(json, "merge")) : null;
            JsonArray skills = GsonHelper.getAsJsonArray(json, "skills", new JsonArray());
            List<ISkill<?>> skillList = new ArrayList<>();
            for (int i = 0; i < skills.size(); i++) {
                ResourceLocation id = new ResourceLocation(GsonHelper.convertToString(skills.get(i), "skill"));
                ISkill<?> s = RegUtil.getSkill(id);
                if (s == null) {
                    throw new IllegalArgumentException("Skill " + id + " is not registered");
                }
                skillList.add(s);
            }
            JsonArray locking = GsonHelper.getAsJsonArray(json, "locking", new JsonArray());
            List<ResourceLocation> lockingList = new ArrayList<>();
            for (int i = 0; i < locking.size(); i++) {
                lockingList.add(new ResourceLocation(GsonHelper.convertToString(locking.get(i), "skill")));
            }
            return new Builder(parent, merge, skillList, lockingList);
        }

        public static @NotNull Builder readFrom(@NotNull FriendlyByteBuf buf) {
            ResourceLocation parent = buf.readBoolean() ? buf.readResourceLocation() : null;
            ResourceLocation merge = buf.readBoolean() ? buf.readResourceLocation() : null;

            List<ISkill<?>> skillList = new ArrayList<>();
            int count = buf.readVarInt();
            for (int i = 0; i < count; i++) {
                ResourceLocation id = buf.readResourceLocation();
                ISkill<?> s = RegUtil.getSkill(id);
                if (s == null) {
                    LOGGER.warn("Skill {} is not registered", id);
                }
                skillList.add(s);
            }

            List<ResourceLocation> lockingList = new ArrayList<>();
            int count2 = buf.readVarInt();
            for (int i = 0; i < count2; i++) {
                lockingList.add(buf.readResourceLocation());
            }


            return new Builder(parent, merge, skillList, lockingList);
        }

        public final List<ResourceLocation> lockingNodes;
        public final ResourceLocation parentId;
        public final List<ISkill<?>> skills;
        public final ResourceLocation mergeId;

        private Builder(ResourceLocation parentId, ResourceLocation mergeId, List<ISkill<?>> skills, List<ResourceLocation> lockingNodes) {
            this.mergeId = mergeId;
            this.parentId = parentId;
            this.skills = skills;
            this.lockingNodes = lockingNodes;
        }

        public @NotNull SkillNode build(ResourceLocation id, @NotNull SkillNode parent) {
            return new SkillNode(id, parent, skills.toArray(new ISkill[0]), lockingNodes.toArray(new ResourceLocation[0]));
        }

        public boolean checkSkillFaction(IPlayableFaction<?> faction) {
            for (ISkill<?> s : skills) {
                if (s.getFaction().map(f -> f != faction).orElse(false)) {
                    return false;
                }
            }
            return true;
        }

        public @NotNull JsonObject serialize() {

            JsonObject jsonobject = new JsonObject();
            if (this.parentId != null) {
                jsonobject.addProperty("parent", this.parentId.toString());
            } else if (mergeId != null) {
                jsonobject.addProperty("merge", mergeId.toString());
            }

            JsonArray skillIds = new JsonArray();
            for (ISkill<?> s : skills) {
                skillIds.add(RegUtil.id(s).toString());
            }
            jsonobject.add("skills", skillIds);

            if (!lockingNodes.isEmpty()) {
                JsonArray lockedIds = new JsonArray();
                for (ResourceLocation s : lockingNodes) {
                    lockedIds.add(s.toString());
                }
                jsonobject.add("locking", skillIds);
            }
            return jsonobject;
        }

        @Override
        public @NotNull String toString() {
            return "SkillNode.Builder{parent=" + parentId + ",merge=" + mergeId + "skills" + skills.toString() + "}";
        }

        public void writeTo(@NotNull FriendlyByteBuf buf) {
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
            for (ISkill<?> s : skills) {
                buf.writeResourceLocation(RegUtil.id(s));
            }

            buf.writeVarInt(this.lockingNodes.size());
            for (ResourceLocation s : lockingNodes) {
                buf.writeResourceLocation(s);
            }

        }
    }
}
