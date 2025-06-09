package core;

import appeng.block.AEBaseBlock;
import appeng.blockentity.AEBaseBlockEntity;
import block.CannonInterface;
import blockentity.CannonInterfaceEntity;
import blockitem.CannonInterfaceBlockItem;
import com.schematicenergistics.SchematicEnergistics;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SchematicEnergistics.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SchematicEnergistics.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SchematicEnergistics.MOD_ID);

    public static final RegistryObject<CannonInterface> CANNON_INTERFACE = BLOCKS.register(
            "cannon_interface",
            () -> new CannonInterface(AEBaseBlock.metalProps())
    );

    public static final RegistryObject<BlockEntityType<CannonInterfaceEntity>> CANNON_INTERFACE_ENTITY =
            BLOCK_ENTITIES.register("cannon_interface",
                    () -> {
                        BlockEntityType<CannonInterfaceEntity> type = BlockEntityType.Builder.of(CannonInterfaceEntity::new, CANNON_INTERFACE.get()).build(null);
                        AEBaseBlockEntity.registerBlockEntityItem(type, CANNON_INTERFACE.get().asItem());
                        CANNON_INTERFACE.get().setBlockEntity(CannonInterfaceEntity.class, type, null, null);
                        return type;
                    });

    public static final RegistryObject<BlockItem> CANNON_INTERFACE_ITEM = ITEMS.register("cannon_interface",
            () -> new CannonInterfaceBlockItem(CANNON_INTERFACE.get(), new Item.Properties()));

    public static void init(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
    }

}
