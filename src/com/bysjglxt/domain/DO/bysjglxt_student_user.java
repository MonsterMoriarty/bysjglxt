package com.bysjglxt.domain.DO;

public class bysjglxt_student_user {

	private String user_student_id;
	private String user_student_num;
	private String user_student_password;
	private String user_student_basic;
	private String user_student_gmt_create;
	private String user_student_gmt_modified;
	private int user_student_is_operate_premission;

	@Override
	public String toString() {
		return "bysjglxt_user_student [user_student_id=" + user_student_id + ", user_student_num=" + user_student_num
				+ ", user_student_password=" + user_student_password + ", user_student_basic=" + user_student_basic
				+ ", user_student_gmt_create=" + user_student_gmt_create + ", user_student_gmt_modified="
				+ user_student_gmt_modified + ", user_student_is_operate_premission="
				+ user_student_is_operate_premission + "]";
	}

	public String getUser_student_id() {
		return user_student_id;
	}

	public void setUser_student_id(String user_student_id) {
		this.user_student_id = user_student_id;
	}

	public String getUser_student_num() {
		return user_student_num;
	}

	public void setUser_student_num(String user_student_num) {
		this.user_student_num = user_student_num;
	}

	public String getUser_student_password() {
		return user_student_password;
	}

	public void setUser_student_password(String user_student_password) {
		this.user_student_password = user_student_password;
	}

	public String getUser_student_basic() {
		return user_student_basic;
	}

	public void setUser_student_basic(String user_student_basic) {
		this.user_student_basic = user_student_basic;
	}

	public String getUser_student_gmt_create() {
		return user_student_gmt_create;
	}

	public void setUser_student_gmt_create(String user_student_gmt_create) {
		this.user_student_gmt_create = user_student_gmt_create;
	}

	public String getUser_student_gmt_modified() {
		return user_student_gmt_modified;
	}

	public void setUser_student_gmt_modified(String user_student_gmt_modified) {
		this.user_student_gmt_modified = user_student_gmt_modified;
	}

	public int getUser_student_is_operate_premission() {
		return user_student_is_operate_premission;
	}

	public void setUser_student_is_operate_premission(int user_student_is_operate_premission) {
		this.user_student_is_operate_premission = user_student_is_operate_premission;
	}

}