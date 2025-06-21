import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SudokuSolver {
    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private static final int EMPTY = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sudoku Solver");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 550);
            
            SudokuPanel sudokuPanel = new SudokuPanel();
            frame.add(sudokuPanel, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel();
            JButton solveButton = new JButton("Solve");
            JButton clearButton = new JButton("Clear");
            
            solveButton.addActionListener(e -> sudokuPanel.solve());
            clearButton.addActionListener(e -> sudokuPanel.clear());
            
            buttonPanel.add(solveButton);
            buttonPanel.add(clearButton);
            frame.add(buttonPanel, BorderLayout.SOUTH);
            
            frame.setVisible(true);
        });
    }

    static class SudokuPanel extends JPanel {
        private JTextField[][] gridCells = new JTextField[SIZE][SIZE];

        public SudokuPanel() {
            setLayout(new GridLayout(SIZE, SIZE));
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    gridCells[row][col] = new JTextField();
                    gridCells[row][col].setHorizontalAlignment(JTextField.CENTER);
                    gridCells[row][col].setFont(new Font("Arial", Font.BOLD, 20));
                    
                    // Add thicker borders for subgrids
                    if ((row / SUBGRID_SIZE + col / SUBGRID_SIZE) % 2 == 0) {
                        gridCells[row][col].setBackground(new Color(240, 240, 240));
                    }
                    
                    add(gridCells[row][col]);
                }
            }
        }

        public void solve() {
            int[][] board = new int[SIZE][SIZE];
            
            // Read values from UI
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    String text = gridCells[row][col].getText();
                    try {
                        board[row][col] = text.isEmpty() ? EMPTY : Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Invalid input at row " + (row+1) + ", column " + (col+1), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            
            if (solveSudoku(board)) {
                // Update UI with solution
                for (int row = 0; row < SIZE; row++) {
                    for (int col = 0; col < SIZE; col++) {
                        gridCells[row][col].setText(String.valueOf(board[row][col]));
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No solution exists for this Sudoku puzzle", 
                    "No Solution", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        public void clear() {
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    gridCells[row][col].setText("");
                }
            }
        }

        private boolean solveSudoku(int[][] board) {
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (board[row][col] == EMPTY) {
                        for (int num = 1; num <= SIZE; num++) {
                            if (isValid(board, row, col, num)) {
                                board[row][col] = num;
                                
                                if (solveSudoku(board)) {
                                    return true;
                                } else {
                                    board[row][col] = EMPTY; // Backtrack
                                }
                            }
                        }
                        return false; // No valid number found
                    }
                }
            }
            return true; // All cells filled
        }

        private boolean isValid(int[][] board, int row, int col, int num) {
            // Check row
            for (int c = 0; c < SIZE; c++) {
                if (board[row][c] == num) return false;
            }
            
            // Check column
            for (int r = 0; r < SIZE; r++) {
                if (board[r][col] == num) return false;
            }
            
            // Check subgrid
            int subgridStartRow = row - row % SUBGRID_SIZE;
            int subgridStartCol = col - col % SUBGRID_SIZE;
            
            for (int r = 0; r < SUBGRID_SIZE; r++) {
                for (int c = 0; c < SUBGRID_SIZE; c++) {
                    if (board[subgridStartRow + r][subgridStartCol + c] == num) {
                        return false;
                    }
                }
            }
            
            return true;
        }
    }
}