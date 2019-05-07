package com.terry.webapp.util;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;


public class WordReader {

	public static void main(String[] args) {
		WordReader test = new WordReader();
		String filePath = "/Users/xzy/OneDrive/文档/智慧城市/共享交换平台.docx";
		test.testWord(filePath);
	}

	/**
	 * 读取文档中表格
	 * @param filePath
	 */
	public void testWord(String filePath) {
		try{
			FileInputStream in = new FileInputStream(filePath);//载入文档
			// 处理docx格式 即office2007以后版本
			if(filePath.toLowerCase().endsWith("docx")){
				//word 2007 图片不会被读取， 表格中的数据会被放在字符串的最后   
				XWPFDocument xwpf = new XWPFDocument(in);//得到word文档的信息
				Iterator<XWPFTable> it = xwpf.getTablesIterator();//得到word中的表格
				// 设置需要读取的表格  set是设置需要读取的第几个表格，total是文件中表格的总数
				int set = 3, total = 10;
				int num = set;
				// 过滤前面不需要的表格
				for (int i = 0; i < set-1; i++) {
					it.hasNext();
					it.next();
				}
				while (it.hasNext()) {
					XWPFTable table = it.next();  
					System.out.println("这是第" + num + "个表的数据");
					List<XWPFTableRow> rows = table.getRows(); 
					//读取每一行数据
					for (int i = 0; i < rows.size(); i++) {
						XWPFTableRow  row = rows.get(i);
						//读取每一列数据
						List<XWPFTableCell> cells = row.getTableCells(); 
						if (cells.size() == 1) {
							System.out.print("Table name = " +  cells.get(0).getText());
						} else {
							for (int j = 0; j < cells.size(); j++) {
								XWPFTableCell cell = cells.get(j);
								//输出当前的单元格的数据
								System.out.print(cell.getText() + "\t");
							}
						}
						System.out.println();
					}
					// 过滤多余的表格
					while (num < total) {
						it.hasNext();
						it.next();
						num += 1;
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
}

class TableColumn {
	private String fieldName; // 字段名
	private String desc; //含义
	private String type; // 类型及长度
	private String nullable; //是否可空(非空, 空)
	private String isPK; // 是否主键(主键, 否)
	
	
}

class DbTable {
	private String tableName;
	private String tableDesc;
	private List<TableColumn> tableColumns = new ArrayList<TableColumn>();
	private List<String> pkList = new ArrayList<String>();
	
	
}
