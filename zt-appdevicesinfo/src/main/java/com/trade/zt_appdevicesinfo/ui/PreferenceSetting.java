package com.trade.zt_appdevicesinfo.ui;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceActivity;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by fangzhu on 2014/12/16.
 */
public class PreferenceSetting {

    /**
     * speed 独有 或 公有 start
     **/
    public static final String PRE_REFERSH_TIME = "PreferenceSetting_refershtime";
    public static final String KEY_REFERSH_TIME_LIVEROOM = "key_liveroom";
    public static final String KEY_REFERSH_TIME_TABROOM = "key_tabroom";
    public static final String KEY_REFERSH_TIME_TABCHAT = "key_tabchat";
    public static final String KEY_REFERSH_TIME_TABCALLLIST = "key_tabcalllist";
    public static final String KEY_REFERSH_TIME_TABHISTORY = "key_tabhistory";
    public static final String PRE_CHAT_MESSAGE = "PreferenceSetting_chat_msg";
    public static final String PRE_USER_INFO = "PreferenceSetting_USER_INFO";
    public static final String PRE_CHCHE_APP = "PreferenceSetting_CACE_APP";
    public static final String KEY_REFERSH_TIME_GLOBAL_NEWS = "key_global_news";
    public static final String KEY_REFERSH_TIME_MACRO_HOT = "key_macro_hot";
    public static final String KEY_REFERSH_TIME_OILSILVER = "key_oilsilver";
    public static final String KEY_REFERSH_TIME_ZIXUN_NEWS = "key_zixun_news";
    public static final String KEY_REFERSH_TIME_NEWS_MAIN = "key_main_news";
    public static final String KEY_REFERSH_TIME_NEWS_FORECAST = "key_forecast_news";
    public static final String KEY_REFERSH_TIME_NEWS_CALLLIST = "key_calllist_news";
    public static final String KEY_REFERSH_TIME_PERSON_BEANS = "key_person_beans";
    public static final String KEY_REFERSH_TIME_PERSON_BEANS_EXCHANGE_HISTORY = "key_person_beans_exchange_history";
    public static final String KEY_REFERSH_TIME_HOME_FRAGMENT = "key_home_fragment";
    public static final String KEY_REFERSH_TIME_PERSON_GENDAN = "key_person_gendan";
    public static final String KEY_REFERSH_TIME_PERSON_POPULARITY = "key_person_popularity";

    public static final String PRE_UMENG_EVENT = "PreferenceSetting_umeng_event";//记录umeng统计
    public static final String SETTING_FILE_NAME = "PreferenceSetting_default";

    public static final String NOT_ALLOW_UNIFYPWDDLG = "not_allow_unifypwddlg";// 是否允许统一交易密码弹窗
    public static final String ACCOUNT_INTEGRAL_VERSION = "account_integral_version";// 本地等级版本
    public static final String ACCOUNT_INTEGRAL_INFO = "account_integral_info";// 本地等级str
    public static final String MY_INTEGRALLEVEL = "my_integrallevel";// 我的积分等级
    public static final String ISINITZIXUAN = "isInitZixuan";// 是否初始化了自选
    public static final String INVITEFRIENDS = "inviteFriends";// 邀请要有是否是新功能

    public static final String WITH_DRAW_FIRST = "withDrawFirst_";// 全部提现当天第一次
    public static final String KEY_SHOW_EVENTS = "showeventsall";// 活动new是否展示key

    public static final String KEY_SHOW_TRADE_GUIDE = "show_trade_guide"; // 展示交易引导banner (当天第一次!)

    public static final String HOLD_CLOSE_TIPS = "hold_close_tips"; //持仓页面平仓提醒
    public static final String HOME_COPYORDER_TIPS = "home_copyorder_tips"; //首页跟单提示

    public static final String LAST_REQUEST_CALENDAR_ID = "last_request_calendar_id"; //请求提醒的最后-条数据id

