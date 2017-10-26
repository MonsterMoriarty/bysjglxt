package com.bysjglxt.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bysjglxt.dao.StudentInformationManagementDao;
import com.bysjglxt.domain.DO.bysjglxt_student_basic;
import com.bysjglxt.domain.DO.bysjglxt_student_user;
import com.bysjglxt.domain.DTO.StudentInformationDTO;
import com.bysjglxt.service.StudentInformationManagementService;

import util.ExcelToBean;
import util.TeamUtil;

public class StudentInformationManagementServiceImpl implements StudentInformationManagementService {

	// Dao层的注入
	private StudentInformationManagementDao studentInformationManagementDao;

	public void setStudentInformationManagementDao(StudentInformationManagementDao studentInformationManagementDao) {
		this.studentInformationManagementDao = studentInformationManagementDao;
	}

	@Override
	public List<bysjglxt_student_basic> convertStudentExcelToList(File studentExcel, String EXCEL_StudentFileName)
			throws Exception {
		String houzhui = EXCEL_StudentFileName.substring(EXCEL_StudentFileName.lastIndexOf(".") + 1);
		FileInputStream input = new FileInputStream(studentExcel);
		List<Map<String, Object>> list = null;
		if ("xlsx".equals(houzhui)) {
			System.out.println("xlsx");
			XSSFWorkbook workbook = new XSSFWorkbook(input);
			list = ExcelToBean.parseUpdateExcel(workbook, "bysjglxt_student_basic");
		} else {
			System.out.println("xls");
			HSSFWorkbook workbook = new HSSFWorkbook(input);
			list = ExcelToBean.parseExcel(workbook, "bysjglxt_student_basic");
		}
		List<bysjglxt_student_basic> lists = ExcelToBean.toObjectPerproList(list, bysjglxt_student_basic.class);
		for (bysjglxt_student_basic bysjglxt_student_basic : lists) {
			System.out.println("\tk\t" + bysjglxt_student_basic);
		}
		return lists;
	}

	@Override
	public boolean saveStudentList(List<bysjglxt_student_basic> studentBasicList) {
		boolean flag = false;
		bysjglxt_student_user bysjglxt_student_user = null;
		for (bysjglxt_student_basic bysjglxt_student_basic : studentBasicList) {
			bysjglxt_student_user = new bysjglxt_student_user();
			bysjglxt_student_basic.setStudent_basic_id(TeamUtil.getUuid());
			flag = studentInformationManagementDao.saveStudentBasic(bysjglxt_student_basic);
			bysjglxt_student_user.setUser_student_id(TeamUtil.getUuid());
			bysjglxt_student_user.setUser_student_num(bysjglxt_student_basic.getStudent_basic_num());
			bysjglxt_student_user.setUser_student_password(bysjglxt_student_basic.getStudent_basic_num());
			bysjglxt_student_user.setUser_student_basic(bysjglxt_student_basic.getStudent_basic_id());
			bysjglxt_student_user.setUser_student_is_operate_premission(1);
			bysjglxt_student_user.setUser_student_gmt_create(TeamUtil.getStringSecond());
			bysjglxt_student_user.setUser_student_gmt_modified(bysjglxt_student_user.getUser_student_gmt_create());
			flag = studentInformationManagementDao.saveStudent(bysjglxt_student_user);
			if (!flag)
				break;
		}
		return flag;
	}

	@Override
	public List<StudentInformationDTO> list_StudentInformationDTO_All() {
		List<StudentInformationDTO> list_StudentInformationDTO_All = new ArrayList<StudentInformationDTO>();
		StudentInformationDTO studentInformationDTO = null;
		List<bysjglxt_student_user> listAllStudentUserInformation = studentInformationManagementDao
				.list_StudentUserInformation_All();
		System.out.println(listAllStudentUserInformation.size());

		for (bysjglxt_student_user student_user : listAllStudentUserInformation) {
			System.out.println("gg");
			studentInformationDTO = new StudentInformationDTO();
			studentInformationDTO.setBysjglxtStudentUser(student_user);
			studentInformationDTO.setBysjglxtStudentBasic(studentInformationManagementDao
					.get_StudentBasicInformation_ByUserBasic(student_user.getUser_student_basic()));
			System.out.println(studentInformationDTO);
			list_StudentInformationDTO_All.add(studentInformationDTO);
		}

		return list_StudentInformationDTO_All;
	}

	@Override
	public boolean save_NewStudent(bysjglxt_student_basic student_basic) {
		boolean flag = false;
		bysjglxt_student_user bysjglxt_student_user = new bysjglxt_student_user();
		student_basic.setStudent_basic_id(TeamUtil.getStringSecond());
		flag = studentInformationManagementDao.saveStudentBasic(student_basic);
		bysjglxt_student_user.setUser_student_id(TeamUtil.getUuid());
		bysjglxt_student_user.setUser_student_num(student_basic.getStudent_basic_num());
		bysjglxt_student_user.setUser_student_password(student_basic.getStudent_basic_num());
		bysjglxt_student_user.setUser_student_basic(student_basic.getStudent_basic_id());
		bysjglxt_student_user.setUser_student_is_operate_premission(1);
		bysjglxt_student_user.setUser_student_gmt_create(TeamUtil.getStringSecond());
		bysjglxt_student_user.setUser_student_gmt_modified(bysjglxt_student_user.getUser_student_gmt_create());
		flag = studentInformationManagementDao.saveStudent(bysjglxt_student_user);
		return false;
	}

	@Override
	public boolean remove_StudentList(List<String> useStudentNumList) {
		boolean flag = false;
		for (String student_num : useStudentNumList) {
			bysjglxt_student_user bysjglxt_student_user = studentInformationManagementDao.getStudentByNum(student_num);
			flag = studentInformationManagementDao
					.deleteStudentBasicInfoById(bysjglxt_student_user.getUser_student_basic());
			flag = studentInformationManagementDao.deleteStudentInfoById(bysjglxt_student_user.getUser_student_id());
		}
		return flag;
	}

}
