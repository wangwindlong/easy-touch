/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.template.material.menu.utils;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.view.MenuItem;

import xyz.template.material.menu.R;
import xyz.template.material.menu.provider.ScheduleContract;

import static xyz.template.material.menu.utils.LogUtils.LOGD;
import static xyz.template.material.menu.utils.LogUtils.makeLogTag;


/**
 * Helper class for dealing with common actions to take on a session.
 */
public final class SessionsHelper {

    private static final String TAG = makeLogTag(SessionsHelper.class);

    private final Activity mActivity;

    public SessionsHelper(Activity activity) {
        mActivity = activity;
    }


    public Intent createShareIntent(int messageTemplateResId, String title, String hashtags,
            String url) {
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(mActivity)
                .setType("text/plain")
                .setText(mActivity.getString(messageTemplateResId,
                        title, UIUtils.getSessionHashtagsString(hashtags), " " + url));
        return builder.getIntent();
    }

    public void tryConfigureShareMenuItem(MenuItem menuItem, int messageTemplateResId,
            final String title, String hashtags, String url) {
        // TODO: remove
    }

    public void shareSession(Context context, int messageTemplateResId, String title,
            String hashtags, String url) {
        /* [ANALYTICS:EVENT]
         * TRIGGER:   Share a session.
         * CATEGORY:  'Session'
         * ACTION:    'Shared'
         * LABEL:     session title/subtitle. Sharing details NOT collected.
         * [/ANALYTICS]
         */
//        AnalyticsManager.sendEvent("Session", "Shared", title, 0L);
        context.startActivity(Intent.createChooser(
                createShareIntent(messageTemplateResId, title, hashtags, url),
                context.getString(R.string.title_share)));
    }

    public void setSessionStarred(Uri sessionUri, boolean starred, String title) {
        LOGD(TAG, "setSessionStarred uri=" + sessionUri + " starred=" +
                starred + " title=" + title);
        String sessionId = ScheduleContract.Sessions.getSessionId(sessionUri);
        Uri myScheduleUri = ScheduleContract.MySchedule.buildMyScheduleUri(mActivity);

        AsyncQueryHandler handler =
                new AsyncQueryHandler(mActivity.getContentResolver()) {
                };
        final ContentValues values = new ContentValues();
        values.put(ScheduleContract.MySchedule.SESSION_ID, sessionId);
        values.put(ScheduleContract.MySchedule.MY_SCHEDULE_IN_SCHEDULE, starred?1:0);
        handler.startInsert(-1, null, myScheduleUri, values);

        /* [ANALYTICS:EVENT]
         * TRIGGER:   Add or remove a session from the schedule.
         * CATEGORY:  'Session'
         * ACTION:    'Starred' or 'Unstarred'
         * LABEL:     session title/subtitle
         * [/ANALYTICS]
         */
//        AnalyticsManager.sendEvent(
//                "Session", starred ? "Starred" : "Unstarred", title, 0L);

        // Because change listener is set to null during initialization, these
        // won't fire on pageview.
//        mActivity.sendBroadcast(ScheduleWidgetProvider.getRefreshBroadcastIntent(mActivity, false));

        // Request an immediate user data sync to reflect the starred user sessions in the cloud
//        SyncHelper.requestManualSync(AccountUtils.getActiveAccount(mActivity), true);

    }
}
