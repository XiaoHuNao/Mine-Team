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
    public static final Type<TeamColorSyncPayload> TYPE = new Type<>(MineTeam.asResource("sync_color"));
    public static final StreamCodec<ByteBuf, TeamColorSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, TeamColorSyncPayload::newTeamColor,
            TeamColorSyncPayload::new
    );
    public static void serverHandle(final TeamColorSyncPayload data, final IPayloadContext context) {
        context.enqueueWork(() ->{
            ServerPlayer player = (ServerPlayer) context.player();

            player.getPersistentData().putString("teamColor", data.newTeamColor());

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
        });
    }


    public static void clientHandle(final TeamColorSyncPayload payload,final IPayloadContext context) {
        context.enqueueWork(() -> {
            LocalPlayer localPlayer = (LocalPlayer) context.player();
            localPlayer.getPersistentData().putString("teamColor", payload.newTeamColor());
        });
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
