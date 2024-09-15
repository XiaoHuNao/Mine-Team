package com.xiaohunao.mine_team.common.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.regex.Pattern;

public class MineTeamConfig {
    public static final ForgeConfigSpec CONFIG;

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> tamingMaterials;
    private static final BiMap<EntityType<?>, Ingredient> tamingMaterialMap = HashBiMap.create();

    public static final ForgeConfigSpec.BooleanValue allowDamageSelf;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        tamingMaterials = builder
                .comment("List of materials that can be used to tame entities")
                .comment("Format: entity-ingredient,'minecraft:wolf-{'item':'minecraft:bone'}'")
                .defineList("tamingMaterials", List.of(), str -> {
                    if (!(str instanceof String)) {
                        return false;
                    }
                    return Pattern.matches("\\w+:\\w+-\\{\"\\w+\":\"\\w+:\\w+\"}", (String) str);
                });

        allowDamageSelf = builder
                .comment("Whether or not to allow entities within the Team to attack themselves")
                .define("allowDamageSelf", true);
        CONFIG = builder.build();
    }

    public static void loadTamingMaterials() {
        tamingMaterialMap.clear();
        for (String material : tamingMaterials.get()) {
            String[] split = material.split("-");
            BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(split[0])).ifPresent(entityType -> {
                Ingredient ingredient = Ingredient.fromJson(new JsonPrimitive(split[1]));
                if (!ingredient.isEmpty()){
                    tamingMaterialMap.put(entityType, ingredient);
                }
            });
        }
    }

    public static BiMap<EntityType<?>, Ingredient> getTamingMaterialMap() {
        return tamingMaterialMap;
    }

    public static Ingredient getTamingMaterial(EntityType<?> entityType) {
        return tamingMaterialMap.getOrDefault(entityType, Ingredient.of(Items.GOLDEN_APPLE));
    }
}
