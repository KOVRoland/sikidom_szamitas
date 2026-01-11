package com.example.szultan2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView infoTextView;
    EditText commandInputEditText;
    Button rollDiceButton, restartGameButton;
    ImageView firstDiceImageView, secondDiceImageView;

    int playerOneTokens = 10;
    int playerTwoTokens = 10;
    int playerThreeTokens = 10;

    int sultanPlayerIndex = 0;
    int currentPlayerIndex = 0;
    int sultanCommand = 0;

    Random randomGenerator = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        infoTextView = findViewById(R.id.infoText);
        commandInputEditText = findViewById(R.id.commandInput);
        rollDiceButton = findViewById(R.id.rollButton);
        restartGameButton = findViewById(R.id.restartButton);
        firstDiceImageView = findViewById(R.id.dice1);
        secondDiceImageView = findViewById(R.id.dice2);

        rollDiceButton.setBackgroundColor(Color.parseColor("#FFD700"));
        restartGameButton.setBackgroundColor(Color.parseColor("#FF4500"));

        startNewGame();

        rollDiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playTurn();
            }
        });

        restartGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGame();
            }
        });
    }

    void startNewGame() {
        playerOneTokens = 10;
        playerTwoTokens = 10;
        playerThreeTokens = 10;
        sultanPlayerIndex = 0;
        currentPlayerIndex = 0;
        sultanCommand = 0;
        updateGameInfo();
    }

    void playTurn() {
        if (isGameOver()) return;

        if (sultanCommand == 0) {
            String commandText = commandInputEditText.getText().toString().trim();
            if (commandText.isEmpty()) {
                infoTextView.setText("Add meg a parancsot!");
                return;
            }
            int commandValue;
            try {
                commandValue = Integer.parseInt(commandText);
            } catch (NumberFormatException e) {
                infoTextView.setText("Érvénytelen szám!");
                return;
            }
            if (commandValue < 2 || commandValue > 12 || commandValue == 7) {
                infoTextView.setText("Érvénytelen parancs! (2–12, nem 7)");
                return;
            }
            sultanCommand = commandValue;
            infoTextView.setText("Parancs elfogadva: " + sultanCommand);
            return;
        }

        int firstDiceValue = randomGenerator.nextInt(6) + 1;
        int secondDiceValue = randomGenerator.nextInt(6) + 1;

        setDiceImage(firstDiceImageView, firstDiceValue);
        setDiceImage(secondDiceImageView, secondDiceValue);

        int diceSum = firstDiceValue + secondDiceValue;

        if (diceSum == 7) {
            sultanPlayerIndex = currentPlayerIndex;
            sultanCommand = 0;
            infoTextView.setText("7-et dobott! Új szultán: " + getPlayerName(sultanPlayerIndex));
        } else if (diceSum == sultanCommand) {
            addTokenToCurrentPlayer();
            removeTokenFromSultan();
            infoTextView.setText(getPlayerName(currentPlayerIndex) + " teljesítette a parancsot!");
        } else {
            removeTokenFromCurrentPlayer();
            addTokenToSultan();
            infoTextView.setText(getPlayerName(currentPlayerIndex) + " nem teljesítette!");
        }

        checkForPlayersWithZeroTokens();
        moveToNextPlayer();
        updateGameInfo();
    }

    void moveToNextPlayer() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % 3;
        } while (getTokensForPlayer(currentPlayerIndex) == 0 && !isGameOver());
    }

    void updateGameInfo() {
        StringBuilder infoBuilder = new StringBuilder();
        infoBuilder.append("Szultán: ").append(getPlayerName(sultanPlayerIndex)).append("\n");
        infoBuilder.append("Jelenlegi játékos: ").append(getPlayerName(currentPlayerIndex)).append("\n");

        infoBuilder.append(getPlayerName(0)).append(" zseton: ").append(playerOneTokens);
        if (playerOneTokens == 0) infoBuilder.append(" (KIESVE!)");
        infoBuilder.append("\n");

        infoBuilder.append(getPlayerName(1)).append(" zseton: ").append(playerTwoTokens);
        if (playerTwoTokens == 0) infoBuilder.append(" (KIESVE!)");
        infoBuilder.append("\n");

        infoBuilder.append(getPlayerName(2)).append(" zseton: ").append(playerThreeTokens);
        if (playerThreeTokens == 0) infoBuilder.append(" (KIESVE!)");
        infoBuilder.append("\n");

        infoTextView.setText(infoBuilder.toString());

        if (isGameOver()) {
            infoTextView.append("\nGyőztes: " + getPlayerName(getWinnerIndex()));
        }
    }

    String getPlayerName(int playerIndex) {
        return "Játékos " + (playerIndex + 1);
    }

    void setDiceImage(ImageView diceImageView, int diceValue) {
        int resourceId = getResources().getIdentifier("dice_" + diceValue, "drawable", getPackageName());
        diceImageView.setImageResource(resourceId);
        diceImageView.setContentDescription("Dobókocka értéke: " + diceValue);
    }

    int getTokensForPlayer(int playerIndex) {
        if (playerIndex == 0) return playerOneTokens;
        if (playerIndex == 1) return playerTwoTokens;
        return playerThreeTokens;
    }

    void addTokenToCurrentPlayer() {
        if (currentPlayerIndex == 0) playerOneTokens++;
        if (currentPlayerIndex == 1) playerTwoTokens++;
        if (currentPlayerIndex == 2) playerThreeTokens++;
    }

    void removeTokenFromCurrentPlayer() {
        if (currentPlayerIndex == 0 && playerOneTokens > 0) playerOneTokens--;
        if (currentPlayerIndex == 1 && playerTwoTokens > 0) playerTwoTokens--;
        if (currentPlayerIndex == 2 && playerThreeTokens > 0) playerThreeTokens--;
    }

    void addTokenToSultan() {
        if (sultanPlayerIndex == 0) playerOneTokens++;
        if (sultanPlayerIndex == 1) playerTwoTokens++;
        if (sultanPlayerIndex == 2) playerThreeTokens++;
    }

    void removeTokenFromSultan() {
        if (sultanPlayerIndex == 0 && playerOneTokens > 0) playerOneTokens--;
        if (sultanPlayerIndex == 1 && playerTwoTokens > 0) playerTwoTokens--;
        if (sultanPlayerIndex == 2 && playerThreeTokens > 0) playerThreeTokens--;
    }

    void checkForPlayersWithZeroTokens() {
        int zeroTokenCount = 0;
        if (playerOneTokens == 0) zeroTokenCount++;
        if (playerTwoTokens == 0) zeroTokenCount++;
        if (playerThreeTokens == 0) zeroTokenCount++;
    }

    boolean isGameOver() {
        int zeroTokenCount = 0;
        if (playerOneTokens == 0) zeroTokenCount++;
        if (playerTwoTokens == 0) zeroTokenCount++;
        if (playerThreeTokens == 0) zeroTokenCount++;
        return zeroTokenCount >= 2;
    }

    int getWinnerIndex() {
        if (playerOneTokens > 0) return 0;
        if (playerTwoTokens > 0) return 1;
        return 2;
    }

}