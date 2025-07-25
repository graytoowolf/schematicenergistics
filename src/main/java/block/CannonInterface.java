package block;

import appeng.block.AEBaseEntityBlock;
import blockentity.CannonInterfaceEntity;
import core.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CannonInterface extends AEBaseEntityBlock<CannonInterfaceEntity> {
    public CannonInterface(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public BlockEntityType<CannonInterfaceEntity> getBlockEntityType() {
        return Registration.CANNON_INTERFACE_ENTITY.get();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof CannonInterfaceEntity cannonInterfaceEntity) {
            if (!level.isClientSide()) {
                ((ServerPlayer) player).openMenu(new SimpleMenuProvider(cannonInterfaceEntity, Component.literal("Cannon Interface")), pos);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.SUCCESS;
    }
}
