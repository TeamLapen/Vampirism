package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.color.item.QuarrelTint;
import de.teamlapen.vampirism.client.color.item.OilBottleTint;
import de.teamlapen.vampirism.client.extensions.ItemExtensions;
import de.teamlapen.vampirism.client.models.armor.*;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.client.models.items.properties.BloodFilled;
import de.teamlapen.vampirism.client.models.items.properties.ClipFilled;
import de.teamlapen.vampirism.client.models.items.properties.HasName;
import de.teamlapen.vampirism.client.models.items.properties.HunterCrossbowCharging;
import de.teamlapen.vampirism.client.models.items.ShatteredArmorModel;
import de.teamlapen.vampirism.client.models.items.properties.HunterCrossbowPull;
import de.teamlapen.vampirism.client.models.items.properties.StrongOil;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.util.ColorListsUtil;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Handles item render registration
 */
public class ModItemsRender {

    public static void registerColors(RegisterColorHandlersEvent.@NotNull ItemTintSources event) {
        event.register(QuarrelTint.ID, QuarrelTint.CODEC);
        event.register(OilBottleTint.ID, OilBottleTint.CODEC);
    }

    public static void registerRangeSelector(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(BloodFilled.ID, BloodFilled.CODEC);
        event.register(ClipFilled.ID, ClipFilled.CODEC);
        event.register(HunterCrossbowPull.ID, HunterCrossbowPull.CODEC);
    }

    public static void registerConditional(RegisterConditionalItemModelPropertyEvent event) {
        event.register(HasName.ID, HasName.CODEC);
        event.register(HunterCrossbowCharging.ID, HunterCrossbowCharging.CODEC);
        event.register(StrongOil.ID, StrongOil.CODEC);
    }

    public static void registerItemModels(RegisterItemModelsEvent event) {
        event.register(ShatteredArmorModel.Unbaked.ID, ShatteredArmorModel.Unbaked.CODEC);
    }

    public static void registerItemDecorator(RegisterItemDecorationsEvent event) {
        Stream.of(ModItems.BASIC_CROSSBOW, ModItems.ENHANCED_CROSSBOW, ModItems.BASIC_DOUBLE_CROSSBOW, ModItems.ENHANCED_DOUBLE_CROSSBOW, ModItems.BASIC_TECH_CROSSBOW, ModItems.ENHANCED_TECH_CROSSBOW)
                .forEach(item -> event.register(item, ModItemDecorators.CROSSBOW_AMMUNITION));
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new ItemExtensions.VampireArmorItemExtension(ModEntitiesRender.CLOAK, CloakModel::new), ColorListsUtil.VAMPIRE_CLOAKS.values().toArray(Item[]::new));
        event.registerItem(new ItemExtensions.VampireArmorItemExtension(ModEntitiesRender.HUNTER_HAT_TALL, part -> new ClothedModel<>(part, false)), ModItems.HUNTER_HAT_TALL.get());
        event.registerItem(new ItemExtensions.VampireArmorItemExtension(ModEntitiesRender.HUNTER_HAT_BROAD, part -> new ClothedModel<>(part, false)), ModItems.HUNTER_HAT_BROAD.get());
        event.registerItem(new ItemExtensions.VampireArmorItemExtension(ModEntitiesRender.CLOTHING_CROWN, part -> new ClothedModel<>(part, false)), ModItems.VAMPIRE_CLOTHING_CROWN.get());
        event.registerItem(new ItemExtensions.VampireArmorItemExtension(ModEntitiesRender.CLOTHING_HAT, part -> new ClothedModel<>(part, false)), ModItems.VAMPIRE_CLOTHING_HAT.get());
        event.registerItem(new ItemExtensions.VampireArmorItemExtension(ModEntitiesRender.CLOTHING_PANTS, part -> new ClothedModel<>(part, false)), ModItems.VAMPIRE_CLOTHING_LEGS.get());
        event.registerItem(new ItemExtensions.VampireArmorItemExtension(ModEntitiesRender.CLOTHING_BOOTS, (part) -> new ClothedModel<>(part, false)), ModItems.VAMPIRE_CLOTHING_BOOTS.get());
        event.registerItem(ItemExtensions.HUNTER_CROSSBOW, ModItems.BASIC_CROSSBOW.get(), ModItems.ENHANCED_CROSSBOW.get(), ModItems.BASIC_DOUBLE_CROSSBOW.get(), ModItems.ENHANCED_DOUBLE_CROSSBOW.get(), ModItems.BASIC_TECH_CROSSBOW.get(), ModItems.ENHANCED_TECH_CROSSBOW.get());
        event.registerItem(ItemExtensions.CRUCIFIX, ModItems.CRUCIFIX_NORMAL.get(), ModItems.CRUCIFIX_ENHANCED.get(), ModItems.CRUCIFIX_ULTIMATE.get());
        event.registerItem(ItemExtensions.SYRINGE, ModItems.SYRINGE_EMPTY);
    }
}
