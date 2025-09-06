package zzhangaao1z.cobblemonmovedex.mixin.client;

import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexEntry;
import com.cobblemon.mod.common.api.pokedex.entry.PokedexForm;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUI;
import com.cobblemon.mod.common.client.gui.pokedex.PokedexGUIConstants;
import com.cobblemon.mod.common.client.gui.pokedex.ScaledButton;
import com.cobblemon.mod.common.client.gui.pokedex.widgets.*;
import com.cobblemon.mod.common.pokemon.FormData;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbility;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import zzhangaao1z.cobblemonmovedex.main.MovesWidget;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mixin(value = PokedexGUI.class, remap = false)
public abstract class PokedexGUIMixin {

    @Shadow @Final private static ResourceLocation[] tabIcons;

    @Unique private static final ResourceLocation[] new_tabIcons = ArrayUtils.add(tabIcons, ResourceLocation.fromNamespaceAndPath("cobblemonmovedex", "textures/tab_moves.png"));

    @Shadow @Final private List<ScaledButton> tabButtons;

    @Shadow private int tabInfoIndex;

    @Shadow public GuiEventListener tabInfoElement;

    @Shadow private PokedexEntry selectedEntry;

    @Shadow private PokedexForm selectedForm;

    @Shadow private PokemonInfoWidget pokemonInfoWidget;

    /**
     * @author ZzhangaAo1z
     * @reason 增加技能图标
     */
    @Overwrite
    public final void displaytabInfoElement(int tabIndex, boolean update){
        PokedexGUI gui = cast(this);
        boolean showActiveTab = this.selectedEntry != null && CobblemonClient.INSTANCE.getClientPokedexData().getCaughtForms(this.selectedEntry).contains(this.selectedForm);
        if(!this.tabButtons.isEmpty() && this.tabButtons.size() > tabIndex){
            for(int i = 0; i < this.tabButtons.size(); i++){
                this.tabButtons.get(i).setWidgetActive(showActiveTab && i == tabIndex);
            }
        }

        if(this.tabInfoIndex == 1 && (this.tabInfoElement instanceof AbilitiesWidget widget)){
            gui.removeWidget(widget.getLeftButton());
            gui.removeWidget(widget.getRightButton());
        }else if(this.tabInfoIndex == 5 && (this.tabInfoElement instanceof MovesWidget widget)){
            gui.removeWidget(widget.getLeftButton());
            gui.removeWidget(widget.getRightButton());
        }

        this.tabInfoIndex = tabIndex;
        if(this.tabInfoElement != null){
            gui.removeWidget(this.tabInfoElement);
        }
        int x = (gui.width - PokedexGUIConstants.BASE_WIDTH) / 2;
        int y = (gui.height - 207) / 2;
        switch (tabIndex){
            case 0:
                gui.setTabInfoElement(new DescriptionWidget(x + 180, y + 135));
                break;
            case 1:
                gui.setTabInfoElement(new AbilitiesWidget(x + 180, y + 135));
                break;
            case 2:
                gui.setTabInfoElement(new SizeWidget(x + 180, y + 135));
                break;
            case 3:
                gui.setTabInfoElement(new StatsWidget(x + 180, y + 135));
                break;
            case 4:
                gui.setTabInfoElement(new DropsScrollingWidget(x + 189, y + 135));
                break;
            case 5:
                gui.setTabInfoElement(new MovesWidget(x + 180, y + 135));
                break;
        }
        if(this.tabInfoElement instanceof Renderable && this.tabInfoElement instanceof NarratableEntry){
            gui.addRenderableWidget((GuiEventListener & Renderable & NarratableEntry) this.tabInfoElement);
        }
        if(update){
            gui.updateTabInfoElement();
        }
    }


