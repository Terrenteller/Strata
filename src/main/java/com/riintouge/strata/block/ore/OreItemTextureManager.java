package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
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
                ? oreInfo.oreItemTextureLayers().toArray( new LayeredTextureLayer[ 0 ] )
                : new LayeredTextureLayer[]{ new LayeredTextureLayer( oreInfo.modelTextureMap().get( (EnumFacing)null ) ) };

            ResourceLocation generatedResourceLocation = getTextureLocation( oreInfo.oreName() );
            Strata.LOGGER.trace( "Stitching " + generatedResourceLocation.toString() );
            TextureAtlasSprite generatedTexture = new LayeredTexture( generatedResourceLocation , layers );
            textureMap.setTextureEntry( generatedTexture );
        }
    }
}
