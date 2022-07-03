package de.teamlapen.vampirism.advancements;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SkillUnlockedTrigger extends SimpleCriterionTrigger<SkillUnlockedTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "skill_unlocked");

    public static Instance builder(ISkill<?> skill) {
        return new Instance(skill);
    }

    public static Instance builder(ResourceLocation id) {
        return new Instance(id);
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayer player, ISkill<?> skill) {
        this.trigger(player, (instance -> instance.test(skill)));
    }

    @Nonnull
    @Override
    protected Instance createInstance(JsonObject json, EntityPredicate.Composite entityPredicate, DeserializationContext conditionsParser) {
        return new Instance(new ResourceLocation(GsonHelper.getAsString(json, "skill")));
    }

    static class Instance extends AbstractCriterionTriggerInstance {
        @Nonnull
        private final ResourceLocation skillId;

        Instance(@Nonnull ISkill<?> skill) {
            super(ID, EntityPredicate.Composite.ANY);
            this.skillId = RegUtil.id(skill) ;
        }

        Instance(@Nonnull ResourceLocation skillId) {
            super(ID, EntityPredicate.Composite.ANY);
            this.skillId = skillId;
        }

        @Nonnull
        @Override
        public JsonObject serializeToJson(SerializationContext serializer) {
            JsonObject jsonObject = super.serializeToJson(serializer);
            jsonObject.addProperty("skill", skillId.toString());
            return jsonObject;
        }

        public boolean test(@Nonnull ISkill<?> skill) {
            return this.skillId.equals(RegUtil.id(skill) );
        }
    }
}
