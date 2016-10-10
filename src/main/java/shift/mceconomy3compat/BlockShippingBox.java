package shift.mceconomy3compat;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import shift.mceconomy3.api.MCEconomyAPI;

public class BlockShippingBox extends Block {

    public BlockShippingBox() {
        super(Material.ROCK);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem,
            EnumFacing side, float hitX, float hitY, float hitZ) {

        if (heldItem == null) return false;

        if (!MCEconomyAPI.hasPurchase(heldItem)) return false;

        if (worldIn.isRemote) return true;

        NBTTagCompound nbt = playerIn.getEntityData();

        int sMP = nbt.getInteger(MCEconomy3Compat.MODID + ":" + "shipping_box_mp");

        int itemMP = MCEconomyAPI.getPurchase(heldItem) * heldItem.stackSize;

        nbt.setInteger(MCEconomy3Compat.MODID + ":" + "shipping_box_mp", sMP + itemMP);

        worldIn.playSound(
                (EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ,
                SoundEvents.BLOCK_CLOTH_FALL, SoundCategory.NEUTRAL, 2.0F, 0.4F / (worldIn.rand.nextFloat() * 0.4F + 0.8F));

        heldItem.stackSize = 0;

        return true;
    }

}
