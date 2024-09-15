package com.xiaohunao.mine_team.common.network.c2s;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record TeamColorSyncC2SPayload(String newTeamColor) {
    public static final Codec<TeamColorSyncC2SPayload> CODEC = Codec.STRING.xmap(TeamColorSyncC2SPayload::new, TeamColorSyncC2SPayload::newTeamColor);

    public static void encode(TeamColorSyncC2SPayload packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeJsonWithCodec(CODEC, packet);
    }

    public static TeamColorSyncC2SPayload decode(FriendlyByteBuf friendlyByteBuf) {
        return friendlyByteBuf.readJsonWithCodec(CODEC);
    }


    public static void handle(TeamColorSyncC2SPayload packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() ->{
            ServerPlayer player = context.getSender();

            player.getPersistentData().putString("teamColor", packet.newTeamColor());

            MinecraftServer server = player.level().getServer();
            if (server != null) {
                ServerScoreboard scoreboard = server.getScoreboard();
                PlayerTeam oldPlayerTeam = scoreboard.getPlayersTeam(player.getScoreboardName());
                if (oldPlayerTeam != null) {
                    scoreboard.removePlayerFromTeam(player.getScoreboardName(), oldPlayerTeam);
                    PlayerTeam newPlayerTeam = scoreboard.getPlayerTeam(packet.newTeamColor());
                    if (newPlayerTeam != null){
                        scoreboard.addPlayerToTeam(player.getScoreboardName(), newPlayerTeam);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
