package com.riintouge.strata.init;

import com.riintouge.strata.GenericOreRegistry;
import com.riintouge.strata.GenericTileSetRegistry;
import com.riintouge.strata.block.*;
import com.riintouge.strata.block.ore.*;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Blocks
{
    @SubscribeEvent
    public static void onEvent( RegistryEvent.Register< Block > event )
    {
        System.out.println( "Blocks::registerBlocks()" );

        GenericTileSetRegistry tileSetRegistry = GenericTileSetRegistry.INSTANCE;

        for( IGenericTileSetInfo info : CrudeGroundTileSetInfo.values() )
            tileSetRegistry.register( new GenericGroundTileSet( info ) );

        for( IGenericTileSetInfo info : ClayTileSetInfo.values() )
            tileSetRegistry.register( new GenericClayTileSet( info ) );

        for( IGenericStoneTileSetInfo info : WeakStoneTileSetInfo.values() )
            tileSetRegistry.register( new GenericStoneTileSet( info ) );

        for( IGenericStoneTileSetInfo info : MediumStoneTileSetInfo.values() )
            tileSetRegistry.register( new GenericStoneTileSet( info ) );

        for( IGenericStoneTileSetInfo info : StrongStoneTileSetInfo.values() )
            tileSetRegistry.register( new GenericStoneTileSet( info ) );

        for( IGenericStoneTileSetInfo info : VeryStrongStoneTileSetInfo.values() )
            tileSetRegistry.register( new GenericStoneTileSet( info ) );

        GenericOreRegistry oreRegistry = GenericOreRegistry.INSTANCE;

        for( IProxyOreInfo info : VanillaOreInfo.values() )
            oreRegistry.register( new VanillaOreTileSet( info ) );

        for( IOreInfo info : ClayOreInfo.values() )
            oreRegistry.register( new GenericOreTileSet( info ) );

        for( IOreInfo info : CrudeOreInfo.values() )
            oreRegistry.register( new GenericOreTileSet( info ) );

        for( IOreInfo info : SandOreInfo.values() )
            oreRegistry.register( new GenericOreTileSet( info ) );

        for( IOreInfo info : WeakOreInfo.values() )
            oreRegistry.register( new GenericOreTileSet( info ) );

        for( IOreInfo info : MediumOreInfo.values() )
            oreRegistry.register( new GenericOreTileSet( info ) );

        for( IOreInfo info : StrongOreInfo.values() )
            oreRegistry.register( new GenericOreTileSet( info ) );

        GameRegistry.registerTileEntity(
            DynamicOreHostTileEntity.class,
            new ResourceLocation( "strata:ore_tile_entity" ) );
    }
}
