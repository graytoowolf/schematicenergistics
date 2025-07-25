package menu;

import core.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CannonInterfaceMenu extends AbstractContainerMenu {
    private final BlockEntity blockEntity;
    private final BlockPos pos;
    private final Level level;

    private static final int INV_START_X = 8;
    private static final int INV_START_Y = 98;

    private static final int HOTBAR_START_X = 8;
    private static final int HOTBAR_START_Y = 156;

    private static final int SLOT_SPACING = 18;

    public CannonInterfaceMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, playerInv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public CannonInterfaceMenu(int containerId, Inventory playerInv, BlockEntity blockEntity) {
        super(Registration.CANNON_INTERFACE_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.pos = blockEntity.getBlockPos();
        this.level = playerInv.player.level();

        addPlayerInventory(playerInv);
        addPlayerHotbar(playerInv);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        INV_START_X + col * SLOT_SPACING,
                        INV_START_Y + row * SLOT_SPACING));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i,
                    HOTBAR_START_X + i * SLOT_SPACING,
                    HOTBAR_START_Y));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.blockEntity == null || blockEntity.isRemoved()) return false;
        return player.distanceToSqr(pos.getCenter()) <= 64;
    }
}