    public static final String KEY_DAILY_ADVANCED_TASK_TIPS = "key_daily_advanced_task_tips";//每日进阶任务标识
    public static final String KEY_TO_BE_RECHARGED_DIALOG_DISPLAY_TIME = "key_to_be_recharged_dialog_display_time";//待支付弹框上一次显示时间
    public static final String KEY_OPTION_TRADE_RECHARGED_DIALOG_DISPLAY_TIME = "key_option_trade_recharge_dialog_display_time";//充值赠送快捷交易模拟金弹框上一次显示时间

    public static final String KEY_TO_UPDATE_VERSION_DIALOG_DISPLAY_TIME = "key_to_update_version_dialog_display_time";//更新弹框上一次显示时间

    public static final String KEY_TO_LOTTERY_OPPORTUNITY_RECEIVE_DIALOG_DISPLAY_TIME = "key_to_lottery_opportunity_receive_dialog_display_time";//抽奖转盘页面抽奖机会领取提示弹框上一次显示时间

    public static final String KEY_LOCAL_HAS_LOGGED = "is_local_user_logged";// 用户是否在这台手机上登陆过
    public static final String KEY_SHOW_IB_NEWS = "show_ib_news";// IB 申请是否展示news Key
    public static final String KEY_IS_NOT_FRIST_OPEN_OPTION = "is_not_frist_ppen_option";// 判断是否是第一次打开期权交易界面
    public static final String KEY_LOGIN_REGISTER_IS_PHONE = "login_register_is_phone";// 保存当前ip是否能手机号码注册
    public static final String KEY_IS_SHOW_TRADE_CREATE = "is_show_trade_create_dialog";// 是否送100元新手券的弹窗
    public static final String KEY_TASK_CENTER_NEW = "key_task_center_new";// 任务中心新标签
    public static final String KEY_TASK_CENTER_NEW_FUN = "key_task_center_new_fun";// 任务中心新功能
    public static final String KEY_TASK_CENTER_LAST_DAY = "task_center_last_day";//
    public static final String KEY_IS_OPTIONAL_FIRST = "is_optional_first";// 产品列表是否首次展示自选
    public static final String KEY_TRADE_LIST_ADD_OPTIONAL = "key_trade_list_add_optional";// 产品列表添加收藏标志位
    public static final String KEY_FORUM_SHOW_BIGGER_PHOTO = "key_forum_show_bigger_photo";// 论坛观点首次进入查看大图界面标志位
    public static final String KEY_IDEAS_SHOW_ZOOM_PHOTO = "key_ideas_show_zoom_photo";// 论坛首次进入观点详情页，是否展示可缩放标志位
    public static final String KEY_PRODUCT_GUIDE_NEW = "key_product_guide_new";// 产品详情页，新改版导航
    public static final String KEY_CALENDAR_GUIDE = "key_calendar_guide";// 新手引导导航
    public static final String KEY_PRODUCT_NOVICE_POP = "key_product_novice_pop";// 新手卷继续交易进入产品详情页
    public static final String KEY_PRODUCT_CREDIT_SECOND_NOVICE_POP = "key_product_credit_second_novice_pop";// 新手卷 券2 点击进入详情展示气泡
    public static final String KEY_TRAD_UPGRADE_DIALOG = "key_trad_upgrade_dialog";// 交易成功后的交易升级弹框
    public static final String KEY_OPTION_QUERY = "key_option_query";//自选搜索
    public static final String KEY_NOT_RECHARGED_QUEST = "key_not_recharged_quest"; // 是否已经填写过问卷
    public static final String KEY_MAIN_NOVICE_GIFT = "key_main_novice_gift"; // 大礼包新任务
    public static final String KEY_MAIN_MARKET_MERGE = "key_main_market_merge"; // 交易大厅行情合并
    public static final String KEY_MAIN_MARKET_AI = "key_main_market_ai"; // 交易大厅行情合并ai盯盘设置
    public static final String KEY_ABOUT_YOU_CACHE = "KEY_ABOUT_YOU_CACHE";// about_you 缓存
    public static final String KEY_ABOUT_YOU_WRITE_RETAIN_DIALOG_TIME = "KEY_ABOUT_YOU_WRITE_RETAIN_DIALOG_TIME";// about_you 引导的填写挽留弹窗上次显示时间
    public static final String KEY_MARKET_BANNER_DEPOSIT = "key_market_banner_deposit";//行情交易 banner 保存时间
    public static final String KEY_MARKET_BANNER_DEPOSIT_NOT = "key_market_banner_deposit";//
    public static final String KEY_MARKET_BANNER_WEEK_PROFIT = "key_market_banner_week_profit";//
    public static final String KEY_OLYMPIC_SHOW_FIRST = "key_olympic_show_first";// 首次展示奥林匹克活动
    public static final String KEY_QUICK_EXIT_LATER = "key_quick_exit_later";// 快捷交易退出不再提示
    public static final String KEY_WELFARE_DIALOG_DISPLAY_TIME = "key_main_welfare_dialog_display_time";//福利专区-每日福利弹窗上次弹出日期
    public static final String KEY_CREATE_TRADE_FIRST_GUIDE = "keyCreateTradeFirstGuide";// 建仓面板现金建仓一笔
    public static final String KEY_DIALOG_NOVICE_PACK_V3 = "keyDialogNovicePackV3";// 新手礼包第三版
    public static final String KEY_SHOW_FIRST_POPUP_TIPS = "key_show_first_popup_tips";//产品列表首次进入
    public static final String KEY_HAS_MARKET_BOTTOM_BANNER = "key_has_market_bottom_banner";//行情页是否存在banner
    public static final String KEY_HAS_MARKET_BOTTOM_BANNER_SHOW_TIME = "key_has_market_bottom_banner_show_time";//行情页的banner展示时间
    public static final String KEY_HOME_PAGE_NEW_USER_TIPS_MSG = "key_home_page_new_user_tips_msg";
    public static final String KEY_DIALOG_NOVICE_FIRST_PACK_V2 = "key_dialog_novice_first_pack_v2";// 第二版300新手卷只展示一次的弹框
    public static final String KEY_NOVICE_GIVE_STOCK_TIPS = "key_novice_give_stock_tips";//新手送的股票持仓订单，只展示一次
    public static final String KEY_NOVICE_PROFIT_3_TIPS = "key_novice_profit_3_tips";//新手送盈利3美元
    public static final String KEY_NOT_LOGIN_CLOSE_DIALOG = "key_not_login_close_dialog";//未登录新手卷关闭弹框挽留
    public static final String KEY_GROUP_POST_MONMENT_SAVE = "key_group_post_monment_save";//圈子-发布帖子页面-保存输入内容
    public static final String KEY_GROUP_POST_ORDER_MONMENT_SAVE = "key_group_post_order_monment_save";//圈子-发布晒单页面-保存输入内容
    public static final String KEY_NOVICE_SCHOOL_IS_FRIST = "key_novice_school_is_frist";//是否是首次进入新手学堂
    public static final String KEY_FINGER_PRINT_PWD = "key_finger_print_pwd";//指纹密码
    public static final String KEY_IS_USE_FINGER_PRINT_PWD = "key_is_use_finger_print_pwd";//是否使用指纹密码
    public static final String KEY_DYNAMIC_NEW_LAST_TIME = "key_dynamic_new_last_time";//动态最新保存最后刷新时间
    public static final String KEY_DYNAMIC_RECOMMEND_LAST_TIME = "key_dynamic_recommend_last_time";//动态推荐保存最后刷新时间
    public static final String KEY_ME_WALLET_TOP_MSG = "key_me_wallet_top_msg";//新手卷上方提示
    public static final String KEY_TRADE_FIRST_CREDIT_ORDER = "key_trade_first_credit_order";//首次奖励金持仓提示
    public static final String KEY_WALLET_SERVICE_HELP = "key_wallet_service_help";//我的钱包悬浮的客服服务
    public static final String KEY_WALLET_RIGHT_CREDIT = "key_wallet_right_credit";//我的钱包右上角奖励金提示
    public static final String KEY_RECEIVE_CREDIT_FIRST = "key_receive_credit_first";//第一次领取奖励金
    public static final String KEY_FINGER_PRINT_TOO_MANY_ERRORS_TIME_RECORD = "key_finger_print_too_many_errors_time_record";//尝试指纹验证次数错误太多时间记录
    public static final String KEY_HTTP_URL_MODE = "key_http_url_mode";
    public static final String KEY_HIDE_TASK_CENTER_GUIDE = "key_hide_Task_center_guide";
    public static final String KEY_DIALOG_NOVICE_PACK_V3_NO_LOGIN = "key_dialog_novice_pack_v3_no_login";// 新手礼包第三版
    public static final String KEY_HIDE_MY_ASSETS_CREDIT = "key_hide_my_assets_credit"; //隐藏我的钱
    public static final String KEY_FIRST_TRADE_CLOSE = "key_first_trade_close"; //第一次平仓亏损单子提现
    public static final String KEY_SHOW_TREASUREHOME_RULE = "key_show_treasurehome_rule"; //
    public static final String KEY_TREASURE_MARQUEE_DATA = "key_treasure_marquee_data"; //交易夺宝跑马灯数据
    public static final String KEY_TREASURE_GUIDE_TRADE = "key_treasure_guide_trade"; //交易夺宝引导去交易弹窗
    public static final String KEY_ME_MT4_TRANSFER = "key_me_mt4_transfer"; //交易夺宝引导去交易弹窗
    public static final String KEY_DIALOG_WITHDRAW_PROCESS = "key_dialog_withdraw_process";//有提现仍在处理中,一天展示一次
    public static final String KEY_DIALOG_WITHDRAW_ANALYSTS = "key_dialog_withdraw_analysts";//经济分析师弹框
    //    public static final String KEY_DIALOG_WITHDRAW_ALL = "key_dialog_withdraw_all";//全部提现,一天展示一次
    public static final String KEY_REGISTER_SOURCE = "key_register_source";//注册来源
    public static final String KEY_FIRST_LOGIN_OPEN_IM = "key_first_login_open_im";//是不是首次登录openIM
    public static final String KEY_DREAM_NUMBER_SHOW = "keyDreamNumberShow";//新注册梦想号码首次
    public static final String KEY_USE_SHARE_BLOCK = "key_use_share_block";//是否分享黑名单
    public static final String KEY_WHATS_APP_RED_VIEW = "key_whats_app_red_view";//是否 whatsapp 小红点
    public static final String KEY_OPEN_IM_RED_VIEW = "key_open_im_red_view";//是否 im 小红点
    public static final String KEY_SHARE_TREASURE = "key_share_treasure";//只有首次展示；
    public static final String KEY_ALI_OSS_TIME_EXPIRED = "key_ali_oss_time_expired";//过期时间
    public static final String KEY_HIDE_INVITE_FRIENDS_NEW = "key_hide_invite_friends_new"; //隐藏邀请好友new标志
    public static final String KEY_ME_OPEN_IM_TOP_MSG = "key_me_open_im_top_msg";//open im 新手提示
    public static final String KEY_INVITE_FRIENDS_SOURCE = "key_invite_friends_source";//邀请好友key
    public static final String KEY_SHARE_PLACARD_CACHE = "key_share_placard_cache"; //物料海报本地缓存
    public static final String KEY_SHARE_BUTTON_PLACARD_CACHE = "key_share_button_placard_cache"; //底部物料海报本地缓存
    public static final String KEY_MY_INVITE_USER_HISTORY_CACHE = "key_my_invite_user_history_cache"; //我的邀请记录本地缓存
    public static final String KEY_MY_BE_INVITE_HISTORY_CACHE = "key_my_be_invite_history_cache"; //我的受邀记录本地缓存
    public static final String KEY_SHARE_PLACARD_URL_CACHE_DATA_NEW = "key_share_placard_url_cache_data_new"; //物料海报接口缓存数据
    public static final String KEY_PUSH_SETTING_CHECK_FULL_STOP = "key_push_setting_check_full_stop"; //设置止盈止损
    public static final String KEY_PUSH_SETTING_CHECK_PEND_SUCC = "key_push_setting_check_pend_succ"; //挂单成功
    public static final String KEY_PUSH_SETTING_CHECK_ADD_NOTICE = "key_push_setting_check_add_notice"; //添加提醒
    public static final String KEY_REGISTER_SEND_SMS_INFO_LIST = "key_register_send_sms_info_list"; //发送验证码时间
    public static final String KEY_NOTICE_SEND_WAY_SETTING_FIRST = "key_notice_send_way_setting_first"; //首次设置提醒方式,初始化，仅调用一次
    public static final String KEY_REGISTER_AB_TEST_INFO = "key_register_ab_test_info"; //注册ABTest信息
    public static final String KEY_LOGIN_COUNTRY_LIST_CACHE = "key_login_country_list_cache"; //后台返回的所有区号
    public static final String KEY_USER_TAG_QUICK_LOGIN_LIST = "key_user_tag_quick_login_list_"; //用户是否需要到设置快捷登录方式页面 (拼接用户id)保证本地缓存唯一性
    public static final String KEY_USER_PRODUCT_PRICE_TYPE = "key_user_product_price_type"; //产品买价卖价
    public static final String KEY_USER_PRODUCT_REMIND_TIP = "key_user_product_remind_tip"; //行情提醒
    public static final String KEY_USER_PRODUCT_TARGET_SETTING = "key_user_product_target_setting"; //指标设置
    public static final String KEY_USER_PRODUCT_HOLD_ORDER = "key_user_product_hold_order"; //持仓引导

