package com.xiaohunao.mine_team.common.init;

import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.attachment.TeamAttachment;
import net.minecraft.core.UUIDUtil;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.UUID;
import java.util.function.Supplier;

public class MTAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MineTeam.MODID);

    public static final Supplier<AttachmentType<TeamAttachment>> TEAM = ATTACHMENT_TYPES.register(
            "mana", () -> AttachmentType.builder(() -> new TeamAttachment(TeamAttachment.EMPTY,false)).serialize(TeamAttachment.CODEC).copyOnDeath().build()
    );
}
