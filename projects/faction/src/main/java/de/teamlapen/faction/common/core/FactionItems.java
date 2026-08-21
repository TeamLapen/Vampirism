package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.world.items.OblivionPotionItem;
import de.teamlapen.faction.common.world.items.consume.FactionBasedConsumeEffect;
import de.teamlapen.faction.common.world.items.consume.OblivionEffect;
import de.teamlapen.faction.common.world.items.consume.PlayerFactionConsumeEffect;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(REFERENCE.MOD_ID);
    public static final DeferredRegister<ConsumeEffect.Type<?>> CONSUME_EFFECTS = DeferredRegister.create(Registries.CONSUME_EFFECT_TYPE, REFERENCE.MOD_ID);

    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<OblivionEffect>> OBLIVION = CONSUME_EFFECTS.register("oblivious", () -> new ConsumeEffect.Type<>(OblivionEffect.CODEC, OblivionEffect.STREAM_CODEC));
    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<FactionBasedConsumeEffect>> FACTION_BASED = CONSUME_EFFECTS.register("faction_based", () -> new ConsumeEffect.Type<>(FactionBasedConsumeEffect.CODEC, FactionBasedConsumeEffect.STREAM_CODEC));
    public static final DeferredHolder<ConsumeEffect.Type<?>, ConsumeEffect.Type<PlayerFactionConsumeEffect>> PLAYER_FACTION_BASED = CONSUME_EFFECTS.register("player_faction_based", () -> new ConsumeEffect.Type<>(PlayerFactionConsumeEffect.CODEC, PlayerFactionConsumeEffect.STREAM_CODEC));


    public static final DeferredItem<OblivionPotionItem> OBLIVION_POTION = ITEMS.registerItem("oblivion_potion", props -> new OblivionPotionItem(props.stacksTo(1).rarity(Rarity.UNCOMMON).component(DataComponents.CONSUMABLE, Consumables.defaultDrink().onConsume(new OblivionEffect()).build()).component(FactionDataComponents.OBLIVION_CHARGES, 24)));
    public static final DeferredItem<BlockItem> TOTEM_BASE = ITEMS.registerSimpleBlockItem(FactionBlocks.TOTEM_BASE);
    public static final DeferredItem<BlockItem> TOTEM_TOP = ITEMS.registerSimpleBlockItem(FactionBlocks.TOTEM_TOP);
    public static final DeferredItem<BlockItem> TOTEM_TOP_CRAFTED = ITEMS.registerSimpleBlockItem(FactionBlocks.TOTEM_TOP_CRAFTED);


    static void register(IEventBus bus) {
        ITEMS.register(bus);
        CONSUME_EFFECTS.register(bus);
    }
}
