package de.teamlapen.vampirism.data.recipebuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class SkillNodeBuilder {

    private final ResourceLocation parent;
    private final ISkill[] skills;
    private ResourceLocation faction;

    public SkillNodeBuilder(ResourceLocation parent, ISkill... skills) {
        this.parent = parent;
        this.skills = skills;
    }

    public SkillNodeBuilder faction (IPlayableFaction<?> faction) {
        this.faction = faction.getID();
        return this;
    }

    private void validate(ResourceLocation id) {
        if(this.skills == null || this.skills.length == 0) {
            throw new IllegalStateException("No skills defined for skill node " + id + "!");
        }else if(this.parent == null) {
            throw new IllegalStateException("No parent skill is set for skill node " + id + "!");
        }
    }

    public ResourceLocation build(Consumer<FinishedSkillNode> consumer, ResourceLocation id) {
        if(faction != null) {
            id = new ResourceLocation(id.getNamespace(), faction.getPath() + "/" + id.getPath());
        }
        this.validate(id);
        consumer.accept(new Result(id, this.parent, skills));
        return id;
    }

    private static class Result implements FinishedSkillNode {
        private final ResourceLocation parent;
        private final ISkill[] skills;
        private final ResourceLocation id;

        public Result(ResourceLocation id, ResourceLocation parent, ISkill[] skills) {
            this.id = id;
            this.parent = parent;
            this.skills = skills;
        }

        @Override
        public void serialize(JsonObject json) {
            json.addProperty("parent", parent.toString());
            JsonArray array = new JsonArray();
            for (ISkill skill : this.skills) {
                array.add(skill.getRegistryName().toString());
            }
            json.add("skills", array);
        }

        @Override
        public ResourceLocation getID() {
            return id;
        }
    }

    public static SkillNodeBuilder skill(ResourceLocation parent, ISkill... skills) {
        return new SkillNodeBuilder(parent, skills);
    }

    public static SkillNodeBuilder hunter(ResourceLocation parent, ISkill... skills) {
        return skill(parent, skills).faction(VReference.HUNTER_FACTION);
    }

    public static SkillNodeBuilder vampire(ResourceLocation parent, ISkill... skills) {
        return skill(parent, skills).faction(VReference.VAMPIRE_FACTION);
    }
}