    /**
     * @author ZzhangaAo1z
     * @reason 增加技能图标
     */
    @Overwrite
    public final void updateTabInfoElement(){
        PokedexGUI gui = cast(this);
        Species species = this.selectedEntry == null ? null : PokemonSpecies.INSTANCE.getByIdentifier(this.selectedEntry.getSpeciesId());
        String form_name = this.selectedForm != null ? this.selectedForm.getDisplayForm() : null;
        boolean canDisplay = this.selectedEntry != null && CobblemonClient.INSTANCE.getClientPokedexData().getCaughtForms(this.selectedEntry).contains(this.selectedForm);
        List<String> description = new ArrayList<>();
        if(canDisplay && species != null){
            FormData form = species.getForms().stream().filter(formData -> formData.getName().equals(form_name)).findFirst().orElse(species.getStandardForm());
            switch (this.tabInfoIndex){
                case 0:
                    description.addAll(form.getPokedex());
                    if(this.tabInfoElement instanceof DescriptionWidget descriptionWidget){
                        descriptionWidget.setShowPlaceholder(false);
                    }
                    break;
                case 1:
                    if(this.tabInfoElement instanceof AbilitiesWidget abilitiesWidget){
                        abilitiesWidget.setAbilitiesList(StreamSupport.stream(form.getAbilities().spliterator(), false).sorted(Comparator.comparing(ability -> (ability instanceof HiddenAbility) ? 1 : 0)).map(PotentialAbility::getTemplate).collect(Collectors.toList()));
                        abilitiesWidget.setSelectedAbilitiesIndex(0);
                        abilitiesWidget.setAbility();
                        abilitiesWidget.setScrollAmount(0);
                        if(abilitiesWidget.getAbilitiesList().size() > 1){
                            gui.addRenderableWidget(abilitiesWidget.getLeftButton());
                            gui.addRenderableWidget(abilitiesWidget.getRightButton());
                        }
                    }
                    break;
                case 2:
                    if(this.pokemonInfoWidget != null && this.pokemonInfoWidget.getRenderablePokemon() != null && this.tabInfoElement instanceof SizeWidget sizeWidget){
                        sizeWidget.setPokemonHeight(form.getHeight());
                        sizeWidget.setWeight(form.getWeight());
                        sizeWidget.setBaseScale(form.getBaseScale());
                        sizeWidget.setRenderablePokemon(this.pokemonInfoWidget.getRenderablePokemon());
                    }
                    break;
                case 3:
                    if(this.tabInfoElement instanceof StatsWidget statsWidget){
                        statsWidget.setBaseStats(form.getBaseStats());
                    }
                    break;
                case 4:
                    if(this.tabInfoElement instanceof DropsScrollingWidget dropsScrollingWidget){
                        dropsScrollingWidget.setDropTable(form.getDrops());
                        dropsScrollingWidget.setEntries();
                    }
                    break;
                case 5:
                    if(this.tabInfoElement instanceof MovesWidget movesWidget){
                        movesWidget.setLearnset(form.getMoves());
                        movesWidget.setEntries();
                        gui.addRenderableWidget(movesWidget.getLeftButton());
                        gui.addRenderableWidget(movesWidget.getRightButton());
                    }
                    break;
            }
        }else {
            if(this.tabInfoIndex != 0){
                gui.displaytabInfoElement(0, true);
            }
            if(this.tabInfoElement instanceof DescriptionWidget descriptionWidget){
                descriptionWidget.setShowPlaceholder(true);
            }
        }

        if(this.tabInfoIndex == 0 && this.tabInfoElement instanceof DescriptionWidget descriptionWidget){
            descriptionWidget.setText(description);
            descriptionWidget.setScrollAmount(0);
        }
    }

    /**
     * @author ZzhangaAo1z
     * @reason 增加技能图标
     */
    @Overwrite
    public final void setUpTabs(){
        PokedexGUI gui = cast(this);
        int x = (gui.width - PokedexGUIConstants.BASE_WIDTH)/2;
        int y = (gui.height - PokedexGUIConstants.BASE_HEIGHT)/2;

        if(!this.tabButtons.isEmpty()){
            this.tabButtons.clear();
        }

        for(int i = 0; i < new_tabIcons.length; i++){
            int j = i;
            this.tabButtons.add(new ScaledButton(x + 197f + (i * 20f), y + 181.5f, PokedexGUIConstants.TAB_ICON_SIZE, PokedexGUIConstants.TAB_ICON_SIZE, new_tabIcons[i], 0.5f, false, button -> {
                if(gui.canSelectTab(j)){
                    gui.displaytabInfoElement(j, true);
                }
            }));
        }

        for(ScaledButton button : this.tabButtons){
            gui.addRenderableWidget(button);
        }
    }

    @Unique
    private static PokedexGUI cast(Object object){
        return (PokedexGUI) (Object) object;
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/api/gui/GuiUtilsKt;blitk$default(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/resources/ResourceLocation;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;Ljava/lang/Number;ZFILjava/lang/Object;)V", ordinal = 5), index = 2, remap = true)
    private Number modifyX(Number number){
        return (((cast(this).width - PokedexGUIConstants.BASE_WIDTH) / 2 + 198) + (20 * this.tabInfoIndex)) / 0.5f;
    }

}
