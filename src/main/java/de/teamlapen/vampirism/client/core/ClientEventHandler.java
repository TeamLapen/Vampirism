package de.teamlapen.vampirism.client.core;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.client.model.blocks.BakedAltarInspirationModel;
import de.teamlapen.vampirism.client.model.blocks.BakedBloodContainerModel;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.data.ClientSkillTreeData;
import de.teamlapen.vampirism.effects.VampirismPotion;
import de.teamlapen.vampirism.entity.player.LevelAttributeModifier;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handle general client side events
 */
@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    private final static Logger LOGGER = LogManager.getLogger();
    private static final String VAMPIRISM_2D_PACK_ID = "vampirism2dtextures";
    private static final String BUILTIN_COMPAT = "modcompat";


    static void onModelBakeRequest(ModelEvent.RegisterAdditional event){

        for (int x = 0; x < BakedBloodContainerModel.FLUID_LEVELS; x++) {
            event.register(new ResourceLocation(REFERENCE.MODID, "block/blood_container/blood_" + (x + 1)));
            event.register(new ResourceLocation(REFERENCE.MODID, "block/blood_container/impure_blood_" + (x + 1)));
        }

        for (int x = 0; x < BakedAltarInspirationModel.FLUID_LEVELS; x++) {
            event.register(new ResourceLocation(REFERENCE.MODID, "block/altar_inspiration/blood" + (x + 1)));
        }

        }
    static void onModelBakeEvent(@NotNull ModelEvent.ModifyBakingResult event) {
        /*
         * Not really a clean solution but it works
         * We have one model file for each fluid and fill level. It is requested to be loaded in {@link onModelBakeRequest}. Here we gather these and store them in {@link BakedBloodContainerModel} which is used to render to appropriate one
         * Might be worth looking into Forge's {@link DynamicFluidContainerModel}
         */
        try {
            for (int x = 0; x < BakedBloodContainerModel.FLUID_LEVELS; x++) {
                ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, "block/blood_container/blood_" + (x + 1));
                ResourceLocation locImpure = new ResourceLocation(REFERENCE.MODID, "block/blood_container/impure_blood_" + (x + 1));

                BakedBloodContainerModel.BLOOD_FLUID_MODELS[x] = event.getModels().get(loc);
                BakedBloodContainerModel.IMPURE_BLOOD_FLUID_MODELS[x] = event.getModels().get(locImpure);
            }
            Map<ResourceLocation, BakedModel> registry = event.getModels();
            ArrayList<ResourceLocation> modelLocations = Lists.newArrayList();

            for (ResourceLocation modelLoc : registry.keySet()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID) && modelLoc.getPath().equals(ModBlocks.BLOOD_CONTAINER.getId().getPath())) {
                    modelLocations.add(modelLoc);
                }
            }

            // replace the registered tank block variants with TankModelFactories

            BakedModel registeredModel;
            BakedModel newModel;
            for (ResourceLocation loc : modelLocations) {
                registeredModel = event.getModels().get(loc);
                newModel = new BakedBloodContainerModel(registeredModel);
                event.getModels().put(loc, newModel);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load fluid models for blood container", e);
        }

        try {
            for (int x = 0; x < BakedAltarInspirationModel.FLUID_LEVELS; x++) {
                ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, "block/altar_inspiration/blood" + (x + 1));
                BakedAltarInspirationModel.FLUID_MODELS[x] = event.getModels().get(loc);
            }
            Map<ResourceLocation, BakedModel> registry = event.getModels();
            ArrayList<ResourceLocation> modelLocations = Lists.newArrayList();

            for (ResourceLocation modelLoc : registry.keySet()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID) && modelLoc.getPath().equals(ModBlocks.ALTAR_INSPIRATION.getId().getPath())) {
                    modelLocations.add(modelLoc);
                }
            }

            BakedModel registeredModel;
            BakedModel newModel;
            for (ResourceLocation loc : modelLocations) {
                registeredModel = event.getModels().get(loc);
                newModel = new BakedAltarInspirationModel(registeredModel);
                event.getModels().put(loc, newModel);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load fluid models for altar inspiration", e);
        }
    }

    @SubscribeEvent
    public void onFovOffsetUpdate(@NotNull ComputeFovModifierEvent event) {
        if (VampirismConfig.CLIENT.disableFovChange.get() && Helper.isVampire(event.getPlayer())) {
            AttributeInstance speed = event.getPlayer().getAttribute(Attributes.MOVEMENT_SPEED);
            AttributeModifier vampirespeed = speed.getModifier(LevelAttributeModifier.getUUID(Attributes.MOVEMENT_SPEED));
            if (vampirespeed == null) {
                return;
            }
            //removes speed buffs, add speed buffs without the vampire speed
            event.setNewFovModifier((float) (((double) (event.getFovModifier()) * ((vampirespeed.getAmount() + 1) * (double) (event.getPlayer().getAbilities().getWalkingSpeed()) + speed.getValue())) / ((vampirespeed.getAmount() + 1) * ((double) (event.getPlayer().getAbilities().getWalkingSpeed()) + speed.getValue()))));
        }
    }

    @SubscribeEvent
    public void onToolTip(@NotNull ItemTooltipEvent event) {
        if (VampirismPotion.isHunterPotion(event.getItemStack(), true).map(Potion::getEffects).map(effectInstances -> effectInstances.stream().map(MobEffectInstance::getEffect).anyMatch(MobEffect::isBeneficial)).orElse(false) && (event.getEntity() == null || !Helper.isHunter(event.getEntity()))) {
            event.getToolTip().add(Component.translatable("text.vampirism.hunter_potion.deadly").withStyle(ChatFormatting.DARK_RED));
        }

    }

    @SubscribeEvent
    public void onWorldClosed(LevelEvent.Unload event) {
        ((ClientProxy) VampirismMod.proxy).clearBossBarOverlay();
    }

    static void onModelRegistry(@NotNull ModelEvent.RegisterAdditional event) {
        for (DyeColor dye : DyeColor.values()) {
            event.register(new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_bottom_" + dye.getName()));
            event.register(new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_top_" + dye.getName()));
            event.register(new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_" + dye.getName()));
        }
    }

    /**
     * This event will handle all items except {@link de.teamlapen.vampirism.api.items.IFactionLevelItem}s. Their oil 
     */
    @SubscribeEvent
    public void onItemToolTip(@NotNull ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof IFactionExclusiveItem) return;
        OilUtils.getAppliedOilStatus(event.getItemStack()).ifPresent(oil -> {
            List<Component> toolTips = event.getToolTip();
            int position = 1;
            int flags = getHideFlags(event.getItemStack());
            if (shouldShowInTooltip(flags, ItemStack.TooltipPart.ADDITIONAL)) {
                ArrayList<Component> additionalComponents = new ArrayList<>();
                event.getItemStack().getItem().appendHoverText(event.getItemStack(), Minecraft.getInstance().player == null ? null : Minecraft.getInstance().player.level(), additionalComponents, event.getFlags());
                position += additionalComponents.size();
                Optional<Component> oilTooltip = oil.getKey().getToolTipLine(event.getItemStack(), oil.getKey(), oil.getValue(), event.getFlags());
                if (oilTooltip.isPresent()) {
                    toolTips.add(position++, oilTooltip.get());
                }
            }
            List<Component> factionToolTips = new ArrayList<>();
            factionToolTips.add(Component.empty());
            factionToolTips.add(Component.translatable("text.vampirism.faction_specifics").withStyle(ChatFormatting.GRAY));
            factionToolTips.add(Component.literal(" ").append(VReference.HUNTER_FACTION.getName()).append(Component.translatable("text.vampirism.faction_only")).withStyle(Minecraft.getInstance().player != null ? Helper.isHunter(Minecraft.getInstance().player) ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED : ChatFormatting.GRAY));
            toolTips.addAll(Math.min(event.getToolTip().size(), position), factionToolTips);
        });
    }

    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ClientProxy.get().getBlockEntityItemRenderer());
    }

    public static void registerStageEvent(RenderLevelStageEvent.RegisterStageEvent event) {
        ClientProxy.get().registerBlockEntityItemRenderer();
    }

    private static boolean shouldShowInTooltip(int p_242394_0_, @NotNull ItemStack.TooltipPart p_242394_1_) {
        return (p_242394_0_ & p_242394_1_.getMask()) == 0;
    }

    private int getHideFlags(@NotNull ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("HideFlags", 99) ? stack.getTag().getInt("HideFlags") : 0;
    }

    public static void registerPackRepository(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addRepositorySource(s -> s.accept(Pack.readMetaAndCreate(VAMPIRISM_2D_PACK_ID, Component.literal("Vanilla Style Vampirism"), false, new Pack.ResourcesSupplier() {
                @Override
                public @NotNull PackResources openPrimary(@NotNull String pId) {
                    return new PathPackResources(pId, ModList.get().getModFileById(REFERENCE.MODID).getFile().findResource("packs/" + VAMPIRISM_2D_PACK_ID), false);
                }

                @Override
                public @NotNull PackResources openFull(@NotNull String pId, Pack.@NotNull Info pInfo) {
                    return openPrimary(pId);
                }
            }, PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN)));
        }
        if (event.getPackType() == PackType.SERVER_DATA) {
            event.addRepositorySource(s -> s.accept(Pack.create(BUILTIN_COMPAT, Component.literal("Vampirism builtin mod compatibility data"), true,  new Pack.ResourcesSupplier() {
                @Override
                public @NotNull PackResources openPrimary(@NotNull String pId) {
                    return new PathPackResources(pId, ModList.get().getModFileById(REFERENCE.MODID).getFile().findResource("packs/" + BUILTIN_COMPAT), true);
                }

                @Override
                public @NotNull PackResources openFull(@NotNull String pId, Pack.@NotNull Info pInfo) {
                    return openPrimary(pId);
                }
            }, new Pack.Info(Component.literal("Vampirism builtin mod compatibility data"), PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of(), true), Pack.Position.TOP, true, PackSource.DEFAULT)));
        }
    }

    @SubscribeEvent
    public void onJoined(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientSkillTreeData.reset();
    }
}
