package ca.bischke.apps.minesweeper;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "Minesweeper";
    private Board board;
    private int time;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemUI();
        startNewGame();
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus)
        {
            hideSystemUI();
        }
    }

    private void hideSystemUI()
    {
        if (Build.VERSION.SDK_INT >= 19)
        {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void buttonNewGame(View view)
    {
        startNewGame();
    }

    private void startNewGame()
    {
        createBoardLayout(10, 10);
        setMineDisplay();
        stopTimer();
        time = 0;
        setTimerDisplay();
    }

    private void createBoardLayout(int columns, int rows)
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int buttonSize = displayWidth / (columns + 1);

        board = new Board(this, columns, rows, buttonSize);
        setBoardClickListeners();

        LinearLayout boardLayout = findViewById(R.id.board_layout);
        boardLayout.removeAllViewsInLayout();
        boardLayout.addView(board);
    }

    private void setBoardClickListeners()
    {
        Cell[][] cells = board.getCells();

        for (int i = 0; i < board.getColumns(); i++)
        {
            for (int j = 0; j < board.getRows(); j++)
            {
                final Cell cell = cells[i][j];

                cell.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Cell cell = ((Cell)view);

                        if (board.isFirstTurn())
                        {
                            startTimer();
                            board.placeMines(cell);
                            board.calculateNearbyMines();
                            board.setFirstTurn(false);
                        }

                        if (!board.isGameOver())
                        {
                            if (!cell.isFlag())
                            {
                                cell.setActive(true);

                                if (cell.isMine())
                                {
                                    stopTimer();
                                    board.lose();
                                }
                                else
                                {
                                    if (cell.getNearbyMines() == 0)
                                    {
                                        board.activateNeighbours(cell);
                                    }
                                    else
                                    {
                                        cell.displayText();
                                    }

                                    cell.setActive(false);
                                    cell.setColor(R.color.colorInactive);

                                    if (board.allMinesFound())
                                    {
                                        stopTimer();
                                        board.win();
                                    }
                                }
                            }
                        }
                    }
                });

                cell.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View view)
                    {
                        Cell cell = ((Cell)view);

                        if (cell.isActive())
                        {
                            cell.setFlag(!cell.isFlag());
                        }

                        return true;
                    }
                });
            }
        }
    }

    private void setMineDisplay()
    {
        TextView minesView = findViewById(R.id.text_mines_value);
        minesView.setText(String.format("%03d", board.getMines()));
    }

    private void setTimerDisplay()
    {
        TextView timerView = findViewById(R.id.text_timer_value);
        timerView.setText(String.format("%03d", time));
    }

    private final Handler handler = new Handler();
    private final Runnable timer = new Runnable()
    {
        @Override
        public void run()
        {
            timerTick();
            handler.postDelayed(this, 1000);
        }
    };

    private void startTimer()
    {
        time = 0;
        setTimerDisplay();
        handler.removeCallbacks(timer);
        handler.postDelayed(timer, 1000);
    }

    private void stopTimer()
    {
        handler.removeCallbacks(timer);
    }

    private void timerTick()
    {
        time += 1;
        TextView timerView = findViewById(R.id.text_timer_value);
        timerView.setText(String.format("%03d", time));
    }
}