    public static final String KEY_FAST_TRADING_NEW_USER_ATTEMPT_TIPS = "key_fast_trading_new_user_attempt_tips"; //快捷交易是否弹出过新用户横幅  跟随设备
    public static final String KEY_MAIN_SHOW_PUBLIE_COMMENT = "key_main_show_public_comment"; //弹出评论弹窗
    public static final String KEY_TOKEN_EXPIRED_FIRST_SHOW = "key_token_expired_first_show"; //首次登录弹出token过期
    public static final String KEY_UPLOAD_PACKAGE_APP_RECORD = "key_upload_package_app_record"; //上次上次列表列表时间
    public static final String KEY_SPECIAL_CROWD_TAG = "key_special_crowd_tag"; //敏感人物标记
    public static final String KEY_PRODUCT_ANNOUNCEMENT = "KEY_PRODUCT_ANNOUNCEMENT"; //产品公告
    public static final String CLICK_MARKET_PRODUCT_TRADE_ADVERT = "click_market_product_trade_advert_"; //行情自选是否关闭广告
    public static final String CLICK_PRODUCT_TRADE_ADVERT = "click_product_trade_advert_"; //产品详情行情是否关闭广告
    public static final String PRODUCT_TRADE_ADVERT_TAG = "product_trade_advert_tag_"; //是否有 行情自选是否广告

