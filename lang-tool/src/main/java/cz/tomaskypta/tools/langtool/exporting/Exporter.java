package cz.tomaskypta.tools.langtool.exporting;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import cz.tomaskypta.tools.langtool.IModelVisitor;
import cz.tomaskypta.tools.langtool.model.Plural;
import cz.tomaskypta.tools.langtool.model.Resource;
import cz.tomaskypta.tools.langtool.model.ResourceEntry;
import cz.tomaskypta.tools.langtool.model.StringArray;
import cz.tomaskypta.tools.langtool.model.Term;
import cz.tomaskypta.tools.langtool.model.TypedArray;

public class Exporter implements IModelVisitor {

	private String project;
	private String fileName;
	private int rowIndex;
	private StylesDef styles;
	private HSSFSheet sheet;
	private Map<String, Integer> languageIndex;
	private String currentResourceName;

	public Exporter(String project, String fileName,
			Map<String, Integer> languageIndex) {
		this.project = project;
		this.fileName = fileName;
		this.languageIndex = languageIndex;
	}

	public void export(Map<String, Resource> resources) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		sheet = wb.createSheet(project);
		styles = new StylesDef(wb);
		rowIndex = 0;

		// Create the header row once and for all
		createColumnTitles();

		for (Resource resource : resources.values()) {
			exportResource(resource);
		}

