package shift.mceconomy3compat;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import shift.mceconomy3.api.MCEconomyAPI;
import shift.mceconomy3.api.purchase.IPurchaseItem;
import shift.mceconomy3.api.shop.IProduct;
import shift.mceconomy3.api.shop.IShop;
import shift.mceconomy3.api.shop.ProductBase;

@Mod(modid = MCEconomy3Compat.MODID, version = MCEconomy3Compat.VERSION, dependencies = MCEconomy3Compat.DEPENDENCY)
public class MCEconomy3Compat {
    public static final String MODID = "mceconomy3compat";
    public static final String VERSION = "1.0";

    public static final String DEPENDENCY = "after:mceconomy3";

    public static final Logger log = LogManager.getLogger("MCEconomy3Compat");

    public static FileManager manager;

    public static Item coin;

    public static Block shippingBox;

    public static VillagerProfession moneychanger;

    public static int shopID;

    @EventHandler
    public void init(FMLPreInitializationEvent event) {

        manager = new FileManager(event.getSourceFile(), event.getModConfigurationDirectory());

        this.coin = new ItemCoin().setRegistryName(MODID, "coin").setUnlocalizedName("mce3c.coin");
        GameRegistry.register(this.coin);
        if (event.getSide().isClient()) {

            ResourceLocation l = new ResourceLocation(MCEconomy3Compat.MODID, "coin");
            // アイテム状態の登録
            ModelLoader.setCustomModelResourceLocation(coin, 0, new ModelResourceLocation(l, "inventory"));

        }

        //出荷箱
        shippingBox = new BlockShippingBox().setRegistryName(MODID, "shipping_box").setUnlocalizedName("mce3c.shipping_box");
        ItemBlock itemBlock = new ItemBlock(shippingBox);
        if (event.getSide().isClient()) {

            ResourceLocation l = new ResourceLocation(MCEconomy3Compat.MODID, "shipping_box");

            // ブロック状態の登録
            //ModelLoader.setCustomStateMapper(shippingBox);
            // アイテム状態の登録
            ModelLoader.setCustomModelResourceLocation(itemBlock, 0, new ModelResourceLocation(l, "inventory"));
        }
        GameRegistry.register(shippingBox);
        GameRegistry.register(itemBlock.setRegistryName(MODID, "shipping_box"));

        //村人
        VillagerProfession moneychanger = new VillagerProfession(MODID + ":" + "moneychanger",
                MODID + ":" + "textures/entity/moneychanger.png");
        VillagerRegistry.instance().register(moneychanger);
        new VillagerCareer(moneychanger, "farmer");

        //
        shopID = MCEconomyAPI.registerShop(new IShop() {

            private ArrayList<IProduct> pr = new ArrayList<IProduct>();

            @Override
            public String getShopName(World world, EntityPlayer player) {
                return "moneychanger";
            }

            @Override
            public ArrayList<IProduct> getProductList(World world, EntityPlayer player) {
                return pr;
            }

            @Override
            public void addProduct(IProduct product) {
                pr.add(product);
            }
        });

        MCEconomyAPI.getShop(shopID).addProduct(new ProductBase(new ItemStack(coin, 1, 1), 1));
        MCEconomyAPI.getShop(shopID).addProduct(new ProductBase(new ItemStack(coin, 1, 100), 100));
        MCEconomyAPI.getShop(shopID).addProduct(new ProductBase(new ItemStack(coin, 1, 1000), 1000));
        MCEconomyAPI.getShop(shopID).addProduct(new ProductBase(new ItemStack(coin, 1, 10000), 10000));
        MCEconomyAPI.getShop(shopID).addProduct(new ProductBase(new ItemStack(coin, 1, 100000), 100000));
        MCEconomyAPI.getShop(shopID).addProduct(new ProductBase(new ItemStack(Items.EMERALD, 1, 0), 1500));
        MCEconomyAPI.getShop(shopID).addProduct(new ProductBase(new ItemStack(shippingBox, 1), 0));

        MinecraftForge.EVENT_BUS.register(this);

    }

    @SubscribeEvent
    public void EntityInteract(PlayerInteractEvent.EntityInteract event) {

        if (!(event.getTarget() instanceof EntityVillager)) return;

        EntityVillager v = (EntityVillager) event.getTarget();
        VillagerProfession vp = v.getProfessionForge();
        vp = v.getProfessionForge();
        if (!(vp.getRegistryName().getResourceDomain().equals(MODID) && vp.getRegistryName().getResourcePath().equals("moneychanger"))) return;

        event.setCanceled(true);

        if (!event.getWorld().isRemote) MCEconomyAPI.openShopGui(shopID, event.getEntityPlayer(), event.getWorld(), (int) event.getEntityPlayer().posX,
                (int) event.getEntityPlayer().posY, (int) event.getEntityPlayer().posZ);

    }

    @SubscribeEvent
    public void LivingUpdateEvent(LivingUpdateEvent event) {

        if (!(event.getEntity() instanceof EntityPlayer)) return;
        EntityPlayer entityPlayer = (EntityPlayer) event.getEntity();

        if (this.getHour(entityPlayer.worldObj) != 7 || this.getMinute(entityPlayer.worldObj) != 0) return;

        NBTTagCompound nbt = entityPlayer.getEntityData();
        int sMP = nbt.getInteger(MCEconomy3Compat.MODID + ":" + "shipping_box_mp");

        if (sMP == 0) return;

        nbt.setInteger(MCEconomy3Compat.MODID + ":" + "shipping_box_mp", 0);

        MCEconomyAPI.addPlayerMP(entityPlayer, sMP, false);

    }

    public int getHour(World world) {

        long t = world.getWorldInfo().getWorldTime() % 24000;
        t += 6000;
        if (t >= 24000) t -= 24000;

        return (int) (t / 1000);

    }

    public int getMinute(World world) {

        long t = world.getWorldInfo().getWorldTime() % 24000;
        t += 6000;
        if (t >= 24000) t -= 24000;
        if (t >= 12000) {
            t -= 12000;
        }

        return (int) ((t % 1000) / (1000f / 60f));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @EventHandler
    public void init(FMLPostInitializationEvent event) {

        manager.loadMP();

        MCEconomyAPI.addPurchaseItem(new ItemStack(shippingBox), 0);

        MCEconomyAPI.addPurchaseItem(new IPurchaseItem() {

            @Override
            public boolean isMatch(ItemStack itemStack) {
                return itemStack.getItem() == MCEconomy3Compat.coin;
            }

            @Override
            public int getPriority() {
                return 5;
            }

            @Override
            public int getPrice(ItemStack itemStack) {
                return itemStack.getItemDamage();
            }
        });

    }

}
