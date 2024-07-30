package com.xiaohunao.mine_team.common.mixin;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.network.TeamColorSyncPayload;
import com.xiaohunao.mine_team.common.network.TeamPvPSyncPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu>{
    private ImageButton teamIcon;
    private ImageButton teamPVPOn;
    private ImageButton teamPVPOff;

    private ImageButton blackTeamSmallIcon;
    private ImageButton darkBlueTeamSmallIcon;
    private ImageButton darkGreenTeamSmallIcon;
    private ImageButton darkAquaTeamSmallIcon;
    private ImageButton darkRedTeamSmallIcon;
    private ImageButton darkPurpleTeamSmallIcon;
    private ImageButton goldTeamSmallIcon;
    private ImageButton grayTeamSmallIcon;
    private ImageButton darkGrayTeamSmallIcon;
    private ImageButton blueTeamSmallIcon;
    private ImageButton greenTeamSmallIcon;
    private ImageButton aquaTeamSmallIcon;
    private ImageButton redTeamSmallIcon;
    private ImageButton lightPurpleTeamSmallIcon;
    private ImageButton yellowTeamSmallIcon;
    private ImageButton whiteTeamSmallIcon;


    public InventoryScreenMixin(InventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        CompoundTag tag = this.minecraft.player.getPersistentData();
        String teamColor = tag.getString("teamColor");
        boolean teamPvP = tag.getBoolean("teamPvP");

        int iconSize = 16;
        int off = 6;

        WidgetSprites teamIconSprites = new WidgetSprites(
                MineTeam.asResource("team/" + teamColor + "_team_icon"), MineTeam.asResource("team/" + teamColor + "_team_icon")
        );
        WidgetSprites teamPVPOnSprites = new WidgetSprites(
                MineTeam.asResource("team/pvp/" + teamColor + "_pvp_on"), MineTeam.asResource("team/pvp/" + teamColor + "_pvp_on")
        );
        WidgetSprites teamPVPOffSprites = new WidgetSprites(
                MineTeam.asResource("team/pvp/" + teamColor + "_pvp_off"), MineTeam.asResource("team/pvp/" + teamColor + "_pvp_off")
        );


        this.teamIcon = new ImageButton(this.leftPos - iconSize,this.topPos, iconSize, iconSize, teamIconSprites,  button-> {
            this.teamIcon.visible = false;
            this.teamPVPOn.visible = false;
            this.teamPVPOff.visible = false;
            team$visibleTeamSmallIcon(true);
        });
        this.teamPVPOff = new ImageButton(this.leftPos - iconSize,this.topPos + iconSize + off, iconSize, iconSize, teamPVPOffSprites,  button-> {
            team$setFriendlyFire(true);
        });
        this.teamPVPOn = new ImageButton(this.leftPos - iconSize,this.topPos + iconSize + off, iconSize, iconSize, teamPVPOnSprites,  button-> {
            team$setFriendlyFire(false);
        });
        team$isFriendlyFire();
        this.addRenderableWidget(this.teamIcon);
        this.addRenderableWidget(this.teamPVPOff);
        this.addRenderableWidget(this.teamPVPOn);
        team$initIcon();
    }



    @Inject(method = "render", at = @At("HEAD"))
    private void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        this.teamIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.teamPVPOff.render(guiGraphics, mouseX, mouseY, partialTick);
        this.teamPVPOn.render(guiGraphics, mouseX, mouseY, partialTick);
        team$renderTeamIcon(guiGraphics, mouseX, mouseY, partialTick);
    }


    @Unique
    public void team$initIcon(){
        List<String> teamColors = Arrays.stream(ChatFormatting.values())
                .filter(ChatFormatting::isColor)
                .map(ChatFormatting::getName)
                .toList().reversed();

        int smallIconSize = 8;

        for (int i = 0; i < teamColors.size(); i++) {
            String newTeamColor = teamColors.get(i);
            ResourceLocation teamSmallIcon = MineTeam.asResource("team/small/" + newTeamColor + "_team_small_icon");
            WidgetSprites teamSmallIconSprites = new WidgetSprites(teamSmallIcon, teamSmallIcon);

            int x = this.leftPos - smallIconSize -(i / 8) * smallIconSize - (i / 8) * 2;
            int y = this.topPos + (i % 8) * smallIconSize + (i % 8) * 2;

            ImageButton teamSmallIconBtn = new ImageButton(x, y, smallIconSize, smallIconSize, teamSmallIconSprites, button-> {
                PacketDistributor.sendToServer(new TeamColorSyncPayload(newTeamColor));
                team$setTeamColor(newTeamColor);
            });
            teamSmallIconBtn.visible = false;
            switch (newTeamColor) {
                case "black":
                    this.blackTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "dark_blue":
                    this.darkBlueTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "dark_green":
                    this.darkGreenTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "dark_aqua":
                    this.darkAquaTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "dark_red":
                    this.darkRedTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "dark_purple":
                    this.darkPurpleTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "gold":
                    this.goldTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "gray":
                    this.grayTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "dark_gray":
                    this.darkGrayTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "blue":
                    this.blueTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "green":
                    this.greenTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "aqua":
                    this.aquaTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "red":
                    this.redTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "light_purple":
                    this.lightPurpleTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "yellow":
                    this.yellowTeamSmallIcon = teamSmallIconBtn;
                    break;
                case "white":
                    this.whiteTeamSmallIcon = teamSmallIconBtn;
                    break;
            }
            this.addRenderableWidget(teamSmallIconBtn);
        }
    }

    @Unique
    private void team$setTeamColor(String teamColor){
        this.minecraft.player.getPersistentData().putString("teamColor", teamColor);
        ImageButtonAccessor accessor = (ImageButtonAccessor) this.teamIcon;
        accessor.setSprites(new WidgetSprites(
                MineTeam.asResource("team/" + teamColor + "_team_icon"), MineTeam.asResource("team/" + teamColor + "_team_icon")
        ));

        ImageButtonAccessor accessor1 = (ImageButtonAccessor) this.teamPVPOn;
        accessor1.setSprites(new WidgetSprites(
                MineTeam.asResource("team/pvp/" + teamColor + "_pvp_on"), MineTeam.asResource("team/pvp/" + teamColor + "_pvp_on")
        ));

        ImageButtonAccessor accessor2 = (ImageButtonAccessor) this.teamPVPOff;
        accessor2.setSprites(new WidgetSprites(
                MineTeam.asResource("team/pvp/" + teamColor + "_pvp_off"), MineTeam.asResource("team/pvp/" + teamColor + "_pvp_off")
        ));
        this.teamIcon.visible = true;
        team$isFriendlyFire();
        team$visibleTeamSmallIcon(false);
    }

    @Unique
    public void team$setFriendlyFire(boolean friendlyFire) {
        PacketDistributor.sendToServer(new TeamPvPSyncPayload(friendlyFire));
        this.minecraft.player.getPersistentData().putBoolean("teamPvP", friendlyFire);
        this.teamPVPOn.visible = friendlyFire;
        this.teamPVPOff.visible = !friendlyFire;
    }

    @Unique
    private void team$isFriendlyFire() {
        boolean teamPvP = this.minecraft.player.getPersistentData().getBoolean("teamPvP");
        this.teamPVPOn.visible = teamPvP;
        this.teamPVPOff.visible = !teamPvP;
    }

    @Unique
    private void team$visibleTeamSmallIcon(boolean visible){
        this.blackTeamSmallIcon.visible = visible;
        this.darkBlueTeamSmallIcon.visible = visible;
        this.darkGreenTeamSmallIcon.visible = visible;
        this.darkAquaTeamSmallIcon.visible = visible;
        this.darkRedTeamSmallIcon.visible = visible;
        this.darkPurpleTeamSmallIcon.visible = visible;
        this.goldTeamSmallIcon.visible = visible;
        this.grayTeamSmallIcon.visible = visible;
        this.darkGrayTeamSmallIcon.visible = visible;
        this.blueTeamSmallIcon.visible = visible;
        this.greenTeamSmallIcon.visible = visible;
        this.aquaTeamSmallIcon.visible = visible;
        this.redTeamSmallIcon.visible = visible;
        this.lightPurpleTeamSmallIcon.visible = visible;
        this.yellowTeamSmallIcon.visible = visible;
        this.whiteTeamSmallIcon.visible = visible;
    }
    @Unique
    private void team$renderTeamIcon(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
        this.blackTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.darkBlueTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.darkGreenTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.darkAquaTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.darkRedTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.darkPurpleTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.goldTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.grayTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.darkGrayTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.blueTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.greenTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.aquaTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.redTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.lightPurpleTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.yellowTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.whiteTeamSmallIcon.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
