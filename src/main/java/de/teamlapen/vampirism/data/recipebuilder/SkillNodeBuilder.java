package de.teamlapen.vampirism.data.recipebuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class SkillNodeBuilder {

    public static SkillNodeBuilder skill(ResourceLocation parent, ISkill<?>... skills) {
        return new SkillNodeBuilder(parent, skills);
    }

    @SafeVarargs
    public static SkillNodeBuilder hunter(ResourceLocation parent, ISkill<IHunterPlayer>... skills) {
        return skill(parent, skills).faction(VReference.HUNTER_FACTION);
    }

    @SafeVarargs
    public static SkillNodeBuilder vampire(ResourceLocation parent, ISkill<IVampirePlayer>... skills) {
        return skill(parent, skills).faction(VReference.VAMPIRE_FACTION);
    }

    private final ResourceLocation parent;
    private final ISkill<?>[] skills;
    private ResourceLocation faction;
    private ResourceLocation[] lockingSkillNodes;

    public SkillNodeBuilder(ResourceLocation parent, ISkill<?>... skills) {
        this.parent = parent;
        this.skills = skills;
        this.lockingSkillNodes = new ResourceLocation[0];
    }

    public ResourceLocation build(Consumer<FinishedSkillNode> consumer, ResourceLocation id) {
        if (faction != null) {
            id = new ResourceLocation(id.getNamespace(), faction.getPath() + "/" + id.getPath());
        }
        this.validate(id);
        consumer.accept(new Result(id, this.parent, this.skills, this.lockingSkillNodes));
        return id;
    }

    public SkillNodeBuilder faction(IPlayableFaction<?> faction) {
        this.faction = faction.getID();
        return this;
    }

    public SkillNodeBuilder lockingNodes(ResourceLocation... skillNodes) {
        this.lockingSkillNodes = skillNodes;
        return this;
    }

    private void validate(ResourceLocation id) {
        if (this.skills.length == 0) {
            throw new IllegalStateException("No skills defined for skill node " + id + "!");
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class Result implements FinishedSkillNode {
        private final ResourceLocation parent;
        private final ISkill<?>[] skills;
        private final ResourceLocation id;
        private final ResourceLocation[] lockingSkillNodes;

        public Result(ResourceLocation id, ResourceLocation parent, ISkill<?>[] skills, ResourceLocation[] lockingSkillNodes) {
            this.id = id;
            this.parent = parent;
            this.skills = skills;
            this.lockingSkillNodes = lockingSkillNodes;
        }

        @Override
        public ResourceLocation getID() {
            return id;
        }

        @Override
        public void serialize(JsonObject json) {
            json.addProperty("parent", parent.toString());
            JsonArray array = new JsonArray();
            for (ISkill<?> skill : this.skills) {
                array.add(RegUtil.id(skill) .toString());
            }
            json.add("skills", array);
            if (lockingSkillNodes.length > 0) {
                JsonArray nodes = new JsonArray();
                for (ResourceLocation lockingSkillNode : this.lockingSkillNodes) {
                    nodes.add(lockingSkillNode.toString());
                }
                json.add("locking", nodes);
            }
        }
    }
}
