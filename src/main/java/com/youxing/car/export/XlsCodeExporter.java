package com.youxing.car.export;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.youxing.car.entity.CountEntity;

@Component("xlsCodeExporter")
@Scope("prototype")
public class XlsCodeExporter {

	private Workbook workbook;
	private Sheet currentSheet;
	private OutputStream writer;
	private int rowIndex = 0;

	public void preExport(String path, String name) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			writer = new BufferedOutputStream(new FileOutputStream(path + "/" + name));
			workbook = createWorkBook();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("创建导出文件失败！");
		}
	}

	public void exportDetail(Map<String, List<CountEntity>> maps, String name) {
		try {
			if (MapUtils.isNotEmpty(maps)) {
				preExport("/home/export", name);
				List<Map.Entry<String, List<CountEntity>>> lists = new ArrayList<Map.Entry<String, List<CountEntity>>>(maps.entrySet());
		        Collections.sort(lists,new Comparator<Map.Entry<String, List<CountEntity>>>() {
		            //升序排序
					@Override
					public int compare(Map.Entry<String, List<CountEntity>> o1, Map.Entry<String, List<CountEntity>> o2) {      
				        return (o1.getKey().hashCode())-(o2.getKey().hashCode());
				    }
		        });
				for (Map.Entry<String, List<CountEntity>> entry : lists) {
					List<CountEntity> list = entry.getValue();
					if (CollectionUtils.isNotEmpty(list)) {
						if (rowIndex == 0) {
							currentSheet = workbook.createSheet(list.get(0).getName());
							createContent(list.get(0).getName());

						}
						for (CountEntity ce : list) {
							createContent(ce);
						}
						currentSheet.autoSizeColumn(0);
						currentSheet.autoSizeColumn(1);
						currentSheet.autoSizeColumn(2);
						currentSheet.autoSizeColumn(3);
						currentSheet.autoSizeColumn(4);
						currentSheet.autoSizeColumn(5);
						currentSheet.autoSizeColumn(6);
						reset();
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reset();
			postExport();
		}
	}

	public void export(Map<String, List<CountEntity>> maps, String name) throws Exception {
		try {
			if (MapUtils.isNotEmpty(maps)) {
				preExport("/home/export", name);
				List<Map.Entry<String, List<CountEntity>>> lists = new ArrayList<Map.Entry<String, List<CountEntity>>>(maps.entrySet());
		        Collections.sort(lists,new Comparator<Map.Entry<String, List<CountEntity>>>() {
		            //升序排序
					@Override
					public int compare(Map.Entry<String, List<CountEntity>> o1, Map.Entry<String, List<CountEntity>> o2) {      
						return (o1.getKey().hashCode())-(o2.getKey().hashCode());
				    }
		        });
				for (Map.Entry<String, List<CountEntity>> entry : lists) {
					List<CountEntity> list = entry.getValue();
					if (CollectionUtils.isNotEmpty(list)) {
						if (rowIndex == 0) {
							currentSheet = workbook.createSheet(name);
						}
						createContent(list.get(0).getName());
						for (CountEntity ce : list) {
							createContent(ce);
						}
						currentSheet.autoSizeColumn(0);
						currentSheet.autoSizeColumn(1);
						currentSheet.autoSizeColumn(2);
						currentSheet.autoSizeColumn(3);
						currentSheet.autoSizeColumn(4);
						currentSheet.autoSizeColumn(5);
						currentSheet.autoSizeColumn(6);
						currentSheet.createRow(rowIndex);
						rowIndex++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reset();
			postExport();
		}
	}

//=========================车辆统计
	public void exportDetailCar(Map<String, List<CountEntity>> maps, String name) {
		try {
			if (MapUtils.isNotEmpty(maps)) {
				preExport("/home/export", name);
				/*List<Map.Entry<String, List<CountEntity>>> lists = new ArrayList<Map.Entry<String, List<CountEntity>>>(maps.entrySet());
		        Collections.sort(lists,new Comparator<Map.Entry<String, List<CountEntity>>>() {
		            //升序排序
					@Override
					public int compare(Map.Entry<String, List<CountEntity>> o1, Map.Entry<String, List<CountEntity>> o2) {      
				        return (o1.getKey().hashCode())-(o2.getKey().hashCode());
				    }
		        });*/
				for (Entry<String, List<CountEntity>> entry : maps.entrySet()) {
					List<CountEntity> list = entry.getValue();
					if (CollectionUtils.isNotEmpty(list)) {
						if (rowIndex == 0) {
							currentSheet = workbook.createSheet(list.get(0).getName());
							createContent(list.get(0).getName());

						}
						for (CountEntity ce : list) {
							createContent(ce);
						}
						currentSheet.autoSizeColumn(0);
						currentSheet.autoSizeColumn(1);
						currentSheet.autoSizeColumn(2);
						currentSheet.autoSizeColumn(3);
						currentSheet.autoSizeColumn(4);
						currentSheet.autoSizeColumn(5);
						currentSheet.autoSizeColumn(6);
						reset();
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reset();
			postExport();
		}
	}

	public void exportCar(Map<String, List<CountEntity>> maps, String name) throws Exception {
		try {
			if (MapUtils.isNotEmpty(maps)) {
				preExport("/home/export", name);
				/*List<Map.Entry<String, List<CountEntity>>> lists = new ArrayList<Map.Entry<String, List<CountEntity>>>(maps.entrySet());
		        Collections.sort(lists,new Comparator<Map.Entry<String, List<CountEntity>>>() {
		            //升序排序
					@Override
					public int compare(Map.Entry<String, List<CountEntity>> o1, Map.Entry<String, List<CountEntity>> o2) {      
						return (o1.getKey().hashCode())-(o2.getKey().hashCode());
				    }
		        });*/
				for (Entry<String, List<CountEntity>> entry : maps.entrySet()) {
					List<CountEntity> list = entry.getValue();
					if (CollectionUtils.isNotEmpty(list)) {
						if (rowIndex == 0) {
							currentSheet = workbook.createSheet(name);
						}
						createContent(list.get(0).getName());
						for (CountEntity ce : list) {
							createContent(ce);
						}
						currentSheet.autoSizeColumn(0);
						currentSheet.autoSizeColumn(1);
						currentSheet.autoSizeColumn(2);
						currentSheet.autoSizeColumn(3);
						currentSheet.autoSizeColumn(4);
						currentSheet.autoSizeColumn(5);
						currentSheet.autoSizeColumn(6);
						currentSheet.createRow(rowIndex);
						rowIndex++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			reset();
			postExport();
		}
	}
//=======================================
	private void createCell(Row row, int index, String value, int type) {
		Cell cell = row.createCell(index);
		CellStyle style =workbook.createCellStyle();
		if("1".equals(type)){			
			style.setDataFormat(workbook.createDataFormat().getFormat("0.0"));
		}else if("2".equals(type)){
			style.setDataFormat(workbook.createDataFormat().getFormat("0"));
		}
		cell.setCellStyle(style);
		cell.setCellValue(value);
	}

	public void postExport() {
		if (workbook != null) {
			try {
				workbook.write(writer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭文件流失败！");
			}
		}
		release(workbook);
		release();
	}

	private void createContent(String name) {
		Row row = currentSheet.createRow(rowIndex);
		rowIndex++;
		createCell(row, 0, name, Cell.CELL_TYPE_STRING);
		createCell(row, 1, "里程(km)", Cell.CELL_TYPE_NUMERIC);
		createCell(row, 2, "油耗(L)", Cell.CELL_TYPE_NUMERIC);
		createCell(row, 3, "急加速", Cell.CELL_TYPE_NUMERIC);
		createCell(row, 4, "急减速", Cell.CELL_TYPE_NUMERIC);
		createCell(row, 5, "急刹车", Cell.CELL_TYPE_NUMERIC);
		createCell(row, 6, "急转弯", Cell.CELL_TYPE_NUMERIC);
	}

	private void createContent(CountEntity ce) {
		Row row = currentSheet.createRow(rowIndex);
		rowIndex++;
		createCell(row, 0, ce.getDay(), Cell.CELL_TYPE_STRING);
		createCell(row, 1, ce.getMil().toString(), Cell.CELL_TYPE_NUMERIC);
		createCell(row, 2, ce.getFuel().toString(), Cell.CELL_TYPE_NUMERIC);
		createCell(row, 3, ce.getAcce().toString(), Cell.CELL_TYPE_NUMERIC);
		createCell(row, 4, ce.getDece().toString(), Cell.CELL_TYPE_NUMERIC);
		createCell(row, 5, ce.getDece().toString(), Cell.CELL_TYPE_NUMERIC);
		createCell(row, 6, ce.getSharp().toString(), Cell.CELL_TYPE_NUMERIC);
	}

	private void release() {
		currentSheet = null;
		workbook = null;
		writer = null;
	}

	private void reset() {
		rowIndex = 0;
	}

	protected Workbook createWorkBook() {
		return new HSSFWorkbook();
	}

	public void release(Workbook workbook) {
	}

}
