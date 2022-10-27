package de.teamlapen.vampirism.world.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.core.ModLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public class FactionCondition implements LootItemCondition {

    private final @NotNull Type type;
    private final IFaction<?> faction;
    private final int minLevel;
    private final int maxLevel;

    public FactionCondition(IFaction<?> faction, int minLevel, int maxLevel) {
        this.type = Type.FACTION;
        this.faction = faction;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    public FactionCondition(int minLevel, int maxLevel) {
        this.type = Type.ANY_FACTION;
        this.faction = null;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    public FactionCondition() {
        this.type = Type.ANY_FACTION;
        this.faction = null;
        this.minLevel = 0;
        this.maxLevel = -1;
    }

    @Override
    public @NotNull LootItemConditionType getType() {
        return ModLoot.faction.get();
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity instanceof Player player) {
            switch (this.type) {
                case FACTION -> {
                    if (this.faction == null) return false;
                    return VampirismAPI.getFactionPlayerHandler(player).filter(a -> a.isInFaction(this.faction)).filter(a -> a.getCurrentLevel() >= this.minLevel && (this.maxLevel == -1 || a.getCurrentLevel() <= this.maxLevel)).isPresent();
                }
                case NO_FACTION -> {
                    return VampirismAPI.getFactionPlayerHandler(player).filter(p -> p.getCurrentFactionPlayer().isEmpty()).isPresent();
                }
                case ANY_FACTION -> {
                    return VampirismAPI.getFactionPlayerHandler(player).filter(a -> a.getCurrentLevel() >= this.minLevel && (this.maxLevel == -1 || a.getCurrentLevel() <= this.maxLevel)).isPresent();
                }
            }
        }
        return false;
    }

    public enum Type {
        NO_FACTION, ANY_FACTION, FACTION
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<FactionCondition> {

        @Override
        public void serialize(@NotNull JsonObject json, @NotNull FactionCondition condition, @NotNull JsonSerializationContext context) {
            json.addProperty("type", condition.type.name());
            switch (condition.type) {
                case FACTION:
                    json.addProperty("faction", condition.faction.getID().toString());
                case ANY_FACTION :
                    json.addProperty("min_level", condition.minLevel);
                    if (condition.maxLevel != -1) json.addProperty("max_level", condition.maxLevel);
                    break;
            }
        }

        @NotNull
        @Override
        public FactionCondition deserialize(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext context) {
            JsonObject json = jsonObject.get("predicate").getAsJsonObject();
            Type type = Type.valueOf(json.get("type").getAsString());
            switch (type) {
                case NO_FACTION -> {
                    return new FactionCondition();
                }
                case ANY_FACTION -> {
                    int minLevel = json.has("min_level") ? json.get("min_level").getAsInt() : 0;
                    int maxLevel = json.has("max_level") ? json.get("max_level").getAsInt() : -1;
                    return new FactionCondition(minLevel, maxLevel);
                }
                case FACTION -> {
                    ResourceLocation factionId = new ResourceLocation(json.get("faction").getAsString());
                    IFaction<?> faction = VampirismAPI.factionRegistry().getFactionByID(factionId);
                    int minLevel = json.has("min_level") ? json.get("min_level").getAsInt() : 0;
                    int maxLevel = json.has("max_level") ? json.get("max_level").getAsInt() : -1;
                    return new FactionCondition(faction, minLevel, maxLevel);
                }
            }
            return null;
        }
    }
}
