package kelon.kelon2048game;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.waps.AppConnect;

public class Game extends AppCompatActivity implements GameView.OnScoreChangeListener {

    final private static String TAG = "Game";

    private static Game mGame;

    /**
     * 分数
     */
    private TextView mGameScore;

    /**
     * 分数增量
     */
    private TextView mGameScoreAdd;

    /**
     * 最高分
     */
    private TextView mHighGameScore;

    /**
     * 游戏界面容器
     */
    private LinearLayout mGameContainer;

    private GameView mGameView;

    /**
     * 新游戏按钮
     */
    private Button mNewGameButton;

    private long mExitTime = 0;

    public Game() {
        mGame = this;
    }

    /**
     * 实现加分回调方法
     */
    @Override
    public void onScoreChangeListener() {
        Log.d(TAG, "onScoreChangeListener...");
        if (Config.SCORE_ADD != 0) {
            mGameScoreAdd.setText("+" + Config.SCORE_ADD);
            mGameScoreAdd.setVisibility(View.VISIBLE);
            //向上移动的动画
            ObjectAnimator moveUp = ObjectAnimator.ofFloat(mGameScoreAdd, "translationY", 0, -200f);
            //消失的动画
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mGameScoreAdd, "alpha", 1f, 0f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(1500);
            animatorSet.play(moveUp).with(fadeOut);
            animatorSet.start();
        }
    }

    public static Game getGameActivity() {
        return mGame;
    }

    public void setScore(int score, int flag) {

        switch (flag) {
            case 0:
                mGameScore.setText("" + score);
                break;
            case 1:
                mHighGameScore.setText("" + score);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        添加万普数据统计入口 begin
         */
        //360渠道
        AppConnect.getInstance("929e330a536be4ed053c1fd37043d06b", "360", this);
        /*
        添加万普数据统计入口 end
         */

        /*
        添加万普广告条 begin
         */
        LinearLayout adlayout = (LinearLayout) findViewById(R.id.AdLinearLayout);
        AppConnect.getInstance(this).showBannerAd(this, adlayout);
        /*
        添加万普广告条 end
         */

        /*
        卸载广告 (需要应用后台才会有效)begin
         */
        AppConnect.getInstance(this).initUninstallAd(this);
        /*
        卸载广告 (需要应用后台才会有效)end
         */


        mGameView = (GameView) findViewById(R.id.gameView);

        //一个窗口抖动的动画
        final CustomAnimation ca = new CustomAnimation();
        ca.setDuration(1000);
        mGameView.startAnimation(ca);
        //注册分数增加回调方法
        mGameView.setOnScoreChangeListener(this);

        mGameContainer = (LinearLayout) findViewById(R.id.container);

        mGameScore = (TextView) findViewById(R.id.score);
        mGameScore.setText("0");

        mHighGameScore = (TextView) findViewById(R.id.record);
        mHighGameScore.setText("" + Config.mSp.getInt(Config.KEY_HIGH_SCROE, 0));


        mGameScoreAdd = (TextView) findViewById(R.id.score_add);
        mGameScoreAdd.setText("+" + Config.SCORE_ADD);
        mGameScoreAdd.setVisibility(View.GONE);

        mNewGameButton = (Button) findViewById(R.id.new_game_button);
        mNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGameView != null) {
                    mGameView.startGame();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startSettingsActivity();
            return true;
        } else if (id == R.id.action_about) {
            startAboutMeActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startSettingsActivity() {
        Intent intent = new Intent();//"kelon.kelon2048game","kelon.kelon2048game.AboutMeActivity"
        intent.setClassName("kelon.kelon2048game", "kelon.kelon2048game.SettingsActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startAboutMeActivity() {
        Intent intent = new Intent();//"kelon.kelon2048game","kelon.kelon2048game.AboutMeActivity"
        intent.setClassName("kelon.kelon2048game", "kelon.kelon2048game.AboutMeActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*
        实现按两次返回键退出游戏功能
         */
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(getGameActivity(), R.string.quit_message, Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*
        添加万普数据统计入口 begin
         */
        AppConnect.getInstance(this).close();
        /*
        添加万普数据统计入口 end
         */
    }
}
