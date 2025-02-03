package com.xiaohunao.mine_team.common.team;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

import java.util.UUID;

public class Team {
    public static final Codec<Team> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("uid").forGetter(Team::getUid),
                Codec.INT.fieldOf("color").forGetter(Team::getColor)
        ).apply(instance, Team::new)
    );
    private UUID uid;
    private int color;

    public Team(UUID uid, int color) {
        this.uid = uid;
        this.color = color;
    }

    public Team() {
    }

    public UUID getUid() {
        return uid;
    }

    public int getColor() {
        return color;
    }

    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putUUID("uid", uid);
        compoundTag.putInt("color", color);
        return compoundTag;
    }

    public Team deserializeNBT(CompoundTag compoundTag) {
        this.uid = compoundTag.getUUID("uid");
        this.color = compoundTag.getInt("color");
        return this;
    }

}
