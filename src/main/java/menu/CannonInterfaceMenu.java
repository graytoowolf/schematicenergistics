package menu;

import appeng.api.stacks.AEItemKey;
import appeng.menu.AEBaseMenu;
import core.Registration;
import logic.CannonInterfaceLogic;
import logic.ICannonInterfaceHost;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import network.payloads.CannonInterfaceSyncPacket;

public class CannonInterfaceMenu extends AEBaseMenu {
    private final CannonInterfaceLogic logic;
    private AEItemKey clientItem;

    public CannonInterfaceMenu(int id, Inventory playerInventory, ICannonInterfaceHost host) {
        super(Registration.CANNON_INTERFACE_MENU.get(), id, playerInventory, host);

        this.createPlayerInventorySlots(playerInventory);
        this.logic = host.getLogic();
    }

    public CannonInterfaceLogic getLogic() {
        return this.logic;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (getPlayer() instanceof ServerPlayer player) {
            if (this.logic == null || this.logic.getItem() == null) return;
           this.clientItem = this.logic.getItem();
           PacketDistributor.sendToPlayer(player, new CannonInterfaceSyncPacket(clientItem.toTag(logic.getLevel().registryAccess())));
        }
    }
}