    public static final String KEY_VISITOR_SUGGEST_EMAIL = "key_visitor_suggest_email";// 游客意见反馈接收邮箱
    public static final String KEY_LAST_SUBMIT_VOURCE_TIME = "key_last_submit_vource_time";// 最后一次电汇上传凭证的时间
    public static final String KEY_LAST_SUBMIT_VOURCE_CONTENT = "key_last_submit_vource_connent";// 最后一次电汇上传凭证内容

    /** speed 独有 或 公有 end  **/

    /**
     * FT211独有 start
     **/
    public static final String KEY_IS_SHOWCHANGE_USERACCOUNT_GUIDE = "is_showchange_useraccount_guide ";// 是否展示切换账户引导
    public static final String KEY_CHINK_IN_CLOSE_STATUS = "chink_in_close_status ";// 签到入口开关
    public static final String KEY_CHINK_IN_IS_NEW = "chink_in_is_new";// 是否是新签到
    public static final String KEY_CURRENT_TRADE_DATE = "current_trade_date ";// 当前交易日期
    public static final String KEY_CASHIN_INFO_CACHE = "cashin_info_cache ";// 首次充值信息填写基本信息的数据
    public static final String KEY_LOGIN_SAVE_TE_CODE = "login_save_te_code"; // 保留上一次区号
    public static final String KEY_LOSS_BACK_ACT_NEW = "loss_back_act_new"; //是否收益保障新用户
    public static final String KEY_LOSS_BACK_ACT_INIT = "loss_back_act_init"; //活动入口是否点击过
    public static final String POF_GIF_URL = "pof_gif_url"; //por 获取方式gif动图
    public static final String KEY_USER_LOGIN_TIMES = "user_login_times"; //用户打开app次数
    public static final String KEY_BURST_CLOSE_TIME = "burst_close_time"; //用户大盘爆仓提醒关闭时间
    public static final String KEY_OPTION_TRADE_PROFIT_COE = "option_trade_profit_coe"; //快捷模式止盈系数
    public static final String KEY_OPTION_TRADE_LOSS_COE = "option_trade_loss_coe"; //快捷模式止损系数
    public static final String KEY_OPTION_TRADE_GUIDE_LEVERAGE = "option_trade_guide_leverage"; //快捷模式引导大盘交易
    public static final String KEY_OPTION_STOP_PROFIT_OR_LOSS_TIP_HIDE = "option_stop_profit_or_loss_tip_hide"; //快捷模式设置止盈止损提示
    /** FT211独有 end **/

