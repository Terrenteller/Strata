package com.riintouge.strata.block;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.geo.info.*;
import com.riintouge.strata.block.geo.tileset.GenericClayTileSet;
import com.riintouge.strata.block.geo.tileset.GenericGroundTileSet;
import com.riintouge.strata.block.geo.tileset.GenericStoneTileSet;
import com.riintouge.strata.block.ore.DynamicOreHostManager;
import com.riintouge.strata.block.ore.DynamicOreHostTileEntity;
import com.riintouge.strata.block.ore.GenericOreRegistry;
import com.riintouge.strata.block.ore.OreItemTextureManager;
import com.riintouge.strata.block.ore.info.*;
import com.riintouge.strata.block.ore.tileset.GenericOreTileSet;
import com.riintouge.strata.block.ore.tileset.VanillaOreTileSet;
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

        GenericHostRegistry hostRegistry = GenericHostRegistry.INSTANCE;
        // TODO: This should process the contents of host and ore registries
        DynamicOreHostManager oreHostManager = DynamicOreHostManager.INSTANCE;

        for( VanillaHostTileSetInfo info : VanillaHostTileSetInfo.values() )
        {
            hostRegistry.register( info.registryName() , info.getMeta() , info );
            oreHostManager.registerHost( info.registryName() , info.getMeta() , info );
        }

        GenericTileSetRegistry tileSetRegistry = GenericTileSetRegistry.INSTANCE;

        for( IGenericTileSetInfo info : CrudeGroundTileSetInfo.values() )
        {
            hostRegistry.register( info.registryName() , 0 , info );
            oreHostManager.registerHost( info.registryName() , 0 , info );
            tileSetRegistry.register( new GenericGroundTileSet( info ) );
        }

        for( IGenericTileSetInfo info : ClayTileSetInfo.values() )
        {
            hostRegistry.register( info.registryName() , 0 , info );
            oreHostManager.registerHost( info.registryName() , 0 , info );
            tileSetRegistry.register( new GenericClayTileSet( info ) );
        }

        for( IGenericStoneTileSetInfo info : WeakStoneTileSetInfo.values() )
        {
            hostRegistry.register( info.registryName() , 0 , info );
            oreHostManager.registerHost( info.registryName() , 0 , info );
            tileSetRegistry.register( new GenericStoneTileSet( info ) );
        }

        for( IGenericStoneTileSetInfo info : MediumStoneTileSetInfo.values() )
        {
            hostRegistry.register( info.registryName() , 0 , info );
            oreHostManager.registerHost( info.registryName() , 0 , info );
            tileSetRegistry.register( new GenericStoneTileSet( info ) );
        }

        for( IGenericStoneTileSetInfo info : StrongStoneTileSetInfo.values() )
        {
            hostRegistry.register( info.registryName() , 0 , info );
            oreHostManager.registerHost( info.registryName() , 0 , info );
            tileSetRegistry.register( new GenericStoneTileSet( info ) );
        }

        for( IGenericStoneTileSetInfo info : VeryStrongStoneTileSetInfo.values() )
        {
            hostRegistry.register( info.registryName() , 0 , info );
            oreHostManager.registerHost( info.registryName() , 0 , info );
            tileSetRegistry.register( new GenericStoneTileSet( info ) );
        }

        GenericOreRegistry oreRegistry = GenericOreRegistry.INSTANCE;
        OreItemTextureManager oreItemRegistry = OreItemTextureManager.INSTANCE;

        for( IProxyOreInfo info : VanillaOreInfo.values() )
        {
            oreRegistry.register( new VanillaOreTileSet( info ) );
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
