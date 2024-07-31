package com.xiaohunao.mine_team.client.gui.team;

import com.google.common.collect.Maps;
import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.mixin.ImageButtonAccessor;
import com.xiaohunao.mine_team.common.network.TeamColorSyncPayload;
import com.xiaohunao.mine_team.common.network.TeamPvPSyncPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TeamRender {
    private final EffectRenderingInventoryScreen<? extends AbstractContainerMenu> screen;


    private ImageButton teamIcon;
    private ImageButton teamPVPOn;
    private ImageButton teamPVPOff;
    private final Map<String, ImageButton> teamSmallIcons = Maps.newHashMap();

    public TeamRender(EffectRenderingInventoryScreen<? extends AbstractContainerMenu> screen) {
        this.screen = screen;
    }
    public void renderTeamIcon(GuiGraphics guiGraphics, int mouseX, int mouseY,float partialTick){
        this.teamIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.teamPVPOff.render(guiGraphics, mouseX, mouseY, partialTick);
        this.teamPVPOn.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTeamSmallIcon(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void initButton(){
        CompoundTag tag = Minecraft.getInstance().player.getPersistentData();
        String teamColor = tag.getString("teamColor");
        boolean teamPvP = tag.getBoolean("teamPvP");

        int iconSize = 16;
        int off = 6;
        this.teamIcon = new ImageButton(screen.leftPos - iconSize,screen.topPos, iconSize, iconSize, createWidgetSprites("team/" + teamColor + "_team_icon"),  button-> {
            this.teamIcon.visible = false;
            this.teamPVPOn.visible = false;
            this.teamPVPOff.visible = false;
            visibleTeamSmallIcon(true);
        });
        this.teamPVPOff = new ImageButton(screen.leftPos - iconSize,screen.topPos + iconSize + off, iconSize, iconSize,
                createWidgetSprites("team/pvp/" + teamColor + "_pvp_off"),
                button-> setTeamPvP(true));
        this.teamPVPOn = new ImageButton(screen.leftPos - iconSize,screen.topPos + iconSize + off, iconSize, iconSize,
                createWidgetSprites("team/pvp/" + teamColor + "_pvp_on"),
                button-> setTeamPvP(false));
        initSmallIcon();
        hasEnableTeamPvP();
        addRenderableWidget();
    }

    private void initSmallIcon(){
        List<String> teamColors = Arrays.stream(ChatFormatting.values())
                .filter(ChatFormatting::isColor)
                .map(ChatFormatting::getName)
                .toList().reversed();

        int size = 8;
        for (int i = 0; i < teamColors.size(); i++) {
            String newTeamColor = teamColors.get(i);
            int x = screen.leftPos - size -(i / 8) * size - (i / 8) * 2;
            int y = screen.topPos + (i % 8) * size + (i % 8) * 2;

            ImageButton teamSmallIconBtn = new ImageButton(x, y, size, size, createWidgetSprites("team/small/" + newTeamColor + "_team_small_icon"),
                    button -> teamSmallIconButtonPressed(newTeamColor));
            teamSmallIconBtn.visible = false;
            teamSmallIcons.put(newTeamColor, teamSmallIconBtn);
        }
    }

    public void addRenderableWidget() {
        screen.addRenderableWidget(this.teamIcon);
        screen.addRenderableWidget(this.teamPVPOn);
        screen.addRenderableWidget(this.teamPVPOff);
        for (ImageButton button : teamSmallIcons.values()) {
            screen.addRenderableWidget(button);
        }
    }

    private void teamSmallIconButtonPressed(String teamColor){
        setTeamColor(teamColor);
        this.teamIcon.visible = true;
        visibleTeamSmallIcon(false);
        hasEnableTeamPvP();
    }

    private void setTeamColor(String teamColor){
        Minecraft.getInstance().player.getPersistentData().putString("teamColor", teamColor);
        PacketDistributor.sendToServer(new TeamColorSyncPayload(teamColor));
        setImageButtonSprites(this.teamIcon, "team/" + teamColor + "_team_icon");
        setImageButtonSprites(this.teamPVPOn, "team/pvp/" + teamColor + "_pvp_on");
        setImageButtonSprites(this.teamPVPOff, "team/pvp/" + teamColor + "_pvp_off");
    }

    public void setTeamPvP(boolean friendlyFire) {
        PacketDistributor.sendToServer(new TeamPvPSyncPayload(friendlyFire));
        Minecraft.getInstance().player.getPersistentData().putBoolean("teamPvP", friendlyFire);
        this.teamPVPOn.visible = friendlyFire;
        this.teamPVPOff.visible = !friendlyFire;
    }

    private void hasEnableTeamPvP() {
        boolean teamPvP = Minecraft.getInstance().player.getPersistentData().getBoolean("teamPvP");
        this.teamPVPOn.visible = teamPvP;
        this.teamPVPOff.visible = !teamPvP;
    }

    private void visibleTeamSmallIcon(boolean visible){
        for (ImageButton button : teamSmallIcons.values()) {
            button.visible = visible;
        }
    }
    private void renderTeamSmallIcon(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
        for (ImageButton button : teamSmallIcons.values()) {
            button.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
    private void setImageButtonSprites(ImageButton button, String path) {
        ((ImageButtonAccessor)button).setSprites(createWidgetSprites(path));
    }
    private WidgetSprites createWidgetSprites(String path) {
        return new WidgetSprites(MineTeam.asResource(path), MineTeam.asResource(path));
    }

}
