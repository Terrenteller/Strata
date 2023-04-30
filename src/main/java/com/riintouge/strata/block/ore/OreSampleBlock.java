package com.riintouge.strata.block.ore;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.SampleBlock;
import com.riintouge.strata.item.WeightedDropCollections;
import com.riintouge.strata.sound.AmbientSoundHelper;
import com.riintouge.strata.sound.SoundEventTuple;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class OreSampleBlock extends SampleBlock
{
    protected IOreInfo oreInfo;

    public OreSampleBlock( IOreInfo oreInfo , IBlockState oreBlockState )
    {
        super( oreBlockState );
        this.oreInfo = oreInfo;

        setRegistryName( Strata.modid + ":" + oreInfo.oreName() + REGISTRY_NAME_SUFFIX );
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        Block proxyBlock = proxyBlockState != null ? proxyBlockState.getBlock() : null;
        if( proxyBlock != null )
            setUnlocalizedName( proxyBlock.getUnlocalizedName() );
        else
            setUnlocalizedName( Strata.modid + ":" + oreInfo.oreName() );

        setSoundType( SoundType.GROUND );
        setCreativeTab( Strata.ORE_SAMPLE_TAB );
    }

    // Block overrides

    @Override
    public boolean canRenderInLayer( IBlockState state , BlockRenderLayer layer )
    {
        // FIXME: The break progress overlay has weird transparency on the default model.
        // Only the overlay faces need to render in the translucent layer. Splitting the model into two
        // would also allow the break overlay to be kept off the satellite pebbles in the default model.
        // Note that DAMAGE_MODEL_FACE_COUNT_HACK may be necessary to keep things looking good.
        // However, as long as the block hardness is zero and breaks instantly, all of this is a moot point.
        // The overlay will never be seen.
        return layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public void getDrops( NonNullList< ItemStack > drops , IBlockAccess world , BlockPos pos , IBlockState state , int fortune )
    {
        IBlockState proxyBlockState = oreInfo.proxyBlockState();
        Block proxyBlock = proxyBlockState != null ? proxyBlockState.getBlock() : null;
        ItemStack itemToDrop = null;

        if( proxyBlock != null )
        {
            NonNullList< ItemStack > proxyDrops = NonNullList.create();

            // We have no control over the proxy block, so try ten times with Fortune X to get something
            for( int index = 0 ; index < 10 ; index++ )
            {
                proxyBlock.getDrops( proxyDrops , world , pos , proxyBlockState , 10 );

                if( !proxyDrops.isEmpty() )
                {
                    itemToDrop = proxyDrops.get( RANDOM.nextInt( proxyDrops.size() ) );
                    itemToDrop.setCount( 1 );
                    break;
                }
            }
        }
        else
        {
            WeightedDropCollections weightedDropCollections = oreInfo.weightedDropGroups();
            itemToDrop = weightedDropCollections != null ? weightedDropCollections.getSingleRandomDrop( RANDOM ) : null;
        }

        if( itemToDrop == null || itemToDrop.isEmpty() )
            itemToDrop = oreInfo.equivalentItemStack();

        if( itemToDrop == null || itemToDrop.isEmpty() )
        {
            IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreInfo.oreName() );
            if( oreTileSet != null )
                drops.add( new ItemStack( oreTileSet.getItem() ) );
        }
        else
            drops.add( itemToDrop );
    }

    @Override
    public ItemStack getItem( World worldIn , BlockPos pos , IBlockState state )
    {
        IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreInfo.oreName() );
        return oreTileSet != null ? new ItemStack( oreTileSet.getItem() ) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack getPickBlock( IBlockState state , RayTraceResult target , World world , BlockPos pos , EntityPlayer player )
    {
        IOreTileSet oreTileSet = OreRegistry.INSTANCE.find( oreInfo.oreName() );
        return oreTileSet != null ? new ItemStack( oreTileSet.getSampleItemBlock() ) : ItemStack.EMPTY;
    }

    @Override
    public SoundType getSoundType( IBlockState state , World world , BlockPos pos , @Nullable Entity entity )
    {
        return SoundType.GROUND;
    }

    @Override
    @SideOnly( Side.CLIENT )
    public void randomDisplayTick( IBlockState stateIn , World worldIn , BlockPos pos , Random rand )
    {
        SoundEventTuple oreAmbientSound = oreInfo.ambientSound();
        if( oreAmbientSound != null )
            AmbientSoundHelper.playForRandomDisplayTick( worldIn , pos , rand , oreAmbientSound );
    }
}
