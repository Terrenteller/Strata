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

import java.util.HashMap;
import java.util.Map;

@SideOnly( Side.CLIENT )
public class BakedModelCache implements IResourceManagerReloadListener
{
    public static final BakedModelCache INSTANCE = new BakedModelCache();

    private Map< MetaResourceLocation , IBakedModel > hostBakedModelMap = new HashMap<>();
    private Map< String , IBakedModel > bakedOreModelMap = new HashMap<>();

    private BakedModelCache()
    {
        ( (IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager() ).registerReloadListener( this );
    }

    public void registerOreBakedModel( String oreName , IBakedModel oreBakedModel )
    {
        bakedOreModelMap.put( oreName , oreBakedModel );
    }

    public IBakedModel getBakedModel( MetaResourceLocation metaResourceLocation )
    {
        IBakedModel hostModel = hostBakedModelMap.getOrDefault( metaResourceLocation , null );
        if( hostModel == null )
        {
            ModelManager modelManager = Minecraft.getMinecraft()
                .getBlockRendererDispatcher()
                .getBlockModelShapes()
                .getModelManager();

            Block hostBlock = Block.REGISTRY.getObject( metaResourceLocation.resourceLocation );
            Map< IBlockState, ModelResourceLocation > variants = modelManager.getBlockModelShapes()
                .getBlockStateMapper()
                .getVariants( hostBlock );

            ModelResourceLocation hostModelResource = variants.get( hostBlock.getStateFromMeta( metaResourceLocation.meta ) );
            hostModel = modelManager.getModel( hostModelResource );
            hostBakedModelMap.put( metaResourceLocation , hostModel );
        }

        return hostModel;
    }

    public IBakedModel getBakedOreModel( String oreName )
    {
        return bakedOreModelMap.getOrDefault( oreName , null );
    }

    // IResourceManagerReloadListener overrides

    @Override
    public void onResourceManagerReload( IResourceManager resourceManager )
    {
        hostBakedModelMap.clear();
    }
}
