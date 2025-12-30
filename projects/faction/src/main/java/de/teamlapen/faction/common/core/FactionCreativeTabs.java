package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.util.REFERENCE;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, REFERENCE.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FACTIONS_TAB = CREATIVE_TABS.register(REFERENCE.MOD_ID,
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.factionapi"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> FactionItems.OBLIVION_POTION.get().getDefaultInstance())
                    .displayItems(new FactionsDisplayItemGenerator())
                    .build());

    static void register(IEventBus bus) {
        CREATIVE_TABS.register(bus);
    }

    public static class FactionsDisplayItemGenerator implements CreativeModeTab.DisplayItemsGenerator {

        @Override
        public void accept(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
            output.accept(FactionItems.OBLIVION_POTION.get());
            output.accept(FactionBlocks.TOTEM_BASE.get());
            output.accept(FactionBlocks.TOTEM_TOP.get());
            output.accept(FactionBlocks.TOTEM_TOP_CRAFTED.get());
        }
    }
}
