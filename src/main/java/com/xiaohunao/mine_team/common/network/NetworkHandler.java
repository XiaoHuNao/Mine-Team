package com.xiaohunao.mine_team.common.network;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.network.c2s.TeamDataSyncC2SPayload;
import com.xiaohunao.mine_team.common.network.s2c.TeamDataSyncS2CPayload;
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
        CHANNEL.registerMessage(packetId++, TeamDataSyncS2CPayload.class, TeamDataSyncS2CPayload::encode, TeamDataSyncS2CPayload::decode, TeamDataSyncS2CPayload::handle);
        CHANNEL.registerMessage(packetId++, TeamDataSyncC2SPayload.class, TeamDataSyncC2SPayload::encode, TeamDataSyncC2SPayload::decode, TeamDataSyncC2SPayload::handle);
    }
}