    /**
     * 信号岛独有 start
     **/
    public static final String KEY_RECOMMEND_FRIENDS_GUIDE = "keyRecommendFriendsGuide";// 推荐好友引导
    public static final String KEY_RECOMMEND_SCORE_TIME = "keyRecommendScoreTime";// 推荐意愿调查
    public static final String KEY_PRODUCT_CHART_GUIDE = "keyProductChartGuide";// 设置买卖线引导
    public static final String KEY_SHOW_HOME_BROKER_TIPS = "key_show_home_broker_tips";// 显示首页broker顶部提示
    public static final String KEY_SHOW_HOME_COPY_BROKER_DIALOG_TIPS = "key_show_home_copy_broker_dialog_tips";// 显示首页跟单broker弹窗的顶部提示
    public static final String KEY_SHOW_TRADE_BROKER_DIALOG_TIPS = "key_show_trade_broker_dialog_tips";// 显示交易相关broker弹窗的顶部提示
    public static final String KEY_SHOW_HOME_COPY_TIPS = "key_show_home_copy_tips";// 显示首页跟单顶部提示
    public static final String KEY_SHOW_TRADE_STATEMENT_SHARE_DIALOG_TIPS = "key_show_trade_statement_share_dialog_tips";// 显示交易机会分享弹窗顶部提示
    /** 信号岛独有 end **/

