package cz.zcu.kiv.eeg.mobile.base.archetypes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import cz.zcu.kiv.eeg.mobile.base.R;
import cz.zcu.kiv.eeg.mobile.base.data.ServiceState;
import cz.zcu.kiv.eeg.mobile.base.data.Values;

/**
 * Activity with capability of recognizing CommonService state.
 * If CommonService was set and activity is recreated, progress dialog is recreated as well.
 *
 * @author Petr Miko
 */
public class CommonActivity extends Activity {

    /**
     * Assigned common service (AsyncTask actually).
     */
    public static CommonService service;
    /**
     * Progress dialog informing of common service state.
     */
    protected volatile ProgressDialog progressDialog;
    /**
     * Message to be shown in progress dialog.
     */
    private String progressMessage;

    /**
     * Actions performed upon activity creation.
     * @param savedInstanceState information bundle from previously destroyed instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        synchronized (CommonActivity.class) {
            if (service != null) {
                service.setActivity(this);

                if (savedInstanceState != null) {
                    progressMessage = savedInstanceState.getString("progressMsg");
                    progressDialog = ProgressDialog.show(CommonActivity.this,
                            getString(R.string.working), progressMessage, true, false);

                }
            }
        }
    }

    /**
     * Sets progress dialog.
     * @param state new state of progress dialog
     */
    public void changeProgress(ServiceState state) {
        changeProgress(state, null);
    }

    /**
     * Sets progress dialog and its message.
     * @param state new state of progress dialog
     * @param message message to be displayed
     */
    public void changeProgress(final ServiceState state, final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case RUNNING:
                        progressMessage = message;
                        progressDialog = ProgressDialog.show(CommonActivity.this,
                                getString(R.string.working), message, true, false);
                        break;
                    case INACTIVE:
                    case DONE:
                        synchronized (CommonActivity.class) {
                            service = null;
                            progressMessage = null;
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        break;
                    case ERROR:
                        showAlert(message);
                    default:
                        break;
                }
            }
        });
    }

    /**
     * Display alert dialog.
     * @param alert message
     */
    public void showAlert(final String alert){
        showAlert(alert, false);
    }

    /**
     * Method for showing alert dialog with option, whether activity should be finished after confirmation.
     * @param alert alert message
     * @param closeActivity close activity on confirmation
     */
    public void showAlert(final String alert, final boolean closeActivity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(alert).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if(closeActivity){
                            finish();
                        }
                    }
                });
        builder.create().show();
    }

    /**
     * Actions performed on save stage (before activity is destroyed).
     * @param outState activity state information bundle
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (service != null && progressDialog != null) {
            outState.putString("progressMsg", progressMessage);
        }
    }

    /**
     * Actions performed when activity stops being visible on screen.
     */
    @Override
    protected void onPause() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onPause();
    }

    /**
     * Getter of credentials information bundle.
     * @return credentials preferences
     */
    protected SharedPreferences getCredentials() {
        return getSharedPreferences(Values.PREFS_CREDENTIALS, Context.MODE_PRIVATE);
    }

    /**
     * Getter of various information bundle (various as without obvious category).
     * @return  various information bundle
     */
    protected SharedPreferences getVarious() {
        return getSharedPreferences(Values.PREFS_VARIOUS, Context.MODE_PRIVATE);
    }
}