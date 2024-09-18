package com.xiaohunao.mine_team.client.gui.team;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.capability.TeamCapability;
import com.xiaohunao.mine_team.common.capability.TeamData;
import com.xiaohunao.mine_team.common.network.NetworkHandler;
import com.xiaohunao.mine_team.common.network.c2s.TeamDataSyncC2SPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TeamRender {
    private final EffectRenderingInventoryScreen<? extends AbstractContainerMenu> screen;
    private final LocalPlayer player;


    private ImageButton teamIcon;
    private ImageButton teamPVPOn;
    private ImageButton teamPVPOff;
    private final Map<String, ImageButton> teamSmallIcons = Maps.newHashMap();

    public TeamRender(EffectRenderingInventoryScreen<? extends AbstractContainerMenu> screen,LocalPlayer player) {
        this.screen = screen;
        this.player = player;
    }
    public void renderTeamIcon(GuiGraphics guiGraphics, int mouseX, int mouseY,float partialTick){
        this.teamIcon.render(guiGraphics, mouseX, mouseY, partialTick);
        this.teamPVPOff.render(guiGraphics, mouseX, mouseY, partialTick);
        this.teamPVPOn.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTeamSmallIcon(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void initButton(){
        TeamCapability.get(player).ifPresent(teamCapability -> {
            TeamData teamData = teamCapability.data;
            int iconSize = 16;
            int off = 6;

            this.teamIcon = new ImageButton(screen.leftPos - iconSize,screen.topPos, iconSize, iconSize,0,0,0,
                    MineTeam.asResource("textures/gui/team/" + teamData.getColor() + "_team_icon.png"),
                    16,16,
                    button-> {
                        this.teamIcon.visible = false;
                        this.teamPVPOn.visible = false;
                        this.teamPVPOff.visible = false;
                        visibleTeamSmallIcon(true);
                    });

            this.teamPVPOff = new ImageButton(screen.leftPos - iconSize,screen.topPos + iconSize + off, iconSize,iconSize, 0,0,0,
                    MineTeam.asResource("textures/gui/team/pvp/" + teamData.getColor() + "_pvp_off.png"),
                    iconSize,iconSize,
                    button-> setTeamPvP(true));

            this.teamPVPOn = new ImageButton(screen.leftPos - iconSize,screen.topPos + iconSize + off, iconSize, iconSize,0, 0,0,
                    MineTeam.asResource("textures/gui/team/pvp/" + teamData.getColor() + "_pvp_on.png"),
                    iconSize,iconSize,
                    button-> setTeamPvP(false));

            initSmallIcon();
            hasEnableTeamPvP();
            addRenderableWidget();
        });
    }

    private void initSmallIcon(){
        List<String> teamColors = Lists.reverse(Arrays.stream(ChatFormatting.values())
                .filter(ChatFormatting::isColor)
                .map(ChatFormatting::getName)
                .toList());

        int size = 8;
        for (int i = 0; i < teamColors.size(); i++) {
            String newTeamColor = teamColors.get(i);
            int x = screen.leftPos - size -(i / 8) * size - (i / 8) * 2;
            int y = screen.topPos + (i % 8) * size + (i % 8) * 2;

            ImageButton teamSmallIconBtn = new ImageButton(x, y, size, size,0, 0,0,
                    MineTeam.asResource("textures/gui/team/small/" + newTeamColor + "_team_small_icon.png"),
                    size,size,
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
        TeamCapability.get(player).ifPresent(teamCapability -> {
            TeamData teamData = teamCapability.data;
            teamData.setColor(teamColor);
            NetworkHandler.CHANNEL.sendToServer(new TeamDataSyncC2SPayload(player.getId(), teamData));
        });
        setImageButtonSprites(this.teamIcon, "textures/gui/team/" + teamColor + "_team_icon.png");
        setImageButtonSprites(this.teamPVPOn, "textures/gui/team/pvp/" + teamColor + "_pvp_on.png");
        setImageButtonSprites(this.teamPVPOff, "textures/gui/team/pvp/" + teamColor + "_pvp_off.png");
    }

    public void setTeamPvP(boolean friendlyFire) {
        TeamCapability.get(player).ifPresent(teamCapability -> {
            TeamData teamData = teamCapability.data;
            teamData.setPvP(friendlyFire);
            NetworkHandler.CHANNEL.sendToServer(new TeamDataSyncC2SPayload(player.getId(), teamData));
        });
        this.teamPVPOn.visible = friendlyFire;
        this.teamPVPOff.visible = !friendlyFire;
    }

    private void hasEnableTeamPvP() {
        TeamCapability.get(player).ifPresent(teamCapability -> {
            boolean teamPvP = teamCapability.data.isPvP();
            setTeamPvP(teamPvP);
        });
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
        button.resourceLocation = MineTeam.asResource(path);
    }


}
