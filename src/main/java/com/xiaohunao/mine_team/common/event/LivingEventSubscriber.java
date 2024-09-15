package com.xiaohunao.mine_team.common.event;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.config.MineTeamConfig;
import com.xiaohunao.mine_team.common.mixed.PlayerTeamMixed;
import com.xiaohunao.mine_team.common.network.NetworkHandler;
import com.xiaohunao.mine_team.common.network.c2s.TeamColorSyncC2SPayload;
import com.xiaohunao.mine_team.common.network.c2s.TeamPvPSyncC2SPayload;
import com.xiaohunao.mine_team.common.network.s2c.TeamColorSyncS2CPayload;
import com.xiaohunao.mine_team.common.network.s2c.TeamPvPSyncS2CPayload;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;


@Mod.EventBusSubscriber(modid = MineTeam.MOD_ID)
public class LivingEventSubscriber {
    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide){
            return;
        }

        String teamColor = player.getPersistentData().getString("teamColor");
        if (teamColor.isEmpty()) {
            teamColor = "white";
            player.getPersistentData().putString("teamColor", teamColor);
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),new TeamColorSyncS2CPayload(teamColor));
        }


        boolean teamPvP = MineTeamConfig.allowDamageSelf.get();
        if (player.getPersistentData().contains("teamPvP")) {
            teamPvP = player.getPersistentData().getBoolean("teamPvP");
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),new TeamPvPSyncS2CPayload(teamPvP));
        } else {
            player.getPersistentData().putBoolean("teamPvP", teamPvP);
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),new TeamPvPSyncS2CPayload(teamPvP));
        }




        ServerLevel level = (ServerLevel)player.level();
        Scoreboard scoreboard = level.getServer().getScoreboard();
        PlayerTeam team = scoreboard.getPlayersTeam(player.getScoreboardName());
        if (team == null) {
            PlayerTeam playerTeam = scoreboard.getPlayerTeam("white");
            if (playerTeam != null) {
                scoreboard.addPlayerToTeam(player.getScoreboardName(), playerTeam);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingPvP(LivingDamageEvent event) {
        LivingEntity hurtEntity = event.getEntity();
        DamageSource source = event.getSource();
        Entity attackEntity = source.getEntity();
        if (hurtEntity.level() instanceof ServerLevel serverLevel && attackEntity != null){
            ServerScoreboard scoreboard = serverLevel.getServer().getScoreboard();
            PlayerTeam attackEntityTeam = scoreboard.getPlayersTeam(attackEntity.getScoreboardName());
            PlayerTeam hurtEntityTeam = scoreboard.getPlayersTeam(hurtEntity.getScoreboardName());
            if (attackEntity == hurtEntity && MineTeamConfig.allowDamageSelf.get()) {
                return;
            }
            if (hurtEntityTeam == null || attackEntityTeam == null ||hurtEntityTeam != attackEntityTeam) {
                return;
            }
            boolean teamPvP = hurtEntity.getPersistentData().getBoolean("teamPvP");
            boolean teamPvP1 = attackEntity.getPersistentData().getBoolean("teamPvP");
            if (!teamPvP && !teamPvP1) {
                event.setCanceled(true);
            }
        }
    }


    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
        Player entity = event.getEntity();
        Player original = event.getOriginal();
        if (event.isWasDeath() && !entity.level().isClientSide()) {
            String teamColor = original.getPersistentData().getString("teamColor");
            boolean teamPvP = original.getPersistentData().getBoolean("teamPvP");
            entity.getPersistentData().putString("teamColor", teamColor);
            entity.getPersistentData().putBoolean("teamPvP", teamPvP);

            NetworkHandler.CHANNEL.sendToServer(new TeamColorSyncS2CPayload(teamColor));
            NetworkHandler.CHANNEL.sendToServer(new TeamPvPSyncS2CPayload(teamPvP));
        }
    }


    @SubscribeEvent
    public static void onSetTeamLastHurtMob(LivingDamageEvent event) {
        LivingEntity hurtEntity = event.getEntity();
        DamageSource source = event.getSource();
        Entity attackEntity = source.getEntity();
        if (hurtEntity.level() instanceof ServerLevel serverLevel && attackEntity != null){
            ServerScoreboard scoreboard = serverLevel.getServer().getScoreboard();
            PlayerTeam attackEntityTeam = scoreboard.getPlayersTeam(attackEntity.getScoreboardName());
            PlayerTeam hurtEntityTeam = scoreboard.getPlayersTeam(hurtEntity.getScoreboardName());
            if (attackEntity instanceof Player player && attackEntityTeam instanceof PlayerTeamMixed playerTeamMixed){
                playerTeamMixed.setLastHurtMob(hurtEntity);
                playerTeamMixed.setLastHurtTeam(hurtEntityTeam);
            }
            if (hurtEntity instanceof Player player && hurtEntityTeam instanceof PlayerTeamMixed playerTeamMixed){
                playerTeamMixed.setLastHurtMob(attackEntity);
                playerTeamMixed.setLastHurtTeam(attackEntityTeam);
            }
        }
    }
}