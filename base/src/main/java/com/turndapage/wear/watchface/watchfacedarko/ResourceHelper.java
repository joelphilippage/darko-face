package com.turndapage.wear.watchface.watchfacedarko;

import android.content.Context;

public class ResourceHelper {
    public static int getResourceId(Context context, String pVariableName, String pResourcename, String pPackageName)
    {
        try {
            return context.getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getCurrentBackground(Context context) {
        String[] backgroundResources = context.getResources().getStringArray(R.array.backgrounds);
        int currentBackground = SettingsUtil.GetCurrentBackground(context);

        return ResourceHelper.getResourceId(context, backgroundResources[currentBackground],
                "drawable", context.getPackageName());
    }

    /*public static int getCurrentStyle(Context context) {
        String[] styleResources = context.getResources().getStringArray(R.array.styles);
        int currentStyle = SettingsUtil.GetCurrentStyle(context);

        return ResourceHelper.getResourceId(context, styleResources[currentStyle],
                "drawable", context.getPackageName());
    }*/
}
