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

package xyz.template.material.menu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import xyz.template.material.menu.MainActivity;
import xyz.template.material.menu.R;
import xyz.template.material.menu.ui.fragment.TalkDetailFragment;
import xyz.template.material.menu.utils.PrefUtils;

import static xyz.template.material.menu.utils.LogUtils.LOGD;
import static xyz.template.material.menu.utils.LogUtils.makeLogTag;


public class TalkActivity extends BaseActivity {
    private static final String TAG = makeLogTag(TalkActivity.class);
    private static final String SCREEN_LABEL = "Social";

    private String toId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                toId = bundle.getString("toid", "18667141169");
            }
            if (TextUtils.isEmpty(toId)) {
                toId = "18667141169";
            }
        }
        if (isFinishing()) {
            return;
        }

        setContentView(R.layout.activity_talk);

        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, TalkDetailFragment.newInstance(toId))
                    .commit();
        }

        LOGD("Tracker", SCREEN_LABEL);

        overridePendingTransition(0, 0);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_MAP;
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            super.onBackPressed();
        }
    }

}
