package com.xiaohunao.mine_team.common.network;

import com.xiaohunao.mine_team.MineTeam;
import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TeamPvPSyncPayload(String teamColor,boolean friendlyFire) implements CustomPacketPayload {
    public static final Type<TeamPvPSyncPayload> NETWORK_TYPE = new Type<>(MineTeam.asResource("sync_pvp"));
    public static final StreamCodec<ByteBuf, TeamPvPSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,TeamPvPSyncPayload::teamColor,
            ByteBufCodecs.BOOL,TeamPvPSyncPayload::friendlyFire,
            TeamPvPSyncPayload::new
    );

    public static void ServerHandle(TeamPvPSyncPayload data, IPayloadContext context) {
        context.enqueueWork(() ->{
            ServerPlayer player = (ServerPlayer)context.player();
            MinecraftServer server = player.level().getServer();
            if (server != null) {
                ServerScoreboard scoreboard = server.getScoreboard();
                PlayerTeam playerTeam = scoreboard.getPlayerTeam(data.teamColor);
                if (playerTeam != null) {
                    playerTeam.setAllowFriendlyFire(data.friendlyFire);
                }
            }
        });
    }
    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return NETWORK_TYPE;
    }
}
