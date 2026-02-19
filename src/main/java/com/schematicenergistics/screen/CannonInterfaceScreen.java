package com.schematicenergistics.screen;

import appeng.api.stacks.AEItemKey;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.schematicenergistics.lib.CannonInterfaceClientState;
import com.schematicenergistics.lib.MaterialListEntry;
import com.schematicenergistics.lib.SEUtils;
import com.schematicenergistics.menu.CannonInterfaceMenu;
import com.schematicenergistics.network.payloads.MaterialListPageRequestPacket;
import com.schematicenergistics.network.payloads.MaterialListSubscribePacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import com.schematicenergistics.network.payloads.CannonInterfaceConfigPacket;
import com.schematicenergistics.network.payloads.CannonStatePacket;
import com.schematicenergistics.network.payloads.ReturnToTerminalPacket;
import com.schematicenergistics.widgets.SEIcon;
import com.schematicenergistics.widgets.SESimpleIconButton;
import com.schematicenergistics.widgets.SEToggleButton;

import java.util.ArrayList;
import java.util.List;

public class CannonInterfaceScreen extends AEBaseScreen<CannonInterfaceMenu> {
    private AEItemKey item;
    private final SEToggleButton toggleGunpowderCrafting;
    private final SEToggleButton toggleCrafting;
    private final SEToggleButton toggleGunpowder;
    private final SEToggleButton toggleBulkCraft;
    private SEToggleButton playPause;
    private SESimpleIconButton backButton = null;

    private boolean craftingState;
    private boolean gunpowderState;
    private boolean gunpowderCraftingState;
    private boolean bulkCraftState;

    private static final int MAX_TEXT_WIDTH = 164;

    private BlockPos terminal = null;

    private Button materialsButton;
    private boolean materialsOpen = false;
    private int materialsVisibleRows = 10;
    private int materialsScroll = 0;
    private int materialsPage = 0;
    private int materialsTotalPages = 0;
    private final List<MaterialRow> materials = new ArrayList<>();

    private Rect2i materialsBounds = new Rect2i(0, 0, 0, 0);
    private Rect2i materialsRowsBounds = new Rect2i(0, 0, 0, 0);
    private Rect2i materialsCloseBounds = new Rect2i(0, 0, 0, 0);
    private Rect2i materialsPrevBounds = new Rect2i(0, 0, 0, 0);
    private Rect2i materialsNextBounds = new Rect2i(0, 0, 0, 0);

    public CannonInterfaceScreen(CannonInterfaceMenu menu, Inventory playerInventory, Component title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.imageWidth = 176;
        this.imageHeight = 182;
        this.terminal = null;

        if (CannonInterfaceClientState.hasState()) {
            this.gunpowderState = CannonInterfaceClientState.getGunpowderState();
            this.craftingState = CannonInterfaceClientState.getCraftingState();
            this.gunpowderCraftingState = CannonInterfaceClientState.getGunpowderCraftingState();
            this.bulkCraftState = CannonInterfaceClientState.getBulkCraftState();
            CannonInterfaceClientState.reset();
        }

        this.toggleBulkCraft = new SEToggleButton(
                SEIcon.BULK_CRAFT_ALLOW,
                SEIcon.BULK_CRAFT_DENY,
                Component.translatable("gui.schematicenergistics.cannon_interface.disable_bulk_craft"),
                Component.translatable("gui.schematicenergistics.cannon_interface.disable_bulk_craft_hint"),
                Component.translatable("gui.schematicenergistics.cannon_interface.enable_bulk_craft"),
                Component.translatable("gui.schematicenergistics.cannon_interface.enable_bulk_craft_hint"),
                state -> sendState("bulkCraftState", state),
                bulkCraftState);

        this.toggleCrafting = new SEToggleButton(
                SEIcon.CRAFTING_ALLOW,
                SEIcon.CRAFTING_DENY,
                Component.translatable("gui.schematicenergistics.cannon_interface.disable_autocraft"),
                Component.translatable("gui.schematicenergistics.cannon_interface.disable_autocraft_hint"),
                Component.translatable("gui.schematicenergistics.cannon_interface.enable_autocraft"),
                Component.translatable("gui.schematicenergistics.cannon_interface.enable_autocraft_hint"),
                state -> {
                    sendState("craftingState", state);
                },
                craftingState);

        this.toggleGunpowder = new SEToggleButton(
                SEIcon.GUNPOWDER_ALLOW,
                SEIcon.GUNPOWDER_DENY,
                Component.translatable("gui.schematicenergistics.cannon_interface.disable_gunpowder"),
                Component.translatable("gui.schematicenergistics.cannon_interface.disable_gunpowder_hint"),
                Component.translatable("gui.schematicenergistics.cannon_interface.enable_gunpowder"),
                Component.translatable("gui.schematicenergistics.cannon_interface.enable_gunpowder_hint"),
                state -> {
                    sendState("gunpowderState", state);
                },
                gunpowderState);

        this.toggleGunpowderCrafting = new SEToggleButton(
                SEIcon.GUNPOWDER_CRAFTING_ALLOW,
                SEIcon.GUNPOWDER_CRAFTING_DENY,
                Component.translatable("gui.schematicenergistics.cannon_interface.disable_gunpowder_crafting"),
                Component.translatable("gui.schematicenergistics.cannon_interface.disable_gunpowder_crafting_hint"),
                Component.translatable("gui.schematicenergistics.cannon_interface.enable_gunpowder_crafting"),
                Component.translatable("gui.schematicenergistics.cannon_interface.enable_gunpowder_crafting_hint"),
                state -> {
                    sendState("gunpowderCraftingState", state);
                },
                gunpowderCraftingState);

        this.addToLeftToolbar(toggleBulkCraft);
        this.addToLeftToolbar(toggleCrafting);
        this.addToLeftToolbar(toggleGunpowder);
        this.addToLeftToolbar(toggleGunpowderCrafting);

    }

