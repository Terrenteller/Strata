package com.riintouge.strata.init;

import com.riintouge.strata.block.*;
import com.riintouge.strata.item.*;
import com.riintouge.strata.item.ore.*;
import com.riintouge.strata.property.*;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class Items
{
    public static GenericItemBlock weakStone;
    public static GenericItemBlock mediumStone;
    public static GenericItemBlock strongStone;
    public static GenericItemBlock veryStrongStone;

    public static GenericItemBlock weakRubble;
    public static GenericItemBlock mediumCobble;
    public static GenericItemBlock strongCobble;
    public static GenericItemBlock veryStrongCobble;

    public static GenericItemBlock weakBrick;
    public static GenericItemBlock mediumBrick;
    public static GenericItemBlock strongBrick;
    public static GenericItemBlock veryStrongBrick;

    public static GenericItemBlock weakSandOre;
    public static GenericItemBlock weakStoneOre;
    public static GenericItemBlock mediumStoneOre;
    public static GenericItemBlock strongStoneOre;

    public static WeakStoneOreItem weakStoneOreItem;
    public static MediumStoneOreItem mediumStoneOreItem;
    public static StrongStoneOreItem strongStoneOreItem;

    static
    {
        weakStone = new GenericItemBlock( Blocks.weakStone );
        mediumStone = new GenericItemBlock( Blocks.mediumStone );
        strongStone = new GenericItemBlock( Blocks.strongStone );
        veryStrongStone = new GenericItemBlock( Blocks.veryStrongStone );

        weakRubble = new GenericItemBlock( Blocks.weakRubble );
        mediumCobble = new GenericItemBlock( Blocks.mediumCobble );
        strongCobble = new GenericItemBlock( Blocks.strongCobble );
        veryStrongCobble = new GenericItemBlock( Blocks.veryStrongCobble );

        weakBrick = new GenericItemBlock( Blocks.weakBrick );
        mediumBrick = new GenericItemBlock( Blocks.mediumBrick );
        strongBrick = new GenericItemBlock( Blocks.strongBrick );
        veryStrongBrick = new GenericItemBlock( Blocks.veryStrongBrick );

        weakSandOre = new GenericItemBlock( Blocks.weakSandOre );
        weakStoneOre = new GenericItemBlock( Blocks.weakStoneOre );
        mediumStoneOre = new GenericItemBlock( Blocks.mediumStoneOre );
        strongStoneOre = new GenericItemBlock( Blocks.strongStoneOre );

        weakStoneOreItem = WeakStoneOreItem.INSTANCE;
        mediumStoneOreItem = MediumStoneOreItem.INSTANCE;
        strongStoneOreItem = StrongStoneOreItem.INSTANCE;
    }

    @SubscribeEvent
    public static void onEvent( RegistryEvent.Register< Item > event )
    {
        System.out.println( "Items::registerBlocks()" );

        IForgeRegistry< Item > itemRegistry = event.getRegistry();

        itemRegistry.register( weakStone );
        itemRegistry.register( mediumStone );
        itemRegistry.register( strongStone );
        itemRegistry.register( veryStrongStone );

        itemRegistry.register( weakRubble );
        itemRegistry.register( mediumCobble );
        itemRegistry.register( strongCobble );
        itemRegistry.register( veryStrongCobble );

        itemRegistry.register( weakBrick );
        itemRegistry.register( mediumBrick );
        itemRegistry.register( strongBrick );
        itemRegistry.register( veryStrongBrick );

        itemRegistry.register( weakSandOre );
        itemRegistry.register( weakStoneOre );
        itemRegistry.register( mediumStoneOre );
        itemRegistry.register( strongStoneOre );

        itemRegistry.register( weakStoneOreItem );
        itemRegistry.register( mediumStoneOreItem );
        itemRegistry.register( strongStoneOreItem );
    }

    @SubscribeEvent
    public static void onEvent( ModelRegistryEvent event )
    {
        System.out.println( "Items::registerModels()" );

        // FIXME: We know a tad too much about implementation details here...

        ResourceUtil.registerModelVariants( Blocks.weakStone , weakStone , PropertyWeakStone.PROPERTY );
        ResourceUtil.registerModelVariants( Blocks.mediumStone , mediumStone , PropertyMediumStone.PROPERTY );
        ResourceUtil.registerModelVariants( Blocks.strongStone , strongStone , PropertyStrongStone.PROPERTY );
        ResourceUtil.registerModelVariants( Blocks.veryStrongStone , veryStrongStone , PropertyVeryStrongStone.PROPERTY );

        ResourceUtil.registerModelVariants( Blocks.weakRubble , weakRubble , PropertyWeakStone.PROPERTY );
        ResourceUtil.registerModelVariants( Blocks.mediumCobble , mediumCobble , PropertyMediumStone.PROPERTY );
        ResourceUtil.registerModelVariants( Blocks.strongCobble , strongCobble , PropertyStrongStone.PROPERTY );
        ResourceUtil.registerModelVariants( Blocks.veryStrongCobble , veryStrongCobble , PropertyVeryStrongStone.PROPERTY );

        ResourceUtil.registerModelVariants( Blocks.weakBrick , weakBrick , PropertyWeakStone.PROPERTY );
        ResourceUtil.registerModelVariants( Blocks.mediumBrick , mediumBrick , PropertyMediumStone.PROPERTY );
        ResourceUtil.registerModelVariants( Blocks.strongBrick , strongBrick , PropertyStrongStone.PROPERTY );
        ResourceUtil.registerModelVariants( Blocks.veryStrongBrick , veryStrongBrick , PropertyVeryStrongStone.PROPERTY );

        ResourceUtil.registerModelVariants( Blocks.weakSandOre , weakSandOre , PropertyWeakSandOre.PROPERTY );
        ResourceUtil.registerModelVariants( Blocks.weakStoneOre , weakStoneOre , PropertyWeakStoneOre.PROPERTY );
        ResourceUtil.registerModelVariants( Blocks.mediumStoneOre , mediumStoneOre , PropertyMediumStoneOre.PROPERTY );
        ResourceUtil.registerModelVariants( Blocks.strongStoneOre , strongStoneOre , PropertyStrongStoneOre.PROPERTY );

        ResourceUtil.registerOreItem( weakStoneOreItem );
        ResourceUtil.registerOreItem( mediumStoneOreItem );
        ResourceUtil.registerOreItem( strongStoneOreItem );

    }
}
