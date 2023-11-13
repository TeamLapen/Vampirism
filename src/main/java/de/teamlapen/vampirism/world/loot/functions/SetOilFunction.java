package de.teamlapen.vampirism.world.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SetOilFunction extends LootItemConditionalFunction {

    private final IOil oil;
    private final boolean random;

    SetOilFunction(LootItemCondition[] conditions, IOil oil, boolean random) {
        super(conditions);
        this.oil = oil;
        this.random = random;
    }

    @Override
    protected @NotNull ItemStack run(@NotNull ItemStack pStack, @NotNull LootContext pContext) {
        IOil oil = this.oil;
        if (this.random) {
            Collection<IOil> values = ModRegistries.OILS.get().getValues();
            oil = values.stream().skip((int) (values.size() * pContext.getRandom().nextDouble())).findFirst().orElseThrow(() -> new IllegalStateException("No oils registered"));
        }
        OilUtils.setOil(pStack, oil);
        return pStack;
    }

    public static LootItemConditionalFunction.Builder<?> setOil(IOil oil) {
        return simpleBuilder((conditions) -> new SetOilFunction(conditions, oil, false));
    }

    public static LootItemConditionalFunction.Builder<?> random() {
        return simpleBuilder((conditions) -> new SetOilFunction(conditions, null, true));
    }

    @Override
    public @NotNull LootItemFunctionType getType() {
        return ModLoot.SET_OIL.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetOilFunction> {

        @Override
        public void serialize(@NotNull JsonObject pJson, @NotNull SetOilFunction pValue, @NotNull JsonSerializationContext pSerializationContext) {
            super.serialize(pJson, pValue, pSerializationContext);
            if (pValue.random) {
                pJson.addProperty("random", true);
            } else if (pValue.oil != null) {
                //noinspection DataFlowIssue
                pJson.addProperty("id", ModRegistries.OILS.get().getKey(pValue.oil).toString());
            } else {
                throw new IllegalStateException("Either random or oil must be set");
            }
        }

        @Override
        public @NotNull SetOilFunction deserialize(@NotNull JsonObject pObject, @NotNull JsonDeserializationContext pDeserializationContext, LootItemCondition @NotNull [] pConditions) {
            if (pObject.has("random") && GsonHelper.getAsBoolean(pObject, "random")) {
                return new SetOilFunction(pConditions, null, true);
            } else if (pObject.has("id")) {
                String id = GsonHelper.getAsString(pObject, "id");
                IOil oil = ModRegistries.OILS.get().getValue(ResourceLocation.tryParse(id));
                if (oil == null) {
                    throw new JsonSyntaxException("Unknown oil " + id);
                }
                return new SetOilFunction(pConditions, oil, false);
            } else {
                throw new JsonSyntaxException("Either random or oil must be set");
            }
        }
    }
}
