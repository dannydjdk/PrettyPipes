package de.ellpeck.prettypipes.packets;

import de.ellpeck.prettypipes.Utility;
import de.ellpeck.prettypipes.blocks.pipe.PipeTileEntity;
import de.ellpeck.prettypipes.network.PipeItem;
import de.ellpeck.prettypipes.network.PipeNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketItemEnterPipe {

    private BlockPos tilePos;
    private CompoundNBT item;

    public PacketItemEnterPipe(BlockPos tilePos, PipeItem item) {
        this.tilePos = tilePos;
        this.item = item.serializeNBT();
    }

    private PacketItemEnterPipe() {

    }

    public static PacketItemEnterPipe fromBytes(PacketBuffer buf) {
        PacketItemEnterPipe client = new PacketItemEnterPipe();
        client.tilePos = buf.readBlockPos();
        client.item = buf.readCompoundTag();
        return client;
    }

    public static void toBytes(PacketItemEnterPipe packet, PacketBuffer buf) {
        buf.writeBlockPos(packet.tilePos);
        buf.writeCompoundTag(packet.item);
    }

    @SuppressWarnings("Convert2Lambda")
    public static void onMessage(PacketItemEnterPipe message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(new Runnable() {
            @Override
            public void run() {
                Minecraft mc = Minecraft.getInstance();
                if (mc.world == null)
                    return;
                PipeItem item = new PipeItem(message.item);
                PipeTileEntity pipe = Utility.getTileEntity(PipeTileEntity.class, mc.world, message.tilePos);
                if (pipe != null)
                    pipe.items.add(item);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}