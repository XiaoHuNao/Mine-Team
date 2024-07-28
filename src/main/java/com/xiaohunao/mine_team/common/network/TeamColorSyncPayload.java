package com.xiaohunao.mine_team.common.network;

import com.xiaohunao.mine_team.MineTeam;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record TeamColorSyncPayload(String newTeamColor) implements CustomPacketPayload {
    public static final Type<TeamColorSyncPayload> NETWORK_TYPE = new Type<>(MineTeam.asResource("sync_color"));
    public static final StreamCodec<ByteBuf, TeamColorSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, TeamColorSyncPayload::newTeamColor,
            TeamColorSyncPayload::new
    );
    public static void serverHandle(TeamColorSyncPayload data, IPayloadContext context) {
        context.enqueueWork(() ->{
            ServerPlayer player = (ServerPlayer) context.player();
            MinecraftServer server = player.level().getServer();
            if (server != null) {
                ServerScoreboard scoreboard = server.getScoreboard();
                PlayerTeam oldPlayerTeam = scoreboard.getPlayersTeam(player.getScoreboardName());
                if (oldPlayerTeam != null) {
                    scoreboard.removePlayerFromTeam(player.getScoreboardName(), oldPlayerTeam);
                    PlayerTeam newPlayerTeam = scoreboard.getPlayerTeam(data.newTeamColor());
                    if (newPlayerTeam != null){
                        scoreboard.addPlayerToTeam(player.getScoreboardName(), newPlayerTeam);
                    }
                }
            }
            player.getPersistentData().putString("teamColor", data.newTeamColor());
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientHandle(TeamColorSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            LocalPlayer localPlayer = (LocalPlayer) context.player();
            localPlayer.getPersistentData().putString("teamColor", payload.newTeamColor());
        });
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return NETWORK_TYPE;
    }
}
