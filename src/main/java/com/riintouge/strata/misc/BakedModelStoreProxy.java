package com.riintouge.strata.misc;

import com.riintouge.strata.util.DebugUtil;
import com.riintouge.strata.util.ReflectionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.IdentityHashMap;
import java.util.Map;

@SideOnly( Side.CLIENT )
public final class BakedModelStoreProxy implements InvocationHandler
{
    protected IBakedModel missingModel;
    protected Map< IBlockState , IBakedModel > bakedModelStore;
    protected Map< Block , IStateMapper > blockStateMapperMap = getBlockStateMapperMap();
    protected Map< Block , Pair< StateMapperBase , Method > > stateMapperBaseMethodPairs = new IdentityHashMap<>();

    public BakedModelStoreProxy( Map< IBlockState , IBakedModel > bakedModelStore )
    {
        this.bakedModelStore = bakedModelStore;

        missingModel = Minecraft
            .getMinecraft()
            .getBlockRendererDispatcher()
            .getBlockModelShapes()
            .getModelManager()
            .getMissingModel();
    }

    @Nullable
    @SuppressWarnings( "unchecked" )
    protected Map< Block , IStateMapper > getBlockStateMapperMap()
    {
        try
        {
            BlockModelShapes blockModelShapes = Minecraft
                .getMinecraft()
                .getBlockRendererDispatcher()
                .getBlockModelShapes();

            Field fieldBlockStateMapper = ReflectionUtil.findFieldByType( BlockModelShapes.class , BlockStateMapper.class , false );
            fieldBlockStateMapper.setAccessible( true );
            BlockStateMapper blockStateMapper = (BlockStateMapper)fieldBlockStateMapper.get( blockModelShapes );

            Field fieldBlockStateMap = ReflectionUtil.findFieldByType( BlockStateMapper.class , IdentityHashMap.class , true );
            fieldBlockStateMap.setAccessible( true );
            return (Map< Block , IStateMapper >)fieldBlockStateMap.get( blockStateMapper );
        }
        catch( Exception e )
        {
            DebugUtil.printException( e );
        }

        return null;
    }

    @Nonnull
    public IBakedModel getModelForStateFromMapper( IBlockState state )
    {
        try
        {
            if( blockStateMapperMap == null )
                return missingModel;

            BlockModelShapes blockModelShapes = Minecraft
                .getMinecraft()
                .getBlockRendererDispatcher()
                .getBlockModelShapes();

            Pair< StateMapperBase , Method > stateMapperBaseMethodPair = stateMapperBaseMethodPairs.get( state.getBlock() );
            if( stateMapperBaseMethodPair != null )
            {
                StateMapperBase stateMapperBase = stateMapperBaseMethodPair.getLeft();
                if( stateMapperBase == null )
                    return missingModel;

                Object modelResourceLocation = stateMapperBaseMethodPair.getRight().invoke( stateMapperBaseMethodPair.getLeft() , state );
                return blockModelShapes.getModelManager().getModel( (ModelResourceLocation)modelResourceLocation );
            }

            IStateMapper stateMapper = blockStateMapperMap.get( state.getBlock() );
            if( stateMapper instanceof StateMapperBase )
            {
                StateMapperBase stateMapperBase = (StateMapperBase)stateMapper;
                Method getModelResourceLocationMethod = ReflectionUtil.findMethodByTypes(
                    StateMapperBase.class,
                    ModelResourceLocation.class,
                    true,
                    IBlockState.class );

                if( getModelResourceLocationMethod != null )
                {
                    getModelResourceLocationMethod.setAccessible( true );
                    stateMapperBaseMethodPairs.put( state.getBlock() , Pair.of( stateMapperBase , getModelResourceLocationMethod ) );
                    Object modelResourceLocation = getModelResourceLocationMethod.invoke( stateMapperBase , state );
                    return blockModelShapes.getModelManager().getModel( (ModelResourceLocation)modelResourceLocation );
                }
            }

            stateMapperBaseMethodPairs.put( state.getBlock() , Pair.of( null , null ) );
            return missingModel;
        }
        catch( Exception e )
        {
            DebugUtil.printException( e );
        }

        return missingModel;
    }

    // InvocationHandler overrides

    @Override
    public Object invoke( Object o , Method method , Object[] objects ) throws Throwable
    {
        Object result = method.invoke( bakedModelStore , objects );
        if( method.getName().equalsIgnoreCase( "get" ) && result == null )
        {
            IBlockState blockState = (IBlockState)objects[ 0 ];
            return getModelForStateFromMapper( blockState );
        }

        return result;
    }

    // Statics

    @SuppressWarnings( "unchecked" )
    public static void inject()
    {
        try
        {
            BlockModelShapes blockModelShapes = Minecraft
                .getMinecraft()
                .getBlockRendererDispatcher()
                .getBlockModelShapes();

            // Open up BlockModelShapes.bakedModelStore
            Field field = ReflectionUtil.findFieldByType( BlockModelShapes.class , IdentityHashMap.class , true );
            field.setAccessible( true );
            ReflectionUtil.unfinalizeField( field );

            // Inject our proxy if not done already
            Object bakedModelStore = field.get( blockModelShapes );
            if( !( bakedModelStore instanceof Proxy ) )
            {
                Map< IBlockState , IBakedModel > originalValue = (Map< IBlockState , IBakedModel >)bakedModelStore;
                Object bakedModelStoreProxyProxy = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class[]{ Map.class },
                    new BakedModelStoreProxy( originalValue ) );
                field.set( blockModelShapes , bakedModelStoreProxyProxy );
                field.setAccessible( false );
            }
        }
        catch( Exception e )
        {
            DebugUtil.printException( e );
        }
    }
}
