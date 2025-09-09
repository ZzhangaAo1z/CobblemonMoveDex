package zzhangaao1z.cobblemonmovedex.main;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.categories.DamageCategory;
import com.cobblemon.mod.common.api.pokemon.moves.Learnset;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.client.gui.ScrollingWidget;
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants;
import com.cobblemon.mod.common.client.gui.pokedex.ScaledButton;
import com.cobblemon.mod.common.client.render.RenderHelperKt;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class MovesWidget extends ScrollingWidget<MovesWidget.MovesWidgetEntry> {

    private static final ResourceLocation arrowLeft = ResourceLocation.fromNamespaceAndPath("cobblemon", "textures/gui/pokedex/info_arrow_left.png");

    private static final ResourceLocation arrowRight = ResourceLocation.fromNamespaceAndPath("cobblemon", "textures/gui/pokedex/info_arrow_right.png");

    private final ScaledButton leftButton;

    private final ScaledButton rightButton;

    private final int x;

    private final int y;

    private Learnset learnset;

    private int page = 0;

    public MovesWidget(int x, int y){
        super(y, x + 9, PokedexGUIConstants.HALF_OVERLAY_WIDTH - 2, 42, 6, 5);
        this.x = x;
        this.y = y;
        this.learnset = new Learnset();
        this.leftButton = new ScaledButton(x + 2.5f, y - 8f, 7, 10, arrowLeft, PokedexGUIConstants.SCALE, false, button -> switchPage(false));
        this.rightButton = new ScaledButton(x + 133f, y - 8f, 7, 10, arrowRight, PokedexGUIConstants.SCALE, false, button -> switchPage(true));
        setEntries();
    }

    public void setLearnset(Learnset learnset){
        this.learnset = learnset;
    }

    public void switchPage(boolean next){
        if(next && this.page < 3){
            this.page += 1;
        }else if(!next && this.page > 0){
            this.page -= 1;
        }
        setEntries();
        setScrollAmount(0);
    }

    public void setEntries(){
        this.clearEntries();
        switch (this.page){
            case 0:
                this.learnset.getEvolutionMoves().forEach(move -> new MovesWidgetEntry(0, move));
                this.learnset.getLevelUpMoves().forEach((level, list) -> list.forEach(move -> addEntry(new MovesWidgetEntry(level, move))));
                break;
            case 1:
                this.learnset.getTmMoves().forEach(move -> addEntry(new MovesWidgetEntry(null, move)));
                break;
            case 2:
                this.learnset.getTutorMoves().forEach(move -> addEntry(new MovesWidgetEntry(null, move)));
                break;
            case 3:
                this.learnset.getEggMoves().forEach(move -> addEntry(new MovesWidgetEntry(null, move)));
                break;
        }
    }

    @Override
    public int getX(){
        return this.x;
    }

    @Override
    public int getY(){
        return this.y;
    }

    @Override
    public int getScrollbarPosition(){
        return this.getLeft() + this.getWidth() - this.getScrollBarWidth() - 7;
    }

    @Override
    public int getBottom(){
        return this.getY() + this.getHeight() - 1;
    }

    public ScaledButton getLeftButton(){
        return this.leftButton;
    }

    public ScaledButton getRightButton(){
        return this.rightButton;
    }

    @Override
    public void renderScrollbar(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int left = this.getScrollbarPosition();
        int right = left + 3;
        int start = this.getY() + 1;
        int height = this.getBottom() - start;
        int bottom = height * height / this.getMaxPosition();
        bottom = Math.clamp(bottom, 32, height - 8);
        int top = ((int) this.getScrollAmount()) * (height - bottom) / this.getMaxScroll() + start;
        if(top < start){
            top = start;
        }

        graphics.fill(left + 1, start, right - 1, this.getBottom(), FastColor.ARGB32.color(255, 126, 231, 229));
        graphics.fill(left, top, right, top + bottom, FastColor.ARGB32.color(255, 58, 150, 182));
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float delta){
        MutableComponent component = switch (this.page) {
            case 0 -> Component.translatable("cobblemonmovedex.ui.pokedex.info.moves_level");
            case 1 -> Component.translatable("cobblemonmovedex.ui.pokedex.info.moves_tm");
            case 2 -> Component.translatable("cobblemonmovedex.ui.pokedex.info.moves_tutor");
            case 3 -> Component.translatable("cobblemonmovedex.ui.pokedex.info.moves_egg");
            default -> null;
        };
        if(component != null){
            RenderHelperKt.drawScaledText(graphics, null, component.setStyle(Style.EMPTY.withBold(true)), this.x + 9, this.y - 10, 1,100, Integer.MAX_VALUE, 0x00FFFFFF, false, true, null, null);
        }
        super.renderWidget(graphics, mouseX, mouseY, delta);
    }

    public static class MovesWidgetEntry extends Slot<MovesWidgetEntry>{

        private static final DecimalFormat df = new DecimalFormat("0");

        private final Integer level;

        private final MoveTemplate move;

        public MovesWidgetEntry(Integer level, MoveTemplate move) {
            this.level = level;
            this.move = move;
        }

        @Override
        public @NotNull Component getNarration() {
            return this.move.getDisplayName();
        }

        @Override
        public void render(GuiGraphics graphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int xOffset = 3;
            if(this.level != null){
                String level_prefix = this.level <= 1 ? "—" : this.level.toString();
                RenderHelperKt.drawScaledText(graphics, null, Component.literal(level_prefix), x + xOffset, y + 2, PokedexGUIConstants.SCALE,100, Integer.MAX_VALUE, 0x606B6E, true, false, null, null);
                xOffset += 14;
            }

            ElementalType type = this.move.getElementalType();
            RenderHelperKt.drawScaledText(graphics, null, type.getDisplayName(), x + xOffset, y + 2, PokedexGUIConstants.SCALE,100, Integer.MAX_VALUE, type.getHue(), true, false, null, null);
            xOffset += 12;

            RenderHelperKt.drawScaledText(graphics, null, this.move.getDisplayName(), x + xOffset, y + 2, PokedexGUIConstants.SCALE,100, Integer.MAX_VALUE, 0x606B6E, false, false, null, null);
            xOffset += this.level != null ? 39 : 53;

            DamageCategory category = this.move.getDamageCategory();
            graphics.blit(category.getResourceLocation(), x + xOffset, y + 2, 6, 4, 0, category.getTextureXMultiplier() * 16, 24, 16, 24, 48);
            xOffset += 11;

            graphics.blit(ResourceLocation.fromNamespaceAndPath("cobblemon", "textures/gui/summary/summary_moves_icon_power.png"), x + xOffset, y + 2, 4, 4, 0, 0, 10, 10, 10, 10);
            xOffset += 5;
            MutableComponent power_component = Component.literal(this.move.getPower() == 0 ? "—" : df.format(this.move.getPower()));
            RenderHelperKt.drawScaledText(graphics, null, power_component, x + xOffset, y + 2, PokedexGUIConstants.SCALE,100, Integer.MAX_VALUE, 0x606B6E, false, false, null, null);
            xOffset += 10;

            graphics.blit(ResourceLocation.fromNamespaceAndPath("cobblemon", "textures/gui/summary/summary_moves_icon_accuracy.png"), x + xOffset, y + 2, 4, 4, 0, 0, 10, 10, 10, 10);
            xOffset += 5;
            MutableComponent accuracy_component = Component.literal(this.move.getAccuracy() == -1 ? "—" : df.format(this.move.getAccuracy()) + "%");
            RenderHelperKt.drawScaledText(graphics, null, accuracy_component, x + xOffset, y + 2, PokedexGUIConstants.SCALE,100, Integer.MAX_VALUE, 0x606B6E, false, false, null, null);
            xOffset += 14;

            MutableComponent pp_component = Component.literal("PP:" + df.format(this.move.getMaxPp()));
            RenderHelperKt.drawScaledText(graphics, null, pp_component, x + xOffset, y + 2, PokedexGUIConstants.SCALE,100, Integer.MAX_VALUE, 0x606B6E, false, false, null, null);
        }
    }

}
