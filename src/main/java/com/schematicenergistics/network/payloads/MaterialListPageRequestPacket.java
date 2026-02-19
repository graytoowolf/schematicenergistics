package com.schematicenergistics.network.payloads;

import com.schematicenergistics.SchematicEnergistics;
import com.schematicenergistics.menu.CannonInterfaceMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MaterialListPageRequestPacket(int page) implements CustomPacketPayload {
    public static final Type<MaterialListPageRequestPacket> TYPE = new Type<>(
            SchematicEnergistics.makeId("material_list_page"));

    public static final StreamCodec<ByteBuf, MaterialListPageRequestPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MaterialListPageRequestPacket::page,
            MaterialListPageRequestPacket::new);

    public static void handle(MaterialListPageRequestPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player
                    && player.containerMenu instanceof CannonInterfaceMenu menu) {
                menu.setMaterialsPage(packet.page());
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
