package com.xiaohunao.mine_team.common.network;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.mixed.TeamManagerContainer;
import com.xiaohunao.mine_team.common.team.TeamManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TeamManagerSyncPayload(CompoundTag compoundTag) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TeamManagerSyncPayload> TYPE = new CustomPacketPayload.Type<>(MineTeam.asResource("team_manager_sync"));
    public static final StreamCodec<ByteBuf, TeamManagerSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, TeamManagerSyncPayload::compoundTag,
            TeamManagerSyncPayload::new
    );


    public static void clientHandle(final TeamManagerSyncPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            TeamManager teamManager = TeamManager.of(level);
            teamManager.deserializeNBT(payload.compoundTag);
        });
    }
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
