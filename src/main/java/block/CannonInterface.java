package block;

import appeng.block.AEBaseEntityBlock;
import blockentity.CannonInterfaceEntity;
import core.Registration;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CannonInterface extends AEBaseEntityBlock<CannonInterfaceEntity> {
    public CannonInterface(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public BlockEntityType<CannonInterfaceEntity> getBlockEntityType() {
        return Registration.CANNON_INTERFACE_ENTITY.get();
    }
}
