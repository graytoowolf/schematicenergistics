package menu;

import core.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CannonInterfaceTerminalMenu extends AbstractContainerMenu {
    private final BlockEntity blockEntity;
    private final BlockPos pos;
    private final Level level;

    public CannonInterfaceTerminalMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, playerInv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public CannonInterfaceTerminalMenu(int containerId, Inventory playerInv, BlockEntity blockEntity) {
        super(Registration.CANNON_INTERFACE_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.pos = blockEntity.getBlockPos();
        this.level = playerInv.player.level();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.blockEntity == null || blockEntity.isRemoved()) return false;
        return player.distanceToSqr(pos.getCenter()) <= 64;
    }
}
