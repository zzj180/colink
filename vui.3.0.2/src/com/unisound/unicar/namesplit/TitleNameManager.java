package com.unisound.unicar.namesplit;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class TitleNameManager {

    private TitleNameManager() {
        this.loadTitleNames();
    }

    private static TitleNameManager instance = new TitleNameManager();

    public static TitleNameManager getInstance() {
        return instance;
    }

    private void loadTitleNames() {
        // TODO: 从FIRST_NAME_FILE中加载姓氏到_firstNameSet中
        BufferedReader titleNameBufferReader = null;
        try {
            titleNameBufferReader = new BufferedReader(new FileReader(this.TITLE_NAME_FILE));

            String titleName;
            while (true) {
                titleName = titleNameBufferReader.readLine();
                if (titleName == null) break;
                String[] titleNameArray = titleName.split("[\\t]");
                String curTitle = titleNameArray[1];

                if (this.titleSet.contains(curTitle)) continue;
                this.titleSet.add(curTitle);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally // 关闭文件
        {
            try {
                if (titleNameBufferReader != null) {
                    titleNameBufferReader.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public Set<String> GetTitleNameSet() {
        // TODO: 从name中获得姓氏
        return this.titleSet;
    }

    public boolean IsTitle(String input) {
        return this.titleSet.contains(input);
    }

    private HashSet<String> titleSet = new HashSet<String>();
    private final String TITLE_NAME_FILE = "conf" + File.separator + "Chfiles" + File.separator
            + "ChTitle.txt";

    public static void main(String[] args) {
        Set<String> titleNameSet = TitleNameManager.getInstance().GetTitleNameSet();
        for (String titleName : titleNameSet) {
            System.out.println(titleName);
        }
    }
}
