package com.xiaohunao.mine_team.common.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mojang.serialization.JsonOps;
import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.capability.TeamData;
import com.xiaohunao.mine_team.common.capability.TeamDataManager;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

public class ModJsonConfig {
    public static final ModJsonConfig INSTANCE = new ModJsonConfig();

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(TeamData.class, new TeamDataDeserializer())
            .setPrettyPrinting()
            .create();

    private final File file;
    public ModJsonConfig() {
        this.file = new File("config", MineTeam.MOD_ID + ".json5");
    }

    public void init() {
        if (!file.exists()) {
            this.saveData();
        } else {
            this.load();
        }
    }
    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            Collection<? extends TeamData> collection = GSON.fromJson(json, new TypeToken<List<TeamData>>(){}.getType());
            TeamDataManager.INSTANCE.load(collection);

        } catch (IOException e) {
            MineTeam.LOGGER.error("Failed to load TeamData", e);
        }
    }


    public void saveData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String json = GSON.toJson(TeamDataManager.INSTANCE.getTeamDatas().values());
            FileUtils.writeStringToFile(file, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            MineTeam.LOGGER.error("Failed to save macros", e);
        }
    }

    static class TeamDataDeserializer implements JsonDeserializer<TeamData> {
        @Override
        public TeamData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return TeamData.CODEC.decode(JsonOps.INSTANCE, json).resultOrPartial(MineTeam.LOGGER::error).orElseThrow().getFirst();
        }
    }
}
