package com.aispeech.aios.adapter.bean;

import java.util.List;

/**
 * @desc 自定义自然语言生成文本
 * @auth AISPEECH
 * @date 2016-01-14
 * @copyright aispeech.com
 */
public class LangGen {

    private Navi navi;
    private Music music;
    private Nearby nearby;
    private Phonecall phonecall;

    public static class Navi extends Base{}

    public static class Music extends Base{}

    public static class Phonecall extends Base{}

    public static class Nearby extends Base{

        private List<String> offerdo;

        public void setOfferdo(List<String> offerdo) {
            this.offerdo = offerdo;
        }
        public List<String> getOfferdo() {
            return offerdo;
        }

    }

    /**
     * 获取进入导航模块的提示语
     * @return 进入导航模块的提示语
     */
    public Navi getNavi() {
        return navi;
    }

    /**
     * 设置进入导航模块的提示语
     * @param navi 进入导航模块的提示语
     */
    public void setNavi(Navi navi) {
        this.navi = navi;
    }

    /**
     * 获取进入音乐模块的提示语
     * @return 进入音乐模块的提示语
     */
    public Music getMusic() {
        return music;
    }

    /**
     * 设置进入音乐模块的提示语
     * @param music 进入音乐模块的提示语
     */
    public void setMusic(Music music) {
        this.music = music;
    }

    /**
     * 获取进入附近模块的提示语
     * @return 进入附近模块的提示语
     */
    public Nearby getNearby() {
        return nearby;
    }

    /**
     * 设置进入附近模块的提示语
     * @param nearby 进入附近模块的提示语
     */
    public void setNearby(Nearby nearby) {
        this.nearby = nearby;
    }

    /**
     * 获取进入电话拨打模块的提示语
     * @return 进入附近模块的提示语
     */
    public Phonecall getPhonecall() {
        return phonecall;
    }

    /**
     * 设置进入附近模块的提示语
     * @param phonecall 进入附近模块的提示语
     */
    public void setPhonecall(Phonecall phonecall) {
        this.phonecall = phonecall;
    }

    @Override
    public String toString() {
        return "LangGen{" +
                "navi=" + navi +
                ", music=" + music +
                ", nearby=" + nearby +
                ", phonecall=" + phonecall +
                '}';
    }

    public static class Base{
        private List<String> enter;
        private List<String> bye;
        private List<String> overflow;

        public void setEnter(List<String> enter) {
            this.enter = enter;
        }

        public void setBye(List<String> bye) {
            this.bye = bye;
        }

        public void setOverflow(List<String> overflow) {
            this.overflow = overflow;
        }

        public List<String> getEnter() {
            return enter;
        }

        public List<String> getBye() {
            return bye;
        }

        public List<String> getOverflow() {
            return overflow;
        }

        @Override
        public String toString() {
            return "Base{" +
                    "enter=" + enter +
                    ", bye=" + bye +
                    ", overflow=" + overflow +
                    '}';
        }
    }
}
