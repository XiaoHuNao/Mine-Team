package com.xiaohunao.mine_team.common.network;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.attachment.TeamAttachment;
import com.xiaohunao.mine_team.common.init.MTAttachmentTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TeamAttachmentSyncPayload(int entityId, TeamAttachment attachment) implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<TeamAttachmentSyncPayload> TYPE = new CustomPacketPayload.Type<>(MineTeam.asResource("team_sync"));
    public static final StreamCodec<ByteBuf, TeamAttachmentSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, TeamAttachmentSyncPayload::entityId,
            ByteBufCodecs.fromCodec(TeamAttachment.CODEC), TeamAttachmentSyncPayload::attachment,
            TeamAttachmentSyncPayload::new
    );


    public static void clientHandle(final TeamAttachmentSyncPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientLevel clientLevel = Minecraft.getInstance().level;
            if (clientLevel != null) {
                Entity entity = clientLevel.getEntity(payload.entityId);
                if (entity != null){
                    entity.setData(MTAttachmentTypes.TEAM,entity.getData(MTAttachmentTypes.TEAM).sync(payload.attachment));
                }
            }
        });
    }

    public static void serverHandle(final TeamAttachmentSyncPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            Entity entity = level.getEntity(payload.entityId);
            if (entity != null){
                entity.setData(MTAttachmentTypes.TEAM,entity.getData(MTAttachmentTypes.TEAM).sync(payload.attachment));
            }
        });
    }
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
