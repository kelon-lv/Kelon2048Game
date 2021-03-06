package kelon.kelon2048game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/18.
 */
public class GameView extends GridLayout implements View.OnTouchListener {

    final private static String TAG = "GameView";

    private int mGameLines = 4;
    private GameItem[][] mGameMatrix;
    private int[][] mGameMatrixHistory;
    private List<Point> mBlanks;
    private List<Integer> calList;
    private int keyItemNum = -1;

    //记录最高分
    private int mHighScore;

    private int startX, startY;
    private int endX, endY;

    public GameView(Context context) {
        super(context);
        Log.d(TAG,"GameView...");
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "GameView2...");

        initGameMatrix();
    }

    public void startNewGame(){
        initGameMatrix();
    }
    /**
     * 定义一个回调接口
     */
    public interface OnScoreChangeListener {
        public void onScoreChangeListener();
    }

    /**
     * 初始化一个接口变量
     */
    private OnScoreChangeListener mOnScoreChangeListener = null;

    /**
     * 回调接口的设置函数，用于外部注册
     * @param listener
     */
    public void setOnScoreChangeListener(OnScoreChangeListener listener){
        mOnScoreChangeListener = listener;
    }

    private void initGameMatrix() {
        removeAllViews();
        Config.SCROE = 0;

        mGameMatrix = new GameItem[mGameLines][mGameLines];
        mGameMatrixHistory = new int[mGameLines][mGameLines];
        mBlanks = new ArrayList<Point>();
        calList = new ArrayList<Integer>();
        mHighScore = Config.mSp.getInt(Config.KEY_HIGH_SCROE, 0);

        //设置Gridview的行列数
        setColumnCount(mGameLines);
        setRowCount(mGameLines);
        setOnTouchListener(this);

        //获取GridView的每一格子的宽高
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);

        int padding = getPaddingBottom();
        Log.d(TAG, "padding = " + padding);
        int cardSize = (metrics.widthPixels - 2 * padding) / mGameLines;
        initGameView(cardSize);
    }

    private void initGameView(int cardSize) {
        GameItem card;
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                card = new GameItem(getContext(), 0);
//                card.setNum(1024);
                addView(card, cardSize, cardSize);
                // 初始化GameMatrix全部为0 空格List为所有
                mGameMatrix[i][j] = card;
            }
        }

        addRandomNum();
        addRandomNum();
    }

    /**
     * 获取空格Item数组
     */
    private void getBlanks() {
        mBlanks.clear();
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == 0) {
                    mBlanks.add(new Point(i, j));
                }
            }
        }
    }

    private void addRandomNum() {
        getBlanks();
        if (mBlanks.size() > 0) {
            int randomNum = (int) (Math.random() * mBlanks.size());
            Point randomPoint = mBlanks.get(randomNum);
            mGameMatrix[randomPoint.x][randomPoint.y]
                    .setNum(Math.random() > 0.2d ? 2 : 4);
            animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
        }
    }

    /**
     * 生成动画
     *
     * @param target GameItem
     */
    private void animCreate(GameItem target) {
        ScaleAnimation sa = new ScaleAnimation(0.1f, 1, 0.1f, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(100);
        target.setAnimation(null);
        target.getItemView().startAnimation(sa);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int widthSize = MeasureSpec.getSize(widthSpec);

        setMeasuredDimension(widthSize, widthSize);

        Log.d(TAG,"widthSize = " + widthSize);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                saveHistoryMatrix();
                startX = (int) event.getX();
                startY = (int) event.getY();
                Config.SCORE_ADD = 0;

                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                endX = (int) event.getX();
                endY = (int) event.getY();
                judgeDirection(endX - startX, endY - startY);
                if (isMoved()) {
                    if (mOnScoreChangeListener != null){
                        mOnScoreChangeListener.onScoreChangeListener();
                    }
                    addRandomNum();
                    Game.getGameActivity().setScore(Config.SCROE, 0);
                    if (Config.SCROE > Config.mSp.getInt(Config.KEY_HIGH_SCROE, 0)){
                        Game.getGameActivity().setScore(Config.SCROE, 1);
                    }
                }
                checkCompleted();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 检查所有数字，看是否有满足条件的
     *
     * @return 0：结束  1：正常
     */
    private int checkNums() {
        getBlanks();
        if (mBlanks.size() == 0) {
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    if (j < mGameLines - 1) {
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i][j + 1].getNum()) {
                            return 1;
                        }
                    }
                    if (i < mGameLines - 1) {
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i + 1][j].getNum()) {
                            return 1;
                        }
                    }
                }
            }
            return 0;
        }
        return 1;
    }

    private void checkCompleted() {
        int result = checkNums();
        switch (result) {
            case 0: {
                if (Config.SCROE > mHighScore) {
                    SharedPreferences.Editor editor = Config.mSp.edit();
                    editor.putInt(Config.KEY_HIGH_SCROE, Config.SCROE);
                    editor.commit();
                    Game.getGameActivity().setScore(Config.SCROE, 1);
                    Config.SCROE = 0;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.game_over).setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startGame();
                    }
                }).create().show();

            }
            break;
            case 1:
                break;
            default:
                break;
        }
    }

    public void startGame() {
        initGameMatrix();
    }

    private void saveHistoryMatrix() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                mGameMatrixHistory[i][j] = mGameMatrix[i][j].getNum();
            }
        }
    }

    private boolean isMoved() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() != mGameMatrixHistory[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }

    private void judgeDirection(int offsetX, int offsetY) {
        if (Math.abs(offsetX) > Math.abs(offsetY)) {
            if (offsetX > 0) {
                swipeRight();
            } else {
                swipeLeft();
            }
        } else {
            if (offsetY > 0) {
                swipeDown();
            } else {
                swipeUp();
            }
        }
    }

    private void swipeRight() {
        Log.d(TAG, "swipeRight...");
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) {
                    if (keyItemNum == -1) {
                        keyItemNum = currentNum;
                    } else {
                        if (keyItemNum == currentNum) {
                            calList.add(keyItemNum * 2);
                            Config.SCROE += keyItemNum * 2;
                            Config.SCORE_ADD += keyItemNum * 2;
                            keyItemNum = -1;
                        } else {
                            calList.add(keyItemNum);
                            keyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (keyItemNum != -1) {
                calList.add(keyItemNum);
            }
            // 改变Item值
            for (int j = 0; j < mGameLines - calList.size(); j++) {
                mGameMatrix[i][j].setNum(0);
            }
            int index = calList.size() - 1;
            for (int m = mGameLines - calList.size(); m < mGameLines; m++) {
                mGameMatrix[i][m].setNum(calList.get(index));
                index--;
            }
            // 重置行参数
            keyItemNum = -1;
            calList.clear();

            index = 0;
        }
    }


    private void swipeLeft() {
        Log.d(TAG, "swipeLeft...");
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) {
                    if (keyItemNum == -1) {
                        keyItemNum = currentNum;
                    } else {
                        if (keyItemNum == currentNum) {
                            calList.add(keyItemNum * 2);
                            Config.SCROE += keyItemNum * 2;
                            Config.SCORE_ADD += keyItemNum * 2;
                            keyItemNum = -1;
                        } else {
                            calList.add(keyItemNum);
                            keyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (keyItemNum != -1) {
                calList.add(keyItemNum);
            }
            for (int j = 0; j < calList.size(); j++) {
                mGameMatrix[i][j].setNum(calList.get(j));
            }
            for (int m = calList.size(); m < mGameLines; m++) {
                mGameMatrix[i][m].setNum(0);
            }
            keyItemNum = -1;
            calList.clear();
        }
    }

    private void swipeUp() {
        Log.d(TAG, "swipeUp...");
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[j][i].getNum();
                if (currentNum != 0) {
                    if (keyItemNum == -1) {
                        keyItemNum = currentNum;
                    } else {
                        if (keyItemNum == currentNum) {
                            calList.add(keyItemNum * 2);
                            Config.SCROE += keyItemNum * 2;
                            Config.SCORE_ADD += keyItemNum * 2;
                            keyItemNum = -1;
                        } else {
                            calList.add(keyItemNum);
                            keyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (keyItemNum != -1) {
                calList.add(keyItemNum);
            }
            // 改变Item值
            for (int j = 0; j < calList.size(); j++) {
                mGameMatrix[j][i].setNum(calList.get(j));
            }
            for (int m = calList.size(); m < mGameLines; m++) {
                mGameMatrix[m][i].setNum(0);
            }
            // 重置行参数
            keyItemNum = -1;
            calList.clear();
        }
    }

    private void swipeDown() {
        Log.d(TAG, "swipeDown...");

        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[j][i].getNum();
                if (currentNum != 0) {
                    if (keyItemNum == -1) {
                        keyItemNum = currentNum;
                    } else {
                        if (keyItemNum == currentNum) {
                            calList.add(keyItemNum * 2);
                            Config.SCROE += keyItemNum * 2;
                            Config.SCORE_ADD += keyItemNum * 2;
                            keyItemNum = -1;
                        } else {
                            calList.add(keyItemNum);
                            keyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (keyItemNum != -1) {
                calList.add(keyItemNum);
            }
            // 改变Item值
            for (int j = 0; j < mGameLines - calList.size(); j++) {
                mGameMatrix[j][i].setNum(0);
            }
            int index = calList.size() - 1;
            for (int m = mGameLines - calList.size(); m < mGameLines; m++) {
                mGameMatrix[m][i].setNum(calList.get(index));
                index--;
            }
            // 重置行参数
            keyItemNum = -1;
            calList.clear();
            index = 0;
        }
    }
}
