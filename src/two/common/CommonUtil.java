package two.common;

import java.io.File;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

import jxl.Sheet;
import jxl.Workbook;
import weaver.general.Util;

public class CommonUtil {

	/**
	 * ���ַ���������ָ���������
	 * 
	 * @param strs
	 * @param flag
	 * @return
	 */
	public String connects(String[] strs, String flag) {
		String resoult = "";
		if (strs != null) {
			for (int i = 0; i < strs.length; i++) {
				resoult += strs[i] + flag;
			}
			resoult = resoult.substring(0, resoult.length() - 1);
		}
		return resoult;
	}
	/**
	 * ��ȡExcel�ļ��е�����
	 */
	public static String[][] readExcel(String filename) {
		return readExcel(filename, 0);
	}

	/**
	 * ��ȡExcel�ļ��е�����
	 */
	public static String[][] readExcel(String filename, int sheetid) {
		String[][] contents = null;
		try {
			Workbook book = Workbook.getWorkbook(new File(filename));// ���ļ�
			Sheet sheet = book.getSheet(sheetid);// ���ļ�
			int rows = sheet.getRows();// ȡ������
			int cols = sheet.getColumns();// ȡ������
			contents = new String[rows][cols];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					contents[i][j] = Util.null2String(
							sheet.getCell(j, i).getContents()).trim();
				}
			}
			book.close();// �ر��ļ�
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contents;
	}
	/**
	 * ��ȡExcel�ļ��е�����
	 */
	public static Table<Integer,Integer,String> jxlReadExcel(String v_field_path) {
		return jxlReadExcel(v_field_path, 0);
	}

	/**
	 * ��ȡExcel�ļ��е�����
	 */
	public static Table<Integer,Integer,String> jxlReadExcel(String v_field_path, int sheetid) {
		Table<Integer,Integer,String> excelTable=TreeBasedTable.create();
		try {
			Workbook book = Workbook.getWorkbook(new File(v_field_path));// ���ļ�
			Sheet sheet = book.getSheet(sheetid);// ���ļ�
			int rows = sheet.getRows();// ȡ������
			int cols = sheet.getColumns();// ȡ������
			for (int _i = 0; _i < rows; _i++) {
				for (int _j = 0; _j < cols; _j++) {
					String _val = Util.null2String(sheet.getCell(_j, _i).getContents()).trim();
					excelTable.put(_i,_j,_val);
				}
			}
			book.close();// �ر��ļ�
		} catch (Exception e) {
			e.printStackTrace();
		}
		return excelTable;
	}
}
