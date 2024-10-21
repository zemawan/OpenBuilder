package ru.slonchig.openbuilder;

public class Config {
    public static String title = "app";
    public static String packageName = "com.myname.myapp";
    public static String versionCode = "1";
    public static String versionName = "beta 1.0";
    public static String path = "/storage/emulated/0";
    public static String luaCode = "";
    public static String iconPath = "";
    public static boolean building = false;

    public static void setTitle(String newTitle) {
        title = newTitle;
    }

    public static void setPackageName(String newPackageName) {
        packageName = newPackageName;
    }

    public static void setVersionCode(String newVersionCode) {
        versionCode = newVersionCode;
    }

    public static void setVersionName(String newVersionName) {
        versionName = newVersionName;
    }

    public static void setPath(String newPath) {
        path = newPath;
    }

    public static void setLuaCode(String newLuaCode) {
        luaCode = newLuaCode;
    }

    public static void setIconPath(String newIconPath) {
        iconPath = newIconPath;
    }

    public static void setBuilding(boolean newBuilding) {
        building = newBuilding;
    }
}