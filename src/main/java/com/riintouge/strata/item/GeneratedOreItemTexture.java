package com.riintouge.strata.item;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Vector;
import java.util.function.Function;

public class GeneratedOreItemTexture extends TextureAtlasSprite
{
    private ResourceLocation source;

    public GeneratedOreItemTexture( ResourceLocation source , String name )
    {
        super( name );

        this.source = source;
    }

    @Override
    public Collection< ResourceLocation > getDependencies()
    {
        Vector< ResourceLocation > deps = new Vector<>();
        deps.add( source );

        return deps;
    }

    @Override
    public boolean hasCustomLoader( @Nonnull IResourceManager manager , @Nonnull ResourceLocation location )
    {
        return true;
    }

    @Override
    public boolean load(
        @Nonnull IResourceManager manager,
        @Nonnull ResourceLocation location,
        @Nonnull Function< ResourceLocation , TextureAtlasSprite > textureGetter )
    {
        // TODO: Actually create something instead of making a shallow copy of the ore block overlay.
        // See ModelDynBucket for checking if a resource already exists before creating something new.

        final TextureAtlasSprite baseTexture = textureGetter.apply( source );
        width = baseTexture.getIconWidth();
        height = baseTexture.getIconHeight();
        this.clearFramesTextureData();
        this.framesTextureData.add( baseTexture.getFrameTextureData( 0 ) );

        // What does it mean for this to be stitched or not?
        return false;
    }
}
