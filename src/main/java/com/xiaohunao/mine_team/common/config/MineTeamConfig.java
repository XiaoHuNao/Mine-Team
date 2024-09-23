package com.xiaohunao.mine_team.common.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeConfigSpec;

public class MineTeamConfig {
    public static final ForgeConfigSpec CONFIG;

    private static final BiMap<EntityType<?>, Ingredient> tamingMaterialMap = HashBiMap.create();

    public static final ForgeConfigSpec.BooleanValue allowDamageSelf;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        allowDamageSelf = builder
                .comment("Whether or not to allow entities within the Team to attack themselves")
                .define("allowDamageSelf", true);
        CONFIG = builder.build();
    }
}