    @Override
    protected void init() {
        super.init();
        updateSchematicName(null);

        int centerX = this.leftPos + (this.imageWidth / 2) - 8;

        this.playPause = new SEToggleButton(
                SEIcon.PAUSE,
                SEIcon.PLAY,
                Component.translatable("gui.schematicenergistics.cannon_interface.pause"),
                Component.translatable("gui.schematicenergistics.cannon_interface.pause_hint"),
                Component.translatable("gui.schematicenergistics.cannon_interface.play"),
                Component.translatable("gui.schematicenergistics.cannon_interface.play_hint"),
                (state) -> sendCannonState(state, false),
                false);

        this.playPause.setPosition(
                centerX - 16,
                this.topPos + 56);

        this.addRenderableWidget(playPause);

        // Stop the cannon
        SESimpleIconButton stop = new SESimpleIconButton(
                SEIcon.STOP,
                Component.translatable("gui.schematicenergistics.cannon_interface.stop"),
                Component.translatable("gui.schematicenergistics.cannon_interface.stop_hint"),
                (btn) -> sendCannonState(false, true) // Stop the cannon
        );

        stop.setPosition(centerX + 16, this.topPos + 56);
        this.addRenderableWidget(stop);

        backButton = new SESimpleIconButton(
                SEIcon.BACK,
                Component.translatable("gui.schematicenergistics.cannon_terminal.return_terminal"),
                Component.empty(),
                (btn) -> {
                    PacketDistributor.sendToServer(new ReturnToTerminalPacket(terminal));
                });

        backButton.setPosition(leftPos + imageWidth - 28, this.topPos - 10);
        backButton.visible = (terminal != null);
        this.addRenderableWidget(backButton);

        materialsButton = Button.builder(
                Component.translatable("gui.schematicenergistics.cannon_interface.materials"),
                btn -> toggleMaterials()).bounds(leftPos + 8, this.topPos + 56, 50, 20).build();
        this.addRenderableWidget(materialsButton);
    }

    public void sendState(String config, boolean state) {
        PacketDistributor.sendToServer(
                new CannonInterfaceConfigPacket(
                        state, config));
    }

