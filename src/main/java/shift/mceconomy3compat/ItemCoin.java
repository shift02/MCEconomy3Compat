package shift.mceconomy3compat;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import shift.mceconomy3.api.MCEconomyAPI;

public class ItemCoin extends Item {

    public ItemCoin() {
        this.setCreativeTab(CreativeTabs.MISC);
        //this.setMaxDamage(Integer.MAX_VALUE);
        this.setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        subItems.add(new ItemStack(itemIn, 1, 1));
        subItems.add(new ItemStack(itemIn, 1, 5));
        subItems.add(new ItemStack(itemIn, 1, 10));
        subItems.add(new ItemStack(itemIn, 1, 50));
        subItems.add(new ItemStack(itemIn, 1, 100));
        subItems.add(new ItemStack(itemIn, 1, 500));
        subItems.add(new ItemStack(itemIn, 1, 1000));
        subItems.add(new ItemStack(itemIn, 1, 5000));
        subItems.add(new ItemStack(itemIn, 1, 10000));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
            EnumHand hand) {
        if (!playerIn.capabilities.isCreativeMode) {
            --itemStackIn.stackSize;
        }

        if (!worldIn.isRemote) {

            MCEconomyAPI.addPlayerMP(playerIn, itemStackIn.getItemDamage(), false);

        }

        return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
    }

    @Override
    public int getMetadata(ItemStack stack) {
        return 0;
    }

}
