package com.riintouge.strata.block.loader;

import com.riintouge.strata.Strata;
import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.ProtoBlockTextureMap;
import com.riintouge.strata.block.ore.IOreInfo;
import com.riintouge.strata.item.LocalizationRegistry;
import com.riintouge.strata.item.WeightedDropCollections;
import com.riintouge.strata.sound.SoundEventTuple;
import com.riintouge.strata.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public final class ImmutableOre implements IOreInfo , IForgeRegistrable
{
    // IOreInfo
    private String oreName;
    private String blockOreDictionaryName;
    private String itemOreDictionaryName;
    private ProtoBlockTextureMap modelTextureMap;
    private ResourceLocation blockstateResourceLocation;
    private ResourceLocation oreItemTextureResource;
    private MetaResourceLocation equivalentItemResourceLocation;
    private MetaResourceLocation furnaceResult;
    private Float furnaceExp;
    private ItemStack equivalentItemStack = null; // Lazily evaluated
    private MetaResourceLocation proxyBlockResourceLocation;
    private IBlockState proxyBlockState = null; // Lazily evaluated
    private WeightedDropCollections weightedDropCollections;
    private int baseExp;
    private String bonusExpExpr;

    // ICommonBlockProperties
    private Material material;
    private SoundType soundType;
    private SoundEventTuple ambientSound;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;
    private int burnTime;

    public ImmutableOre( TileData tileData ) throws IllegalArgumentException
    {
        Util.argumentNullCheck( tileData , "tileData" );

        // IOreInfo
        this.oreName = Util.argumentNullCheck( tileData.oreName , "oreName" );
        this.blockOreDictionaryName = tileData.blockOreDictionaryName;
        this.itemOreDictionaryName = tileData.itemOreDictionaryName;
        this.modelTextureMap = Util.argumentNullCheck( tileData.textureMap , "texture" );
        this.blockstateResourceLocation = Util.coalesce( tileData.blockstateResourceLocation , Strata.resource( "proto_cube_gimbal_overlay" ) );
        this.oreItemTextureResource = this.modelTextureMap.get( (EnumFacing)null );
        this.equivalentItemResourceLocation = tileData.equivalentItemResourceLocation;
        this.furnaceResult = tileData.furnaceResult;
        this.furnaceExp = tileData.furnaceExp;
        this.proxyBlockResourceLocation = tileData.proxyOreResourceLocation;
        this.ambientSound = tileData.ambientSound;
        this.weightedDropCollections = tileData.weightedDropCollections;
        this.baseExp = Util.coalesce( tileData.baseExp , 0 );
        this.bonusExpExpr = tileData.bonusExpExpr;

        // ICommonBlockProperties
        this.material = Util.argumentNullCheck( tileData.material , "material" );
        this.soundType = Util.argumentNullCheck( tileData.soundType , "soundType" );
        this.harvestTool = Util.argumentNullCheck( tileData.harvestTool , "harvestTool" );
        this.harvestLevel = Util.coalesce( tileData.harvestLevel , 0 );
        this.hardness = Util.coalesce( tileData.hardness , 1.0f );
        this.explosionResistance = Util.coalesce( tileData.explosionResistance , 1.7f * this.hardness );
        this.burnTime = Util.coalesce( tileData.burnTime , 0 );

        LocalizationRegistry.INSTANCE.register(
            this,
            Strata.resource( oreName ).toString() + ".name",
            Util.lazyCoalesce( tileData.languageMap , HashMap::new ) );
    }

    // IOreInfo overrides

    @Nonnull
    @Override
    public String oreName()
    {
        return oreName;
    }

    @Nullable
    @Override
    public String blockOreDictionaryName()
    {
        return blockOreDictionaryName;
    }

    @Nullable
    @Override
    public String itemOreDictionaryName()
    {
        return itemOreDictionaryName;
    }

    @Nonnull
    @Override
    @SideOnly( Side.CLIENT )
    public ProtoBlockTextureMap modelTextureMap()
    {
        return modelTextureMap;
    }

    @Nonnull
    @Override
    public ResourceLocation blockstateResourceLocation()
    {
        return blockstateResourceLocation;
    }

    @Nonnull
    @Override
    public ResourceLocation oreItemTextureResource()
    {
        return oreItemTextureResource;
    }

    @Override
    public ItemStack equivalentItemStack()
    {
        // Deferred resolution until reasonably sure the item has been created
        if( equivalentItemResourceLocation != null )
        {
            if( !equivalentItemResourceLocation.resourceLocation.equals( Blocks.AIR.getRegistryName() ) )
            {
                Item equivalentItem = Item.REGISTRY.getObject( equivalentItemResourceLocation.resourceLocation );
                if( equivalentItem != null )
                    equivalentItemStack = new ItemStack( equivalentItem , 1 , equivalentItemResourceLocation.meta );
            }

            equivalentItemResourceLocation = null;
        }

        return equivalentItemStack;
    }

    @Nullable
    @Override
    public MetaResourceLocation furnaceResult()
    {
        return furnaceResult;
    }

    @Override
    public Float furnaceExp()
    {
        return furnaceExp;
    }

    @Nullable
    @Override
    public IBlockState proxyBlockState()
    {
        // Deferred resolution until reasonably sure the block has been created
        if( proxyBlockResourceLocation != null )
        {
            Block proxyBlock = Block.REGISTRY.getObject( proxyBlockResourceLocation.resourceLocation );
            proxyBlockState = proxyBlock.getStateFromMeta( proxyBlockResourceLocation.meta );
            proxyBlockResourceLocation = null;
        }

        return proxyBlockState;
    }

    @Override
    public SoundEventTuple ambientSound()
    {
        return ambientSound;
    }

    @Nullable
    @Override
    public String localizedName()
    {
        return LocalizationRegistry.INSTANCE.get( this );
    }

    // ICommonBlockProperties overrides

    @Nonnull
    @Override
    public Material material()
    {
        return material;
    }

    @Nonnull
    @Override
    public SoundType soundType()
    {
        return soundType;
    }

    @Nonnull
    @Override
    public String harvestTool()
    {
        return harvestTool;
    }

    @Override
    public int harvestLevel()
    {
        return harvestLevel;
    }

    @Override
    public float hardness()
    {
        return hardness;
    }

    @Override
    public float explosionResistance()
    {
        return explosionResistance;
    }

    @Override
    public int burnTime()
    {
        return burnTime;
    }

    @Nullable
    @Override
    public WeightedDropCollections weightedDropGroups()
    {
        return weightedDropCollections;
    }

    @Override
    public int baseExp()
    {
        return baseExp;
    }

    @Override
    public String bonusExpExpr()
    {
        return bonusExpExpr;
    }

    // IForgeRegistrable overrides

    @Override
    @SideOnly( Side.CLIENT )
    public void stitchTextures( TextureMap textureMap )
    {
        modelTextureMap.stitchTextures( textureMap );
    }
}
