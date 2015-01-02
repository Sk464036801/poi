package org.apache.poi.ss.formula.eval;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.apache.poi.ss.ITestDataProvider;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Common superclass for testing cases of circular references
 * both for HSSF and XSSF
 */
public abstract class BaseTestCircularReferences extends TestCase {

    protected final ITestDataProvider _testDataProvider;

    /**
     * @param testDataProvider an object that provides test data in HSSF / XSSF specific way
     */
    protected BaseTestCircularReferences(ITestDataProvider testDataProvider) {
        _testDataProvider = testDataProvider;
    }


    /**
     * Translates StackOverflowError into AssertionFailedError
     */
    private CellValue evaluateWithCycles(Workbook wb, Cell testCell)
            throws AssertionFailedError {
        FormulaEvaluator evaluator = _testDataProvider.createFormulaEvaluator(wb);
        try {
            return evaluator.evaluate(testCell);
        } catch (StackOverflowError e) {
            throw new AssertionFailedError( "circular reference caused stack overflow error");
        }
    }
    /**
     * Makes sure that the specified evaluated cell value represents a circular reference error.
     */
    private static void confirmCycleErrorCode(CellValue cellValue) {
        assertTrue(cellValue.getCellType() == Cell.CELL_TYPE_ERROR);
        assertEquals(ErrorEval.CIRCULAR_REF_ERROR.getErrorCode(), cellValue.getErrorValue());
    }


    /**
     * ASF Bugzilla Bug 44413
     * "INDEX() formula cannot contain its own location in the data array range"
     */
    public void testIndexFormula() {

        Workbook wb = _testDataProvider.createWorkbook();
        Sheet sheet = wb.createSheet("Sheet1");

        int colB = 1;
        sheet.createRow(0).createCell(colB).setCellValue(1);
        sheet.createRow(1).createCell(colB).setCellValue(2);
        sheet.createRow(2).createCell(colB).setCellValue(3);
        Row row4 = sheet.createRow(3);
        Cell testCell = row4.createCell(0);
        // This formula should evaluate to the contents of B2,
        testCell.setCellFormula("INDEX(A1:B4,2,2)");
        // However the range A1:B4 also includes the current cell A4.  If the other parameters
        // were 4 and 1, this would represent a circular reference.  Prior to v3.2 POI would
        // 'fully' evaluate ref arguments before invoking operators, which raised the possibility of
        // cycles / StackOverflowErrors.


        CellValue cellValue = evaluateWithCycles(wb, testCell);

        assertTrue(cellValue.getCellType() == Cell.CELL_TYPE_NUMERIC);
        assertEquals(2, cellValue.getNumberValue(), 0);
    }

    /**
     * Cell A1 has formula "=A1"
     */
    public void testSimpleCircularReference() {

        Workbook wb = _testDataProvider.createWorkbook();
        Sheet sheet = wb.createSheet("Sheet1");

        Row row = sheet.createRow(0);
        Cell testCell = row.createCell(0);
        testCell.setCellFormula("A1");

        CellValue cellValue = evaluateWithCycles(wb, testCell);

        confirmCycleErrorCode(cellValue);
    }

    /**
     * A1=B1, B1=C1, C1=D1, D1=A1
     */
    public void testMultiLevelCircularReference() {

        Workbook wb = _testDataProvider.createWorkbook();
        Sheet sheet = wb.createSheet("Sheet1");

        Row row = sheet.createRow(0);
        row.createCell(0).setCellFormula("B1");
        row.createCell(1).setCellFormula("C1");
        row.createCell(2).setCellFormula("D1");
        Cell testCell = row.createCell(3);
        testCell.setCellFormula("A1");

        CellValue cellValue = evaluateWithCycles(wb, testCell);

        confirmCycleErrorCode(cellValue);
    }

    public void testIntermediateCircularReferenceResults_bug46898() {
        Workbook wb = _testDataProvider.createWorkbook();
        Sheet sheet = wb.createSheet("Sheet1");

        Row row = sheet.createRow(0);

        Cell cellA1 = row.createCell(0);
        Cell cellB1 = row.createCell(1);
        Cell cellC1 = row.createCell(2);
        Cell cellD1 = row.createCell(3);
        Cell cellE1 = row.createCell(4);

        cellA1.setCellFormula("IF(FALSE, 1+B1, 42)");
        cellB1.setCellFormula("1+C1");
        cellC1.setCellFormula("1+D1");
        cellD1.setCellFormula("1+E1");
        cellE1.setCellFormula("1+A1");

        FormulaEvaluator fe = _testDataProvider.createFormulaEvaluator(wb);
        CellValue cv;

        // Happy day flow - evaluate A1 first
        cv = fe.evaluate(cellA1);
        assertEquals(Cell.CELL_TYPE_NUMERIC, cv.getCellType());
        assertEquals(42.0, cv.getNumberValue(), 0.0);
        cv = fe.evaluate(cellB1); // no circ-ref-error because A1 result is cached
        assertEquals(Cell.CELL_TYPE_NUMERIC, cv.getCellType());
        assertEquals(46.0, cv.getNumberValue(), 0.0);

        // Show the bug - evaluate another cell from the loop first
        fe.clearAllCachedResultValues();
        cv = fe.evaluate(cellB1);
        if (cv.getCellType() == ErrorEval.CIRCULAR_REF_ERROR.getErrorCode()) {
            throw new AssertionFailedError("Identified bug 46898");
        }
        assertEquals(Cell.CELL_TYPE_NUMERIC, cv.getCellType());
        assertEquals(46.0, cv.getNumberValue(), 0.0);

        // start evaluation on another cell
        fe.clearAllCachedResultValues();
        cv = fe.evaluate(cellE1);
        assertEquals(Cell.CELL_TYPE_NUMERIC, cv.getCellType());
        assertEquals(43.0, cv.getNumberValue(), 0.0);
    }
}