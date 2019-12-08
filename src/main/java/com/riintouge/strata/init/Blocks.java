package com.riintouge.strata.init;

import com.riintouge.strata.GenericOreRegistry;
import com.riintouge.strata.GenericStoneRegistry;
import com.riintouge.strata.block.*;
import com.riintouge.strata.block.brick.*;
import com.riintouge.strata.block.cobble.*;
import com.riintouge.strata.block.ore.*;
import com.riintouge.strata.block.rubble.*;
import com.riintouge.strata.block.stone.*;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class Blocks
{
    public static WeakStoneBlock weakStone;
    public static MediumStoneBlock mediumStone;
    public static StrongStoneBlock strongStone;
    public static VeryStrongStoneBlock veryStrongStone;

    public static WeakRubbleBlock weakRubble;
    public static MediumCobbleBlock mediumCobble;
    public static StrongCobbleBlock strongCobble;
    public static VeryStrongCobbleBlock veryStrongCobble;

    public static WeakBrickBlock weakBrick;
    public static MediumBrickBlock mediumBrick;
    public static StrongBrickBlock strongBrick;
    public static VeryStrongBrickBlock veryStrongBrick;

    public static WeakSandOreBlock weakSandOre;
    public static WeakStoneOreBlock weakStoneOre;
    public static MediumStoneOreBlock mediumStoneOre;
    public static StrongStoneOreBlock strongStoneOre;

    static
    {
        GenericStoneRegistry stoneRegistry = GenericStoneRegistry.INSTANCE;

        for( IGenericStoneTileSetInfo info : WeakStoneTileSetInfo.values() )
            stoneRegistry.register( new GenericStoneTileSet( info ) );

        for( IGenericStoneTileSetInfo info : MediumStoneTileSetInfo.values() )
            stoneRegistry.register( new GenericStoneTileSet( info ) );

        for( IGenericStoneTileSetInfo info : StrongStoneTileSetInfo.values() )
            stoneRegistry.register( new GenericStoneTileSet( info ) );

        for( IGenericStoneTileSetInfo info : VeryStrongStoneTileSetInfo.values() )
            stoneRegistry.register( new GenericStoneTileSet( info ) );

        GenericOreRegistry oreRegistry = GenericOreRegistry.INSTANCE;

        for( IOreInfo info : WeakOreInfo.values() )
            oreRegistry.register( new GenericOreTileSet( info ) );

        for( IOreInfo info : MediumOreInfo.values() )
            oreRegistry.register( new GenericOreTileSet( info ) );

        for( IOreInfo info : StrongOreInfo.values() )
            oreRegistry.register( new GenericOreTileSet( info ) );

        weakStone = WeakStoneBlock.INSTANCE;
        mediumStone = MediumStoneBlock.INSTANCE;
        strongStone = StrongStoneBlock.INSTANCE;
        veryStrongStone = VeryStrongStoneBlock.INSTANCE;

        weakRubble = WeakRubbleBlock.INSTANCE;
        mediumCobble = MediumCobbleBlock.INSTANCE;
        strongCobble = StrongCobbleBlock.INSTANCE;
        veryStrongCobble = VeryStrongCobbleBlock.INSTANCE;

        weakBrick = WeakBrickBlock.INSTANCE;
        mediumBrick = MediumBrickBlock.INSTANCE;
        strongBrick = StrongBrickBlock.INSTANCE;
        veryStrongBrick = VeryStrongBrickBlock.INSTANCE;

        weakSandOre = WeakSandOreBlock.INSTANCE;
        weakStoneOre = WeakStoneOreBlock.INSTANCE;
        mediumStoneOre = MediumStoneOreBlock.INSTANCE;
        strongStoneOre = StrongStoneOreBlock.INSTANCE;
    }

    @SubscribeEvent
    public static void onEvent( RegistryEvent.Register< Block > event )
    {
        System.out.println( "Blocks::registerBlocks()" );

        IForgeRegistry< Block > blockRegistry = event.getRegistry();

        GenericStoneRegistry.INSTANCE.registerBlocks( event );
        GenericOreRegistry.INSTANCE.registerBlocks( event );

        blockRegistry.register( weakStone );
        blockRegistry.register( mediumStone );
        blockRegistry.register( strongStone );
        blockRegistry.register( veryStrongStone );

        blockRegistry.register( weakRubble );
        blockRegistry.register( mediumCobble );
        blockRegistry.register( strongCobble );
        blockRegistry.register( veryStrongCobble );

        blockRegistry.register( weakBrick );
        blockRegistry.register( mediumBrick );
        blockRegistry.register( strongBrick );
        blockRegistry.register( veryStrongBrick );

        blockRegistry.register( weakSandOre );
        blockRegistry.register( weakStoneOre );
        blockRegistry.register( mediumStoneOre );
        blockRegistry.register( strongStoneOre );

        GameRegistry.registerTileEntity(
            DynamicOreHostTileEntity.class,
            new ResourceLocation( "strata:ore_tile_entity" ) );
    }
}
