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
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SkillNodeBuilder {

    public static @NotNull SkillNodeBuilder skill(@NotNull ResourceLocation parent, @NotNull ISkill<?>... skills) {
        return new SkillNodeBuilder(parent, skills);
    }

    @SafeVarargs
    public static SkillNodeBuilder hunter(@NotNull ResourceLocation parent, @NotNull ISkill<IHunterPlayer>... skills) {
        return skill(parent, skills).faction(VReference.HUNTER_FACTION);
    }

    @SafeVarargs
    public static SkillNodeBuilder vampire(@NotNull ResourceLocation parent, @NotNull ISkill<IVampirePlayer>... skills) {
        return skill(parent, skills).faction(VReference.VAMPIRE_FACTION);
    }

    private final @NotNull ResourceLocation parent;
    private final ISkill<?>[] skills;
    private ResourceLocation faction;
    private ResourceLocation[] lockingSkillNodes;

    public SkillNodeBuilder(@NotNull ResourceLocation parent, @NotNull ISkill<?>... skills) {
        this.parent = parent;
        this.skills = skills;
        this.lockingSkillNodes = new ResourceLocation[0];
    }

    public @NotNull ResourceLocation build(@NotNull Consumer<FinishedSkillNode> consumer, @NotNull ResourceLocation id) {
        if (faction != null) {
            id = new ResourceLocation(id.getNamespace(), faction.getPath() + "/" + id.getPath());
        }
        this.validate(id);
        consumer.accept(new Result(id, this.parent, this.skills, this.lockingSkillNodes));
        return id;
    }

    public @NotNull SkillNodeBuilder faction(@NotNull IPlayableFaction<?> faction) {
        this.faction = faction.getID();
        return this;
    }

    public @NotNull SkillNodeBuilder lockingNodes(@NotNull ResourceLocation... skillNodes) {
        this.lockingSkillNodes = skillNodes;
        return this;
    }

    private void validate(@NotNull ResourceLocation id) {
        if (this.skills.length == 0) {
            throw new IllegalStateException("No skills defined for skill node " + id + "!");
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class Result implements FinishedSkillNode {
        private final @NotNull ResourceLocation parent;
        private final ISkill<?>[] skills;
        private final @NotNull ResourceLocation id;
        private final ResourceLocation[] lockingSkillNodes;

        public Result(@NotNull ResourceLocation id, @NotNull ResourceLocation parent, @NotNull ISkill<?>[] skills, @NotNull ResourceLocation[] lockingSkillNodes) {
            this.id = id;
            this.parent = parent;
            this.skills = skills;
            this.lockingSkillNodes = lockingSkillNodes;
        }

        @Override
        public @NotNull ResourceLocation getID() {
            return id;
        }

        @Override
        public void serialize(@NotNull JsonObject json) {
            json.addProperty("parent", parent.toString());
            JsonArray array = new JsonArray();
            for (ISkill<?> skill : this.skills) {
                array.add(RegUtil.id(skill).toString());
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
