package part;

import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.util.AEColor;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.parts.AEBasePart;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractDisplayPart;
import com.schematicenergistics.SchematicEnergistics;
import net.minecraft.resources.ResourceLocation;

public class CannonInterfaceTerminal extends AbstractDisplayPart {

    @PartModels
    public static ResourceLocation MODEL_BASE = SchematicEnergistics.makeId("part/display_base");

    @PartModels
    private static final ResourceLocation MODEL_TERMINAL_ON = SchematicEnergistics.makeId("part/cannon_terminal_on");

    @PartModels
    private static final ResourceLocation MODEL_TERMINAL_OFF = SchematicEnergistics.makeId("part/cannon_terminal_off");

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_TERMINAL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_TERMINAL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_TERMINAL_ON, MODEL_STATUS_HAS_CHANNEL);

    public CannonInterfaceTerminal(IPartItem<?> partItem, boolean requireChannel) {
        super(partItem, requireChannel);
    }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }

}