    /**
     * XtrendSC独有 start
     **/
    public static final String KEY_FIRST_SHOW_LOADING = "Key_first_show_loading";//是否展示引导
    public static final String KEY_IS_HIDE_FUND = "key_is_hide_fund"; //是否隐藏资金页面各项数字
    public static final String KEY_IS_OPEN_MARK_INVESTMENT = "key_is_open_mark_investment";//资金列表折叠/展开 状态
    public static final String KEY_IS_HIDE_INVESTMENTS = "key_is_hide_investments"; //是否隐藏投资布局
    public static final String KEY_IS_OPEN_MARK_HOLDLIST = "key_is_open_mark_holdlist"; //是否隐藏资金二级页面持仓布局
    public static final String KEY_IS_HIDE_HOLDLIST = "key_is_hide_holdlist"; //是否隐藏投资布局
    public static final String KEY_IS_OPEN_MARK_ORDERLIS = "key_is_open_mark_orderlis"; //是否隐藏资金二级页面挂单布局
    public static final String KEY_IS_HIDE_ORDERLIS = "key_is_hide_orderlis"; //是否隐藏资金二级页面挂单布局
    public static final String KEY_IS_HIDE_ME_ACCOUNT_LIST = "key_is_hide_me_account_list";//是否隐藏我的界面资金账户页面
    public static final String KEY_OPEN_MARK_ACCOUNTLIST = "key_open_mark_accountlist";//我的界面资金账户页面 安全设置的开关
    public static final String KEY_MY_TAB_CURRENT_CURRENCY = "key_my_tab_current_currency";//我的资金页面当前币种
    public static final String KEY_CURRENT_CURRENCY = "key_current_currency";//资金页面当前币种
    public static final String KEY_CURRENT_SYMBOL = "key_current_symbol";//资金页面当前货币符号
    public static final String KEY_FROM_CURRENCY = "key_from_currency";//转账页面当前币种
    public static final String KEY_IS_NOT_FRIST_DEPOSIT = "key_is_not_frist_deposit";//是否是多次充值页面
    public static final String KEY_IS_NOT_FRIST_WITHDRAW = "key_is_not_frist_withdraw";//是否是多次提现页面
    public static final String KEY_IS_NOT_FRIST_TRANSFERACCOUNTS = "key_is_not_frist_transferaccounts";//是否是多次进入转账页面
    public static final String KEY_FV_TAX_NUMBER = "key_fv_taxNumber";// 保存税号填写信息
    public static final String KEY_FV_TAX_NO = "key_fv_taxNo";// 保存税号填写信息
    public static final String KEY_FV_TAX_NO_NUMBER = "key_fv_taxNoNumber";// 保存税号填写信息
    public static final String KEY_STOCK_K_LINE_TYPE = "key_stock_k_line_type";//保存k线默认图
    public static final String KEY_SELECT_OPTIONAL_OR_HOLD = "key_select_optional_or_hold";//记录用户最后一次选择是自选还是持仓
    public static final String KEY_REFRESH_NEWEST_CARD_EVENT = "key_refresh_newest_card_no_event";// 记录用户最后一次选择的卡

