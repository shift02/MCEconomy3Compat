package shift.mceconomy3compat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import shift.mceconomy3.api.MCEconomyAPI;

public class FileManager {

    private File directory;
    private boolean isFile;

    public FileManager(File directory) {

        this.directory = directory;
        this.isFile = directory.isDirectory() && (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    }

    public void loadMP() {

        if (isFile) {
            this.loadMPFromFiles();

        } else {
            this.loadMPFromJar();
        }

    }

    public void loadMPFromFiles() {

        File mpFile = new File(directory, "./assets/mceconomy3compat/purchase");
        File files[] = mpFile.listFiles();

        for (File file : files) {

            if (file.isDirectory()) continue;
            if (!file.getName().endsWith(".mp")) continue;
            this.loadMPFromFile(file);

        }

    }

    public void loadMPFromFile(File file) {

        MCEconomy3Compat.log.info("Load File - " + file);

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));

            String str = br.readLine();
            while (str != null) {

                if (str.trim().startsWith("#") || str.trim().length() == 0) {
                    str = br.readLine();
                    continue;
                }

                this.registerMP(str);

                str = br.readLine();

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void loadMPFromJar() {

    }

    public void registerMP(String str) {

        String[] datas = str.split("=");

        int mp = Integer.parseInt(datas[1]);
        String[] data = datas[0].split(":");

        if (data[0].equals("block")) {

            Block block = Block.REGISTRY.getObject(new ResourceLocation(data[1], data[2]));
            int meta = Integer.parseInt(data[3]);
            MCEconomyAPI.addPurchaseItem(new ItemStack(block, 1, meta), mp);

        } else if (data[0].equals("item")) {

            Item item = Item.REGISTRY.getObject(new ResourceLocation(data[1], data[2]));
            int meta = Integer.parseInt(data[3]);
            MCEconomyAPI.addPurchaseItem(new ItemStack(item, 1, meta), mp);

        }

    }

}
