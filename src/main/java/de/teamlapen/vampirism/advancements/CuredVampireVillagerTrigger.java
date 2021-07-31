package de.teamlapen.vampirism.advancements;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class CuredVampireVillagerTrigger extends AbstractCriterionTrigger<CuredVampireVillagerTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "cured_vampire_villager");

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, ConvertedVillagerEntity vampire, VillagerEntity villager) {
        LootContext lootcontext = EntityPredicate.createContext(player, vampire);
        LootContext lootcontext1 = EntityPredicate.createContext(player, villager);
        this.trigger(player, (instance) -> instance.test(lootcontext, lootcontext1));
    }

    @Nonnull
    @Override
    protected Instance createInstance(@Nonnull JsonObject json, @Nonnull EntityPredicate.AndPredicate entityPredicate, @Nonnull ConditionArrayParser conditionsParser) {
        EntityPredicate.AndPredicate vampire = EntityPredicate.AndPredicate.fromJson(json, "vampire", conditionsParser);
        EntityPredicate.AndPredicate villager = EntityPredicate.AndPredicate.fromJson(json, "villager", conditionsParser);
        return new Instance(entityPredicate, vampire, villager);
    }

    public static class Instance extends CriterionInstance {
        public static Instance any() {
            return new Instance(EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.ANY, EntityPredicate.AndPredicate.ANY);
        }
        private final EntityPredicate.AndPredicate vampire;
        private final EntityPredicate.AndPredicate villager;

        public Instance(EntityPredicate.AndPredicate player, EntityPredicate.AndPredicate vampire, EntityPredicate.AndPredicate villager) {
            super(ID, player);
            this.vampire = vampire;
            this.villager = villager;
        }

        @Nonnull
        @Override
        public JsonObject serializeToJson(@Nonnull ConditionArraySerializer conditions) {
            JsonObject json = super.serializeToJson(conditions);
            json.add("vampire", this.vampire.toJson(conditions));
            json.add("villager", this.villager.toJson(conditions));
            return json;
        }

        public boolean test(LootContext vampire, LootContext villager) {
            if (!this.vampire.matches(vampire)) {
                return false;
            } else {
                return this.villager.matches(villager);
            }
        }
    }
}
