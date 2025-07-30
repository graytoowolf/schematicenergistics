package lib;

import net.minecraft.network.chat.Component;

public class SEUtils {

    public static Component formatCannonStatus(String statusMsg) {
        return Component.literal("Cannon ").copy().append(Component.translatable("create.schematicannon.status." + statusMsg));
    }

}