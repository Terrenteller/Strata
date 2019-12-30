package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.ore.DynamicOreHostManager;
import com.riintouge.strata.block.ore.DynamicOreHostTileEntity;
import com.riintouge.strata.block.ore.GenericOreRegistry;
import com.riintouge.strata.block.ore.OreItemTextureManager;
import com.riintouge.strata.block.ore.info.*;
import com.riintouge.strata.block.ore.tileset.GenericOreTileSet;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.IOException;

public class Blocks
{
    @SubscribeEvent
    public static void onEvent( RegistryEvent.Register< Block > event ) throws IOException
    {
        System.out.println( "Blocks::registerBlocks()" );

        TileLoader loader = new TileLoader();
        loader.load( "assets/strata/tiledata/vanillaHosts.txt" );
        loader.load( "assets/strata/tiledata/geo.txt" );
        // TODO: Read from on-disk config directory

        GenericOreRegistry oreRegistry = GenericOreRegistry.INSTANCE;
        OreItemTextureManager oreItemRegistry = OreItemTextureManager.INSTANCE;
        DynamicOreHostManager oreHostManager = DynamicOreHostManager.INSTANCE;

        for( IOreInfo info : VanillaOreInfo.values() )
        {
            oreRegistry.register( new GenericOreTileSet( info ) );
            oreItemRegistry.registerOre( info.oreName() , info.oreItemTextureResource() );
            oreHostManager.registerOre( new ResourceLocation( Strata.modid , info.oreName() ) , 0 , info );
        }

        for( IOreInfo info : ClayOreInfo.values() )
        {
            oreRegistry.register( new GenericOreTileSet( info ) );
            oreItemRegistry.registerOre( info.oreName() , info.oreItemTextureResource() );
            oreHostManager.registerOre( new ResourceLocation( Strata.modid , info.oreName() ) , 0 , info );
        }

        for( IOreInfo info : CrudeOreInfo.values() )
        {
            oreRegistry.register( new GenericOreTileSet( info ) );
            oreItemRegistry.registerOre( info.oreName() , info.oreItemTextureResource() );
            oreHostManager.registerOre( new ResourceLocation( Strata.modid , info.oreName() ) , 0 , info );
        }

        for( IOreInfo info : SandOreInfo.values() )
        {
            oreRegistry.register( new GenericOreTileSet( info ) );
            oreItemRegistry.registerOre( info.oreName() , info.oreItemTextureResource() );
            oreHostManager.registerOre( new ResourceLocation( Strata.modid , info.oreName() ) , 0 , info );
        }

        for( IOreInfo info : WeakOreInfo.values() )
        {
            oreRegistry.register( new GenericOreTileSet( info ) );
            oreItemRegistry.registerOre( info.oreName() , info.oreItemTextureResource() );
            oreHostManager.registerOre( new ResourceLocation( Strata.modid , info.oreName() ) , 0 , info );
        }

        for( IOreInfo info : MediumOreInfo.values() )
        {
            oreRegistry.register( new GenericOreTileSet( info ) );
            oreItemRegistry.registerOre( info.oreName() , info.oreItemTextureResource() );
            oreHostManager.registerOre( new ResourceLocation( Strata.modid , info.oreName() ) , 0 , info );
        }

        for( IOreInfo info : StrongOreInfo.values() )
        {
            oreRegistry.register( new GenericOreTileSet( info ) );
            oreItemRegistry.registerOre( info.oreName() , info.oreItemTextureResource() );
            oreHostManager.registerOre( new ResourceLocation( Strata.modid , info.oreName() ) , 0 , info );
        }

        GameRegistry.registerTileEntity(
            DynamicOreHostTileEntity.class,
            new ResourceLocation( "strata:ore_tile_entity" ) );
    }
}
