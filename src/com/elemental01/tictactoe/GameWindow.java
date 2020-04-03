package com.elemental01.tictactoe;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author simple0x47
 */
public class GameWindow extends JFrame
{
    private class ClickListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) 
        {
            if (!(e.getSource() instanceof JButton))
            {
                return;
            }
            
            updateGameState((JButton)e.getSource());
        }
    }
    
    private enum LineAlign
    {
        TOP,
        LEFT,
        VERTICAL_MIDDLE,
        HORIZONTAL_MIDDLE,
        RIGHT,
        BOTTOM,
        LOWER_DIAGONAL,
        UPPER_DIAGONAL,
    }
    
    private static final int FRAME_WIDTH = 235;
    private static final int FRAME_HEIGHT = 235;
    private static final short GAME_MATRIX_SIZE = 3;
    
    private final char[][] gameState;
    private final ArrayList<JButton> gameButtons;
    private boolean gameTurn;
    private int gameMoves;
    
    public GameWindow()
    {
        super();
        
        setTitle("Tic-Tac-Toe");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLayout(new GridLayout(GAME_MATRIX_SIZE, GAME_MATRIX_SIZE));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        gameState = new char[GAME_MATRIX_SIZE][GAME_MATRIX_SIZE];
        gameButtons = new ArrayList<>(GAME_MATRIX_SIZE * GAME_MATRIX_SIZE);
        gameTurn = false;
        gameMoves = 0;
        
        createEnvironment();
    }
    
    private void createEnvironment()
    {
        JButton loopButton;
        
        for (int i = 0; i < GAME_MATRIX_SIZE; i++)
        {
            for (int j = 0; j < GAME_MATRIX_SIZE; j++)
            {
                gameState[i][j] = ' ';
                
                loopButton = new JButton();
                loopButton.addActionListener(new ClickListener());
                loopButton.setText("");
                gameButtons.add(loopButton);
                add(loopButton);
            }
        }
        
        setVisible(true);
    }
    
    private void updateGameState(final JButton clickedButton)
    {
        if (clickedButton == null)
        {
            throw new NullPointerException("'clickedButton' is null.");
        }
        
        if (!(clickedButton.getText().equals("")))
        {
            return;
        }
        
        
        final String value = (gameTurn) ? "X" : "O";
        clickedButton.setText(value);
        int buttonPosition = gameButtons.indexOf(clickedButton);
        
        if (buttonPosition >= GAME_MATRIX_SIZE * GAME_MATRIX_SIZE)
        {
            throw new ArrayIndexOutOfBoundsException();
        }
        
        final int x = buttonPosition / GAME_MATRIX_SIZE;
        final int y = buttonPosition % GAME_MATRIX_SIZE;
        
        if (gameState[x][y] != ' ')
        {
            throw new IllegalStateException("'gameState' not synced with buttons.");
        }
        
        
        gameState[x][y] = value.charAt(0);
        gameMoves = gameMoves + 1;
        gameTurn = (!(gameTurn));
        
        if (gameMoves >= (GAME_MATRIX_SIZE * 2 - 1))
        {
            try
            {
                if (checkPositions(buttonPosition))
                {
                    for (int i = 0; i < gameButtons.size(); i++)
                    {
                        gameButtons.get(i).setEnabled(false);
                    }
                    
                    int input = JOptionPane.showConfirmDialog(this, String.format("%s has won!", value), "Congratulations!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null);
                   
                    if (input == 0)
                    {
                        this.restart();
                    }
                }
                else if (gameMoves >= (GAME_MATRIX_SIZE * GAME_MATRIX_SIZE))
                {
                    int input = JOptionPane.showConfirmDialog(this, "Draw", "Well done!", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null);
                   
                    if (input == 0)
                    {
                        this.restart();
                    }
                }
            }
            catch (InvalidValueException invalidValue)
            {
                throw new IllegalStateException("'buttonPosition' is invalid.");
            }
        }
    }
    
    private boolean checkPositions(final int sourcePosition) 
            throws InvalidValueException
    {
        if (sourcePosition < 0 || sourcePosition >= (GAME_MATRIX_SIZE * GAME_MATRIX_SIZE))
        {
            throw new InvalidValueException();
        }
        
        final ArrayList<LineAlign> possibleAligns = new ArrayList<>();
        
        final int sourceX = sourcePosition / GAME_MATRIX_SIZE;
        final int sourceY = sourcePosition % GAME_MATRIX_SIZE;
        
        if (sourcePosition % 2 == 0)
        {
            if (sourceX == 0)
            {
               if (sourceY == 0)
               {
                   possibleAligns.add(LineAlign.LOWER_DIAGONAL);
               }
               else if (sourceY == (GAME_MATRIX_SIZE - 1))
               {
                   possibleAligns.add(LineAlign.UPPER_DIAGONAL);
               }
            }
            else if (sourceX == (GAME_MATRIX_SIZE - 1))
            {
                if (sourceY == 0)
                {
                    possibleAligns.add(LineAlign.UPPER_DIAGONAL);
                }
                else if (sourceY == (GAME_MATRIX_SIZE - 1))
                {
                    possibleAligns.add(LineAlign.LOWER_DIAGONAL);
                }
            }
            else if (sourceX == (GAME_MATRIX_SIZE / 2) && sourceY == (GAME_MATRIX_SIZE / 2))
            {
                possibleAligns.add(LineAlign.LOWER_DIAGONAL);
                possibleAligns.add(LineAlign.UPPER_DIAGONAL);
            }
        }
        
        switch (sourceX) {
            case 0:
                possibleAligns.add(LineAlign.TOP);
                break;
            case GAME_MATRIX_SIZE / 2:
                possibleAligns.add(LineAlign.HORIZONTAL_MIDDLE);
                break;
            case (GAME_MATRIX_SIZE - 1):
                possibleAligns.add(LineAlign.BOTTOM);
                break;
            default:
                throw new IllegalStateException("'sourceX' has an illegal value: " + sourceX);
        }
        
        switch (sourceY) {
            case 0:
                possibleAligns.add(LineAlign.LEFT);
                break;
            case GAME_MATRIX_SIZE / 2:
                possibleAligns.add(LineAlign.VERTICAL_MIDDLE);
                break;
            case (GAME_MATRIX_SIZE - 1):
                possibleAligns.add(LineAlign.RIGHT);
                break;
            default:
                throw new IllegalStateException("'sourceY' has an illegal value: " + sourceY);
        }
        
        for (int i = 0; i < possibleAligns.size(); i++)
        {
            if (checkLine(possibleAligns.get(i)))
            {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean checkLine(final LineAlign lineAlign) 
            throws InvalidValueException
    {
        if (lineAlign == null)
        {
            throw new NullPointerException("'lineAlign' is null.");
        }
        
        
        final int startI;
        final int maxI;
        int startJ;
        int maxJ;
               
        switch (lineAlign)
        {
            case TOP:
                startI = 0;
                maxI = 0;
                startJ = 0;
                maxJ = GAME_MATRIX_SIZE - 1;
                break;
                
            case LEFT:
                startI = 0;
                maxI = GAME_MATRIX_SIZE - 1;
                startJ = 0;
                maxJ = 0;
                break;
                
            case HORIZONTAL_MIDDLE:
                startI = GAME_MATRIX_SIZE / 2;
                maxI = GAME_MATRIX_SIZE / 2;
                startJ = 0;
                maxJ = GAME_MATRIX_SIZE - 1;
                break;
                
            case VERTICAL_MIDDLE:
                startI = 0;
                maxI = GAME_MATRIX_SIZE - 1;
                startJ = GAME_MATRIX_SIZE / 2;
                maxJ = GAME_MATRIX_SIZE / 2;
                break;
                  
            case RIGHT:
                startI = 0;
                maxI = GAME_MATRIX_SIZE - 1;
                startJ = GAME_MATRIX_SIZE - 1;
                maxJ = GAME_MATRIX_SIZE - 1;
                break;
                                
            case BOTTOM:
                startI = GAME_MATRIX_SIZE - 1;
                maxI = GAME_MATRIX_SIZE - 1;
                startJ = 0;
                maxJ = GAME_MATRIX_SIZE - 1;
                break;
                
            case LOWER_DIAGONAL:
                startI = 0;
                maxI = GAME_MATRIX_SIZE - 1;
                startJ = 0;
                maxJ = startJ;
                break;
                
            case UPPER_DIAGONAL:
                startI = 0;
                maxI = (GAME_MATRIX_SIZE - 1);
                startJ = ((GAME_MATRIX_SIZE - 1) - startI) * -1;
                maxJ = ((GAME_MATRIX_SIZE - 1) - startI) * -1;
                break;
                
            default:
                throw new InvalidValueException("'lineAlign' holds an invalid value.");
        }
        
        final char startValue = gameState[Math.abs(startI)][Math.abs(startJ)];
        
        for (int i = startI; i <= maxI; i++)
        {
            if (lineAlign == LineAlign.LOWER_DIAGONAL)
            {
                startJ = i;
                maxJ = i;
            }
            else if (lineAlign == LineAlign.UPPER_DIAGONAL)
            {
                startJ = ((GAME_MATRIX_SIZE - 1) - i) * -1;
                maxJ = ((GAME_MATRIX_SIZE - 1) - i) * -1;
            }
            
            for (int j = startJ; j <= maxJ; j++)
            {
                if (gameState[Math.abs(i)][Math.abs(j)] != startValue)
                {
                    return false;
                }
            }
        }
                
        return true;
    }
    
    private void restart()
    {
        JButton loopButton;
        
        for (int i = 0; i < GAME_MATRIX_SIZE; i++)
        {
            for (int j = 0; j < GAME_MATRIX_SIZE; j++)
            {
                gameState[i][j] = ' ';
                loopButton = gameButtons.get((i * GAME_MATRIX_SIZE) + j);
                loopButton.setText("");
                loopButton.setEnabled(true);
            }
        }
        
        gameTurn = false;
        gameMoves = 0;
    }
    
    public static void main(String[] args)
    {
        new GameWindow();
    }
}