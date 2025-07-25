package screen;
import appeng.api.stacks.AEItemKey;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import logic.CannonInterfaceLogic;
import menu.CannonInterfaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class CannonInterfaceScreen extends AEBaseScreen<CannonInterfaceMenu> {
    private final CannonInterfaceLogic logic;
    private AEItemKey item;

    public CannonInterfaceScreen(CannonInterfaceMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.imageWidth = 176;
        this.imageHeight = 182;

        this.logic = menu.getLogic();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (this.item != null) {
            int centerX = this.leftPos + (this.imageWidth / 2) - 8;
            int centerY = this.topPos + 98/2 - 8;

            guiGraphics.renderItem(this.item.toStack(), centerX, centerY);
        }
    }

    public void updateScreenItem(CompoundTag data) {
        var item = AEItemKey.fromTag(menu.getLogic().getLevel().registryAccess(), data);
        this.item = item != null ? item : AEItemKey.of(ItemStack.EMPTY);
    }

}
