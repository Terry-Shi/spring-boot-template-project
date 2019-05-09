package com.terry.webapp.util;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

	private static String START_TABLE = "人口基本信息CityDB_Popu_Base";
	private static String END_TABLE = "单位社保缴纳信息表CityDB_Corp_Spe_SocialSecExt";
	
	// 表格中列的index
	enum colIndex {
		fieldName, // 字段名
		desc, //含义
		type, // 类型及长度
		nullable, //是否可空(非空, 空)
		isPK // 是否主键(主键, 否)
	}
	
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
				
				// 设置需要读取的表格
				int num = 0;
				boolean foundFirstTable = false; // 已经遍历到第一个需要处理的表
				boolean foundLastTable = false; // 已经遍历到最后一个需要处理的表
				
				while (it.hasNext()) {
					XWPFTable table = it.next();  
					num++;
					// TODO: 已知第3 - 33的表格是和表定义有关的
					if (num < 3 || num > 33) {
						continue;
					}
					
					System.out.println("-- word中第" + num + "个表格");
					List<XWPFTableRow> rows = table.getRows(); 
					//System.out.println(rows.get(0).getTableCells().get(0).getText());
					if (!rows.get(0).getTableCells().get(0).getText().contains("CityDB") ) {
						continue;
					}
					
					DbTable dbTable = new DbTable();
					//读取每一行数据
					for (int i = 0; i < rows.size(); i++) {
						XWPFTableRow  row = rows.get(i);
						//读取每一列数据
						List<XWPFTableCell> cells = row.getTableCells(); 
						if (i == 0) {
							System.out.println("-- Table name = " +  cells.get(0).getText());
							dbTable.setRawTablename(cells.get(0).getText());
							if (START_TABLE.equals(cells.get(0).getText())) {
								// found first table 
								foundFirstTable = true;
							} else if (END_TABLE.equals(cells.get(0).getText())) {
								// found last table
								foundLastTable = true;
							}
							if (!foundFirstTable) {
								continue;
							}
						} else if (i >= 2) {
							if (foundFirstTable) {
//								for (int j = 0; j < cells.size(); j++) {
//									XWPFTableCell cell = cells.get(j);
//									//输出当前的单元格的数据
//									System.out.print(cell.getText() + "\t");
//								}
								if ("字段名".equals(cells.get(colIndex.fieldName.ordinal()).getText())) {
									continue;
								}
								TableColumn tableColumn = new TableColumn();
								tableColumn.setFieldName(cells.get(colIndex.fieldName.ordinal()).getText());
								tableColumn.setDesc(cells.get(colIndex.desc.ordinal()).getText());
								tableColumn.setType(cells.get(colIndex.type.ordinal()).getText());
								tableColumn.setNullable(cells.get(colIndex.nullable.ordinal()).getText());
								tableColumn.setIsPK(cells.get(colIndex.isPK.ordinal()).getText());
								//System.out.println(tableColumn);
								dbTable.getTableColumns().add(tableColumn);
								if (tableColumn.isPK()) {
									dbTable.getPkList().add(tableColumn.getFieldName());
								}
							}
						}
					}
					System.out.println(dbTable);
					if (dbTable.getPkList().size()==0) {
						//System.err.println(" Must have at least one PK filed !");
						throw new RuntimeException("Must have at least one PK filed");
					}
					// 过滤多余的表格
					if (foundFirstTable && foundLastTable) {
						continue;
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String removeBlankChar(String input) {
		StringBuilder str = new StringBuilder();
		if (input != null) {
			for (int i=0; i<input.length(); i++) {
				//StringUtils.isBlank();
				if (input.charAt(i) != ' '){
					str.append(input.charAt(i));
				}
			}
		}
		return str.toString();
	}
}

class TableColumn {
	private String fieldName; // 字段名
	private String desc; //含义
	private String type; // 类型及长度
	private String nullable; //是否可空(非空, 空)
	private String isPK; // 是否主键(主键, 否)
	
	private String defaultValue = "  ";
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNullable() {
		return nullable;
	}
	public void setNullable(String nullable) {
		this.nullable = nullable;
	}
	public String getIsPK() {
		return isPK;
	}
	public void setIsPK(String isPK) {
		this.isPK = isPK;
	}
	
	public boolean isPK() {
		if ("主键".equals(isPK) || "是".equals(isPK) ) {
			return true;
		} else {
			return false;
		}
	}
	
	private String convertNullable() {
		if ("非空".equals(nullable) || "否".equals(nullable)) {
			return " NOT NULL ";
		} else {
			return "";
		}
	}
	// oracle 的字段类型转换为mysql的字段类型
	// 参考 mysql 字段类型 http://www.runoob.com/mysql/mysql-data-types.html
	// oracle 字段类型 https://www.cnblogs.com/kerrycode/p/3265120.html
	//     oracle的NUMBER(P,S) P是整数长度，S是小数长度，对应 mysql的DECIMAL(M,D) 
	//     oracle 的int相当于number(22),存储总长度为22的整数。
	private String convertType() {
		type = type.toUpperCase();
		if (type.startsWith("VARCHAR2")) {
			return type.replace("VARCHAR2", "VARCHAR");
		} else if (type.startsWith("NUMBER")) {
			return type.replace("NUMBER", "DECIMAL");
		} else if (type.startsWith("BLOB")) {
			return type.replace("BLOB", "MEDIUMBLOB");
		} else if (type.startsWith("DATE")) {
			    this.defaultValue = " DEFAULT CURRENT_TIMESTAMP ";
				return type.replace("DATE", "TIMESTAMP");
		} else {
			return type;
		}
	}
	
	@Override
	public String toString() {
		return "  " + fieldName + " " + convertType() + defaultValue + convertNullable() + "COMMENT '" + desc + "' ,";
	}
}

class DbTable {
	private String rawTablename; // 原始表名；eg:单位社保缴纳信息表CityDB_Corp_Spe_SocialSecExt
	private String tableName; // 表名
	private String tableDesc; // 表描述
	private List<TableColumn> tableColumns = new ArrayList<TableColumn>(); // 字段列表
	private List<String> pkList = new ArrayList<String>(); // 主键字段列表
	
	public String getRawTablename() {
		return rawTablename;
	}
//	public void setRawTablename(String rawTablename) {
//		this.rawTablename = rawTablename;
//	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableDesc() {
		return tableDesc;
	}
	public void setTableDesc(String tableDesc) {
		this.tableDesc = tableDesc;
	}
	public List<TableColumn> getTableColumns() {
		return tableColumns;
	}
	public void setTableColumns(List<TableColumn> tableColumns) {
		this.tableColumns = tableColumns;
	}
	public List<String> getPkList() {
		return pkList;
	}
	public void setPkList(List<String> pkList) {
		this.pkList = pkList;
	}
	
	public void setRawTablename(String rawTablename) {
		this.rawTablename = rawTablename;
		int index = rawTablename.indexOf("CityDB");
		tableDesc = rawTablename.substring(0, index).trim();
		tableName = WordReader.removeBlankChar(rawTablename.substring(index).trim());
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("DROP TABLE IF EXISTS " + tableName + ";" + "\n");
		
		str.append("CREATE TABLE " + tableName + " (" + "\n");
		for (TableColumn col : tableColumns) {
			str.append(col + "\n");
		}
		str.append(" PRIMARY KEY (" );
		for (String pk : pkList) {
			str.append(pk);
		}
		str.append( ") " + "\n");
		str.append(" ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='" + tableDesc + "';");
		return str.toString();
	}
}
