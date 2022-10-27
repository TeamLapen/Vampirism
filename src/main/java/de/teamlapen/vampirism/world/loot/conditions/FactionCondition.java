package de.teamlapen.vampirism.world.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.entity.factions.PlayableFaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class FactionCondition implements ILootCondition {

    private final @Nonnull Type type;
    private final IPlayableFaction<? extends IFactionPlayer<?>> faction;
    private final int minLevel;
    private final int maxLevel;

    public FactionCondition(IPlayableFaction<? extends IFactionPlayer<?>> faction, int minLevel, int maxLevel) {
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
    public @Nonnull LootConditionType getType() {
        return ModLoot.faction;
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.getParamOrNull(LootParameters.THIS_ENTITY);
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            switch (this.type) {
                case FACTION:
                    if (this.faction == null) return false;
                    return VampirismAPI.getFactionPlayerHandler(player).filter(a -> a.isInFaction(this.faction)).filter(a -> a.getCurrentLevel() >= this.minLevel && (this.maxLevel == -1 || a.getCurrentLevel() <= this.maxLevel)).isPresent();
                case NO_FACTION:
                    return VampirismAPI.getFactionPlayerHandler(player).filter(p -> !p.getCurrentFactionPlayer().isPresent()).isPresent();
                case ANY_FACTION:
                    return VampirismAPI.getFactionPlayerHandler(player).filter(a -> a.getCurrentLevel() >= this.minLevel && (this.maxLevel == -1 || a.getCurrentLevel() <= this.maxLevel)).isPresent();
            }
        }
        return false;
    }

    public enum Type {
        NO_FACTION, ANY_FACTION, FACTION
    }

    public static class Serializer implements ILootSerializer<FactionCondition> {

        @Override
        public void serialize(@Nonnull JsonObject json, @Nonnull FactionCondition condition, @Nonnull JsonSerializationContext context) {
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

        @Nonnull
        @Override
        public FactionCondition deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext context) {
            JsonObject json = jsonObject.get("predicate").getAsJsonObject();
            Type type = Type.valueOf(json.get("type").getAsString());
            int minLevel;
            int maxLevel;
            switch (type) {
                case NO_FACTION:
                    return new FactionCondition();
                case ANY_FACTION:
                    minLevel = json.has("min_level") ? json.get("min_level").getAsInt() : 0;
                    maxLevel = json.has("max_level") ? json.get("max_level").getAsInt() : -1;
                    return new FactionCondition(minLevel, maxLevel);
                case FACTION:
                    ResourceLocation factionId = new ResourceLocation(json.get("faction").getAsString());
                    IFaction<?> faction = VampirismAPI.factionRegistry().getFactionByID(factionId);
                    minLevel = json.has("min_level") ? json.get("min_level").getAsInt() : 0;
                    maxLevel = json.has("max_level") ? json.get("max_level").getAsInt() : -1;
                    return new FactionCondition(faction instanceof PlayableFaction<?> ? ((PlayableFaction) faction) : null, minLevel, maxLevel);
            }
            return null;
        }
    }
}
