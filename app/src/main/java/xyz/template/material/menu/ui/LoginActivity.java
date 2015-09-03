package xyz.template.material.menu.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yzxtcp.UCSManager;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.template.material.menu.Config;
import xyz.template.material.menu.ImApplication;
import xyz.template.material.menu.MainActivity;
import xyz.template.material.menu.R;
import xyz.template.material.menu.model.LoginData;
import xyz.template.material.menu.utils.NetUtils;
import xyz.template.material.menu.utils.PrefUtils;
import xyz.template.material.menu.volley.toolbox.GsonRequest;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    private static final int TIMEOUT_IN_MILLIONS = 15000;

    private static final String LOGIN_URL_PRE = "http://imas.ucpaas.com/user/login.do?phone=";
    private static final String REG_URL_PRE = "http://imas.ucpaas.com/user/reg.do?";
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
//    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(1);
                    return true;
                }
                return false;
            }
        });
        LoginData loginData = PrefUtils.getUserInfo(this);
        mEmailView.setText(loginData.getPhone());
        mPasswordView.setText(loginData.getNickname());

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(1);
            }
        });

        findViewById(R.id.email_register_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin(0);
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(int type) {
        if (isLoading) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else  {
            if (type == 0) {
                if (!isEmailValid(email)) {
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    focusView = mEmailView;
                    cancel = true;
                }
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

//            AndroidHttpClient httpClient = AndroidHttpClient.newInstance("");
            RequestQueue queue = ImApplication.getRequestQueue();
//            String uri = String.format(Config.BaseUri + "login.do?phone=%1$s&param2=%2$s", email, password);
            String urlStr;
            if (type == 1) { //登陆
                urlStr = String.format(Config.BaseUri + "login.do?phone=%1$s", email);
            } else { //注册
                urlStr = String.format(Config.BaseUri + "reg.do?phone=%1$s&nickname=%2$s", email, password);
            }
            Log.d("wangyl", "urlstr=" + urlStr);

            isLoading = true;
            GsonRequest<LoginData> myReq = new GsonRequest<LoginData>(Request.Method.GET,
                        urlStr,
                        LoginData.class,
                        createMyReqSuccessListener(type),
                        createMyReqErrorListener(type));
            queue.add(myReq);
        }
    }

    private Response.Listener<LoginData> createMyReqSuccessListener(final int type) {
        final String str = type == 0 ? "注册" : "登陆";
        return new Response.Listener<LoginData>() {
            @Override
            public void onResponse(final LoginData response) {
                Log.d("wangyl", "onResponse response=" + response.toString());
                boolean needToConnect = false;
                if ("0". equals(response.getResult())) {
                    needToConnect = true;
//                    Toast.makeText(LoginActivity.this, str + "成功,正在登陆...", Toast.LENGTH_SHORT).show();
                } else {
                    String errMsg = "";
                    switch (Integer.parseInt(response.getResult())) {
                        case -1:
                            needToConnect = true;
                            break;
                        case -2:
                        default:
                            showProgress(false);
                            isLoading = false;
                            errMsg = "未知错误,请联系管理员";
                            Toast.makeText(LoginActivity.this, str + "失败！失败原因：" + errMsg, Toast.LENGTH_SHORT).show();
                            break;
                    }
//                    mPasswordView.setError(getString(R.string.error_incorrect_password));
//                    mPasswordView.requestFocus();
                }
                if (needToConnect) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            connect(response);
                        }
                    });
                }
            }
        };
    }


    private Response.ErrorListener createMyReqErrorListener(int type) {
        final String str = type == 0 ? "注册" : "登陆";
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                mTvResult.setText(error.getMessage());
                showProgress(false);
                isLoading = false;
                Toast.makeText(LoginActivity.this, str + "失败！请检查网络是否可用", Toast.LENGTH_SHORT).show();
                Log.d("wangyl", "onErrorResponse error="+error.getMessage());
            }
        };
    }

    private void connect(final LoginData loginData) {
        UCSManager.connect(loginData.getToken(), new ILoginListener() {
            @Override
            public void onLogin(final UcsReason arg0) {
                Log.d("wangyl", "onLogin result=" + arg0.getReason() +",isLoading="+isLoading);
                if (isLoading) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgress(false);
                            isLoading = false;

                            if (arg0.getReason() == 0) {
                                //登入成功
                                PrefUtils.markTosLoged(LoginActivity.this, loginData, true);
                                Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                //登入失败
                                Toast.makeText(LoginActivity.this, "登陆失败！失败原因：" + arg0.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email != null && !TextUtils.isEmpty(email) && email.length() == 11 && isNumber(email);
    }

    private boolean isNumber(String str) {
        try {
            double d = Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final int mType;

        UserLoginTask(String email, String password, int type) {
            mEmail = email;
            mPassword = password;
            mType = type;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String urlStr;
                if (mType == 1) { //登陆
                    urlStr = LOGIN_URL_PRE + mEmail;
                } else { //注册
                    urlStr = REG_URL_PRE + "phone=" + mEmail
                            + "&nickname=" + mPassword;
                }
                Log.d("wangyl", "urlstr=" + urlStr);
                String result = doGet(urlStr, mType);

                if (result != null) {
                    parseGetTokenJson(result);
                }
            } catch (Exception e) {
                Log.d("wangyl", "doInBackground exception=" + e.toString());
                return false;
            }

//            for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    return pieces[1].equals(mPassword);
//                }
//            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {
//                PrefUtils.markTosLoged(LoginActivity.this);
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    private void parseGetTokenJson(String json) {
        try {
            JSONTokener jsonParser = new JSONTokener(json);
            JSONObject tokens = (JSONObject) jsonParser.nextValue();
            String result = tokens.getString("result");
            if (result.equals("0")) {
                Log.d("wangyl", "result=" + result);
            } else {
                Log.d("wangyl", "register failed! errorcode=" + result);
//                Toast.makeText(this, "register failed! errorcode="+result, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String doGet(String urlStr, int type) {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
            conn.setRequestMethod(type == 1 ? "GET" : "POST");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "text/html");
            conn.setRequestProperty("charset", "utf-8");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[128];

                while ((len = is.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                return baos.toString();
            } else {
                throw new RuntimeException(" responseCode is not 200 ... "
                        + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
            }
            conn.disconnect();
        }

        return null;

    }
}

