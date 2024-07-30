package com.xiaohunao.mine_team.common.network;

import com.xiaohunao.mine_team.MineTeam;
import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record TeamPvPSyncPayload(boolean friendlyFire) implements CustomPacketPayload {
    public static final Type<TeamPvPSyncPayload> TYPE = new Type<>(MineTeam.asResource("sync_pvp"));
    public static final StreamCodec<ByteBuf, TeamPvPSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,TeamPvPSyncPayload::friendlyFire,
            TeamPvPSyncPayload::new
    );

    public static void serverHandle(TeamPvPSyncPayload data, IPayloadContext context) {
        context.enqueueWork(() ->{
            ServerPlayer player = (ServerPlayer)context.player();
            MinecraftServer server = player.level().getServer();
            if (server != null) {
                ServerScoreboard scoreboard = server.getScoreboard();
                PlayerTeam playerTeam = scoreboard.getPlayersTeam(player.getScoreboardName());
                if (playerTeam != null) {
                    playerTeam.setAllowFriendlyFire(data.friendlyFire);
                    player.getPersistentData().putBoolean("teamPvP", data.friendlyFire);
                }
            }
        });
    }

    public static void clientHandle(final TeamPvPSyncPayload payload,final IPayloadContext context) {
        context.enqueueWork(() -> {
            LocalPlayer localPlayer = (LocalPlayer) context.player();
            localPlayer.getPersistentData().putBoolean("teamPvP", payload.friendlyFire);
        });
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
