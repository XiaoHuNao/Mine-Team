package com.xiaohunao.mine_team.common.event;

import com.xiaohunao.mine_team.MineTeam;

import com.xiaohunao.mine_team.common.network.TeamColorSyncPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.Locale;

@EventBusSubscriber(modid = MineTeam.MOD_ID)
public class LevelEventSubscriber {
    @SubscribeEvent
    public static void onCreateSpawnPosition(LevelEvent.CreateSpawnPosition event) {
        ServerLevel level = (ServerLevel)event.getLevel();
        ServerScoreboard scoreboard = level.getServer().getScoreboard();
        Arrays.stream(ChatFormatting.values())
                .filter(ChatFormatting::isColor)
                .map(ChatFormatting::getName)
                .filter(name -> scoreboard.getPlayerTeam(name) == null)
                .forEach(name -> {
                    PlayerTeam team = scoreboard.addPlayerTeam(name);
                    team.setColor(ChatFormatting.valueOf(name.toUpperCase(Locale.ROOT)));
                    team.setDisplayName(Component.translatable(MineTeam.asResourceKey("team." + name)));
                });
    }
    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        ServerLevel level = (ServerLevel)player.level();
        Scoreboard scoreboard = level.getServer().getScoreboard();
        PlayerTeam team = scoreboard.getPlayersTeam(player.getScoreboardName());
        if (team == null) {
            PlayerTeam playerTeam = scoreboard.getPlayerTeam("white");
            if (playerTeam != null) {
                scoreboard.addPlayerToTeam(player.getScoreboardName(), playerTeam);
                player.getPersistentData().putString("teamColor", "white");
                PacketDistributor.sendToPlayer((ServerPlayer) player, new TeamColorSyncPayload("white"));
            }
        }
    }



}
