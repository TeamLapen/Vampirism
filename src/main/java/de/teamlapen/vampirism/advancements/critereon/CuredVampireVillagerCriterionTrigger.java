package de.teamlapen.vampirism.advancements.critereon;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;

public class CuredVampireVillagerCriterionTrigger extends SimpleCriterionTrigger<CuredVampireVillagerCriterionTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "cured_vampire_villager");

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(@NotNull ServerPlayer player, @NotNull Entity vampire, @NotNull Villager villager) {
        LootContext lootcontext = EntityPredicate.createContext(player, vampire);
        LootContext lootcontext1 = EntityPredicate.createContext(player, villager);
        this.trigger(player, (instance) -> instance.test(lootcontext, lootcontext1));
    }

    @NotNull
    @Override
    protected Instance createInstance(@NotNull JsonObject json, @NotNull EntityPredicate.Composite entityPredicate, @NotNull DeserializationContext conditionsParser) {
        EntityPredicate.Composite vampire = EntityPredicate.Composite.fromJson(json, "vampire", conditionsParser);
        EntityPredicate.Composite villager = EntityPredicate.Composite.fromJson(json, "villager", conditionsParser);
        return new Instance(entityPredicate, vampire, villager);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        public static @NotNull Instance any() {
            return new Instance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY);
        }

        private final EntityPredicate.Composite vampire;
        private final EntityPredicate.Composite villager;

        public Instance(EntityPredicate.@NotNull Composite player, EntityPredicate.Composite vampire, EntityPredicate.Composite villager) {
            super(ID, player);
            this.vampire = vampire;
            this.villager = villager;
        }

        @NotNull
        @Override
        public JsonObject serializeToJson(@NotNull SerializationContext conditions) {
            JsonObject json = super.serializeToJson(conditions);
            json.add("vampire", this.vampire.toJson(conditions));
            json.add("villager", this.villager.toJson(conditions));
            return json;
        }

        public boolean test(@NotNull LootContext vampire, @NotNull LootContext villager) {
            if (!this.vampire.matches(vampire)) {
                return false;
            } else {
                return this.villager.matches(villager);
            }
        }
    }
}
