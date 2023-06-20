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
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
                if(modelLoc.getPath().contains("fluid")){
                    LOGGER.info(modelLoc);
                }
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
     * @param event
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

    private static boolean shouldShowInTooltip(int p_242394_0_, @NotNull ItemStack.TooltipPart p_242394_1_) {
        return (p_242394_0_ & p_242394_1_.getMask()) == 0;
    }

    private int getHideFlags(@NotNull ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("HideFlags", 99) ? stack.getTag().getInt("HideFlags") : 0;
    }
}
