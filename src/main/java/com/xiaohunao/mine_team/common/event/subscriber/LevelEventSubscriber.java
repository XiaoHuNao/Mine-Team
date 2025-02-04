package com.xiaohunao.mine_team.common.event.subscriber;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.team.TeamManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.Arrays;
import java.util.UUID;

@EventBusSubscriber(modid = MineTeam.MODID)
public class LevelEventSubscriber {
    @SubscribeEvent
    public static void onCreateSpawnPosition(LevelEvent.CreateSpawnPosition event) {
        ServerLevel serverLevel = (ServerLevel)event.getLevel();
        TeamManager teamManager = TeamManager.of(serverLevel);

        if (teamManager.isTeamEmpty()){
            Arrays.stream(DyeColor.values())
                    .forEach(dyeColor -> {
                        teamManager.createTeam(UUID.randomUUID(), dyeColor);
                    });
        }
        teamManager.setDirty();
    }
}