package sudoku.dao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import sudoku.dao.factories.SudokuBoardDaoFactory;
import sudoku.dao.interfaces.Dao;
import sudoku.model.exceptions.FillingBoardSudokuException;
import sudoku.model.models.SudokuBoard;
import sudoku.model.solver.BacktrackingSudokuSolver;

public class FileSudokuBoardDaoTest {
    // Directory path for testing
    private static final String TEST_DIRECTORY = "test_dir";

    private void cleanUpDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !file.getName().startsWith(".git")) {
                    file.delete();
                }
            }
        }
    }

    @AfterEach
    void tearDown() {
        cleanUpDirectory(new File(TEST_DIRECTORY));
    }

    @Test
    public void testWriteAndRead() {
        SudokuBoard sampleBoard = new SudokuBoard(new BacktrackingSudokuSolver());
        try {
            sampleBoard.solveGame();
        } catch (FillingBoardSudokuException e) {
            e.printStackTrace();
        }

        sampleBoard.setField(0, 0, 5);
        sampleBoard.setField(0, 5, 9);
        sampleBoard.setField(4, 7, 2);

        final String boardName = "test_board";

        assertNotNull(sampleBoard);

        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.createSudokuBoardDao(TEST_DIRECTORY)) {
            assertDoesNotThrow(() -> dao.write(boardName, sampleBoard));

            assertTrue(new File(TEST_DIRECTORY, boardName).exists());

            SudokuBoard readBoard = assertDoesNotThrow(() -> dao.read(boardName));
            assertNotNull(readBoard);

            assertEquals(sampleBoard, readBoard);

            assertEquals(sampleBoard.getField(0, 0), readBoard.getField(0, 0));
            assertEquals(sampleBoard.getField(0, 5), readBoard.getField(0, 5));
            assertEquals(sampleBoard.getField(4, 7), readBoard.getField(4, 7));

        } catch (Exception e) {
            Assertions.fail();
        }

    }

    @Test
    public void testNames_returnsWrittenFileNames() {
        SudokuBoard board = new SudokuBoard(new BacktrackingSudokuSolver());

        try (Dao<SudokuBoard> dao = SudokuBoardDaoFactory.createSudokuBoardDao(TEST_DIRECTORY)) {
            dao.write("game_one", board);
            dao.write("game_two", board);

            var names = dao.names();

            assertTrue(names.contains("game_one"));
            assertTrue(names.contains("game_two"));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

}
