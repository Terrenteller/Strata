package com.riintouge.strata.item.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.ore.IOreInfo;
import com.riintouge.strata.block.ore.IOreTileSet;
import com.riintouge.strata.block.ore.OreRegistry;
import com.riintouge.strata.image.BlendMode;
import com.riintouge.strata.image.LayeredTexture;
import com.riintouge.strata.image.LayeredTextureLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class OreItemTextureManager
{
    // Redundant with Strata's configs, but necessary to make resource packs match without updating them.
    // As a bonus, being verbose in user-visible tile data is documentation by example.
    public static LayeredTextureLayer[] createReasonableDefault( ResourceLocation oreOverlayResource )
    {
        return new LayeredTextureLayer[]
        {
            new LayeredTextureLayer( new ResourceLocation( "strata:items/ore/mask" ) , BlendMode.ERASE ),
            new LayeredTextureLayer( new ResourceLocation( "strata:items/ore/border" ) ),
            new LayeredTextureLayer( oreOverlayResource ),
            new LayeredTextureLayer( new ResourceLocation( "strata:items/ore/base" ) )
        };
    }

    public static ResourceLocation getTextureLocation( String oreName )
    {
        return Strata.resource( "items/" + oreName );
    }

    @SideOnly( Side.CLIENT )
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void stitchTextures( TextureStitchEvent.Pre event )
    {
        Strata.LOGGER.trace( "OreItemTextureManager::stitchTextures()" );

        TextureMap textureMap = event.getMap();

        for( IOreTileSet oreTileSet : OreRegistry.INSTANCE.all() )
        {
            IOreInfo oreInfo = oreTileSet.getInfo();
            LayeredTextureLayer[] layers = oreInfo.oreItemTextureLayers() != null
                ? oreInfo.oreItemTextureLayers()
                : createReasonableDefault( oreInfo.modelTextureMap().get( (EnumFacing)null ) );

            ResourceLocation generatedResourceLocation = getTextureLocation( oreInfo.oreName() );
            Strata.LOGGER.trace( "Stitching " + generatedResourceLocation.toString() );
            TextureAtlasSprite generatedTexture = new LayeredTexture( generatedResourceLocation , layers );
            textureMap.setTextureEntry( generatedTexture );
        }
    }
}
