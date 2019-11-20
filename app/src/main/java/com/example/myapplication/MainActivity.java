package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.oppwa.mobile.connect.checkout.dialog.CheckoutActivity;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSettings;
import com.oppwa.mobile.connect.checkout.meta.CheckoutSkipCVVMode;
import com.oppwa.mobile.connect.exception.PaymentError;
import com.oppwa.mobile.connect.provider.Connect;
import com.oppwa.mobile.connect.provider.Transaction;
import com.oppwa.mobile.connect.provider.TransactionType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CheckoutIdRequestListener, PaymentStatusRequestListener {

    String checkoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            resourcePath = savedInstanceState.getString(STATE_RESOURCE_PATH);
        }
        Button openBtn = findViewById(R.id.openBtn);
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCheckoutId(getString(R.string.checkout_ui_callback_scheme));
            }
        });
    }


    @Override
    public void onErrorOccurred() {
        hideProgressDialog();
        showAlertDialog(R.string.error_message);
    }


    @Override
    public void onPaymentStatusReceived(String paymentStatus) {
        hideProgressDialog();

        if ("true".equals(paymentStatus)) {
            showAlertDialog(R.string.message_successful_payment);
            return;
        }

        showAlertDialog(R.string.message_unsuccessful_payment);
    }

    private String STATE_RESOURCE_PATH = "STATE_RESOURCE_PATH";

    protected String resourcePath = null;


    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(STATE_RESOURCE_PATH, resourcePath);
    }

    protected void onResume() {
        super.onResume();
        if (resourcePath != null && hasCallbackScheme(getIntent())) {
            requestPaymentStatus(resourcePath, this);
        }
    }


    protected Boolean hasCallbackScheme(Intent intent) {
        String scheme = intent.getScheme();

        return getString(R.string.checkout_ui_callback_scheme).equals(scheme) ||
                getString(R.string.payment_button_callback_scheme).equals(scheme) ||
                getString(R.string.custom_ui_callback_scheme).equals(scheme);
    }

    public void onCheckoutIdReceived(String checkoutId, String url) {
//        super.onCheckoutIdReceivedved(checkoutId = checkoutId)
        this.checkoutId = checkoutId;
        Log.e("checkoutId", checkoutId);
        Log.e("callBack", url);
        hideProgressDialog();
        openCheckoutUI(checkoutId, "http://salahalimohamed.website/tmajog/api/v1/user/callback");
    }

    private PaymentStatusRequestListener listener = null;


    public void requestPaymentStatus(String resourcePath, PaymentStatusRequestListener listener) {
        showProgressDialog();
        this.listener = listener;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://salahalimohamed.website/tmajog/api/v1/user/paymentStatus/" + checkoutId
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    Log.d("RequestPaymentStatus", "RequestPaymentStatus " + response);

                    hideProgressDialog();
                    JSONObject r = new JSONObject(response);
                    Log.e("RequestPaymentStatus", "RequestPaymentStatus " + r);
//
//                    JSONObject result = r.getJSONObject("result");
//                    String description = result.getString("description");
//                    Toast.makeText(PaymaenActivity.this, "description" + description, Toast.LENGTH_SHORT).show();
//                    Log.d("description", "description " + description);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("KHH", "JSONException " + e);

                    hideProgressDialog();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Log.d("KHH", "VolleyError " + error);


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap();
                //map.put("amount",amount+"");


                return map;

            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();

                headers.put("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImM2OTMxZDNkM2U3ZTViYzEwZDg0NDhmNDBlODQ3NTBmYWRmNGI5MTNmODA5NGQ2NmM1ZjhlMDAwYjdmYzgxM2MwODNmZDdjNGRhMjk4MzczIn0.eyJhdWQiOiIxIiwianRpIjoiYzY5MzFkM2QzZTdlNWJjMTBkODQ0OGY0MGU4NDc1MGZhZGY0YjkxM2Y4MDk0ZDY2YzVmOGUwMDBiN2ZjODEzYzA4M2ZkN2M0ZGEyOTgzNzMiLCJpYXQiOjE1Njc5OTE0NDcsIm5iZiI6MTU2Nzk5MTQ0NywiZXhwIjoxNTk5NjEzODQ3LCJzdWIiOiIyIiwic2NvcGVzIjpbXX0.Tfw3ex_BnGdr26Vr4U9X2jcsBa2kKddf8xf-Go0kALnQn1PJpqJuXoxou9WjRtODtRUDvwPoW3U4vn0EpTzZVU6udBxi9J7MaiDqKL3QTlt1OHLoby9T8pSoHMl0PMTlfg28mSthoAf8O0jijaO4Nb1_btKzcTS5-dro2g_jATTmw_RuVQGsG1nXgHvUm6H3hlQyA8WNA17OraOUzOk8oadTXDcT5X7aO5avk8skxLH_rA9-4FfgyzVY_HGSxFmbva3LJ0KCVkXWt9IbkdssBd2L3f0kkc8UkuC3tL5SioG_IjaO1lkmdL6bR_LdD9gELe1V9u1aJR6wab3LjrEh1zcXVaiJfEUVwJuMNs3PQ6-BaUVbcKQTo98MtrgmnoUGNCkBcFqINPIBxiVo3EfK_pajuHpQx6X83Gp4XakXqG6lu4hyPRWyEUvJXeJPM6t3ElAs6jffbnOz9p3sD53NtCpbKeC4v7LVcwxfGTYY4cjei0ShJyhsxPT05Lx6JZ564Rm4QTRsMaSwr262y1X6pe0vMGBk4TcA5FZ5IbbzD3-pmxE9H-INiLf2kpMX93WH6cd1vei16mvjcO8IGyR3bI2_omKPHmRD3qxAYxavMlpStVR7UAA35zBuS5eVqIJne4xP6f0Ekl9q9doIhBhz9LgmuCJ1_jyoXOgZsYSgDbU");
                headers.put("lang", "ar");
                return headers;

            }

        };

        MyApplication.getInstance().addToRequestQueue(stringRequest);


    }

    private void openCheckoutUI(String checkoutId, String url) {

        Log.e("checkout***", "chechout");
        CheckoutSettings checkoutSettings = createCheckoutSettings(checkoutId, url);

        /* Set componentName if you want to receive callbacks from the checkout */
        ComponentName componentName = new ComponentName(getPackageName(), CheckoutBroadcastReceiver.class.getName());

        /* Set up the Intent and start the checkout activity. */
        Intent intent = checkoutSettings.createCheckoutActivityIntent(this, componentName);

        Log.e("ddddd", CheckoutActivity.REQUEST_CODE_CHECKOUT + "");

        startActivityForResult(intent, CheckoutActivity.REQUEST_CODE_CHECKOUT);
    }

    protected CheckoutSettings createCheckoutSettings(
            String checkoutId,
            String callbackScheme
    ) {
        return new CheckoutSettings(
                checkoutId, Constants.Config.PAYMENT_BRANDS,
                Connect.ProviderMode.TEST
        )
                .setSkipCVVMode(CheckoutSkipCVVMode.FOR_STORED_CARDS)
//                .setShopperResultUrl(callbackScheme);
                .setShopperResultUrl("checkoutui://result");
        //                .setGooglePayPaymentDataRequest(getGooglePayRequest())
    }

    protected void requestCheckoutId(String callbackScheme) {

        Log.e("checkout", "chechout");
        new CheckoutIdRequestAsyncTask(500, this);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* Override onActivityResult to get notified when the checkout process is done. */
        if (requestCode == CheckoutActivity.REQUEST_CODE_CHECKOUT) {

            if (resultCode == CheckoutActivity.RESULT_OK) {
                Log.e("resourcePath", "يبيبيب");
                /* Transaction completed. */
                Transaction transaction = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_TRANSACTION);
                resourcePath = data.getStringExtra(CheckoutActivity.CHECKOUT_RESULT_RESOURCE_PATH);
                assert resourcePath != null;
                Log.e("resourcePath", resourcePath);

                Log.e("transaction", transaction.getTransactionType().toString());
                /* Check the transaction type. */
                assert (transaction != null);
                if (transaction.getTransactionType() == TransactionType.ASYNC) {
                    /* Check the status of synchronous transaction. */
                    requestPaymentStatus(resourcePath, this);
                    hideProgressDialog();
                } else {
                    /* Asynchronous transaction is processed in the onNewIntent(). */
                    onNewIntent(data);
                    hideProgressDialog();
                }
            } else if (resultCode == CheckoutActivity.RESULT_CANCELED) {
                hideProgressDialog();
            } else if (resultCode == CheckoutActivity.RESULT_ERROR) {
                hideProgressDialog();
                PaymentError error = data.getParcelableExtra(CheckoutActivity.CHECKOUT_RESULT_ERROR);
                assert error != null;
                Log.e("resourcePath", error.getErrorMessage());
                showAlertDialog(R.string.error_message);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        /* Check if the intent contains the callback scheme. */
        if (resourcePath != null && hasCallbackScheme(intent)) {
            requestPaymentStatus(resourcePath, this);
        }
    }


    private ProgressDialog progressDialog = null;

    protected void showProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }

        progressDialog.setMessage(getString(R.string.progress_message_payment_status));
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog == null) {
            return;
        }

        progressDialog.dismiss();
    }

    protected void showAlertDialog(String message) {
        new AlertDialog.Builder(this).setMessage(message)
                .setPositiveButton(R.string.button_ok, null)
                .setCancelable(false)
                .show();
    }


    private void showAlertDialog(int messageId) {
        showAlertDialog(getString(messageId));
    }

}
