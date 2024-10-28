package com.xiaohunao.mine_team.common.network;

import com.xiaohunao.mine_team.MineTeam;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MobTamingS2CPayload(int entityId,BlockPos pos) implements CustomPacketPayload {
    public static final Type<MobTamingS2CPayload> TYPE = new Type<>(MineTeam.asResource("mob_taming"));
    public static final StreamCodec<ByteBuf, MobTamingS2CPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, MobTamingS2CPayload::entityId,
            BlockPos.STREAM_CODEC, MobTamingS2CPayload::pos,
            MobTamingS2CPayload::new
    );


    public static void clientHandle(final MobTamingS2CPayload payload,final IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientLevel clientLevel = Minecraft.getInstance().level;
            Entity entity = clientLevel.getEntity(payload.entityId);
            if (entity != null) {
                entity.level().playLocalSound(payload.pos.getX(), payload.pos.getY(), payload.pos.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, entity.getSoundSource(), 1.0F + entity.level().random.nextFloat(), entity.level().random.nextFloat() * 0.7F + 0.3F, false);
            }}
        );
    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
