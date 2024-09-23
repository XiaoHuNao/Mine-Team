package com.xiaohunao.mine_team.common.capability;

import com.xiaohunao.mine_team.common.config.MineTeamConfig;
import com.xiaohunao.mine_team.common.init.ModCapability;
import com.xiaohunao.mine_team.common.mixed.PlayerTeamMixed;
import com.xiaohunao.mine_team.common.network.NetworkHandler;
import com.xiaohunao.mine_team.common.network.s2c.TeamDataSyncS2CPayload;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;


public class TeamCapability{
    public TeamData data;
    public TeamCapability(LivingEntity livingEntity) {
        TeamData teamData = new TeamData(livingEntity.getType(), "white", false, new Random().nextInt(2401) + 3600, false, Ingredient.of(Items.GOLDEN_APPLE), true);
        this.data = TeamDataManager.INSTANCE.getOrDefault(livingEntity.getType(), teamData);
    }

    public static LazyOptional<TeamCapability> get(LivingEntity living){
        return living.getCapability(ModCapability.TEAM);
    }

    public boolean canAttack(LivingEntity attackEntity, LivingEntity hurtEntity) {
        if (attackEntity.level() instanceof ServerLevel serverLevel){
            if (MineTeamConfig.allowDamageSelf.get() && attackEntity == hurtEntity) {
                return true;
            }
            ServerScoreboard scoreboard = serverLevel.getServer().getScoreboard();
            PlayerTeam attackEntityTeam = scoreboard.getPlayersTeam(attackEntity.getScoreboardName());
            PlayerTeam hurtEntityTeam = scoreboard.getPlayersTeam(hurtEntity.getScoreboardName());


            if (attackEntityTeam != hurtEntityTeam || attackEntityTeam == null || hurtEntityTeam == null){
                return true;
            }


            if (!data.isPvP() && !get(hurtEntity).orElse(new TeamCapability(hurtEntity)).data.isPvP()) {
                return false;
            }
        }
        return true;
    }

    public void setLastHurtMob(LivingEntity attackEntity, LivingEntity hurtEntity) {
        if (hurtEntity.level() instanceof ServerLevel serverLevel){
            ServerScoreboard scoreboard = serverLevel.getServer().getScoreboard();
            PlayerTeam attackEntityTeam = scoreboard.getPlayersTeam(attackEntity.getScoreboardName());
            PlayerTeam hurtEntityTeam = scoreboard.getPlayersTeam(hurtEntity.getScoreboardName());
            if (attackEntity instanceof Player && attackEntityTeam instanceof PlayerTeamMixed playerTeamMixed){
                playerTeamMixed.mineTeam$setLastHurtMob(hurtEntity);
                playerTeamMixed.mineTeam$setLastHurtTeam(hurtEntityTeam);
            }
            if (hurtEntity instanceof Player && hurtEntityTeam instanceof PlayerTeamMixed playerTeamMixed){
                playerTeamMixed.mineTeam$setLastHurtMob(attackEntity);
                playerTeamMixed.mineTeam$setLastHurtTeam(attackEntityTeam);
            }
        }
    }

    public void init(Player player) {
        if (player.level() instanceof ServerLevel serverLevel){
            NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),new TeamDataSyncS2CPayload(player.getId(), data));

