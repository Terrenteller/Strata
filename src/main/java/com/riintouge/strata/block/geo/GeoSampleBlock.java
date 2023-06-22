package com.riintouge.strata.block.geo;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.ParticleHelper;
import com.riintouge.strata.block.SampleBlock;
import com.riintouge.strata.gui.StrataCreativeTabs;
import com.riintouge.strata.sound.AmbientSoundHelper;
import com.riintouge.strata.sound.SoundEventTuple;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;
import java.util.function.Supplier;

public class GeoSampleBlock extends SampleBlock
{
    protected IGeoTileInfo tileInfo;

    public GeoSampleBlock( IGeoTileInfo tileInfo , IBlockState geoBlockState )
    {
        super( geoBlockState );
        this.tileInfo = tileInfo;

        setCreativeTab( StrataCreativeTabs.BLOCK_SAMPLE_TAB );
        setRegistryName( Strata.resource( tileInfo.tileSetName() + REGISTRY_NAME_SUFFIX ) );
        setSoundType( tileInfo.soundType() );
        setUnlocalizedName( Strata.resource( tileInfo.tileSetName() ).toString() );
    }

    // Block overrides

    @Override
    @SideOnly( Side.CLIENT )
    public boolean addDestroyEffects( World world , BlockPos pos , ParticleManager manager )
    {
        TextureAtlasSprite textures[] = tileInfo.modelTextureMap().getTextures();
        Supplier< TextureAtlasSprite > textureGetter = () -> textures[ RANDOM.nextInt( 6 ) ];
        ParticleHelper.addDestroyEffects( world , pos , manager , textureGetter );

        return true;
    }

    @Override
    public void getDrops( NonNullList< ItemStack > drops , IBlockAccess world , BlockPos pos , IBlockState state , int fortune )
    {
        IGeoTileSet geoTileSet = GeoTileSetRegistry.INSTANCE.find( tileInfo.tileSetName() );
        if( geoTileSet == null )
            return;

        Item fragmentItem = geoTileSet.getFragmentItem();
        Item sampleItemBlock = geoTileSet.getSampleItemBlock();

        if( fragmentItem != null )
            drops.add( new ItemStack( fragmentItem ) );
        else if( sampleItemBlock != null )
            drops.add( new ItemStack( sampleItemBlock ) );
    }

    @Deprecated
    @Override
    public ItemStack getItem( World worldIn , BlockPos pos , IBlockState state )
    {
        IGeoTileSet geoTileSet = GeoTileSetRegistry.INSTANCE.find( tileInfo.tileSetName() );
        Item fragmentItem = geoTileSet != null ? geoTileSet.getFragmentItem() : null;

        return fragmentItem != null ? new ItemStack( fragmentItem ) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack getPickBlock( IBlockState state , RayTraceResult target , World world , BlockPos pos , EntityPlayer player )
    {
        IGeoTileSet geoTileSet = GeoTileSetRegistry.INSTANCE.find( tileInfo.tileSetName() );
        ItemBlock sampleItemBlock = geoTileSet != null ? geoTileSet.getSampleItemBlock() : null;

        return sampleItemBlock != null ? new ItemStack( sampleItemBlock ) : ItemStack.EMPTY;
    }

    @Override
    @SideOnly( Side.CLIENT )
    public void randomDisplayTick( IBlockState stateIn , World worldIn , BlockPos pos , Random rand )
    {
        SoundEventTuple ambientSound = tileInfo.ambientSound();
        if( ambientSound != null )
            AmbientSoundHelper.playForRandomDisplayTick( worldIn , pos , rand , ambientSound );

        super.randomDisplayTick( stateIn , worldIn , pos , rand );
    }
}
