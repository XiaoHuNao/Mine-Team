package com.xiaohunao.mine_team.common.network.c2s;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record TeamPvPSyncC2SPayload(boolean friendlyFire){
    public static final Codec<TeamPvPSyncC2SPayload> CODEC = Codec.BOOL.xmap(TeamPvPSyncC2SPayload::new, TeamPvPSyncC2SPayload::friendlyFire);

    public static void encode(TeamPvPSyncC2SPayload packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeJsonWithCodec(CODEC, packet);
    }

    public static TeamPvPSyncC2SPayload decode(FriendlyByteBuf friendlyByteBuf) {
        return friendlyByteBuf.readJsonWithCodec(CODEC);
    }

    public static void handle(TeamPvPSyncC2SPayload packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() ->{
            ServerPlayer player = context.getSender();
            MinecraftServer server = player.level().getServer();
            if (server != null) {
                ServerScoreboard scoreboard = server.getScoreboard();
                PlayerTeam playerTeam = scoreboard.getPlayersTeam(player.getScoreboardName());
                if (playerTeam != null) {
                    playerTeam.setAllowFriendlyFire(packet.friendlyFire);
                    player.getPersistentData().putBoolean("teamPvP", packet.friendlyFire);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
