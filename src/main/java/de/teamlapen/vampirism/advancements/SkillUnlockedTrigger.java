package de.teamlapen.vampirism.advancements;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

public class SkillUnlockedTrigger extends SimpleCriterionTrigger<SkillUnlockedTrigger.Instance> {
    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "skill_unlocked");

    public static @NotNull Instance builder(@NotNull ISkill<?> skill) {
        return new Instance(skill);
    }

    public static @NotNull Instance builder(@NotNull ResourceLocation id) {
        return new Instance(id);
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(@NotNull ServerPlayer player, @NotNull ISkill<?> skill) {
        this.trigger(player, (instance -> instance.test(skill)));
    }

    @NotNull
    @Override
    protected Instance createInstance(@NotNull JsonObject json, @NotNull EntityPredicate.Composite entityPredicate, @NotNull DeserializationContext conditionsParser) {
        return new Instance(new ResourceLocation(GsonHelper.getAsString(json, "skill")));
    }

    static class Instance extends AbstractCriterionTriggerInstance {
        @NotNull
        private final ResourceLocation skillId;

        Instance(@NotNull ISkill<?> skill) {
            super(ID, EntityPredicate.Composite.ANY);
            this.skillId = RegUtil.id(skill) ;
        }

        Instance(@NotNull ResourceLocation skillId) {
            super(ID, EntityPredicate.Composite.ANY);
            this.skillId = skillId;
        }

        @NotNull
        @Override
        public JsonObject serializeToJson(@NotNull SerializationContext serializer) {
            JsonObject jsonObject = super.serializeToJson(serializer);
            jsonObject.addProperty("skill", skillId.toString());
            return jsonObject;
        }

        public boolean test(@NotNull ISkill<?> skill) {
            return this.skillId.equals(RegUtil.id(skill) );
        }
    }
}
