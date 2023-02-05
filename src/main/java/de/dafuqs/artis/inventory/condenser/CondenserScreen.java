package de.dafuqs.artis.inventory.condenser;

import com.mojang.blaze3d.systems.*;
import de.dafuqs.artis.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.text.*;
import net.minecraft.util.*;

public class CondenserScreen extends HandledScreen<CondenserScreenHandler> {

    public static final Identifier BACKGROUND = new Identifier(Artis.MODID, "textures/gui/condenser.png");

    public CondenserScreen(CondenserScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title);
        this.backgroundHeight = 219;
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        this.textRenderer.draw(matrices, this.title, 8, 6, 3289650);
        this.textRenderer.draw(matrices, this.playerInventoryTitle, 8, 73, 3289650);

        this.textRenderer.draw(matrices, "x" + this.handler.getInputItemCount(), 76, titleY + 16, 3289650);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = this.x;
        int y = this.y;
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (this.handler.isBurning()) {
            int fuelProgress = this.handler.getFuelProgress();
            this.drawTexture(matrices, x + 56, y + 36 + 12 - fuelProgress, 176, 12 - fuelProgress, 14, fuelProgress + 1);
        }

        int cookProgress = this.handler.getCookProgress();
        this.drawTexture(matrices, x + 79, y + 34, 176, 14, cookProgress + 1, 16);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

}