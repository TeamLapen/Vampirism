package de.teamlapen.vampirism.advancements;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SkillUnlockedTrigger extends AbstractCriterionTrigger<SkillUnlockedTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "skill_unlocked");

    public static Instance builder(ISkill skill) {
        return new Instance(skill);
    }

    public static Instance builder(ResourceLocation id) {
        return new Instance(id);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, ISkill skill) {
        this.trigger(player, (instance -> {
            return instance.test(skill);
        }));
    }

    @Override
    protected Instance createInstance(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        return new Instance(new ResourceLocation(JSONUtils.getAsString(json, "skill")));
    }

    static class Instance extends CriterionInstance {
        @Nonnull
        private final ResourceLocation skillId;

        Instance(@Nonnull ISkill skill) {
            super(ID, EntityPredicate.AndPredicate.ANY);
            this.skillId = skill.getRegistryName();
        }

        Instance(@Nonnull ResourceLocation skillId) {
            super(ID, EntityPredicate.AndPredicate.ANY);
            this.skillId = skillId;
        }

        @Override
        public JsonObject serializeToJson(ConditionArraySerializer serializer) {
            JsonObject jsonObject = super.serializeToJson(serializer);
            jsonObject.addProperty("skill", skillId.toString());
            return jsonObject;
        }

        public boolean test(@Nonnull ISkill skill) {
            return this.skillId.equals(skill.getRegistryName());
        }
    }
}
