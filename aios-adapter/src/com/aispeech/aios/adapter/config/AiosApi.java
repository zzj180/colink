package com.aispeech.aios.adapter.config;

/**
 * @desc aios对应接口
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public final class AiosApi {

    public static final class Asset {
        public static final String ASSET_SONGS = "asset.songs";
    }

    public static final class TTS {
        public static final String STATE = "tts.state";
    }


    public static final class Wakeup {
        public static final String WAKEUP_RESULT = "wakeup.result";
    }

    public static final class Player {
        public static final String STATE = "player.state";
        public static final String STATE_IDLE = "idle";
        public static final String STATE_BUSY = "busy";
        public static final String STATE_WAIT = "wait";
    }

    public static final class Music {
        public static final String DOMAIN = "/music";
        public static final String PLAY = "/music/play";
        public static final String SONG_SEARCH = "/music/songs/search";
        public static final String SONGS_LIST = "/music/songs/list";
        public static final String SONGS_SELECT = "/music/songs/select/";
        public static final String PREV_PAGE = "prevpage";
        public static final String NEXT_PAGE = "nextpage";

        public static final String SEARCH_RESULT = "music.songs.search.result";
    }

    public static final class Navigation {

        /**
         * 进入导航领域, 显示导航主页面
         **/
        public static final String NAVIGATION_ENTER = "/navigation";
        /**
         * POI搜索
         **/
        public static final String POI_SEARCH = "/navigation/poi/search";
        /**
         * 显示poi搜索的结果, 这时的poi列表是经过aios优化处理过的列表,
         * 包括去除无关/聚类等优化处理
         **/
        public static final String POI_LIST = "/navigation/poi/list";
        /**
         * poi搜索结果选择, 上一页
         **/
        public static final String POI_SELECT_PREV_PAGE = "/navigation/poi/select/prevpage";
        /**
         * poi搜索结果选择, 上一页
         **/
        public static final String POI_SELECT_NEXT_PAGE = "/navigation/poi/select/nextpage";
        /**
         * 路径规划
         **/
        public static final String NAVIGATION_ROUTE = "/navigation/route";
    }

    public static final class Nearby {

        /**
         * 查询附近信息，如：酒店, 加油站等
         **/
        public static final String POI_SEARCH = "/nearby/poi/search";
        /**
         * 列出查询结果里附近的地点
         **/
        public static final String POI_LIST = "/nearby/poi/list";
        /**
         * 上一页
         **/
        public static final String POI_SELECT_PREV_PAGE = "/nearby/poi/select/prevpage";
        /**
         * 下一页
         **/
        public static final String POI_SELECT_NEXT_PAGE = "/nearby/poi/select/nextpage";
    }

    public static final class Netfm {
        public static final String NAME = "netfm";
        public static final String DOMAIN = "/netfm";
        public static final String PLAY = "/netfm/play";
        public static final String LIST = "/netfm/list";
        public static final String SEARCH = "/netfm/search";
        public static final String SELECT = "/netfm/select/";

        public static final String STATE = "netfm.state";
        public static final String SEARCH_RESULT = "netfm.search.result";

        public static final String PREV_PAGE = "prevpage";
        public static final String NEXT_PAGE = "nextpage";
    }

    public static final class Chat {
        public static final String QUESTION_QUERY = "/chat/question/query";
        public static final String VOICE_PLAY = "/chat/play/voice";
        public static final String VOICE_STOP = "/chat/stop/voice";
        public static final String QUESTION_AND_RESULT = "chat.question.query.result";
        public static final String PLAY_VOICE_STATE = "chat.play.voice.state";
    }

    public static final class Radio {
        public static final String NAME = "radio";
        public static final String FM_PLAY = "/radio/fm/play";
        public static final String AM_PLAY = "/radio/am/play";
    }

    public static final class System {
        public static final String OPEN_APP = "/system/app/open";
        public static final String CLOSE_APP = "/system/app/close";
        public static final String VOLUME_UP = "/system/volume/up";
        public static final String VOLUME_DOWN = "/system/volume/down";
        public static final String VOLUME_MAX = "/system/volume/max";
        public static final String VOLUME_MIN = "/system/volume/min";
        public static final String VOLUME_MUTE = "/system/volume/mute";
        public static final String VOLUME_UNMUTE = "/system/volume/unmute";
        public static final String BRIGHTNESS_UP = "/system/brightness/up";
        public static final String BRIGHTNESS_DOWN = "/system/brightness/down";
        public static final String SCREEN_ON = "/system/screen/on";
        public static final String SCREEN_OFF = "/system/screen/off";
        public static final String MEDIA_PLAY = "/system/media/play";
        public static final String MEDIA_PAUSE = "/system/media/pause";
        public static final String MEDIA_STOP = "/system/media/stop";
        public static final String MEDIA_PREV = "/system/media/prev";
        public static final String MEDIA_NEXT = "/system/media/next";
        public static final String BLUETOOTH_ON = "/system/bluetooth/on";
        public static final String BLUETOOTH_OFF = "/system/bluetooth/off";
        public static final String WIFI_ON = "/system/wifi/on";
        public static final String WIFI_OFF = "/system/wifi/off";
        public static final String HOTSPOT_ON = "/system/hotspot/on";
        public static final String HOTSPOT_OFF = "/system/hotspot/off";
        public static final String DVR_CAPTURE = "/system/dvr/capture";
        public static final String FM_ON = "/system/fmsend/on";
        public static final String FM_OFF = "/system/fmsend/off";
        public static final String EDOG_ON = "/system/edog/on";
        public static final String EDOG_OFF = "/system/edog/off";
    }

    public static final class Customize {
        public static final String NAME = "customize";
        public static final String COMMAND = "customize.commands";
        public static final String GENERATE_NATURAL_LANGUAGE = "customize.natural.language.generation";
        public static final String TTS = "customize.tts";
        public static final String TIPS_COMMON = "customize.tips.common";
    }

    public static final class Phone {

        /**
         * 切换到打电话领域, 默认显示打电话界面
         **/
        public static final String OUT_GOING_CALL = "/phone/outgoingcall";
        /**
         * 列出联系人供选择
         **/
        public static final String CONTACTS_LIST = "/phone/outgoingcall/contacts/list";
        /**
         * 上一页
         **/
        public static final String CONTACTS_SELECT_PREV_PAGE = "/phone/outgoingcall/contacts/select/prevpage";
        /**
         * 下一页
         **/
        public static final String CONTACTS_SELECT_NEXT_PAGE = "/phone/outgoingcall/contacts/select/nextpage";
        /**
         * 拨打某个号码, phone节点在实现时一般会倒计时5s拨打, 在倒计时过程中用户还可以通过语音取消拨打
         **/
        public static final String OUT_GOING_CALL_DIAL = "/phone/outgoingcall/dial";
        /**
         * 打断拨号流程, 如果当前正处于倒计时状态, 则打断倒计时, 等待进一步用户语音输入.
         * aios在检测到语音时就会调用该接口, 防止用户在说"取消", 还没说完倒计时就结束了, 造成用户困扰.
         **/
        public static final String OUT_GOING_CALL_DIAL_WAIT = "/phone/outgoingcall/dial/wait";
        /**
         * 确定拨打
         **/
        public static final String OUT_GOING_CALL_DIAL_OK = "/phone/outgoingcall/dial/ok";
        /**
         * 取消拨打
         **/
        public static final String OUT_GOING_CALL_DIAL_CANCLE = "/phone/outgoingcall/dial/cancel";
        /**
         * 接听当前来电
         **/
        public static final String IN_COMING_ACCEPT = "/phone/incomingcall/accept";
        /**
         * 拒绝当前来电
         **/
        public static final String IN_COMING_REJECT = "/phone/incomingcall/reject";
    }

    public static final class Weather {
        public static final String NAME = "weather";
        public static final String QUERY_RESULT = "weather.query.result";
    }

    public static final class Other {
        public static final String SPEAK = "speak";
        public static final String AIOS_WAKE_UP_ALLOW = "aios.wakeup.allow";
        public static final String AIOS_STATE = "aios.state";
        public static final String CONTEXT_INPUT_TEXT = "context.input.text";
        public static final String CONTEXT_OUTPUT_TEXT = "context.output.text";
        public static final String UI_MIC_CLICK = "ui.mic.click";
        
        public static final String UI_AUDIO_STATE_MUTE= "aios.audio.mute";
        public static final String UI_AUDIO_STATE_UNMUTE= "aios.audio.unmute";

        @Deprecated
        public static final String UI_CLICK = "ui.click";
        public static final String UI_PAUSE = "ui.pause";
        public static final String CONTACTS_SYNC_OK = "contacts.sync.ok";
        public static final String ACTION_SLIDE_LEFT = "com.aispeech.goHome";
        public static final String ACTION_SLIDE_RIGHT = "com.aispeech.goCompany";
        public static final String KEYS_AIOS_STATE = "keys.aios.state";

    }
}
