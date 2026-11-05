package de.teamlapen.vampirism.client.gui.screens;

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
import de.teamlapen.vampirism.common.network.packets.server.ServerboundAppearancePacket;
import de.teamlapen.vampirism.common.util.LordTitles;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VampirePlayerAppearanceScreen extends AppearanceScreen<Player> {

    private static final Component NAME = Component.translatable("gui.vampirism.appearance");

    private boolean titleGender;
    private IWingsEntity.Texture wingsTexture = IWingsEntity.Texture.DEFAULT;
    private List<IWingsEntity.Texture> availableWingsTextures = List.of();

    @SuppressWarnings("DataFlowIssue")
    public VampirePlayerAppearanceScreen(@Nullable ILastScreenProvider backScreen) {
        super(NAME, Minecraft.getInstance().player, backScreen);
    }

    @Override
    public void removed() {
        var map = new AppearanceKey.AppearanceMap();
        map.set(VampirePlayer.TitleGenderType, titleGender ? IPlayableFaction.TitleGender.FEMALE : IPlayableFaction.TitleGender.MALE);
        map.set(VampirePlayer.WingsTexture, wingsTexture);
        VampirismMod.proxy.sendToServer(new ServerboundAppearancePacket(this.entity.getId(), new AppearancePacket(map)));
        super.removed();
    }

    @Override
    protected void init() {
        VampirePlayer vampire = VampirePlayer.get(player());
        var customization = vampire.getCustomization();
        this.titleGender = vampire.getPlayerLord().map(ILordPlayer::titleGender).orElse(IPlayableFaction.TitleGender.UNKNOWN) == IPlayableFaction.TitleGender.FEMALE;
        this.wingsTexture = customization.wingsTexture();
        if (IDraculaPlayer.getPresentDracula(player()).isPresent()) {
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

        vertical.addChild(new ExtendedButton(0, 0, 120, 20, Component.translatable("gui.vampirism.appearance.face_editor"), _ -> minecraft.setScreen(new VampireFaceEditorScreen(ILastScreenProvider.current()))));

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

        StringWidget titlePreview = new StringWidget(titlePreview(), minecraft.font);
        Checkbox titleGenderBox = Checkbox.builder(Component.translatable("gui.vampirism.appearance.title_gender"), minecraft.font).selected(titleGender).onValueChange((button, selected) -> {
            titleGender = selected;
            FactionPlayerHandler.get(entity).getPlayerLord().ifPresent(lord -> lord.setTitleGender(titleGender));
            titlePreview.setMessage(titlePreview());
        }).build();
        vertical.addChild(titleGenderBox);
        vertical.addChild(titlePreview);

        return vertical;
    }

    private void wingsTexture(int wingsTexture) {
        VampirePlayer vampire = VampirePlayer.get(player());
        vampire.getCustomization().setWingsTexture(this.wingsTexture = availableWingsTextures.get(wingsTexture));
    }

    private Component titlePreview() {        int level = VampirePlayer.get(player()).getPlayerLord().map(ILordPlayer::getLordLevel).orElse(0);
        if (level < 1) return Component.empty();
        return Component.translatable("gui.vampirism.appearance.title_preview", new LordTitles.VampireTitles().getLordTitle(level, titleGender ? IPlayableFaction.TitleGender.FEMALE : IPlayableFaction.TitleGender.MALE)).withStyle(ChatFormatting.GRAY);
    }
}
