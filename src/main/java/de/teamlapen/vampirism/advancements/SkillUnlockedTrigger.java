package de.teamlapen.vampirism.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.util.REFERENCE;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SkillUnlockedTrigger extends AbstractCriterionTrigger<SkillUnlockedTrigger.Instance> {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "skill_unlocked");

    public SkillUnlockedTrigger() {
        super(ID, Listeners::new);
    }

    public static Instance builder(ISkill skill) {
        return new Instance(skill);
    }

    public static Instance builder(ResourceLocation id) {
        return new Instance(id);
    }

    public void trigger(ServerPlayerEntity player, ISkill skill) {
        Listeners listeners = (Listeners) this.listenersForPlayers.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger(skill);
        }
    }

    @Override
    public Instance deserializeInstance(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return new Instance(new ResourceLocation(JSONUtils.getString(jsonObject, "skill")));
    }

    static class Instance extends CriterionInstance {
        @Nonnull
        private final ResourceLocation skillId;

        Instance(@Nonnull ISkill skill) {
            super(ID);
            this.skillId = skill.getRegistryName();
        }

        Instance(@Nonnull ResourceLocation skillId) {
            super(ID);
            this.skillId = skillId;
        }

        public boolean test(@Nonnull ISkill skill) {
            return this.skillId.equals(skill.getRegistryName());
        }

        @Override
        public JsonElement serialize() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("skill", skillId.toString());
            return jsonObject;
        }
    }

    static class Listeners extends GenericListeners<Instance> {

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        void trigger(ISkill skill) {
            List<Listener<Instance>> list = null;

            for (Listener<Instance> listener : this.playerListeners) {
                if (listener.getCriterionInstance().test(skill)) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }
                    list.add(listener);
                }
            }

            if (list != null) {
                for (Listener<Instance> listener : list) {
                    listener.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}