    public void sendCannonState(boolean state, boolean isStop) {
        if (isStop) {
            var stoppedState = SchematicannonBlockEntity.State.STOPPED;
            PacketDistributor.sendToServer(
                    new CannonStatePacket(stoppedState.toString()));
            return;
        }

        this.playPause.setState(state);
        SchematicannonBlockEntity.State cannonState = state ? SchematicannonBlockEntity.State.RUNNING
                : SchematicannonBlockEntity.State.PAUSED;

        PacketDistributor.sendToServer(
                new CannonStatePacket(cannonState.toString()));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int centerX = this.leftPos + 147;
        int centerY = this.topPos + 22;

        if (this.item != null && !this.item.toStack().isEmpty()) {
            guiGraphics.renderItem(this.item.toStack(), centerX, centerY);
        }

        if (mouseX >= centerX && mouseX < centerX + 16 && mouseY >= centerY && mouseY < centerY + 16) {
            if (this.item == null || this.item.toStack().isEmpty()) {
                guiGraphics.renderTooltip(this.font,
                        Component.translatable("gui.schematicenergistics.cannon_interface.no_item"), mouseX, mouseY);
            } else {
                guiGraphics.renderTooltip(this.font, this.item.getDisplayName(), mouseX, mouseY);
            }
        }

        if (materialsOpen) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 600);
            renderMaterialsOverlay(guiGraphics, mouseX, mouseY);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (materialsOpen) {
            if (contains(materialsCloseBounds, mouseX, mouseY)) {
                closeMaterials();
                return true;
            }

            if (materialsPage > 0 && contains(materialsPrevBounds, mouseX, mouseY)) {
                requestMaterialsPage(materialsPage - 1);
                return true;
            }

            if (materialsTotalPages > 0 && materialsPage + 1 < materialsTotalPages
                    && contains(materialsNextBounds, mouseX, mouseY)) {
                requestMaterialsPage(materialsPage + 1);
                return true;
            }

            if (contains(materialsRowsBounds, mouseX, mouseY)) {
                int rowHeight = 20;
                int relativeY = (int) mouseY - materialsRowsBounds.getY();
                int rowIndex = relativeY / rowHeight;
                int dataIndex = materialsScroll + rowIndex;
                return true;
            }

            if (!contains(materialsBounds, mouseX, mouseY)) {
                closeMaterials();
                return true;
            }

            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!materialsOpen || !contains(materialsBounds, mouseX, mouseY)) {
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }

