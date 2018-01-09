package com.bysjglxt.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.bysjglxt.domain.DO.bysjglxt_college;
import com.bysjglxt.service.CollegeManagementService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opensymphony.xwork2.ActionSupport;

public class CollegeManagementAction extends ActionSupport implements ServletResponseAware, ServletRequestAware {

	private HttpServletResponse http_response;
	private HttpServletRequest http_request;
	/*
	 * 
	 */
	private CollegeManagementService collegeManagementService;
	private String tmpString;
	private bysjglxt_college college;

	/*
	 * 
	 */
	/**
	 * 
	 * @return
	 */
	public String CollegeManagementPage() {
		return "CollegeManagementPage";
	}

	/**
	 * 遍历出所有的学院
	 * 
	 */
	public void listAllCollege() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();// 格式化json数据
		Gson gson = gsonBuilder.create();
		http_response.setContentType("text/html;charset=utf-8");
		try {
			http_response.getWriter().write(gson.toJson(collegeManagementService.listCollegeInformationDTO()));
		} catch (IOException e) {
			System.out.println("遍历学院出错");
			e.printStackTrace();
		}
	}

	/**
	 * 修改管理员
	 * 
	 */
	public void updateAdmin() {
		if (collegeManagementService.updateCollegeAdmin(tmpString) == -1) {
			try {
				http_response.getWriter().write("系统错误修改失败");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				http_response.getWriter().write("修改成功");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 添加学院
	 */
	public void addCollege() {
		System.out.println(college);
		try {
			http_response.getWriter().write(collegeManagementService.addCollege(college));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 
	 */
	/*
	 * 
	 */

	@Override
	public void setServletRequest(HttpServletRequest http_request) {
		this.http_request = http_request;
	}

	public void setCollegeManagementService(CollegeManagementService collegeManagementService) {
		this.collegeManagementService = collegeManagementService;
	}

	@Override
	public void setServletResponse(HttpServletResponse http_response) {
		this.http_response = http_response;

	}

	public HttpServletResponse getHttp_response() {
		return http_response;
	}

	public void setHttp_response(HttpServletResponse http_response) {
		this.http_response = http_response;
	}

	public HttpServletRequest getHttp_request() {
		return http_request;
	}

	public void setHttp_request(HttpServletRequest http_request) {
		this.http_request = http_request;
	}

	public String getTmpString() {
		return tmpString;
	}

	public void setTmpString(String tmpString) {
		this.tmpString = tmpString;
	}

	public bysjglxt_college getCollege() {
		return college;
	}

	public void setCollege(bysjglxt_college college) {
		this.college = college;
	}

	public CollegeManagementService getCollegeManagementService() {
		return collegeManagementService;
	}

	/*
	 * 
	 */

}
