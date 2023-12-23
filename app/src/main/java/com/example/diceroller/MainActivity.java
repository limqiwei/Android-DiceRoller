package com.example.diceroller;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.diceroller.Dice.Dice;

import java.util.Random;

/**
 * Dice Roller class that allows user to spin the dice and see if it lands on the lucky side.
 */
public class MainActivity extends AppCompatActivity {

    private static final int DEFAULT_LAYOUT_BG_COLOR = R.color.roll_layout_bg_color;
    private static final int NUMBER_OF_SIDES = 6;
    private static final int LUCKY_SIDE = 6;
    private static final int DEFAULT_FADE_DURATION = 250;
    private static final int DEFAULT_EVALUATION_DELAY = 250;

    Dice dice;
    TextView rollResultTv;
    ImageView diceIv;
    Button rollBtn;
    ConstraintLayout rollDiceLayout;

    TextView versionTv;


    static final String STATE_ROLL_RESULT = "roll_result";
    static final String STATE_DICE_SIDE = "dice_side";
    static final String STATE_LAYOUT_BG = "layout_bg";

    static final String PARCEABLE_DICE = "parceable_dice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize(savedInstanceState);
    }

    /**
     * Initializes and map view elements to variables, attach listeners and prepares the dice roller initial state.
     */
    protected void initialize(Bundle savedInstanceState) {
        this.rollDiceLayout = findViewById(R.id.rollDiceLayout);
        this.diceIv = findViewById(R.id.diceIv);
        this.rollBtn = findViewById(R.id.rollBtn);
        this.rollResultTv = findViewById(R.id.rollResultTv);
        this.versionTv = findViewById(R.id.versionTv);

        this.versionTv.setText(String.format(getString(R.string.version), BuildConfig.VERSION_NAME));


        this.dice = (savedInstanceState != null && savedInstanceState.getParcelable(MainActivity.PARCEABLE_DICE) != null) ? savedInstanceState.getParcelable(MainActivity.PARCEABLE_DICE) : new Dice(MainActivity.NUMBER_OF_SIDES);
        assert this.dice != null;
        this.dice.setLuckySide(MainActivity.LUCKY_SIDE);
        boolean rolledBefore = this.dice.getCurrentSide() != -1;
        if (rolledBefore) {
            // Restore on orientation changes
            int prevRolledSide = this.dice.getCurrentSide();
            updateDiceIv(prevRolledSide);
            evaluateDiceRoll(prevRolledSide, 0);
        } else {
            this.rollDiceLayout.setBackgroundColor(getColor(MainActivity.DEFAULT_LAYOUT_BG_COLOR));
            Random random = new Random();
            int initialSide = random.nextInt(MainActivity.NUMBER_OF_SIDES) + 1;
            this.updateDiceIv(initialSide);
        }


        this.rollBtn.setOnClickListener(v -> {
            Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
            this.diceIv.startAnimation(rotateAnimation);
            ConstraintLayout rollDiceLayout = this.rollDiceLayout;

            int fromColor = this.getLayoutBackgroundColor(rollDiceLayout);
            int toColor = getColor(MainActivity.DEFAULT_LAYOUT_BG_COLOR);
            this.fadeBackgroundColor(this.rollDiceLayout, fromColor, toColor);

            int rolledSide = this.dice.roll();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateDiceIv(rolledSide);
                    evaluateDiceRoll(rolledSide);
                }
            }, 250);
        });
    }

    /**
     * Check the result of the dice roll and show message based on the whether the result of the roll lands on a lucky side
     *
     * @param rolledSide
     */
    private void evaluateDiceRoll(int rolledSide, int evaluationDelay) {
        boolean lucky = rolledSide == this.dice.getLuckySide();

        int fromColor = this.getLayoutBackgroundColor(this.rollDiceLayout);
        int toColor = lucky ? getColor(R.color.green) : getColor(R.color.orange);
        this.fadeBackgroundColor(this.rollDiceLayout, fromColor, toColor);

        TextView rollResultTv = this.rollResultTv;

        if (this.dice.getCurrentSide() == -1) {
            String message = getString(R.string.no_roll_result);
            new Handler().postDelayed(() -> rollResultTv.setText(message), evaluationDelay);
        } else {
            if (lucky) {
                String message = String.format(getString(R.string.congratulations_message), this.dice.getLuckySide());
                new Handler().postDelayed(() -> rollResultTv.setText(message), evaluationDelay);
            } else {
                String message = getString(R.string.better_luck_next_time_message);
                new Handler().postDelayed(() -> rollResultTv.setText(message), evaluationDelay);
            }
        }

    }

    private void evaluateDiceRoll(int rolledSide) {
        evaluateDiceRoll(rolledSide, MainActivity.DEFAULT_EVALUATION_DELAY);
    }

    /**
     * Updates the background color of the view provided gently with fade effect
     *
     * @param fromColor
     * @param toColor
     * @param duration
     */
    protected void fadeBackgroundColor(View view, int fromColor, int toColor, int duration) {
        ObjectAnimator colorFade = ObjectAnimator.ofObject(view, "backgroundColor", new ArgbEvaluator(), fromColor, toColor);
        colorFade.setDuration(duration);
        colorFade.start();
    }

    /**
     * Helper method to get the background color of the layout provided
     *
     * @param layout
     * @return
     */
    protected int getLayoutBackgroundColor(View layout) {
        return ((ColorDrawable) layout.getBackground()).getColor();
    }

    /**
     * Overloaded method of fadeBackgroundColor with a default DEFAULT_FADE_DURATION
     *
     * @param view
     * @param colorFrom
     * @param colorTo
     */
    protected void fadeBackgroundColor(View view, int colorFrom, int colorTo) {
        this.fadeBackgroundColor(view, colorFrom, colorTo, MainActivity.DEFAULT_FADE_DURATION);
    }

    /**
     * Updates the image view of the dice to the correct drawable, as well as set contentDescription for better accessibility
     *
     * @param side
     */
    protected void updateDiceIv(int side) {
        int diceViewResourceId = this.getCurrentSideDrawable(this.dice);

        // UI Update
        this.diceIv.setImageResource(diceViewResourceId);

        // Accessibility Meta Data Update
        this.diceIv.setContentDescription(String.valueOf(side));
    }

    public int getCurrentSideDrawable(Dice dice) {
        int resourceId;
        int currentSide = dice.getCurrentSide();
        if (currentSide == 6) {
            resourceId = R.drawable.dice_6;
        } else if (currentSide == 5) {
            resourceId = R.drawable.dice_5;
        } else if (currentSide == 4) {
            resourceId = R.drawable.dice_4;
        } else if (currentSide == 3) {
            resourceId = R.drawable.dice_3;
        } else if (currentSide == 2) {
            resourceId = R.drawable.dice_2;
        } else {
            resourceId = R.drawable.dice_1;
        }
        return resourceId;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(MainActivity.PARCEABLE_DICE, this.dice);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


}