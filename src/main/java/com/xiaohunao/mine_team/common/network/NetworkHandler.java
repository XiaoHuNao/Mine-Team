package com.xiaohunao.mine_team.common.network;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.network.c2s.TeamColorSyncC2SPayload;
import com.xiaohunao.mine_team.common.network.c2s.TeamPvPSyncC2SPayload;
import com.xiaohunao.mine_team.common.network.s2c.MobTamingS2CPayload;
import com.xiaohunao.mine_team.common.network.s2c.TeamColorSyncS2CPayload;
import com.xiaohunao.mine_team.common.network.s2c.TeamPvPSyncS2CPayload;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class NetworkHandler {
    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            MineTeam.asResource("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, MobTamingS2CPayload.class, MobTamingS2CPayload::encode, MobTamingS2CPayload::decode, MobTamingS2CPayload::handle);
        CHANNEL.registerMessage(packetId++, TeamColorSyncS2CPayload.class, TeamColorSyncS2CPayload::encode, TeamColorSyncS2CPayload::decode, TeamColorSyncS2CPayload::handle);
        CHANNEL.registerMessage(packetId++, TeamColorSyncC2SPayload.class, TeamColorSyncC2SPayload::encode, TeamColorSyncC2SPayload::decode, TeamColorSyncC2SPayload::handle);
        CHANNEL.registerMessage(packetId++, TeamPvPSyncS2CPayload.class, TeamPvPSyncS2CPayload::encode, TeamPvPSyncS2CPayload::decode, TeamPvPSyncS2CPayload::handle);
        CHANNEL.registerMessage(packetId++, TeamPvPSyncC2SPayload.class, TeamPvPSyncC2SPayload::encode, TeamPvPSyncC2SPayload::decode, TeamPvPSyncC2SPayload::handle);
    }
}