            Scoreboard scoreboard = serverLevel.getServer().getScoreboard();
            PlayerTeam team = scoreboard.getPlayersTeam(player.getScoreboardName());
            if (team == null) {
                PlayerTeam playerTeam = scoreboard.getPlayerTeam(data.getColor());
                if (playerTeam != null) {
                    scoreboard.addPlayerToTeam(player.getScoreboardName(), playerTeam);
                }
            }
        }
    }

    public void withMobTeam(Player player, LivingEntity interactEntity, InteractionHand hand) {
        ItemStack handStack = player.getItemInHand(hand);
        if (interactEntity instanceof Player){
            return;
        }

        Level level = player.level();
        TeamCapability.get(interactEntity).ifPresent(teamCapability -> {
            TeamData interactEntityTeamData = teamCapability.data;
            if (interactEntityTeamData.isCanTeam() && interactEntityTeamData.getTeamIngredient().test(handStack)){
                PlayerTeam playersTeam = level.getScoreboard().getPlayersTeam(interactEntity.getScoreboardName());
                if (interactEntity.hasEffect(MobEffects.WEAKNESS) && playersTeam == null && !level.isClientSide){
                    interactEntityTeamData.setFranticTime(interactEntityTeamData.getFranticTime());
                    interactEntityTeamData.setColor(data.getColor());
                    interactEntityTeamData.setFrantic(true);
                    NetworkHandler.CHANNEL.send(PacketDistributor.DIMENSION.with(level::dimension),new TeamDataSyncS2CPayload(interactEntity.getId(),interactEntityTeamData));

                    handStack.hurtAndBreak(1, player, (playerEntity) -> playerEntity.broadcastBreakEvent(hand));

                    interactEntity.removeEffect(MobEffects.WEAKNESS);
                    interactEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, Math.min(level.getDifficulty().getId() - 1, 0)));
                }
            }
        });


    }

    public void tickMobTeam(LivingEntity livingEntity) {
        Level level = livingEntity.level();
        if (data.getFranticTime() >= 0){
            if (!level.isClientSide){
                data.setFranticTime(data.getFranticTime() - 1);
                ServerLevel serverLevel = (ServerLevel) level;
                NetworkHandler.CHANNEL.send(PacketDistributor.DIMENSION.with(serverLevel::dimension),new TeamDataSyncS2CPayload(livingEntity.getId(), data));
            }else {
                for (int i = 0; i < 2; i++) {
                    double offsetX = level.random.nextGaussian() * 0.02D;
                    double offsetY = level.random.nextGaussian() * 0.02D;
                    double offsetZ = level.random.nextGaussian() * 0.02D;
                    double x = livingEntity.getX() + (double) (level.random.nextFloat() * livingEntity.getBbWidth() * 2.0F) - (double) livingEntity.getBbWidth();
                    double y = livingEntity.getY() + 0.5D + (double) (level.random.nextFloat() * livingEntity.getBbHeight());
                    double z = livingEntity.getZ() + (double) (level.random.nextFloat() * livingEntity.getBbWidth() * 2.0F) - (double) livingEntity.getBbWidth();
                    level.addParticle((ParticleOptions) ParticleTypes.EFFECT, x, y, z, offsetX, offsetY, offsetZ);
                }
            }
        }

        if (data.getFranticTime() == -1){
            if (!level.isClientSide){
                ServerLevel serverLevel = (ServerLevel) level;
                ServerScoreboard scoreboard = serverLevel.getServer().getScoreboard();
                scoreboard.addPlayerToTeam(livingEntity.getScoreboardName(), scoreboard.getPlayerTeam(data.getColor()));
                livingEntity.setGlowingTag(true);
                data.setFrantic(false);
                data.setFranticTime(data.getTotalFranticTime());
                NetworkHandler.CHANNEL.send(PacketDistributor.DIMENSION.with(serverLevel::dimension),new TeamDataSyncS2CPayload(livingEntity.getId(), data));
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<Tag> {
        private final LazyOptional<TeamCapability> instance;
        private final LivingEntity livingEntity;

        public Provider(LivingEntity livingEntity) {
            this.livingEntity = livingEntity;
            this.instance = LazyOptional.of(() -> new TeamCapability(livingEntity));
        }
        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return ModCapability.TEAM.orEmpty(cap, instance);
        }

        @Override
        public Tag serializeNBT() {
            return TeamData.CODEC.encodeStart(NbtOps.INSTANCE, instance.orElse(new TeamCapability(livingEntity)).data).result().orElse(new CompoundTag());
        }

        @Override
        public void deserializeNBT(Tag compoundTag) {
            instance.ifPresent(teamCapability -> {
                teamCapability.data.copy(TeamData.CODEC.parse(NbtOps.INSTANCE, compoundTag).result().orElse(new TeamData(livingEntity.getType(), "white", false, new Random().nextInt(2401) + 3600, false, Ingredient.of(Items.GOLDEN_APPLE), true)));
            });
        }
    }
}
