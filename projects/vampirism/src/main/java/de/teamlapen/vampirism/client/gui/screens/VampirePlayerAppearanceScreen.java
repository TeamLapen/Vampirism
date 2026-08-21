package de.teamlapen.vampirism.client.gui.screens;

import com.google.common.collect.Streams;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.common.world.entities.appearance.AppearanceKey;
import de.teamlapen.faction.common.world.entities.appearance.AppearancePacket;
import de.teamlapen.gui.components.DropdownWidget;
import de.teamlapen.gui.components.IRenderLast;
import de.teamlapen.faction.client.gui.screens.AppearanceScreen;
import de.teamlapen.faction.client.gui.screens.ILastScreenProvider;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDraculaPlayer;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IWingsEntity;
import de.teamlapen.vampirism.client.renderer.TextureLoader;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundAppearancePacket;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.SequencedMap;
import java.util.stream.IntStream;

public class VampirePlayerAppearanceScreen extends AppearanceScreen<Player> {

    private static final Component NAME = Component.translatable("gui.vampirism.appearance");

    @UnknownNullability
    private Identifier fangType;
    @UnknownNullability
    private Identifier eyeType;
    private boolean glowingEyes;
    private boolean titleGender;
    private IWingsEntity.Texture wingsTexture = IWingsEntity.Texture.DEFAULT;
    private List<IWingsEntity.Texture> availableWingsTextures = List.of();
    private final SequencedMap<Identifier, Identifier> fangTextures;
    private final SequencedMap<Identifier, Identifier> eyeTextures;


    public VampirePlayerAppearanceScreen(@Nullable ILastScreenProvider backScreen) {
        super(NAME, Minecraft.getInstance().player, backScreen);
        this.fangTextures = TextureLoader.mapTexturesInById("textures/entity/fangs");
        this.eyeTextures = TextureLoader.mapTexturesInById("textures/entity/eyes");
    }

    @Override
    public void removed() {
        var map = new AppearanceKey.AppearanceMap();
        map.set(VampirePlayer.FangType, fangType);
        map.set(VampirePlayer.EyeType, eyeType);
        map.set(VampirePlayer.GlowingEye, glowingEyes);
        map.set(VampirePlayer.TitleGenderType, titleGender ? IPlayableFaction.TitleGender.FEMALE : IPlayableFaction.TitleGender.MALE);
        map.set(VampirePlayer.WingsTexture, wingsTexture);
        VampirismMod.proxy.sendToServer(new ServerboundAppearancePacket(this.entity.getId(), new AppearancePacket(map)));
        super.removed();
    }

    @Override
    protected void init() {
        VampirePlayer vampire = VampirePlayer.get(minecraft.player);
        var customization = vampire.getCustomization();
        this.fangType = customization.fangType();
        this.eyeType = customization.eyeType();
        this.glowingEyes = customization.glowingEyes();
        this.titleGender = vampire.getPlayerLord().map(ILordPlayer::titleGender).orElse(IPlayableFaction.TitleGender.UNKNOWN) == IPlayableFaction.TitleGender.FEMALE;
        this.wingsTexture = customization.wingsTexture();
        if (IDraculaPlayer.getPresentDracula(minecraft.player).isPresent()) {
            this.availableWingsTextures = VampirismMod.services().wingsManager().getAvailableWings(vampire.asEntity()).sorted().toList();
        }
        super.init();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTicks) {
        super.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTicks);

        for (Renderable renderable : this.renderables) {
            if (renderable instanceof IRenderLast last) {
                last.renderLast(GuiGraphicsExtractor, mouseX, mouseY, partialTicks);
            }
        }
    }

    @Override
    protected LayoutElement createLayout() {
        LinearLayout vertical = LinearLayout.vertical();
        vertical.spacing(4);

        vertical.addChild(DropdownWidget.<Identifier>builder(0,0)
                        .width(120)
                        .itemHeight(20)
                        .maxVisibleItems(5)
                        .initialSelection(this.eyeType)
                        .onSelect(this::eye)
                        .onHover(this::hoverEye)
                .items(Streams.mapWithIndex(this.eyeTextures.sequencedEntrySet().stream(), (from, index) -> new DropdownWidget.Value<>(from.getKey(), Component.translatable("gui.vampirism.appearance.eye").append(" " + (index + 1))))
                        .toList())
                        .build());

        vertical.addChild(DropdownWidget.<Identifier>builder(0,0)
                        .width(120)
                        .itemHeight(20)
                        .maxVisibleItems(5)
                        .initialSelection(this.fangType)
                        .onSelect(this::fang)
                        .onHover(this::hoverFang)
                .items(Streams.mapWithIndex(this.fangTextures.sequencedEntrySet().stream(), (from, index) -> new DropdownWidget.Value<>(from.getKey(), Component.translatable("gui.vampirism.appearance.fang").append(" " + (index + 1))))
                        .toList())
                        .build());

        if (this.availableWingsTextures.size() > 1) {
            vertical.addChild(DropdownWidget.simple(0,0)
                    .width(120)
                    .itemHeight(20)
                    .maxVisibleItems(5)
                    .initialSelection(this.wingsTexture.ordinal())
                    .onSelect(this::wingsTexture)
                    .simpleItems(availableWingsTextures.stream().map(x -> (Component)Component.translatable("gui.vampirism.wings", x.name)).toList())
                    .build());
        }

        vertical.addChild(Checkbox.builder(Component.translatable("gui.vampirism.appearance.title_gender"), minecraft.font).selected(titleGender).onValueChange((button, selected) -> {
            titleGender = selected;
            FactionPlayerHandler.get(entity).getPlayerLord().ifPresent(lord -> lord.setTitleGender(titleGender));
        }).build());

        vertical.addChild(Checkbox.builder(Component.translatable("gui.vampirism.appearance.glowing_eye"), minecraft.font).selected(glowingEyes).onValueChange((button, selected) -> {
            glowingEyes = selected;
            VampirePlayer.get(entity).setGlowingEyes(glowingEyes);
        }).build());


        return vertical;
    }

    private void eye(Identifier eyeType) {
        VampirePlayer vampire = VampirePlayer.get(this.minecraft.player);
        vampire.setEyeType(this.eyeType = eyeType);
    }

    private void wingsTexture(int wingsTexture) {
        VampirePlayer vampire = VampirePlayer.get(this.minecraft.player);
        vampire.getCustomization().setWingsTexture(this.wingsTexture = availableWingsTextures.get(wingsTexture));
    }

    private void fang(Identifier fangType) {
        VampirePlayer vampire = VampirePlayer.get(this.minecraft.player);
        vampire.setFangType(this.fangType = fangType);
    }

    private void hoverEye(Identifier eyeType, boolean hovered) {
        VampirePlayer vampire = VampirePlayer.get(this.minecraft.player);
        if (hovered) {
            vampire.setEyeType(eyeType);
        } else {
            if (vampire.getEyeType() == eyeType) {
                vampire.setEyeType(this.eyeType);
            }
        }
    }

    private void hoverFang(Identifier fangType, boolean hovered) {
        VampirePlayer vampire = VampirePlayer.get(this.minecraft.player);
        if (hovered) {
            vampire.setFangType(fangType);
        } else {
            if (vampire.getFangType() == fangType) {
                vampire.setFangType(this.fangType);
            }
        }
    }
}