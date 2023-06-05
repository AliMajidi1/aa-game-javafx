package com.alimajidi.aa.view;

public enum MultiLanguage {
    USERNAME("username", "نام کاربری"),
    PASSWORD("password", "گذرواژه"),
    NEW_USERNAME("new username", "نام کاربری جدید"),
    NEW_PASSWORD("new password", "گذرواژه جدید"),
    LOGIN("Login", "ورود"),
    SIGNUP("Signup", "ثبتنام"),
    GUEST("Guest", "مهمان"),
    GAME_1v1("1 vs 1", "دو نفره"),
    USER_PASS_INCORRECT("username or password is incorrect!", "نام کاربری یا گذرواژه اشتباه است!"),
    LOGIN_SUCCESS("Login successful!", "ورود موفقیت آمیز بود!"),
    USER_UNAVAILABLE("Username unavailable!", "نام کاربری در دسترس نیست!"),
    REGISTER_SUCCESS("Register successful!", "ثبتنام موفقیت آمیز بود!"),
    EXIT("Exit", "خروج"),
    SETTING("Setting", "تنظیمات"),
    LEADER_BOARD("Leader board", "جدول امتیزات"),
    PROFILE("Profile", "پروفایل"),
    CONTINUE("Continue", "ادامه"),
    NEW_GAME("New game", "بازی جدید"),
    LOGOUT("Logout", "خروج از حساب"),
    DELETE_ACC("Delete Account", "حذف حساب"),
    CHANGE_USER_PASS("Change Details", "تغییر مشخصات"),
    CHANGE_USER_PASS_SUCCESS("Change details successful!", "تغییر مشخصات موفقیت آمیز بود!"),
    GUEST_CHANGE_USER_PASS("You are guest!", "شما مهمان هستید!"),
    APPLY("Apply", "تایید"),
    DIFFICULTY("difficulty", "سختی"),
    SCORE("score", "امتیاز"),
    TIME("time", "زمان"),
    BY_DIFFICULTY1("By difficulty 1", "بر اساس سختی ۱"),
    BY_DIFFICULTY2("By difficulty 2", "بر اساس سختی ۲"),
    BY_DIFFICULTY3("By difficulty 3", "بر اساس سختی ۳"),
    DIFFICULTY1("Difficulty 1", "سختی ۱"),
    DIFFICULTY2("Difficulty 2", "سختی ۲"),
    DIFFICULTY3("Difficulty 3", "سختی ۳"),
    ALL_DIFFICULTY("All difficulties", "تمام سطوح"),
    MAP1("Map 1", "نقشه ۱"),
    MAP2("Map 2", "نقشه ۲"),
    MAP3("Map 3", "نقشه ۳"),
    BALLS_COUNT("Balls count", "تعداد توپ ها"),
    BACK("Back", "برگشت"),
    LIGHT_MODE("Light mode", "حالت روز"),
    DARK_MODE("Dark mode", "حالت شب"),
    ENGLISH("English", "انگلیسی"),
    PERSIAN("Persian", "فارسی"),
    SHOOT_KEY("Shoot key", "دکمه پرتاب"),
    FREEZE_KEY("Freeze key", "دکمه فریز"),
    SAVE("Save", "ذخیره"),
    RESTART("Restart", "دوباره"),
    SHOOT_KEY_GUIDE("Your custom shoot key", "دکمه پرتاب شما"),
    FREEZE_KEY_GUIDE("Your custom freeze key", "دکمه فریز شما"),
    PLAYER1_SHOOT_KEY_GUIDE("Player 1 shoot key", "دکمه پرتاب بازیکن اول"),
    PLAYER2_SHOOT_KEY_GUIDE("Player 2 shoot key", "دکمه پرتاب بازیکن دوم"),
    BOTH_FREEZE_KEY_GUIDE("Both players freeze key", "دکمه فریز هر دو بازیکن"),
    MUSIC1("Music 1", "موسیقی ۱"),
    MUSIC2("Music 2", "موسیقی ۲"),
    MUSIC3("Music 3", "موسیقی ۳"),
    MUTE("Mute", "بی صدا"),
    PLAYER("Player ", "بازیکن "),
    WINS(" Wins!", " برنده شد!");
    private final String en, fa;

    MultiLanguage(String en, String fa) {
        this.en = en;
        this.fa = fa;
    }

    public String getText() {
        return App.isPersian() ? fa : en;
    }
}
