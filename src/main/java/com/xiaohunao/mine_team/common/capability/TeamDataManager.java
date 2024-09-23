package com.xiaohunao.mine_team.common.capability;

import com.google.common.collect.Maps;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.Event;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class TeamDataManager {
    public static final TeamDataManager INSTANCE = new TeamDataManager();
    private final Map<EntityType<?>,TeamData> teamDatas = Maps.newHashMap();

    public void registerTeamData(EntityType<?> entityType, TeamData teamData){
        this.teamDatas.put(entityType, teamData);
    }

    public void load(Collection<? extends TeamData> collection){
        this.teamDatas.clear();
        collection.forEach(teamData -> {
            if (teamData.getEntityType() != null) {
                this.teamDatas.put(teamData.getEntityType(), teamData);
            }
        });
    }

    public Map<EntityType<?>, TeamData> getTeamDatas() {
        return teamDatas;
    }

    public TeamData getOrDefault(EntityType<?> type, TeamData teamData) {
        TeamData orDefault = this.teamDatas.getOrDefault(type, null);
        if (orDefault != null) {
            return orDefault.copy();
        }
        return teamData;
    }


}
