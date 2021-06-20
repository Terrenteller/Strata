package com.riintouge.strata.block.loader;

import com.riintouge.strata.block.GenericCubeTextureMap;
import com.riintouge.strata.block.IForgeRegistrable;
import com.riintouge.strata.block.MetaResourceLocation;
import com.riintouge.strata.block.ore.IOreInfo;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public final class ImmutableOre implements IOreInfo , IForgeRegistrable
{
    private String oreName;
    private String blockOreDictionaryName;
    private String itemOreDictionaryName;
    private GenericCubeTextureMap genericCubeTextureMap;
    private Material material;
    private SoundType soundType;
    private String harvestTool;
    private int harvestLevel;
    private float hardness;
    private float explosionResistance;
    private int burnTime;
    private MetaResourceLocation proxyBlockResourceLocation;
    private IBlockState proxyBlockState;
    private MetaResourceLocation equivalentItemResourceLocation;
    private ItemStack equivalentItemStack;
    private int baseDropAmount;
    private String bonusDropExpr;
    private int baseExp;
    private String bonusExpExpr;

    public ImmutableOre(
        String oreName,
        String blockOreDictionaryName,
        String itemOreDictionaryName,
        GenericCubeTextureMap textureMap,
        MetaResourceLocation proxyBlockResourceLocation,
        MetaResourceLocation equivalentItemResourceLocation,
        Material material,
        SoundType soundType,
        String harvestTool,
        int harvestLevel,
        float hardness,
        float explosionResistance,
        int burnTime,
        int baseDropAmount,
        String bonusDropExpr,
        int baseExp,
        String bonusExpExpr )
    {
        this.oreName = oreName;
        this.blockOreDictionaryName = blockOreDictionaryName;
        this.itemOreDictionaryName = itemOreDictionaryName;
        this.genericCubeTextureMap = textureMap;
        this.material = material;
        this.soundType = soundType;
        this.harvestTool = harvestTool;
        this.harvestLevel = harvestLevel;
        this.hardness = hardness;
        this.explosionResistance = explosionResistance == 0.0f ? 1.7f * hardness : explosionResistance;
        this.burnTime = burnTime;
        this.proxyBlockResourceLocation = proxyBlockResourceLocation;
        this.equivalentItemResourceLocation = equivalentItemResourceLocation;
        this.baseDropAmount = baseDropAmount;
        this.bonusDropExpr = bonusDropExpr;
        this.baseExp = baseExp;
        this.bonusExpExpr = bonusExpExpr;
    }

    // IOreInfo overrides

    @Override
    public String oreName()
    {
        return oreName;
    }

    @Override
    public String blockOreDictionaryName()
    {
        return blockOreDictionaryName;
    }

    @Override
    public String itemOreDictionaryName()
    {
        return itemOreDictionaryName;
    }

    @Override
    public GenericCubeTextureMap modelTextureMap()
    {
        return genericCubeTextureMap;
    }

    @Override
    public ResourceLocation oreItemTextureResource()
    {
        return genericCubeTextureMap.getOrDefault( (EnumFacing)null );
    }

    @Override
    public ItemStack equivalentItem()
    {
        // Deferred resolution until reasonably sure the item has been created
        if( equivalentItemResourceLocation != null )
        {
            Item equivalentItem = Item.REGISTRY.getObject( equivalentItemResourceLocation.resourceLocation );
            equivalentItemStack = new ItemStack( equivalentItem , 1 , equivalentItemResourceLocation.meta );
            equivalentItemResourceLocation = null;
        }

        return equivalentItemStack;
    }

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

    // IGenericBlockProperties overrides

    @Override
    public Material material()
    {
        return material;
    }

    @Override
    public SoundType soundType()
    {
        return soundType;
    }

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

    @Override
    public int baseDropAmount()
    {
        return baseDropAmount;
    }

    @Override
    public String bonusDropExpr()
    {
        return bonusDropExpr;
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
    public void stitchTextures( TextureMap textureMap )
    {
        genericCubeTextureMap.stitchTextures( oreName , textureMap );
    }
}
