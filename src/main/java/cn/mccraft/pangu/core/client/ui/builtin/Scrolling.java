package cn.mccraft.pangu.core.client.ui.builtin;

import cn.mccraft.pangu.core.client.ui.Component;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@Accessors(chain = true)
public abstract class Scrolling extends Component {
    public static final int SCROLL_BAR_WIDTH = 6;

    protected float scrollFactor;
    protected float initialMouseClickY = -2.0F;
    @Getter
    protected float scrollDistance;

    @Getter
    @Setter
    protected float generalScrollingDistance = 8;

    @Getter
    @Setter
    protected boolean showScrollBar = true;

    public Scrolling(float width, float height) {
        setSize(width, height);
    }

    public abstract int getContentHeight();

    public float getContentWidth() {
        if (isShowScrollBar())
            return getWidth() - SCROLL_BAR_WIDTH;
        return getWidth();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDraw(float partialTicks, int mouseX, int mouseY) {
        drawBackground();

        float scrollBarLeft = getX() + getContentWidth();
        float scrollBarRight = scrollBarLeft + SCROLL_BAR_WIDTH;

        if ((getScreen() == null || getScreen().getModal() == null) && Mouse.isButtonDown(0)) {
            if (this.initialMouseClickY == -1.0F) {
                if (isHovered()) {
                    float mouseListY = mouseY - getY() + this.scrollDistance;

                    // on element click
                    if (mouseX - getX() <= getContentWidth()) {
                        onContentClick(mouseX - getX(), mouseListY);
                        playPressSound();
                    }

                    // on scroll bar clicked
                    if (isShowScrollBar() && mouseX >= scrollBarLeft && mouseX <= scrollBarRight) {
                        this.scrollFactor = -1.0F;
                        float scrollHeight = this.getContentHeight() - getHeight();
                        if (scrollHeight < 1) scrollHeight = 1;

                        float var13 = (getHeight() * getHeight()) / this.getContentHeight();

                        if (var13 < 32) var13 = 32;
                        if (var13 > getHeight()) var13 = getHeight();

                        this.scrollFactor /= (getHeight() - var13) / scrollHeight;
                    } else {
                        this.scrollFactor = 1.0F;
                    }

                    this.initialMouseClickY = mouseY;
                } else {
                    this.initialMouseClickY = -2.0F;
                }
            } else if (this.initialMouseClickY >= 0.0F) {
                this.scrollDistance -= ((float) mouseY - this.initialMouseClickY) * this.scrollFactor;
                this.initialMouseClickY = (float) mouseY;
            }
        } else {
            this.initialMouseClickY = -1.0F;
        }

        applyScrollLimits();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();

        Minecraft client = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(client);

        double scaleW = client.displayWidth / res.getScaledWidth_double();
        double scaleH = client.displayHeight / res.getScaledHeight_double();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                (int) (getX() * scaleW), (int) (client.displayHeight - ((getY() + getHeight()) * scaleH)),
                (int) (getWidth() * scaleW), (int) (getHeight() * scaleH)
        );

        float baseY = this.getY() - this.scrollDistance;

        float mouseListY = mouseY - getY() + this.scrollDistance;

        this.onContentDraw(baseY, mouseX - getX(), mouseListY);

        // Scrolling bar
        float extraHeight = this.getContentHeight() - getHeight();

        if (isShowScrollBar() && extraHeight > 0) {
            float height = (getHeight() * getHeight()) / this.getContentHeight();

            if (height > getHeight()) height = getHeight();

            if (height < 32) height = 32;

            float barTop = this.scrollDistance * (getHeight() - height) / extraHeight + getY();
            if (barTop < getY()) barTop = getY();

            GlStateManager.disableTexture2D();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(scrollBarLeft, getY() + getHeight(), 0.0D).tex(0.0D, 1.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            buffer.pos(scrollBarRight, getY() + getHeight(), 0.0D).tex(1.0D, 1.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            buffer.pos(scrollBarRight, getY(), 0.0D).tex(1.0D, 0.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            buffer.pos(scrollBarLeft, getY(), 0.0D).tex(0.0D, 0.0D).color(0x00, 0x00, 0x00, 0xFF).endVertex();
            tess.draw();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(scrollBarLeft, barTop + height, 0.0D).tex(0.0D, 1.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            buffer.pos(scrollBarRight, barTop + height, 0.0D).tex(1.0D, 1.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            buffer.pos(scrollBarRight, barTop, 0.0D).tex(1.0D, 0.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            buffer.pos(scrollBarLeft, barTop, 0.0D).tex(0.0D, 0.0D).color(0x80, 0x80, 0x80, 0xFF).endVertex();
            tess.draw();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            buffer.pos(scrollBarLeft, barTop + height - 1, 0.0D).tex(0.0D, 1.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            buffer.pos(scrollBarRight - 1, barTop + height - 1, 0.0D).tex(1.0D, 1.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            buffer.pos(scrollBarRight - 1, barTop, 0.0D).tex(1.0D, 0.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            buffer.pos(scrollBarLeft, barTop, 0.0D).tex(0.0D, 0.0D).color(0xC0, 0xC0, 0xC0, 0xFF).endVertex();
            tess.draw();
            GlStateManager.enableTexture2D();
        }

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void applyScrollLimits() {
        float listHeight = this.getContentHeight() - getHeight();

        if (listHeight < 0) listHeight /= 2;

        if (this.scrollDistance < 0.0F) this.scrollDistance = 0.0F;

        if (this.scrollDistance > listHeight)
            this.scrollDistance = listHeight;
    }

    @Override
    public void onMouseInput(int mouseX, int mouseY) {
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0) {
            this.scrollDistance += (-1 * scroll / 120.0F) * this.generalScrollingDistance / 2;
        }
    }

    public abstract void onContentClick(float mouseListX, float mouseListY);

    @Deprecated
    public void onContentDraw(float baseY){
    }

    public void onContentDraw(float baseY, float mouseListX, float mouseListY) {
        onContentDraw(baseY);
    }

    public void drawBackground() {
    }
}