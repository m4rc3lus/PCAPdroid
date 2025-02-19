/*
 * This file is part of PCAPdroid.
 *
 * PCAPdroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PCAPdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PCAPdroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2020-21 - Emanuele Faranda
 */

package com.emanuelef.remote_capture;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.emanuelef.remote_capture.model.AppDescriptor;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class AppsResolver {
    private static final String TAG = "AppsResolver";
    private final Map<Integer, AppDescriptor> mApps;
    private final PackageManager mPm;
    private final Context mContext;

    public AppsResolver(Context context) {
        mApps = new HashMap<>();
        mContext = context;
        mPm = context.getPackageManager();

        initVirtualApps();
    }

    private void initVirtualApps() {
        final Drawable mVirtualAppIcon = ContextCompat.getDrawable(mContext, android.R.drawable.sym_def_app_icon);
        final Drawable mUnknownAppIcon = ContextCompat.getDrawable(mContext, android.R.drawable.ic_menu_help);

        // https://android.googlesource.com/platform/system/core/+/master/libcutils/include/private/android_filesystem_config.h
        // NOTE: these virtual apps cannot be used as a permanent filter (via addAllowedApplication)
        // as they miss a valid package name
        mApps.put(Utils.UID_UNKNOWN, new AppDescriptor(mContext.getString(R.string.unknown_app),
            mUnknownAppIcon, "unknown", Utils.UID_UNKNOWN, true)
            .setDescription(mContext.getString(R.string.unknown_app_info)));
        mApps.put(0, new AppDescriptor("Root",
                mVirtualAppIcon,"root", 0, true)
                .setDescription(mContext.getString(R.string.root_app_info)));
        mApps.put(1000, new AppDescriptor("Android",
                mVirtualAppIcon,"android", 1000, true)
                .setDescription(mContext.getString(R.string.android_app_info)));
        mApps.put(1013, new AppDescriptor("MediaServer",
                mVirtualAppIcon,"mediaserver", 1013, true));
        mApps.put(1020, new AppDescriptor("MulticastDNSResponder",
                 mVirtualAppIcon,"multicastdnsresponder", 1020, true));
        mApps.put(1021, new AppDescriptor("GPS",
                 mVirtualAppIcon,"gps", 1021, true));
        mApps.put(1051, new AppDescriptor("netd",
                 mVirtualAppIcon,"netd", 1051, true)
                 .setDescription(mContext.getString(R.string.netd_app_info)));
        mApps.put(9999, new AppDescriptor("Nobody",
                 mVirtualAppIcon,"nobody", 9999, true));
    }

    public static AppDescriptor resolve(PackageManager pm, String packageName, int pm_flags) {
        PackageInfo pinfo;

        try {
            pinfo = pm.getPackageInfo(packageName, pm_flags);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "could not retrieve package: " + packageName);
            return null;
        }

        return new AppDescriptor(pm, pinfo);
    }

    public @Nullable AppDescriptor get(int uid, int pm_flags) {
        AppDescriptor app = mApps.get(uid);

        if(app != null)
            return app;

        String[] packages = mPm.getPackagesForUid(uid);

        if((packages == null) || (packages.length < 1)) {
            Log.w(TAG, "could not retrieve package: uid=" + uid);
            return null;
        }

        String packageName = packages[0];

        app = resolve(mPm, packageName, pm_flags);

        if(app != null)
            mApps.put(uid, app);

        return app;
    }

    public void clear() {
        mApps.clear();
        initVirtualApps();
    }
}