    public static final String KEY_DEMO_CURRENT_CURRENCY = "key_demo_current_currency";
    public static final String KEY_DEMO_CURRENT_SYMBOL = "key_demo_current_symbol";//资金页面当前货币符号
    public static final String KEY_DEMO_IS_OPEN_MARK_INVESTMENT = "key_demo_is_open_mark_investment";//资金列表折叠/展开 状态
    public static final String KEY_DEMO_IS_HIDE_INVESTMENTS = "key_demo_is_hide_investments"; //是否隐藏投资布局
    public static final String KEY_DEMO_IS_HIDE_FUND = "key_demo_is_hide_fund"; //是否隐藏资金页面各项数字
    public static final String KEY_DEMO_IS_HIDE_HOLDLIST = "key_demo_is_hide_holdlist"; //是否隐藏投资布局
    public static final String KEY_DEMO_IS_HIDE_ORDERLIS = "key_demo_is_hide_orderlis"; //是否隐藏资金二级页面挂单布局
    public static final String KEY_DEMO_IS_OPEN_MARK_HOLDLIST = "key_demo_is_open_mark_holdlist"; //是否隐藏资金二级页面持仓布局
    public static final String KEY_DEMO_IS_OPEN_MARK_ORDERLIS = "key_demo_is_open_mark_orderlis"; //是否隐藏资金二级页面挂单布局
    public static final String KEY_BIND_THIRD_INFO_MENU = "key_bind_third_info_menu";//绑定三方信息

    /**
     * XtrendSC独有 end
     **/

