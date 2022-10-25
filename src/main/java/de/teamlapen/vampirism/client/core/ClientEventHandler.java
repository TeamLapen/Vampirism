package de.teamlapen.vampirism.client.core;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.api.items.oil.IArmorOil;
import de.teamlapen.vampirism.api.items.oil.IToolOil;
import de.teamlapen.vampirism.api.items.oil.IWeaponOil;
import de.teamlapen.vampirism.client.model.blocks.BakedAltarInspirationModel;
import de.teamlapen.vampirism.client.model.blocks.BakedBloodContainerModel;
import de.teamlapen.vampirism.client.model.blocks.BakedWeaponTableModel;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.effects.VampirismPotion;
import de.teamlapen.vampirism.player.LevelAttributeModifier;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handle general client side events
 */
@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    public static final ResourceLocation COFFIN_SHEET = new ResourceLocation(REFERENCE.MODID, "textures/atlas/coffins.png");
    private final static Logger LOGGER = LogManager.getLogger();


    @SubscribeEvent
    public static void onModelBakeEvent(ModelBakeEvent event) {
        /*
         * Not really a clean solution but it works
         * Bake the each model, then replace the fluid texture with impure blood, then bake it again.
         * Use {@link BakedBloodContainerModel} to render to appropriate one
         */
        try {
            for (int x = 0; x < BakedBloodContainerModel.FLUID_LEVELS; x++) {
                ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, "block/blood_container/fluid_" + (x + 1));
                IUnbakedModel model = event.getModelLoader().getModelOrMissing(loc);
                BakedBloodContainerModel.BLOOD_FLUID_MODELS[x] = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, loc);
                if (model instanceof BlockModel) {
                    ((BlockModel) model).textureMap.put("fluid", Either.left(ForgeHooksClient.getBlockMaterial(ModFluids.IMPURE_BLOOD.get().getAttributes().getStillTexture())));
                    BakedBloodContainerModel.IMPURE_BLOOD_FLUID_MODELS[x] = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, loc);
                } else {
                    LOGGER.error("Cannot apply impure blood texture to blood container model {}", model);
                    BakedBloodContainerModel.IMPURE_BLOOD_FLUID_MODELS[x] = BakedBloodContainerModel.BLOOD_FLUID_MODELS[x];
                }
            }
            Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();
            ArrayList<ResourceLocation> modelLocations = Lists.newArrayList();

            for (ResourceLocation modelLoc : registry.keySet()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID) && modelLoc.getPath().equals(ModBlocks.BLOOD_CONTAINER.getId().getPath())) {
                    modelLocations.add(modelLoc);
                }
            }

            // replace the registered tank block variants with TankModelFactories

            IBakedModel registeredModel;
            IBakedModel newModel;
            for (ResourceLocation loc : modelLocations) {
                registeredModel = event.getModelRegistry().get(loc);
                newModel = new BakedBloodContainerModel(registeredModel);
                event.getModelRegistry().put(loc, newModel);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load fluid models for blood container", e);
        }

        try {
            for (int x = 0; x < BakedAltarInspirationModel.FLUID_LEVELS; x++) {
                ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, "block/altar_inspiration/blood" + (x + 1));
                IUnbakedModel model = event.getModelLoader().getModelOrMissing(loc);
                BakedAltarInspirationModel.FLUID_MODELS[x] = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, loc);
            }
            Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();
            ArrayList<ResourceLocation> modelLocations = Lists.newArrayList();

            for (ResourceLocation modelLoc : registry.keySet()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID) && modelLoc.getPath().equals(ModBlocks.ALTAR_INSPIRATION.getId().getPath())) {
                    modelLocations.add(modelLoc);
                }
            }

            // replace the registered tank block variants with TankModelFactories

            IBakedModel registeredModel;
            IBakedModel newModel;
            for (ResourceLocation loc : modelLocations) {
                registeredModel = event.getModelRegistry().get(loc);
                newModel = new BakedAltarInspirationModel(registeredModel);
                event.getModelRegistry().put(loc, newModel);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load fluid models for altar inspiration", e);
        }

        try {
            for (int x = 0; x < BakedWeaponTableModel.FLUID_LEVELS; x++) {
                ResourceLocation loc = new ResourceLocation(REFERENCE.MODID, "block/weapon_table/weapon_table_lava" + (x + 1));
                IUnbakedModel model = event.getModelLoader().getModelOrMissing(loc);
                BakedWeaponTableModel.FLUID_MODELS[x][0] = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y180, loc);
                BakedWeaponTableModel.FLUID_MODELS[x][1] = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y270, loc);
                BakedWeaponTableModel.FLUID_MODELS[x][2] = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, loc);
                BakedWeaponTableModel.FLUID_MODELS[x][3] = model.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y90, loc);
            }
            Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();
            ArrayList<ResourceLocation> modelLocations = Lists.newArrayList();

            for (ResourceLocation modelLoc : registry.keySet()) {
                if (modelLoc.getNamespace().equals(REFERENCE.MODID) && modelLoc.getPath().equals(ModBlocks.WEAPON_TABLE.getId().getPath())) {
                    modelLocations.add(modelLoc);
                }
            }

            // replace the registered tank block variants with TankModelFactories

            IBakedModel registeredModel;
            IBakedModel newModel;
            for (ResourceLocation loc : modelLocations) {
                registeredModel = event.getModelRegistry().get(loc);
                newModel = new BakedWeaponTableModel(registeredModel);
                event.getModelRegistry().put(loc, newModel);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load fluid models for weapon crafting table", e);

        }
    }

    @SubscribeEvent
    public void onFovOffsetUpdate(FOVUpdateEvent event) {
        if (VampirismConfig.CLIENT.disableFovChange.get() && Helper.isVampire(event.getEntity())) {
            ModifiableAttributeInstance speed = event.getEntity().getAttribute(Attributes.MOVEMENT_SPEED);
            AttributeModifier vampirespeed = speed.getModifier(LevelAttributeModifier.getUUID(Attributes.MOVEMENT_SPEED));
            if (vampirespeed == null)
                return;
            //removes speed buffs, add speed buffs without the vampire speed
            event.setNewfov((float) (((double) (event.getFov()) * ((vampirespeed.getAmount() + 1) * (double) (event.getEntity().abilities.getWalkingSpeed()) + speed.getValue())) / ((vampirespeed.getAmount() + 1) * ((double) (event.getEntity().abilities.getWalkingSpeed()) + speed.getValue()))));
        }
    }

    @SubscribeEvent
    public void onToolTip(ItemTooltipEvent event) {
        if (VampirismPotion.isHunterPotion(event.getItemStack(), true).map(Potion::getEffects).map(effectInstances -> effectInstances.stream().map(EffectInstance::getEffect).anyMatch(Effect::isBeneficial)).orElse(false) && (event.getPlayer() == null || !Helper.isHunter(event.getPlayer()))) {
            event.getToolTip().add(new TranslationTextComponent("text.vampirism.hunter_potion.deadly").withStyle(TextFormatting.DARK_RED));
        }

    }

    @SubscribeEvent
    public void onWorldClosed(WorldEvent.Unload event) {
        ((ClientProxy) VampirismMod.proxy).clearBossBarOverlay();
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        for (DyeColor dye : DyeColor.values()) {
            ModelLoader.addSpecialModel(new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_bottom_" + dye.getName()));
            ModelLoader.addSpecialModel(new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_top_" + dye.getName()));
            ModelLoader.addSpecialModel(new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_" + dye.getName()));
        }
    }

    /**
     * This event will handle all items except {@link de.teamlapen.vampirism.api.items.IFactionLevelItem}s. Their oil
     * @param event
     */
    @SubscribeEvent
    public void onItemToolTip(@Nonnull ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof IFactionExclusiveItem) return;
        OilUtils.getAppliedOilStatus(event.getItemStack()).ifPresent(oil -> {
            List<ITextComponent> toolTips = event.getToolTip();
            int position = 1;
            int flags = getHideFlags(event.getItemStack());
            if (shouldShowInTooltip(flags, ItemStack.TooltipDisplayFlags.ADDITIONAL)) {
                ArrayList<ITextComponent> additionalComponents = new ArrayList<>();
                event.getItemStack().getItem().appendHoverText(event.getItemStack(), Minecraft.getInstance().player == null ? null : Minecraft.getInstance().player.level, additionalComponents, event.getFlags());
                position += additionalComponents.size();
                Optional<ITextComponent> oilTooltip = oil.getKey().getToolTipLine(event.getItemStack(), oil.getKey(), oil.getValue(), event.getFlags());
                if (oilTooltip.isPresent()) {
                    toolTips.add(position++, oilTooltip.get());
                }
            }
            List<ITextComponent> factionToolTips = new ArrayList<>();
            factionToolTips.add(StringTextComponent.EMPTY);
            factionToolTips.add(new TranslationTextComponent("text.vampirism.faction_specifics").withStyle(TextFormatting.GRAY));
            factionToolTips.add(new TranslationTextComponent(" ").append(VReference.HUNTER_FACTION.getName()).append(new TranslationTextComponent("text.vampirism.faction_only")).withStyle(Minecraft.getInstance().player != null ? Helper.isHunter(Minecraft.getInstance().player) ? TextFormatting.DARK_GREEN : TextFormatting.DARK_RED : TextFormatting.GRAY));
            toolTips.addAll(Math.min(event.getToolTip().size(), position), factionToolTips);
        });
    }

    private static boolean shouldShowInTooltip(int p_242394_0_, ItemStack.TooltipDisplayFlags p_242394_1_) {
        return (p_242394_0_ & p_242394_1_.getMask()) == 0;
    }

    private int getHideFlags(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("HideFlags", 99) ? stack.getTag().getInt("HideFlags") : 0;
    }
}
