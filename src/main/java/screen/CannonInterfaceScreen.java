package screen;
import com.mojang.blaze3d.systems.RenderSystem;
import menu.CannonInterfaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class CannonInterfaceScreen extends AbstractContainerScreen<CannonInterfaceMenu> {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath("schematicenergistics", "textures/gui/cannon_interface_bg.png");

    public CannonInterfaceScreen(CannonInterfaceMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 182;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShaderTexture(0, BG);
        guiGraphics.blit(BG, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    /*
    * Adapted from the AE2 source code.
    * Original Author: Applied Energistics 2 Team
    * Source: https://github.com/AppliedEnergistics/Applied-Energistics-2/blob/main/src/main/java/appeng/client/gui/AEBaseScreen.java
    * */

    @Override
    protected void renderSlotHighlight(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY, float partialTick) {
        int x = slot.x;
        int y = slot.y;
        int w = 16;
        int h = 16;

        guiGraphics.hLine(x, x + w, y - 1, 0xFFdaffff);
        guiGraphics.hLine(x - 1, x + w, y + h, 0xFFdaffff);
        guiGraphics.vLine(x - 1, y - 2, y + h, 0xFFdaffff);
        guiGraphics.vLine(x + w, y - 2, y + h, 0xFFdaffff);
        guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + w, y + h, 0x669cd3ff, 0x669cd3ff, 0);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false);
    }
}