        int maxScroll = Math.max(0, materials.size() - materialsVisibleRows);
        if (scrollY > 0) {
            if (materialsScroll > 0) {
                materialsScroll = Math.max(0, materialsScroll - 1);
            } else if (materialsPage > 0) {
                requestMaterialsPage(materialsPage - 1);
            }
        } else if (scrollY < 0) {
            if (materialsScroll < maxScroll) {
                materialsScroll = Math.min(maxScroll, materialsScroll + 1);
            } else if (materialsPage + 1 < materialsTotalPages) {
                requestMaterialsPage(materialsPage + 1);
            }
        }

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (materialsOpen && keyCode == 256) {
            closeMaterials();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void removed() {
        if (materialsOpen) {
            PacketDistributor.sendToServer(new MaterialListSubscribePacket(false));
        }
        super.removed();
    }

    public void updateStates(boolean gunpowderState, boolean craftingState, boolean gunpowderCraftingState,
            boolean bulkCraftState) {
        this.gunpowderState = gunpowderState;
        this.craftingState = craftingState;

        if (this.toggleGunpowder != null) {
            this.toggleGunpowder.setState(gunpowderState);
        }
        if (this.toggleCrafting != null) {
            this.toggleCrafting.setState(craftingState);
        }
        if (this.toggleGunpowderCrafting != null) {
            this.toggleGunpowderCrafting.setState(gunpowderCraftingState);
        }

        if (this.toggleBulkCraft != null) {
            this.toggleBulkCraft.setState(bulkCraftState);
        }
    }

    private Component limitTextWidth(Component originalText, int maxWidth) {
        String textString = originalText.getString();

        if (this.font.width(textString) <= maxWidth) {
            return originalText;
        }

        String ellipsis = "...";
        int ellipsisWidth = this.font.width(ellipsis);
        int availableWidth = maxWidth - ellipsisWidth;

        String truncatedText = this.font.plainSubstrByWidth(textString, availableWidth);

        return Component.literal(truncatedText + ellipsis);
    }

    public void updateSchematicName(String schematicName) {

        Component text = schematicName == null || schematicName.isEmpty()
                ? Component.translatable("gui.schematicenergistics.cannon_interface.schematic_name")
                : Component.literal(schematicName);

        Component limitedText = limitTextWidth(text, MAX_TEXT_WIDTH);
        setTextContent("schematic_text", limitedText);
    }

    public void updateStatusMsg(String statusMsg) {
        Component text = statusMsg == null || statusMsg.isEmpty()
                ? Component.translatable("gui.schematicenergistics.cannon_interface.missing_cannon")
                : SEUtils.formatCannonStatus(statusMsg);

        Component limitedText = limitTextWidth(text, MAX_TEXT_WIDTH);
        setTextContent("status_text", limitedText);
    }

    public void updateCannonState(String state) {
        boolean cState = !"PAUSED".equals(state);
        this.playPause.setState(cState);
    }

    public void updateScreenItem(CompoundTag data, String schematicName, String statusMsg, String state,
            BlockPos terminalPos) {
        var item = AEItemKey.fromTag(menu.getLogic().getLevel().registryAccess(), data);
        this.item = item != null ? item : AEItemKey.of(ItemStack.EMPTY);

        this.terminal = terminalPos;

        if (backButton != null) {
            backButton.visible = (terminal != null);
        }

        updateSchematicName(schematicName);
        updateStatusMsg(statusMsg);
        updateCannonState(state);
    }

    public void receiveMaterialsData(int page, int totalPages, List<MaterialListEntry> entries) {
        this.materialsPage = Math.max(0, page);
        this.materialsTotalPages = Math.max(0, totalPages);
        this.materials.clear();

        var registryAccess = menu.getLogic().getLevel().registryAccess();
        for (var entry : entries) {
            var key = AEItemKey.fromTag(registryAccess, entry.item());
            ItemStack stack = key != null ? key.toStack() : ItemStack.EMPTY;
            String name = stack.isEmpty() ? "" : stack.getHoverName().getString();
            name = limitNameLength(name, 50);
            this.materials.add(new MaterialRow(key, stack, name, entry.available(), entry.required(), entry.gathered(),
                    entry.craftable()));
        }

        int maxScroll = Math.max(0, this.materials.size() - this.materialsVisibleRows);
        this.materialsScroll = Math.min(this.materialsScroll, maxScroll);
    }

    private void toggleMaterials() {
        if (materialsOpen) {
            closeMaterials();
        } else {
            openMaterials();
        }
    }

    private void openMaterials() {
        materialsOpen = true;
        materialsScroll = 0;
        PacketDistributor.sendToServer(new MaterialListSubscribePacket(true));
        requestMaterialsPage(0);
    }

    private void closeMaterials() {
        materialsOpen = false;
        PacketDistributor.sendToServer(new MaterialListSubscribePacket(false));
    }

    private void requestMaterialsPage(int page) {
        if (page < 0) {
            return;
        }
        if (materialsTotalPages > 0 && page >= materialsTotalPages) {
            return;
        }
        materialsPage = page;
        materialsScroll = 0;
        PacketDistributor.sendToServer(new MaterialListPageRequestPacket(page));
    }

    private void renderMaterialsOverlay(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int rowHeight = 20;
        int panelX = this.leftPos + 6;
        int panelY = this.topPos + 6;
        int panelWidth = this.imageWidth - 12;
        int panelHeight = this.imageHeight - 12;

        materialsBounds = new Rect2i(panelX, panelY, panelWidth, panelHeight);

        guiGraphics.fill(0, 0, this.width, this.height, 0x66000000);
        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFFBFC2CC);
        guiGraphics.fill(panelX + 1, panelY + 1, panelX + panelWidth - 1, panelY + panelHeight - 1, 0xFFE8EAF2);

        String title = Component.translatable("gui.schematicenergistics.cannon_interface.materials_title").getString();
        guiGraphics.drawString(this.font, title, panelX + 10, panelY + 8, 0xFF202020, false);

        materialsCloseBounds = new Rect2i(panelX + panelWidth - 14, panelY + 7, 8, 8);
        boolean closeHover = contains(materialsCloseBounds, mouseX, mouseY);
        guiGraphics.drawString(this.font, "X", materialsCloseBounds.getX(), materialsCloseBounds.getY(),
                closeHover ? 0xFFB00020 : 0xFF202020, false);

        int listX = panelX + 8;
        int listY = panelY + 22;
        int listW = panelWidth - 16;
        int availableListHeight = panelHeight - 22 - 20;
        materialsVisibleRows = Math.max(3, Math.min(10, availableListHeight / rowHeight));
        int listH = materialsVisibleRows * rowHeight;
        materialsRowsBounds = new Rect2i(listX, listY, listW, listH);

        int maxScroll = Math.max(0, materials.size() - materialsVisibleRows);
        materialsScroll = Math.min(materialsScroll, maxScroll);

        for (int row = 0; row < materialsVisibleRows; row++) {
            int index = materialsScroll + row;
            int y = listY + row * rowHeight;

            if (index >= materials.size()) {
                guiGraphics.fill(listX, y, listX + listW, y + rowHeight - 1, 0xFFD8DAE3);
                continue;
            }

            MaterialRow entry = materials.get(index);

            boolean hovered = mouseX >= listX && mouseX < listX + listW && mouseY >= y && mouseY < y + rowHeight;

            int bg = hovered ? 0xFFF5F6FA : 0xFFE0E2EA;
            guiGraphics.fill(listX, y, listX + listW, y + rowHeight - 1, bg);

            if (!entry.stack().isEmpty()) {
                guiGraphics.renderItem(entry.stack(), listX + 2, y + 2);
            }

            String name = entry.name();
            int nameMaxWidth = listW - 22 - 10 - 60;
            String displayName = truncateByWidth(name, nameMaxWidth);
            guiGraphics.drawString(this.font, displayName, listX + 22, y + 6, 0xFF202020, false);

            String qty = entry.available() + "/" + entry.required();
            int qtyWidth = this.font.width(qty);
            int qtyX = listX + listW - 6 - qtyWidth;
            guiGraphics.drawString(this.font, qty, qtyX, y + 6, getQtyColor(entry), false);
        }

        int footerY = listY + listH + 4;

        materialsPrevBounds = new Rect2i(panelX + 8, footerY, 12, 12);
        materialsNextBounds = new Rect2i(panelX + 22, footerY, 12, 12);

        boolean prevEnabled = materialsPage > 0;
        boolean nextEnabled = materialsTotalPages > 0 && materialsPage + 1 < materialsTotalPages;

        guiGraphics.drawString(this.font, "<", materialsPrevBounds.getX() + 3, materialsPrevBounds.getY() + 2,
                prevEnabled ? (contains(materialsPrevBounds, mouseX, mouseY) ? 0xFF202020 : 0xFF202020) : 0xFF8A8C96,
                false);
        guiGraphics.drawString(this.font, ">", materialsNextBounds.getX() + 3, materialsNextBounds.getY() + 2,
                nextEnabled ? (contains(materialsNextBounds, mouseX, mouseY) ? 0xFF202020 : 0xFF202020) : 0xFF8A8C96,
                false);

        String pageText = materialsTotalPages <= 0 ? "0/0" : (materialsPage + 1) + "/" + materialsTotalPages;
        int pageTextWidth = this.font.width(pageText);
        guiGraphics.drawString(this.font, pageText, panelX + panelWidth - 8 - pageTextWidth, footerY + 2, 0xFF202020,
                false);
    }

    private int getQtyColor(MaterialRow row) {
        if (row.available() >= row.required()) {
            return 0xFF4CAF50;
        }
        return row.craftable() ? 0xFFFFC107 : 0xFFF44336;
    }

    private boolean contains(Rect2i rect, double x, double y) {
        return x >= rect.getX() && x < rect.getX() + rect.getWidth()
                && y >= rect.getY() && y < rect.getY() + rect.getHeight();
    }

    private String truncateByWidth(String text, int maxWidth) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (this.font.width(text) <= maxWidth) {
            return text;
        }
        String ellipsis = "...";
        int availableWidth = Math.max(0, maxWidth - this.font.width(ellipsis));
        return this.font.plainSubstrByWidth(text, availableWidth) + ellipsis;
    }

    private String limitNameLength(String text, int maxChars) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxChars) {
            return text;
        }
        return text.substring(0, maxChars);
    }

    private record MaterialRow(AEItemKey key, ItemStack stack, String name, long available, long required, int gathered,
            boolean craftable) {
    }
}
