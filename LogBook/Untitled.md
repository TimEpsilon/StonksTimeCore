Hi, I'm having some trouble warping my head around partial models. I have my base model, basic block entity and block classes, and they're all registered using a createRegistrate with my mod's id. However, when I try to add any partial model, nothing shows up in game. Could anyone please help me pinpoint what I missed? 
```java 
public static final BlockEntityEntry<BankBlockEntity> BANK = REGISTRATE  
        .blockEntity("bank", BankBlockEntity::new)  
        .visual(() -> OrientedRotatingVisual.of(AllPartialModels.SHAFT), false)  
        .validBlocks(ModBlocks.BANK)  
        .renderer(() -> BankRenderer::new)  
        .register();
```
```java
public static final BlockEntry<BankBlock> BANK = REGISTRATE  
        .block("bank", BankBlock::new)  
        .initialProperties(SharedProperties::softMetal)  
        .properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))  
        .transform(pickaxeOnly())  
        .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))  
        .onRegister(block -> BlockStressValues.IMPACTS.register(block, () -> 16))  
        .item()  
        .transform(customItemModel())  
        .register();
```
```java
public class BankRenderer extends KineticBlockEntityRenderer<BankBlockEntity> {  
  
    public BankRenderer(BlockEntityRendererProvider.Context context) {  
        super(context);  
    }  
  
    @Override  
    protected SuperByteBuffer getRotatedModel(BankBlockEntity be, BlockState state) {  
        return CachedBuffers.partial(AllPartialModels.SHAFT, state);  
    }  
}
```
