var userTeacherDTO = null;
var userStudentDTO = null;
var admin = null;
function getUserSessionForAjax() {
	var xhr = false;
	xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function() {
		var message;
		if (xhr.readyState == 4) {
			if (xhr.status == 200) {
				/*
				 * 
				 */
				try {
					var userJsonDTO = JSON.parse(xhr.responseText);
					console.debug(xhr.responseText);
				} catch (e) {
					toastr.error("登录状态失效");
					window.location = "/bysjglxt/loginLogout/LoginLogoutManagement_logout";
					return;
				}
				/*
				 * 
				 */
				if (userJsonDTO.bysjglxtStudentUser != null) {
					userStudentDTO = userJsonDTO;
					//
					var USER_NAME = document.getElementById("USER_NAME");
					USER_NAME.innerHTML = userStudentDTO.bysjglxtStudentBasic.student_basic_name;
					roleControl();
				} else if (userJsonDTO.bysjglxtTeacherUser != null) {
					userTeacherDTO = userJsonDTO;
					//
					var USER_NAME = document.getElementById("USER_NAME");
					USER_NAME.innerHTML = userTeacherDTO.bysjglxtTeacherBasic.name;
					roleControl();
				} else if (userJsonDTO.admin_id != null) {
					admin = userJsonDTO;
					//
					var USER_NAME = document.getElementById("USER_NAME");
					USER_NAME.innerHTML = '系统管理员';
					roleControl();
				} else {
					roleControl();
					toastr.error("登录状态失效");
				}
				/*
				 * 
				 */

				/*
				 * 
				 */

				if (userJsonDTO.admin_id != null) {

				} else {
					List_Navbar_Notice();
				}
				/*
				 * 获取选题流程
				 */
				getTopicCurrentProcess();
				/*
				 * 
				 */
				try {
					List_Student_By_PageAndSearch(1);
				} catch (e) {
					try {
						List_Teacher_By_PageAndSearch(1);
					} catch (e) {
						try {
							List_Section_By_Page(1);
						} catch (e) {
							try {
								List_Topic_By_PageAndSearch(1);
							} catch (e) {
								try {
									List_MyTopic_By_PageAndSearch(1);
								} catch (e) {
									try {
										List_ProcessDefinition(1);
									} catch (e) {
										try {
											List_MyTask(1);
										} catch (e) {
											try {
												List_MyTutorGraduationProject_By_PageAndSearch(1);
											} catch (e) {
												try {
													List_Notice_By_PageAndSearch(1);
												} catch (e) {
													try {
														List_MyManagementGraduationProject_By_PageAndSearch(1);
													} catch (e) {
														try {
															List_MyReviewGraduationProject_By_PageAndSearch(1);
														} catch (e) {
															try {
																List_College();
															} catch (e) {
																try {
																	List_Comment_By_College(1);
																} catch (e) {
																	try {
																		List_Major_By_Page(1);
																	} catch (e) {
																	}
																}
															}
														}
													}
												}

											}
										}
									}
								}
							}
						}
					}
				}
				/*
				 * 
				 */

			} else {
				toastr.error(xhr.status);
			}
		}
	}
	var formData = new FormData();
	xhr
			.open("POST",
					"/bysjglxt/loginLogout/LoginLogoutManagement_getUserSessionForAjax");
	xhr.send(formData);
}