		FileOutputStream outStream = new FileOutputStream(fileName);
		wb.write(outStream);
		outStream.close();

	}

	private void exportResource(Resource resource) {
		currentResourceName = resource.getFileName();
		for (ResourceEntry entry : resource.getSortedEntries()) {
			entry.visit(this);
		}
	}

	private void createColumnTitles() throws IOException {

		sheet.createRow(rowIndex++);
		createTitle(styles, sheet);

		for (Map.Entry<String, Integer> languageSpec : languageIndex.entrySet()) {
			addLang2Tilte(styles, sheet, languageSpec.getKey(),
					languageSpec.getValue());
		}

		sheet.createFreezePane(2, 1);
	}

	private static void createTitle(StylesDef styles, HSSFSheet sheet) {
		HSSFRow titleRow = sheet.getRow(0);

		HSSFCell contextCell = titleRow.createCell(0);
		contextCell.setCellStyle(styles.titleStyle);
		contextCell.setCellValue("CONTEXT");

		sheet.setColumnWidth(contextCell.getColumnIndex(), (40 * 256));

		HSSFCell keyCell = titleRow.createCell(1);
		keyCell.setCellStyle(styles.titleStyle);
		keyCell.setCellValue("KEY");

		sheet.setColumnWidth(keyCell.getColumnIndex(), (40 * 256));
	}

	private static void addLang2Tilte(StylesDef styles, HSSFSheet sheet,
			String lang, Integer langPos) {
		HSSFRow titleRow = sheet.getRow(0);
		HSSFCell langCell = titleRow.createCell(langPos);
		langCell.setCellStyle(styles.titleStyle);
		langCell.setCellValue(lang);

		sheet.setColumnWidth(langCell.getColumnIndex(), (60 * 256));
	}

	private HSSFRow createRowForKey(HSSFSheet sheet, String fileName,
			String key, int rowIndex, StylesDef styles) {
		HSSFRow row = sheet.createRow(rowIndex);

		HSSFCell contextCell = row.createCell(0);
		contextCell.setCellValue(fileName);
		contextCell.setCellStyle(styles.keyStyle);

		HSSFCell keyCell = row.createCell(1);
		keyCell.setCellValue(key);
		keyCell.setCellStyle(styles.keyStyle);

		return row;
	}

	
	public void visitTerm(Term term) {
		exportTerm(term.getKey(), term);
	}

	private void exportTerm(String key, Term term) {
		HSSFRow row = createRowForKey(sheet, currentResourceName, key,
				rowIndex++, styles);

		for (Map.Entry<String, Integer> language : languageIndex.entrySet()) {
			createTermCell(row, language.getValue(),
					term.getTranslation(language.getKey()));
		}
	}

	private void createTermCell(HSSFRow row, int langIndex, String text) {
		HSSFCell itemCell = row.createCell(langIndex);
		if (text != null && !text.isEmpty()) {
			itemCell.setCellStyle(styles.textStyle);
			itemCell.setCellValue(text);
		} else {
			itemCell.setCellStyle(styles.missedStyle);
		}
	}
	
	public void visitPlural(Plural plural) {

		for (Map.Entry<String, Term> term : plural.getTerms().entrySet()) {
			if (term != null) {
				String key = plural.getKey() + "#" + term.getKey();
				exportTerm(key, term.getValue());
			}
		}
	}

	
	public void visitStringArray(StringArray stringArray) {
		Term[] terms = stringArray.getTerms();

		for (int i = 0; i < terms.length; i++) {
			Term term = terms[i];
			if (term != null) {
				String key = stringArray.getKey() + "[" + i + "]";
				exportTerm(key, term);
			}
		}
	}
	
	public void visitTypedArray(TypedArray typedArray) {
		// we don't export typed arrays to excel		
	}

	private static class StylesDef {
		HSSFCellStyle commentStyle;
		HSSFCellStyle plurarStyle;
		HSSFCellStyle keyStyle;
		HSSFCellStyle textStyle;
		HSSFCellStyle titleStyle;
		HSSFCellStyle missedStyle;

		private StylesDef(HSSFWorkbook wb) {
			commentStyle = createCommentStyle(wb);
			plurarStyle = createPlurarStyle(wb);
			keyStyle = createKeyStyle(wb);
			textStyle = createTextStyle(wb);
			titleStyle = createTilteStyle(wb);
			missedStyle = createMissedStyle(wb);
		}

		private static HSSFCellStyle createTilteStyle(HSSFWorkbook wb) {
			HSSFFont bold = wb.createFont();
			bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

			HSSFCellStyle style = wb.createCellStyle();
			style.setFont(bold);
			style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			style.setWrapText(true);

			return style;
		}

		private static HSSFCellStyle createCommentStyle(HSSFWorkbook wb) {

			HSSFFont commentFont = wb.createFont();
			commentFont.setColor(HSSFColor.GREEN.index);
			commentFont.setItalic(true);
			commentFont.setFontHeightInPoints((short) 12);

			HSSFCellStyle commentStyle = wb.createCellStyle();
			commentStyle.setFont(commentFont);
			return commentStyle;
		}

		private static HSSFCellStyle createPlurarStyle(HSSFWorkbook wb) {

			HSSFFont commentFont = wb.createFont();
			commentFont.setColor(HSSFColor.GREY_50_PERCENT.index);
			commentFont.setItalic(true);
			commentFont.setFontHeightInPoints((short) 12);

			HSSFCellStyle commentStyle = wb.createCellStyle();
			commentStyle.setFont(commentFont);
			return commentStyle;
		}

		private static HSSFCellStyle createKeyStyle(HSSFWorkbook wb) {
			HSSFFont bold = wb.createFont();
			bold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			bold.setFontHeightInPoints((short) 11);

			HSSFCellStyle keyStyle = wb.createCellStyle();
			keyStyle.setFont(bold);

			return keyStyle;
		}

		private static HSSFCellStyle createTextStyle(HSSFWorkbook wb) {
			HSSFFont plain = wb.createFont();
			plain.setFontHeightInPoints((short) 12);

			HSSFCellStyle textStyle = wb.createCellStyle();
			textStyle.setFont(plain);
			textStyle.setWrapText(true);

			return textStyle;
		}

		private static HSSFCellStyle createMissedStyle(HSSFWorkbook wb) {

			HSSFCellStyle style = wb.createCellStyle();
			style.setFillForegroundColor(HSSFColor.RED.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			return style;
		}
	}	
}
