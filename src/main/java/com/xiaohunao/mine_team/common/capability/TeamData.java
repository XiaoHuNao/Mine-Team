package com.xiaohunao.mine_team.common.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TeamData{
    public static final TeamData DEFAULT = new TeamData("white",false,0,false);

    public static final Codec<TeamData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("color","white").forGetter(TeamData::getColor),
            Codec.BOOL.optionalFieldOf("PvP",false).forGetter(TeamData::isPvP),
            Codec.INT.optionalFieldOf("franticTime", 0).forGetter(TeamData::getFranticTime),
            Codec.BOOL.optionalFieldOf("isFrantic", false).forGetter(TeamData::isFrantic)
    ).apply(instance, TeamData::new));



    private String color;
    private boolean PvP;
    private int franticTime;
    private boolean isFrantic;

    public TeamData(String color, boolean pvP, int franticTime, boolean isFrantic) {
        this.color = color;
        PvP = pvP;
        this.franticTime = franticTime;
        this.isFrantic = isFrantic;
    }

    public String getColor() {
        return color;
    }

    public boolean isPvP() {
        return PvP;
    }

    public boolean isFrantic() {
        return isFrantic;
    }

    public TeamData setFrantic(boolean frantic) {
        isFrantic = frantic;
        return this;
    }

    public TeamData setFranticTime(int franticTime) {
        this.franticTime = franticTime;
        return this;
    }

    public int getFranticTime() {
        return franticTime;
    }

    public TeamData setColor(String color) {
        this.color = color;
        return this;
    }

    public TeamData setPvP(boolean pvP) {
        PvP = pvP;
        return this;
    }
}
