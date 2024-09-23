package com.xiaohunao.mine_team.common.event;

import com.xiaohunao.mine_team.common.capability.TeamData;
import com.xiaohunao.mine_team.common.capability.TeamDataManager;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.Function;

public class RegisterTeamDataEvent extends Event {
    public static final TeamDataManager INSTANCE = TeamDataManager.INSTANCE;

    public void registerTeamData(EntityType<?> entityType, Function<TeamData,TeamData> teamData) {
        INSTANCE.registerTeamData(entityType, teamData.apply(TeamData.DEFAULT.setEntityType(entityType)));
    }
}