    public static String getDownloadDir(Context app) {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在

        if (sdCardExist)      //如果SD卡存在，则获取跟目录
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            return sdDir.toString() + "/myapp/cache/download";
        }
        return app.getExternalCacheDir() + "myapp/cache/download";
    }

    //get history refreshtime by key
    public static long getRefershTime(Context app, String key, long defaultValu) {
        return getSharedPreferences(app, PRE_REFERSH_TIME, key, defaultValu);
    }

    public static void setRefershTime(Context app, String key, long value) {
        setSharedPreferences(app, PRE_REFERSH_TIME, key, value);
    }

    public static String getCacheString(Context app, String key, String defaule) {
        String result = null;
        SharedPreferences settings = app.getSharedPreferences(PRE_CHCHE_APP, 0);
        result = settings.getString(key, defaule);
        return result;
    }

    public static void setCacheString(Context app, String key, String value) {
        SharedPreferences settings = app.getSharedPreferences(PRE_CHCHE_APP, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public static String getSharedPreferences(Context app, String preferName, String key) {
        String result = null;
        SharedPreferences settings = app.getSharedPreferences(preferName, 0);
        result = settings.getString(key, null);
        return result;
    }

    public static void setSharedPreferences(Context app, String preferName, String key, String value) {
        SharedPreferences settings = app.getSharedPreferences(preferName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getSharedPreferences(Application app, String preferName, String key) {
        String result = null;
        SharedPreferences settings = app.getSharedPreferences(preferName, 0);
        result = settings.getString(key, null);
        return result;
    }

    public static Map<String, String> getSharedPreferences(Context app, String preferName) {
        SharedPreferences settings = app.getSharedPreferences(preferName, 0);
        Map<String, String> preferences = (Map<String, String>) settings.getAll();
        return preferences;
    }

    public static void removeSharedPreferences(Context context, String preferName, String key) {
        SharedPreferences settings = context.getSharedPreferences(preferName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void removeSharedPreferences(Context app, String preferName) {
        SharedPreferences settings = app.getSharedPreferences(preferName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    public static long getSharedPreferences(Context app, String preferName, String key, long defaultValue) {
        long result = 0;
        SharedPreferences settings = app.getSharedPreferences(preferName, 0);
        result = settings.getLong(key, defaultValue);
        return result;
    }

    public static void setSharedPreferences(Context app, String preferName, String key, long value) {
        SharedPreferences settings = app.getSharedPreferences(preferName, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static String getUmengEventTimeTag(Context app, String key) {
        SharedPreferences settings = app.getSharedPreferences(PRE_UMENG_EVENT, 0);
        return settings.getString(key, null);
    }

    public static void setUmengEventTimeTag(Context app, String key, String value) {
        SharedPreferences settings = app.getSharedPreferences(PRE_UMENG_EVENT, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public static boolean getBoolean(Context context, String key) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(
                    SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
            return sp.getBoolean(key, false);
        } else {
            return false;
        }
    }

    public static void setBoolean(Context context, String key, boolean value) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(
                    SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
            sp.edit().putBoolean(key, value).commit();
        }
    }

    public static boolean getBooleanDefaultTrue(Context context, String key) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(
                    SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
            return sp.getBoolean(key, true);
        } else {
            return false;
        }
    }

    public static boolean getBooleanDefaultFalse(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    /**
     * 获取保存不同文件的 SharedPreferences
     */
    public static SharedPreferences getSPName(Context context, String spName) {
        return context.getSharedPreferences(spName, PreferenceActivity.MODE_PRIVATE);
    }

    public static void putString(SharedPreferences sp, @NonNull final String key, final String value) {
        sp.edit().putString(key, value).apply();
    }

    public static String getString(SharedPreferences sp, @NonNull final String key) {
        return sp.getString(key, "");
    }

    public static String getString(Context context, String key) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(
                    SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
            return sp.getString(key, null);
        } else {
            return "";
        }
    }

    public static void setString(Context context, String key, String value) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(
                    SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
            sp.edit().putString(key, value).commit();
        }
    }

    public static long getLong(Context context, String key) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(
                    SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
            return sp.getLong(key, -1L);
        } else {
            return 0L;
        }
    }

    public static void setLong(Context context, String key, long value) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(
                    SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
            sp.edit().putLong(key, value).commit();
        }
    }

    public static int getInt(Context context, String key) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(
                    SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
            return sp.getInt(key, -1);
        } else {
            return 0;
        }
    }

    public static int getInt(Context context, String key, int defValue) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(
                    SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
            return sp.getInt(key, defValue);
        } else {
            return defValue;
        }
    }

    public static void setInt(Context context, String key, int value) {
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(
                    SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
            sp.edit().putInt(key, value).commit();
        }
    }


    public static float getFloat(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        return sp.getFloat(key, 2.0f);
    }

    public static void setFloat(Context context, String key, float value) {
        SharedPreferences sp = context.getSharedPreferences(
                SETTING_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putFloat(key, value).commit();
    }

    public static Map<String, ?> getApps(Context context) {
        //        SharedPreferences sp = context.getSharedPreferences(
        //                "my_cached_app", PreferenceActivity.MODE_PRIVATE);
        SharedPreferences sp = context.getSharedPreferences(
                "my_cached_app_V2", PreferenceActivity.MODE_PRIVATE);
        return sp.getAll();
    }

    public static void setApps(Context context, List<String> list) {
        if (list == null)
            return;
        for (String pkg : list) {
            SharedPreferences sp = context.getSharedPreferences(
                    "my_cached_app_V2", PreferenceActivity.MODE_PRIVATE);
            sp.edit().putString(pkg, pkg).commit();
        }
    }

}
