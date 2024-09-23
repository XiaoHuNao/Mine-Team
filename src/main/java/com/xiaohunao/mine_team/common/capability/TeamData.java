package com.xiaohunao.mine_team.common.capability;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Random;

public class TeamData{
    public static Codec<Ingredient> Ingredient_codec = Codec.STRING.xmap(
            string -> Ingredient.fromJson(JsonParser.parseString(string)),
            ingredient -> ingredient.toJson().toString());

    public static final Codec<TeamData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entityType").forGetter(TeamData::getEntityType),
            Codec.STRING.optionalFieldOf("color","white").forGetter(TeamData::getColor),
            Codec.BOOL.optionalFieldOf("PvP",false).forGetter(TeamData::isPvP),
            Codec.INT.optionalFieldOf("franticTime", new Random().nextInt(2401) + 3600).forGetter(TeamData::getFranticTime),
            Codec.BOOL.optionalFieldOf("isFrantic", false).forGetter(TeamData::isFrantic),
            Ingredient_codec.optionalFieldOf("teamIngredient",Ingredient.of(Items.GOLDEN_APPLE)).forGetter(TeamData::getTeamIngredient),
            Codec.BOOL.optionalFieldOf("canTeam",true).forGetter(TeamData::isCanTeam)
            ).apply(instance, TeamData::new));


    private EntityType<?> entityType;
    private String color;
    private boolean pvp;
    private int franticTime;
    private int totalFranticTime;
    private boolean isFrantic;

    private Ingredient teamIngredient;
    private boolean canTeam;

    public TeamData(EntityType<?> entityType, String color, boolean pvp, int franticTime, boolean isFrantic, Ingredient teamIngredient, boolean canTeam) {
        this.entityType = entityType;
        this.color = color;
        this.pvp = pvp;
        this.franticTime = franticTime;
        this.totalFranticTime = franticTime;
        this.isFrantic = isFrantic;
        this.teamIngredient = teamIngredient;
        this.canTeam = canTeam;
    }
    public void copy(TeamData orDefault) {
        this.entityType = orDefault.entityType;
        this.color = orDefault.color;
        this.pvp = orDefault.pvp;
        this.franticTime = orDefault.franticTime;
        this.totalFranticTime = orDefault.totalFranticTime;
        this.isFrantic = orDefault.isFrantic;
        this.teamIngredient = orDefault.teamIngredient;
        this.canTeam = orDefault.canTeam;
    }
    public TeamData copy() {
        return new TeamData(this.entityType, this.color, this.pvp, this.franticTime, this.isFrantic, this.teamIngredient, this.canTeam);
    }

    public EntityType<?> getEntityType() {
        return this.entityType;
    }
    public String getColor() {
        return color;
    }
    public boolean isPvP() {
        return pvp;
    }
    public int getFranticTime() {
        return franticTime;
    }
    public int getTotalFranticTime() {
        return totalFranticTime;
    }
    public boolean isFrantic() {
        return isFrantic;
    }
    public Ingredient getTeamIngredient() {
        return teamIngredient;
    }
    public boolean isCanTeam() {
        return canTeam;
    }

    public TeamData setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
        return this;
    }

    public TeamData setColor(String color) {
        this.color = color;
        return this;
    }

    public TeamData setPvp(boolean pvp) {
        this.pvp = pvp;
        return this;
    }

    public TeamData setFranticTime(int franticTime) {
        this.franticTime = franticTime;
        return this;
    }

    public TeamData setFrantic(boolean frantic) {
        isFrantic = frantic;
        return this;
    }

    public TeamData setTeamIngredient(Ingredient teamIngredient) {
        this.teamIngredient = teamIngredient;
        return this;
    }

    public TeamData setCanTeam(boolean canTeam) {
        this.canTeam = canTeam;
        return this;
    }
}
