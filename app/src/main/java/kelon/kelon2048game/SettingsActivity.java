package kelon.kelon2048game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

import cn.waps.AppConnect;

/**
 * Created by Administrator on 2016/3/10.
 */
public class SettingsActivity extends PreferenceActivity {

    final private static String TAG = "SettingsActivity";

    final private static String KEY_RESET_HIGH_SCORE = "reset_high_score";
    private Preference mResetHighScorePreference;

    final private static String KEY_USER_FEEDBACK = "user_feedback";
    private Preference mUserFeedbackPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preference);

        mResetHighScorePreference = (Preference) findPreference(KEY_RESET_HIGH_SCORE);
        mUserFeedbackPreference = (Preference) findPreference(KEY_USER_FEEDBACK);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mResetHighScorePreference) {
            Log.d(TAG, "reset high score ...");

            resetHighScoreConfirm();
            return true;
        } else if (preference == mUserFeedbackPreference) {
            /*
            用户反馈接口 begin
            */
            AppConnect.getInstance(this).showFeedback(this);
            /*
            用户反馈接口 end
             */
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    /**
     * 是否重置最高分对话框
     */
    private void resetHighScoreConfirm() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.reset_high_score_title).setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = Config.mSp.edit();
                editor.putInt(Config.KEY_HIGH_SCROE, 0);
                editor.commit();
                Game.getGameActivity().setScore(0, 1);
            }
        }).setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
