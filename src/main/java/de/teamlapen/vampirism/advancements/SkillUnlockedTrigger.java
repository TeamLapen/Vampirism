package de.teamlapen.vampirism.advancements;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SkillUnlockedTrigger extends SimpleCriterionTrigger<SkillUnlockedTrigger.Instance> {
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

    public void trigger(ServerPlayer player, ISkill skill) {
        this.trigger(player, (instance -> {
            return instance.test(skill);
        }));
    }

    @Override
    protected Instance createInstance(JsonObject json, EntityPredicate.Composite entityPredicate, DeserializationContext conditionsParser) {
        return new Instance(new ResourceLocation(GsonHelper.getAsString(json, "skill")));
    }

    static class Instance extends AbstractCriterionTriggerInstance {
        @Nonnull
        private final ResourceLocation skillId;

        Instance(@Nonnull ISkill skill) {
            super(ID, EntityPredicate.Composite.ANY);
            this.skillId = skill.getRegistryName();
        }

        Instance(@Nonnull ResourceLocation skillId) {
            super(ID, EntityPredicate.Composite.ANY);
            this.skillId = skillId;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext serializer) {
            JsonObject jsonObject = super.serializeToJson(serializer);
            jsonObject.addProperty("skill", skillId.toString());
            return jsonObject;
        }

        public boolean test(@Nonnull ISkill skill) {
            return this.skillId.equals(skill.getRegistryName());
        }
    }
}
