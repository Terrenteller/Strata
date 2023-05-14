package com.riintouge.strata.block.geo;

import com.riintouge.strata.block.MetaResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@SideOnly( Side.CLIENT )
public final class BakedModelCache implements IResourceManagerReloadListener
{
    public static final BakedModelCache INSTANCE = new BakedModelCache();

    private final Map< MetaResourceLocation , IBakedModel > modelMap = new HashMap<>();

    private BakedModelCache()
    {
        ( (IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager() ).registerReloadListener( this );
    }

    @Nonnull
    public IBakedModel getBakedModel( MetaResourceLocation blockRegistryNameAndMeta )
    {
        IBakedModel model = modelMap.getOrDefault( blockRegistryNameAndMeta , null );
        if( model == null )
        {
            ModelManager modelManager = Minecraft.getMinecraft()
                .getBlockRendererDispatcher()
                .getBlockModelShapes()
                .getModelManager();

            Block block = Block.REGISTRY.getObject( blockRegistryNameAndMeta.resourceLocation );
            Map< IBlockState , ModelResourceLocation > variants = modelManager.getBlockModelShapes()
                .getBlockStateMapper()
                .getVariants( block );

            ModelResourceLocation modelResource = variants.get( block.getStateFromMeta( blockRegistryNameAndMeta.meta ) );
            model = modelManager.getModel( modelResource );
            modelMap.put( blockRegistryNameAndMeta , model );
        }

        return model;
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        modelMap.clear();
    }
}
