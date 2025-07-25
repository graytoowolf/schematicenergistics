package screen;

import appeng.core.AppEng;
import com.mojang.blaze3d.systems.RenderSystem;
import menu.CannonInterfaceTerminalMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CannonInterfaceTerminalScreen extends AbstractContainerScreen<CannonInterfaceTerminalMenu> {
    /*
    * TODO:
    *  REMOVE BORDERS FROM SELECTOR TEXTURE
    *  CREATE THE TERMINAL ITSELF
    * */

    private static final ResourceLocation SELECTOR_TEXTURE = AppEng.makeId("textures/guis/cpu_selector.png");
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath("schematicenergistics", "textures/gui/cannon_interface_bg.png");

    public CannonInterfaceTerminalScreen(CannonInterfaceTerminalMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 182;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShaderTexture(0, BG);
        guiGraphics.blit(BG, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        RenderSystem.setShaderTexture(0, SELECTOR_TEXTURE);
        int selectorX = this.leftPos - 89;
        int selectorY = this.topPos;

        guiGraphics.blit(SELECTOR_TEXTURE, selectorX, selectorY, 0, 0, 89, 164);
    }
}
