package kelon.kelon2048game;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/2/18.
 */
public class GameItem extends FrameLayout {
    // 数字Title
    private TextView tvNum;

    // Item显示数字
    private int cardShowNum;

    // 数字Title LayoutParams
    private LayoutParams params;

    public GameItem(Context context, int cardShowNum) {
        super(context);
        this.cardShowNum = cardShowNum;
        //初始化Item
        initCardItem();
    }

    public GameItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化Item
        initCardItem();
    }

    public GameItem(Context context) {
        super(context);
        //初始化Item
        initCardItem();
    }

    private void initCardItem() {

//        setBackgroundColor(Color.GRAY);
        setBackgroundResource(R.color.board_bg);
        tvNum = new TextView(getContext());
        setNum(cardShowNum);

        tvNum.setTextSize(30);
        tvNum.setTextColor(Color.GRAY);
        tvNum.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        tvNum.setGravity(Gravity.CENTER);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.setMargins(10, 10, 10, 10);
        addView(tvNum, params);
    }

    public View getItemView() {
        return tvNum;
    }

    public int getNum() {
        return cardShowNum;
    }

    public void setNum(int num) {
        this.cardShowNum = num;
        if (num == 0) {
            tvNum.setText("");
        } else {
            if (num > 512) {
                tvNum.setTextSize(20);
            } else {
                tvNum.setTextSize(30);
            }
            tvNum.setText("" + num);
        }

        switch (num) {
            case 0:
                tvNum.setBackgroundResource(R.color.key0_bg);
                break;
            case 2:
                tvNum.setBackgroundResource(R.color.key2_bg);
                tvNum.setTextColor(Color.GRAY);
                break;
            case 4:
                tvNum.setBackgroundResource(R.color.key4_bg);
                tvNum.setTextColor(Color.GRAY);
                break;
            case 8:
                tvNum.setBackgroundResource(R.color.key8_bg);
                tvNum.setTextColor(Color.WHITE);
                break;
            case 16:
                tvNum.setBackgroundResource(R.color.key16_bg);
                tvNum.setTextColor(Color.WHITE);
                break;
            case 32:
                tvNum.setBackgroundResource(R.color.key32_bg);
                tvNum.setTextColor(Color.WHITE);
                break;
            case 64:
                tvNum.setBackgroundResource(R.color.key64_bg);
                tvNum.setTextColor(Color.WHITE);
                break;
            case 128:
                tvNum.setBackgroundResource(R.color.key128_bg);
                tvNum.setTextColor(Color.WHITE);
                break;
            case 256:
                tvNum.setBackgroundResource(R.color.key256_bg);
                tvNum.setTextColor(Color.WHITE);
                break;
            case 512:
                tvNum.setBackgroundResource(R.color.key512_bg);
                tvNum.setTextColor(Color.WHITE);
                break;
            case 1024:
                tvNum.setBackgroundResource(R.color.key1024_bg);
                tvNum.setTextColor(Color.WHITE);
                break;
            case 2048:
                tvNum.setBackgroundResource(R.color.key1024_bg);
                tvNum.setTextColor(Color.WHITE);
                break;
            default:
                tvNum.setBackgroundResource(R.color.key1024_bg);
                tvNum.setTextColor(Color.WHITE);
                break;
        }
    }
}
