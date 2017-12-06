package com.bysjglxt.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.bysjglxt.dao.TopicInformationManagementDao;
import com.bysjglxt.domain.DO.bysjglxt_leader;
import com.bysjglxt.domain.DO.bysjglxt_notice;
import com.bysjglxt.domain.DO.bysjglxt_process_instance;
import com.bysjglxt.domain.DO.bysjglxt_section;
import com.bysjglxt.domain.DO.bysjglxt_student_basic;
import com.bysjglxt.domain.DO.bysjglxt_student_user;
import com.bysjglxt.domain.DO.bysjglxt_task_definition;
import com.bysjglxt.domain.DO.bysjglxt_task_instance;
import com.bysjglxt.domain.DO.bysjglxt_teacher_basic;
import com.bysjglxt.domain.DO.bysjglxt_teacher_user;
import com.bysjglxt.domain.DO.bysjglxt_topic;
import com.bysjglxt.domain.DO.bysjglxt_topic_select;
import com.bysjglxt.domain.DTO.DesignationStudentInformationDTO;
import com.bysjglxt.domain.DTO.StudentInformationDTO;
import com.bysjglxt.domain.DTO.TeacherInformationDTO;
import com.bysjglxt.domain.DTO.TopicInformationManagementDTO;
import com.bysjglxt.domain.VO.TopicInformationManagementVO;
import com.bysjglxt.service.TopicInformationManagementService;

import util.TeamUtil;

public class TopicInformationManagementServiceImpl implements TopicInformationManagementService {

	// Dao层的注入
	private TopicInformationManagementDao topicInformationManagementDao;

	public TopicInformationManagementDao getTopicInformationManagementDao() {
		return topicInformationManagementDao;
	}

	public void setTopicInformationManagementDao(TopicInformationManagementDao topicInformationManagementDao) {
		this.topicInformationManagementDao = topicInformationManagementDao;
	}

	// 添加评阅老师
	@Override
	public int assignment(String selectId, String reviewId) {
		boolean flag = false;
		bysjglxt_topic_select bysjglxt_topic_select = new bysjglxt_topic_select();
		bysjglxt_task_instance bysjglxt_task_instance = new bysjglxt_task_instance();
		bysjglxt_task_definition bysjglxt_task_definition = new bysjglxt_task_definition();
		bysjglxt_process_instance bysjglxt_process_instance = new bysjglxt_process_instance();
		// 根据选题Id获取选题表信息
		bysjglxt_topic_select = topicInformationManagementDao.getSelectTopicById(selectId);
		if (bysjglxt_topic_select == null)
			return -1;
		bysjglxt_topic_select.setTopic_select_teacher_review(reviewId);
		bysjglxt_topic_select.setTopic_select_gmt_modified(TeamUtil.getStringSecond());
		flag = topicInformationManagementDao.createStudentSclectInformation(bysjglxt_topic_select);
		if (!flag)
			return -1;
		// 判断该学生是否开启毕业设计流程
		// 用man.state.bysjglxt_process_definitionName得到该学生流程实例表
		bysjglxt_process_instance = topicInformationManagementDao
				.getProcessInstanceByManStateAndName(bysjglxt_topic_select.getTopic_select_student());
		if (bysjglxt_process_instance != null) {
			// 根据任务定义名获取任务定义表
			bysjglxt_task_definition = topicInformationManagementDao.getTaskDefinitionByName("评阅老师填写评阅审查表");
			if (bysjglxt_task_definition != null) {
				// 根据流程实例Id以及任务定义ID可以获取任务实例表
				bysjglxt_task_instance = topicInformationManagementDao.getTaskInstanceByNameAndProcessInstanceId(
						bysjglxt_task_definition.getTask_definition_id(),
						bysjglxt_process_instance.getProcess_instance_id());
				if (bysjglxt_task_instance != null) {
					bysjglxt_task_instance
							.setTask_instance_role(bysjglxt_topic_select.getTopic_select_teacher_review());
				}
			}
		}
		return 1;
	}

	@Override
	public boolean CreateTopic(TopicInformationManagementDTO topicInformationDTO) {
		boolean flag = false;
		/**
		 * 判断教师userId是否符合格式
		 */
		flag = topicInformationManagementDao.teacherIsUserId(
				topicInformationDTO.getTeacherInformationDTO().getBysjglxtTeacherUser().getUser_teacher_id());
		if (!flag) {
			return flag;
		}
		/**
		 * 
		 * 这里的判断是否有存在的必要
		 * 
		 */
		bysjglxt_topic newTopic = new bysjglxt_topic();
		newTopic = topicInformationDTO.getBysjglxtTopic();
		if (newTopic != null) {
			newTopic.setTopic_id(TeamUtil.getUuid());
			newTopic.setTopic_gmt_create(TeamUtil.getStringSecond());
			newTopic.setTopic_gmt_modified(TeamUtil.getStringSecond());
			newTopic.setTopic_examine_state("未审核");
			newTopic.setTopic_teacher(
					topicInformationDTO.getTeacherInformationDTO().getBysjglxtTeacherUser().getUser_teacher_id());
			newTopic.setTopic_student_num(0);
			newTopic.setTopic_student_max(-1);
			flag = topicInformationManagementDao.CreateTopic(newTopic);
		} else {
			return false;
		}
		if (!flag)
			return false;
		// 添加通知记录
		bysjglxt_notice bysjglxt_notice = new bysjglxt_notice();
		List<bysjglxt_leader> listLeader = new ArrayList<bysjglxt_leader>();
		bysjglxt_notice.setNotice_id(TeamUtil.getStringSecond());
		bysjglxt_notice.setNotice_launch(
				topicInformationDTO.getTeacherInformationDTO().getBysjglxtTeacherUser().getUser_teacher_id());
		// 获取领导小组
		listLeader = topicInformationManagementDao.getAllLeader();
		bysjglxt_notice.setNotice_belong(listLeader.get(0).getLeader_teacher_id());
		bysjglxt_notice.setNotice_content("您有新的课题未审核");
		bysjglxt_notice.setNotice_state(2);
		bysjglxt_notice.setNotice_gmt_create(TeamUtil.getStringSecond());
		bysjglxt_notice.setNotice_gmt_modified(bysjglxt_notice.getNotice_gmt_create());
		flag = topicInformationManagementDao.createNoti1ceRecord(bysjglxt_notice);
		return flag;
	}

	@Override
	public boolean DeleteTopic(List<String> topic_id) {
		boolean flag = false;
		bysjglxt_topic bysjglxt_topic = null;
		List<bysjglxt_topic_select> listTopicSelect = new ArrayList<bysjglxt_topic_select>();
		for (String topicId : topic_id) {
			bysjglxt_topic = new bysjglxt_topic();
			// 根据课题ID获得课题对象
			bysjglxt_topic = topicInformationManagementDao.getBysjglxtTopicById(topicId);
			if ("已关闭".equals(bysjglxt_topic.getTopic_examine_state())) {
				continue;
			}
			if (bysjglxt_topic != null) {
				// 删除选题的话需要将学生选题表里面的关于该课题记录删除
				// 1.先获得学生选题表关于该选题集合
				listTopicSelect = topicInformationManagementDao.getTopicSelectList(bysjglxt_topic.getTopic_id());
				if (listTopicSelect.size() > 0) {
					for (bysjglxt_topic_select bysjglxt_topic_select : listTopicSelect) {
						// 根据选题表中学生字段将学生登录表中的选题字段变化
						flag = topicInformationManagementDao
								.updateStudentUserNotSelect(bysjglxt_topic_select.getTopic_select_student());
						if (!flag)
							break;
						// 删除学生选题表
						flag = topicInformationManagementDao
								.deleteTopicSelect(bysjglxt_topic_select.getTopic_select_id());
						if (!flag)
							break;
					}
				}
				flag = topicInformationManagementDao.DeleteTopic(topicId);
			}
		}
		return flag;
	}

	@Override
	public boolean adoptTopic(List<String> topicID) {
		boolean flag = false;
		bysjglxt_topic bysjglxt_topic = null;
		bysjglxt_notice bysjglxt_notice = null;
		List<bysjglxt_leader> listLeader = new ArrayList<bysjglxt_leader>();
		bysjglxt_leader bysjglxt_leader = new bysjglxt_leader();
		listLeader = topicInformationManagementDao.getAllLeader();
		bysjglxt_leader = listLeader.get(0);
		for (String string : topicID) {
			bysjglxt_notice = new bysjglxt_notice();
			bysjglxt_topic = new bysjglxt_topic();
			bysjglxt_topic = topicInformationManagementDao.getBysjglxtTopicById(string);
			if (bysjglxt_topic == null) {
				return false;
			} else {
				if ("已关闭".equals(bysjglxt_topic.getTopic_examine_state())) {
					continue;
				}
			}
			flag = topicInformationManagementDao.updateTopicState(string, TeamUtil.getStringSecond());
			bysjglxt_notice.setNotice_id(TeamUtil.getUuid());
			bysjglxt_notice.setNotice_launch(bysjglxt_leader.getLeader_teacher_id());
			bysjglxt_notice.setNotice_belong(bysjglxt_topic.getTopic_teacher());
			bysjglxt_notice.setNotice_content("您的课题已通过审核");
			bysjglxt_notice.setNotice_state(2);
			bysjglxt_notice.setNotice_gmt_create(TeamUtil.getStringSecond());
			bysjglxt_notice.setNotice_gmt_modified(bysjglxt_notice.getNotice_gmt_create());
			flag = topicInformationManagementDao.createNoti1ceRecord(bysjglxt_notice);
		}
		return flag;
	}

	@Override
	public boolean closeTopic(List<String> topicID) {
		boolean flag = false;
		bysjglxt_topic bysjglxt_topic = null;
		for (String string : topicID) {
			// 只有已通过的课题可以进行关闭
			bysjglxt_topic = new bysjglxt_topic();
			// 根据课题Id获取课题对象
			bysjglxt_topic = topicInformationManagementDao.getBysjglxtTopicById(string);
			if (bysjglxt_topic == null) {
				return false;
			}
			if (!("审核已通过".equals(bysjglxt_topic.getTopic_examine_state()))) {
				continue;
			}
			flag = topicInformationManagementDao.closeTopic(string, TeamUtil.getStringSecond());
		}
		return flag;
	}

	@Override
	public boolean notAdoptTopic(List<String> topicID) {
		boolean flag = false;
		bysjglxt_topic bysjglxt_topic = null;
		bysjglxt_notice bysjglxt_notice = null;
		List<bysjglxt_leader> listLeader = new ArrayList<bysjglxt_leader>();
		listLeader = topicInformationManagementDao.getAllLeader();
		bysjglxt_leader bysjglxt_leader = new bysjglxt_leader();
		bysjglxt_leader = listLeader.get(0);
		for (String string : topicID) {
			bysjglxt_notice = new bysjglxt_notice();
			bysjglxt_topic = new bysjglxt_topic();
			bysjglxt_topic = topicInformationManagementDao.getBysjglxtTopicById(string);
			if ("已关闭".equals(bysjglxt_topic.getTopic_examine_state())) {
				continue;
			}
			flag = topicInformationManagementDao.notAdoptTopic(string, TeamUtil.getStringSecond());
			bysjglxt_notice.setNotice_id(TeamUtil.getUuid());
			bysjglxt_notice.setNotice_launch(bysjglxt_leader.getLeader_teacher_id());
			bysjglxt_notice.setNotice_belong(bysjglxt_topic.getTopic_teacher());
			bysjglxt_notice.setNotice_content("课题未通过审核");
			bysjglxt_notice.setNotice_state(2);
			bysjglxt_notice.setNotice_gmt_create(TeamUtil.getStringSecond());
			bysjglxt_notice.setNotice_gmt_modified(bysjglxt_notice.getNotice_gmt_create());
			flag = topicInformationManagementDao.createNoti1ceRecord(bysjglxt_notice);
		}
		return flag;
	}

	@Override
	public TopicInformationManagementVO VO_Topic_By_PageAndSearch(TopicInformationManagementVO topicManagementVO,
			int studentOrTeacher) {
		List<TopicInformationManagementDTO> list_TopicInformationDTO = new ArrayList<TopicInformationManagementDTO>();
		TopicInformationManagementDTO topicInformationDTO = null;
		List<bysjglxt_topic> list_bysjglxt_topic = new ArrayList<bysjglxt_topic>();
		List<bysjglxt_topic> listAll = new ArrayList<bysjglxt_topic>();
		TeacherInformationDTO teacherInformationDTO = null;
		bysjglxt_teacher_basic bysjglxt_teacher_basic = null;
		bysjglxt_teacher_user bysjglxt_teacher_user = null;
		bysjglxt_section bysjglxt_section = null;
		// 获得符合条件的10条课题
		list_bysjglxt_topic = topicInformationManagementDao.VO_Topic_By_PageAndSearch(topicManagementVO,
				studentOrTeacher);
		System.out.println(list_bysjglxt_topic.size());
		for (bysjglxt_topic tbysjglxt_topic : list_bysjglxt_topic) {
			topicInformationDTO = new TopicInformationManagementDTO();
			// 在DTO里面设置TeacherInformationDTO
			teacherInformationDTO = new TeacherInformationDTO();
			bysjglxt_teacher_basic = new bysjglxt_teacher_basic();
			bysjglxt_teacher_user = new bysjglxt_teacher_user();
			bysjglxt_section = new bysjglxt_section();
			bysjglxt_teacher_user = topicInformationManagementDao
					.getTeacherUserInfo(tbysjglxt_topic.getTopic_teacher());
			if (bysjglxt_teacher_user != null) {
				teacherInformationDTO.setBysjglxtTeacherUser(bysjglxt_teacher_user);
				bysjglxt_teacher_basic = topicInformationManagementDao
						.getTeacherBasicInfo(bysjglxt_teacher_user.getUser_teacher_basic());
				teacherInformationDTO.setBysjglxtTeacherBasic(bysjglxt_teacher_basic);
				bysjglxt_section = topicInformationManagementDao
						.getTeacherSection(bysjglxt_teacher_user.getUser_teacher_section());
				teacherInformationDTO.setBysjglxtSection(bysjglxt_section);
			}
			topicInformationDTO.setTeacherInformationDTO(teacherInformationDTO);
			// 在DTO里面设置被邀请课题信息
			topicInformationDTO.setBysjglxtTopic(tbysjglxt_topic);
			// 根据课题表中被邀请老师ID，获得被邀请老师的信息
			list_TopicInformationDTO.add(topicInformationDTO);
		}
		topicManagementVO.setList_TopicInformationDTO(list_TopicInformationDTO);
		int i = 0;
		listAll = topicInformationManagementDao.VO_Topic_BySearch(topicManagementVO, studentOrTeacher);
		i = listAll.size();
		topicManagementVO.setTotalRecords(i);
		topicManagementVO.setTotalPages(((i - 1) / topicManagementVO.getPageSize()) + 1);
		if (topicManagementVO.getPageIndex() <= 1) {
			topicManagementVO.setHavePrePage(false);
		} else {
			topicManagementVO.setHavePrePage(true);
		}
		if (topicManagementVO.getPageIndex() >= topicManagementVO.getTotalPages()) {
			topicManagementVO.setHaveNextPage(false);
		} else {
			topicManagementVO.setHaveNextPage(true);
		}
		return topicManagementVO;
	}

	@Override
	public int selectTopic(String studentID, String topicID) {
		if (topicID == null || topicID.trim().length() <= 0) {
			return -4;
		}
		if (studentID == null || studentID.trim().length() <= 0) {
			return -4;
		}
		bysjglxt_topic_select bysjglxt_topic_select = new bysjglxt_topic_select();
		boolean flag = true;
		bysjglxt_topic bysjglxt_topic = new bysjglxt_topic();
		bysjglxt_teacher_user bysjglxt_teacher_user = new bysjglxt_teacher_user();
		bysjglxt_student_user bysjglxt_student_user = new bysjglxt_student_user();
		bysjglxt_topic = topicInformationManagementDao.getBysjglxtTopicById(topicID);
		bysjglxt_teacher_user = topicInformationManagementDao.getTeacherUser(bysjglxt_topic.getTopic_teacher());
		// 1. ②判断是否达到教师可选上限
		if (bysjglxt_teacher_user != null && bysjglxt_teacher_user.getUser_teacher_max_guidance() != -1) {
			flag = topicInformationManagementDao.teacherIsSelect(bysjglxt_topic.getTopic_teacher());
		}
		if (!flag)
			return -1;
		// 2.②判断是否达到课题可选上限
		if (flag && bysjglxt_topic != null && bysjglxt_topic.getTopic_student_max() != -1) {
			flag = topicInformationManagementDao.topicIsSelect(bysjglxt_topic.getTopic_id());
		}
		if (!flag)
			return -2;
		// 3.③学生是否已选题
		if (flag) {
			bysjglxt_student_user = topicInformationManagementDao.studentIsSelectTopic(studentID);
			if (bysjglxt_student_user != null) {
				if (bysjglxt_student_user.getUser_student_is_select_topic() == 1) {
					return -3;
				}
				// 判断学生是否有选题权限
				if (bysjglxt_student_user.getUser_student_is_operate_premission() == 0) {
					return -5;
				}
			} else {
				return -4;
			}
		}

		// 创建学生选题记录
		bysjglxt_topic_select.setTopic_select_id(TeamUtil.getUuid());
		bysjglxt_topic_select.setTopic_select_student(studentID); // user表
		bysjglxt_topic_select.setTopic_select_teacher_tutor(bysjglxt_teacher_user.getUser_teacher_id());
		bysjglxt_topic_select.setTopic_select_teacher_review("");
		bysjglxt_topic_select.setTopic_select_topic(topicID);
		bysjglxt_topic_select.setTopic_select_gmt_create(TeamUtil.getStringSecond());
		bysjglxt_topic_select.setTopic_select_gmt_modified(bysjglxt_topic_select.getTopic_select_gmt_create());
		flag = topicInformationManagementDao.createStudentSclectInformation(bysjglxt_topic_select);
		if (flag) {
			// 课题记录中数据增加1
			flag = topicInformationManagementDao.addTopicStudentNum(topicID);
		} else {
			return -4;
		}
		if (flag) {
			// 教师学生指导人数+1
			flag = topicInformationManagementDao.addTeacherUserSrtudentNum(bysjglxt_teacher_user.getUser_teacher_id());
		} else {
			return -4;
		}
		if (flag) {
			// 修改学生登陆表状态
			flag = topicInformationManagementDao.updateStudentUserRecord(studentID);
		} else {
			return -4;
		}

		// 创建学生选题通知记录
		bysjglxt_notice bysjglxt_notice = new bysjglxt_notice();
		bysjglxt_notice.setNotice_id(TeamUtil.getUuid());
		bysjglxt_notice.setNotice_launch(studentID);
		bysjglxt_notice.setNotice_belong(bysjglxt_teacher_user.getUser_teacher_id());
		bysjglxt_notice.setNotice_content("选了您的课题");
		bysjglxt_notice.setNotice_state(2);
		bysjglxt_notice.setNotice_gmt_create(TeamUtil.getStringSecond());
		bysjglxt_notice.setNotice_gmt_modified(bysjglxt_notice.getNotice_gmt_create());
		flag = topicInformationManagementDao.createNoti1ceRecord(bysjglxt_notice);
		return 1;
	}

	@Override
	public boolean teacherIsPermissionAddStudentInTopic(String teacherId, String topicId) {
		boolean flag = false;
		bysjglxt_topic bysjglxt_topic = new bysjglxt_topic();
		bysjglxt_topic = topicInformationManagementDao.getBysjglxtTopicById(topicId);
		if (bysjglxt_topic != null) {
			if (teacherId.equals(bysjglxt_topic.getTopic_teacher())) {
				flag = true;
			} else {
				flag = false;
			}
		}
		return flag;
	}

	/**
	 * 遍历出所有可以进行选择的课题
	 */
	@Override
	public List<bysjglxt_topic> listSelectBysjglxtTopic() {
		boolean flag = true;
		List<bysjglxt_topic> listAllBysjglxtTopic = new ArrayList<bysjglxt_topic>();
		List<bysjglxt_topic> listSelectBysjglxtTopic = new ArrayList<bysjglxt_topic>();
		bysjglxt_teacher_user bysjglxt_teacher_user = null;
		listAllBysjglxtTopic = topicInformationManagementDao.getAllTopic();
		for (bysjglxt_topic bysjglxt_topic : listAllBysjglxtTopic) {
			flag = true;
			bysjglxt_teacher_user = new bysjglxt_teacher_user();
			bysjglxt_teacher_user = topicInformationManagementDao.getTeacherUserInfo(bysjglxt_topic.getTopic_teacher());
			// 1. ②判断是否达到教师可选上限
			if (bysjglxt_teacher_user != null && bysjglxt_teacher_user.getUser_teacher_max_guidance() != -1) {
				flag = topicInformationManagementDao.teacherIsSelect(bysjglxt_topic.getTopic_teacher());
			}
			if (!flag) {
				continue;
			}
			// 2.②判断是否达到课题可选上限
			if (flag && bysjglxt_topic != null && bysjglxt_topic.getTopic_student_max() != -1) {
				flag = topicInformationManagementDao.topicIsSelect(bysjglxt_topic.getTopic_id());
			}
			if (!flag) {
				continue;
			}
			listSelectBysjglxtTopic.add(bysjglxt_topic);
		}
		return listSelectBysjglxtTopic;
	}

	/**
	 * 
	 * 学生点击我的课题
	 * 
	 */
	@Override
	public TopicInformationManagementDTO studentTopicInformationManagementDTO(String studentUserId) {
		TopicInformationManagementDTO topicInformationManagementDTO = new TopicInformationManagementDTO();
		bysjglxt_topic bysjglxtTopic = new bysjglxt_topic();
		TeacherInformationDTO teacherInformationDTO = new TeacherInformationDTO();
		bysjglxt_teacher_basic bysjglxtTeacherBasic = new bysjglxt_teacher_basic();
		bysjglxt_teacher_user bysjglxtTeacherUser = new bysjglxt_teacher_user();
		bysjglxt_section bysjglxtSection = new bysjglxt_section();
		bysjglxt_leader bysjglxtLeader = new bysjglxt_leader();
		bysjglxt_topic_select studentTopicSelect = new bysjglxt_topic_select();
		// 1.根据学生userId查看学生选择了哪一个课题
		studentTopicSelect = topicInformationManagementDao.getStudentTopicSelectByUserId(studentUserId);
		if (studentTopicSelect == null) {
			return null;
		} else {
			// 2.根据课题ID获取课题表中的信息
			bysjglxtTopic = topicInformationManagementDao
					.getBysjglxtTopicById(studentTopicSelect.getTopic_select_topic());
			topicInformationManagementDTO.setBysjglxtTopic(bysjglxtTopic);
			// 4.根据课题信息获取课题指导老师user表信息
			bysjglxtTeacherUser = topicInformationManagementDao.getTeacherUser(bysjglxtTopic.getTopic_teacher());
			if (bysjglxtTeacherUser != null) {
				bysjglxtTeacherBasic = topicInformationManagementDao
						.getTeacherBasicInfo(bysjglxtTeacherUser.getUser_teacher_basic());
				bysjglxtSection = topicInformationManagementDao
						.getTeacherSection(bysjglxtTeacherUser.getUser_teacher_section());
				bysjglxtLeader = topicInformationManagementDao.getLeader(bysjglxtTeacherUser.getUser_teacher_id());
			}
			teacherInformationDTO.setBysjglxtTeacherUser(bysjglxtTeacherUser);
			teacherInformationDTO.setBysjglxtTeacherBasic(bysjglxtTeacherBasic);
			teacherInformationDTO.setBysjglxtSection(bysjglxtSection);
			teacherInformationDTO.setBysjglxtLeader(bysjglxtLeader);
			topicInformationManagementDTO.setTeacherInformationDTO(teacherInformationDTO);
		}

		return topicInformationManagementDTO;
	}

	@Override
	public TopicInformationManagementVO VO_TopicBelongTeacher_By_PageAndSearch(
			TopicInformationManagementVO topicManagementVO, String teacherUserId) {
		List<TopicInformationManagementDTO> list_TopicInformationDTO = new ArrayList<TopicInformationManagementDTO>();
		TopicInformationManagementDTO topicInformationDTO = null;
		List<bysjglxt_topic> list_bysjglxt_topic = new ArrayList<bysjglxt_topic>();
		List<bysjglxt_topic> listAll = new ArrayList<bysjglxt_topic>();
		TeacherInformationDTO teacherInformationDTO = null;
		bysjglxt_teacher_basic bysjglxt_teacher_basic = null;
		bysjglxt_teacher_user bysjglxt_teacher_user = null;
		bysjglxt_section bysjglxt_section = null;
		// 获得符合条件的10条课题
		list_bysjglxt_topic = topicInformationManagementDao.VO_Topic_By_PageAndSearch(topicManagementVO, teacherUserId);
		for (bysjglxt_topic tbysjglxt_topic : list_bysjglxt_topic) {
			topicInformationDTO = new TopicInformationManagementDTO();
			// 在DTO里面设置TeacherInformationDTO
			teacherInformationDTO = new TeacherInformationDTO();
			bysjglxt_teacher_basic = new bysjglxt_teacher_basic();
			bysjglxt_teacher_user = new bysjglxt_teacher_user();
			bysjglxt_section = new bysjglxt_section();
			bysjglxt_teacher_user = topicInformationManagementDao
					.getTeacherUserInfo(tbysjglxt_topic.getTopic_teacher());
			if (bysjglxt_teacher_user != null) {
				teacherInformationDTO.setBysjglxtTeacherUser(bysjglxt_teacher_user);
				bysjglxt_teacher_basic = topicInformationManagementDao
						.getTeacherBasicInfo(bysjglxt_teacher_user.getUser_teacher_basic());
				teacherInformationDTO.setBysjglxtTeacherBasic(bysjglxt_teacher_basic);
				bysjglxt_section = topicInformationManagementDao
						.getTeacherSection(bysjglxt_teacher_user.getUser_teacher_section());
				teacherInformationDTO.setBysjglxtSection(bysjglxt_section);
			}
			topicInformationDTO.setTeacherInformationDTO(teacherInformationDTO);
			topicInformationDTO.setBysjglxtTopic(tbysjglxt_topic);
			list_TopicInformationDTO.add(topicInformationDTO);
		}
		topicManagementVO.setList_TopicInformationDTO(list_TopicInformationDTO);
		int i = 0;
		listAll = topicInformationManagementDao.VO_Topic_BySearch(topicManagementVO, teacherUserId);
		i = listAll.size();
		topicManagementVO.setTotalRecords(i);
		topicManagementVO.setTotalPages(((i - 1) / topicManagementVO.getPageSize()) + 1);
		if (topicManagementVO.getPageIndex() <= 1) {
			topicManagementVO.setHavePrePage(false);
		} else {
			topicManagementVO.setHavePrePage(true);
		}
		if (topicManagementVO.getPageIndex() >= topicManagementVO.getTotalPages()) {
			topicManagementVO.setHaveNextPage(false);
		} else {
			topicManagementVO.setHaveNextPage(true);
		}
		return topicManagementVO;
	}

	@Override
	public List<StudentInformationDTO> listStudentSelectTopic(String topicId) {
		List<StudentInformationDTO> listStudentInfoDTO = new ArrayList<StudentInformationDTO>();
		StudentInformationDTO studentInformationDTO = null;
		bysjglxt_student_user bysjglxt_student_user = null;
		bysjglxt_student_basic bysjglxt_student_basic = null;
		List<bysjglxt_topic_select> listTopicSelect = new ArrayList<bysjglxt_topic_select>();
		listTopicSelect = topicInformationManagementDao.getTopicSelectByTopicId(topicId);
		for (bysjglxt_topic_select bysjglxt_topic_select : listTopicSelect) {
			studentInformationDTO = new StudentInformationDTO();
			bysjglxt_student_user = topicInformationManagementDao
					.studentIsSelectTopic(bysjglxt_topic_select.getTopic_select_student());
			if (bysjglxt_student_user != null) {
				studentInformationDTO.setBysjglxtStudentUser(bysjglxt_student_user);
				bysjglxt_student_basic = topicInformationManagementDao
						.getStudentBasic(bysjglxt_student_user.getUser_student_basic());
				studentInformationDTO.setBysjglxtStudentBasic(bysjglxt_student_basic);
			}
			listStudentInfoDTO.add(studentInformationDTO);
		}
		return listStudentInfoDTO;
	}

	/**
	 * 指定学生选题 弃用
	 */
	@Override
	public int specialStudentSelectTopic(String studentID, String topicID) {

		if (topicID == null || topicID.trim().length() <= 0) {
			return -4;
		}
		if (studentID == null || studentID.trim().length() <= 0) {
			return -4;
		}
		boolean flag = true;
		bysjglxt_topic_select bysjglxt_topic_select = new bysjglxt_topic_select();
		bysjglxt_topic bysjglxt_topic = new bysjglxt_topic();
		bysjglxt_topic topicIsSelect = new bysjglxt_topic();
		bysjglxt_teacher_user bysjglxt_teacher_user = new bysjglxt_teacher_user();
		bysjglxt_student_user bysjglxt_student_user = new bysjglxt_student_user();
		bysjglxt_topic = topicInformationManagementDao.getBysjglxtTopicById(topicID);
		bysjglxt_teacher_user = topicInformationManagementDao.getTeacherUser(bysjglxt_topic.getTopic_teacher());
		// 因为是指定学生先选题,所以并不存在教师指导人数已达上限或者课题已达上限
		// 1.判断学生是否已经选题
		bysjglxt_student_user = topicInformationManagementDao.studentIsSelectTopic(studentID);
		if (bysjglxt_student_user != null) {
			if (bysjglxt_student_user.getUser_student_is_select_topic() == 1) {
				return -3;
			}
			// 判断学生是否有选题权限
			if (bysjglxt_student_user.getUser_student_is_operate_premission() == 0) {
				return -5;
			}
		} else {
			return -4;
		}
		// 2.判断学生是否存在于老师的指定学生中
		topicIsSelect = topicInformationManagementDao.studentIsExistSpecial(studentID, topicID);
		if (topicIsSelect == null) {
			return -6;
		} else {

			// 创建学生选题记录
			bysjglxt_topic_select.setTopic_select_id(TeamUtil.getUuid());
			bysjglxt_topic_select.setTopic_select_student(studentID); // user表
			bysjglxt_topic_select.setTopic_select_teacher_tutor(bysjglxt_teacher_user.getUser_teacher_id());
			bysjglxt_topic_select.setTopic_select_teacher_review("");
			bysjglxt_topic_select.setTopic_select_topic(topicID);
			bysjglxt_topic_select.setTopic_select_gmt_create(TeamUtil.getStringSecond());
			bysjglxt_topic_select.setTopic_select_gmt_modified(bysjglxt_topic_select.getTopic_select_gmt_create());
			flag = topicInformationManagementDao.createStudentSclectInformation(bysjglxt_topic_select);
			if (flag) {
				// 课题记录中数据增加1
				flag = topicInformationManagementDao.addTopicStudentNum(topicID);
			} else {
				return -4;
			}
			if (flag) {
				// 教师学生指导人数+1
				flag = topicInformationManagementDao
						.addTeacherUserSrtudentNum(bysjglxt_teacher_user.getUser_teacher_id());
			} else {
				return -4;
			}
			if (flag) {
				// 修改学生登陆表状态
				flag = topicInformationManagementDao.updateStudentUserRecord(studentID);
			} else {
				return -4;
			}

		}
		return 1;
	}

	@Override
	public TopicInformationManagementVO VO_TopicBelongStudent_By_PageAndSearch(
			TopicInformationManagementVO topicManagementVO, String studentUserId) {
		List<TopicInformationManagementDTO> list_TopicInformationDTO = new ArrayList<TopicInformationManagementDTO>();
		TopicInformationManagementDTO topicInformationDTO = null;
		List<bysjglxt_topic> list_bysjglxt_topic = new ArrayList<bysjglxt_topic>();
		TeacherInformationDTO teacherInformationDTO = null;
		bysjglxt_teacher_basic bysjglxt_teacher_basic = null;
		bysjglxt_teacher_user bysjglxt_teacher_user = null;
		bysjglxt_section bysjglxt_section = null;
		// 获得符合条件的课题
		list_bysjglxt_topic = topicInformationManagementDao.VO_Topic_By_StudentPageAndSearch(topicManagementVO,
				studentUserId);

		for (bysjglxt_topic tbysjglxt_topic : list_bysjglxt_topic) {
			topicInformationDTO = new TopicInformationManagementDTO();
			// 在DTO里面设置TeacherInformationDTO
			teacherInformationDTO = new TeacherInformationDTO();
			bysjglxt_teacher_basic = new bysjglxt_teacher_basic();
			bysjglxt_teacher_user = new bysjglxt_teacher_user();
			bysjglxt_section = new bysjglxt_section();
			bysjglxt_teacher_user = topicInformationManagementDao
					.getTeacherUserInfo(tbysjglxt_topic.getTopic_teacher());
			if (bysjglxt_teacher_user != null) {
				teacherInformationDTO.setBysjglxtTeacherUser(bysjglxt_teacher_user);
				bysjglxt_teacher_basic = topicInformationManagementDao
						.getTeacherBasicInfo(bysjglxt_teacher_user.getUser_teacher_basic());
				teacherInformationDTO.setBysjglxtTeacherBasic(bysjglxt_teacher_basic);
				bysjglxt_section = topicInformationManagementDao
						.getTeacherSection(bysjglxt_teacher_user.getUser_teacher_section());
				teacherInformationDTO.setBysjglxtSection(bysjglxt_section);
			}
			topicInformationDTO.setTeacherInformationDTO(teacherInformationDTO);
			// 在DTO里面设置被课题信息
			topicInformationDTO.setBysjglxtTopic(tbysjglxt_topic);
			list_TopicInformationDTO.add(topicInformationDTO);
		}
		topicManagementVO.setList_TopicInformationDTO(list_TopicInformationDTO);
		int i = list_bysjglxt_topic.size();
		topicManagementVO.setTotalRecords(i);
		topicManagementVO.setTotalPages(((i - 1) / topicManagementVO.getPageSize()) + 1);
		if (topicManagementVO.getPageIndex() <= 1) {
			topicManagementVO.setHavePrePage(false);
		} else {
			topicManagementVO.setHavePrePage(true);
		}
		if (topicManagementVO.getPageIndex() >= topicManagementVO.getTotalPages()) {
			topicManagementVO.setHaveNextPage(false);
		} else {
			topicManagementVO.setHaveNextPage(true);
		}
		return topicManagementVO;
	}

	@Override
	public int updateTopic(bysjglxt_topic topicBy) {

		bysjglxt_topic topic = new bysjglxt_topic();
		topic = topicInformationManagementDao.getBysjglxtTopicById(topicBy.getTopic_id());
		if (topic == null) {
			return -1; // 系统繁忙
		}
		topic.setTopic_name_chinese(topicBy.getTopic_name_chinese());
		topic.setTopic_name_english(topicBy.getTopic_name_english());
		topic.setTopic_requirement(topicBy.getTopic_requirement());
		topic.setTopic_source(topicBy.getTopic_source());
		topic.setTopic_type(topicBy.getTopic_type());
		topic.setTopic_examine_state("未审核");
		topic.setTopic_student_max(topicBy.getTopic_student_max());
		topic.setTopic_remark(topicBy.getTopic_remark());
		topicInformationManagementDao.CreateTopic(topic);
		// 添加通知记录
		bysjglxt_notice bysjglxt_notice = new bysjglxt_notice();
		List<bysjglxt_leader> listLeader = new ArrayList<bysjglxt_leader>();
		bysjglxt_notice.setNotice_id(TeamUtil.getStringSecond());
		bysjglxt_notice.setNotice_launch(topic.getTopic_teacher());
		// 获取领导小组
		listLeader = topicInformationManagementDao.getAllLeader();
		bysjglxt_notice.setNotice_belong(listLeader.get(0).getLeader_teacher_id());
		bysjglxt_notice.setNotice_content("您有新的课题未审核");
		bysjglxt_notice.setNotice_state(2);
		bysjglxt_notice.setNotice_gmt_create(TeamUtil.getStringSecond());
		bysjglxt_notice.setNotice_gmt_modified(bysjglxt_notice.getNotice_gmt_create());
		topicInformationManagementDao.createNoti1ceRecord(bysjglxt_notice);
		return 1;
	}

	/**
	 * 退选
	 */
	@Override
	public int dropTopic(String studentUserId) {
		boolean flag = true;
		bysjglxt_topic_select bysjglxt_topic_select = new bysjglxt_topic_select();
		bysjglxt_student_user bysjglxt_student_user = new bysjglxt_student_user();
		// 1.根据学生userID获取学生选题表
		bysjglxt_topic_select = topicInformationManagementDao.getStudentTopicSelectByUserId(studentUserId);
		// 2.根据学生user Id获取学生user表
		bysjglxt_student_user = topicInformationManagementDao.studentIsSelectTopic(studentUserId);
		if (bysjglxt_student_user == null) {
			return -3;
		}
		if (bysjglxt_topic_select == null) {
			return -3;
		}
		// 3.将学生user表中的学生选题状态改为未选题
		flag = topicInformationManagementDao.updateStudentUserNotSelect(bysjglxt_student_user.getUser_student_id());
		if (!flag)
			return -1;
		// 4.将教师的选题人数-1
		flag = topicInformationManagementDao
				.updateTeacherSelectNum(bysjglxt_topic_select.getTopic_select_teacher_tutor());
		if (!flag)
			return -1;
		// 5.将课题的选择人数减1
		flag = topicInformationManagementDao.updateTopicNum(bysjglxt_topic_select.getTopic_select_topic());
		if (!flag)
			return -1;
		// 4.删除学生选题记录
		flag = topicInformationManagementDao.deleteTopicSelect(bysjglxt_topic_select.getTopic_select_id());
		if (!flag)
			return -1;
		// 通知学生
		bysjglxt_notice bysjglxt_notice = new bysjglxt_notice();
		bysjglxt_notice.setNotice_id(TeamUtil.getUuid());
		bysjglxt_notice.setNotice_launch(studentUserId);
		bysjglxt_notice.setNotice_belong(bysjglxt_topic_select.getTopic_select_teacher_tutor());
		bysjglxt_notice.setNotice_content("学生退选课题");
		bysjglxt_notice.setNotice_gmt_create(TeamUtil.getStringSecond());
		bysjglxt_notice.setNotice_state(2);
		bysjglxt_notice.setNotice_gmt_modified(bysjglxt_notice.getNotice_gmt_create());
		flag = topicInformationManagementDao.createNoti1ceRecord(bysjglxt_notice);

		return 1;
	}

	/**
	 * 分配学生选题 -1选题失败
	 */
	@Override
	public int assignmentStudentTopic(String userId, String topic) {
		bysjglxt_student_user bysjglxt_student_user = new bysjglxt_student_user();
		bysjglxt_topic_select bysjglxt_topic_select = new bysjglxt_topic_select();
		bysjglxt_teacher_user bysjglxt_teacher_user = new bysjglxt_teacher_user();
		boolean flag = false;
		// 1.根据学生ID获取user表信息
		bysjglxt_topic bysjglxt_topic = new bysjglxt_topic();
		bysjglxt_student_user = topicInformationManagementDao.getStudentUserByUserId(userId);
		if (bysjglxt_student_user != null) {
			// 更改user表的状态
			flag = topicInformationManagementDao.updateStudentUserRecord(bysjglxt_student_user.getUser_student_id());
			if (!flag)
				return -1;
		}
		// 2.获取课题记录 课题选择人数+1
		bysjglxt_topic = topicInformationManagementDao.getBysjglxtTopicById(topic);
		if (bysjglxt_topic != null) {
			flag = topicInformationManagementDao.addTopicStudentNum(topic);
			if (!flag)
				return -1;
			// 3.获取教师记录
			bysjglxt_teacher_user = topicInformationManagementDao.getTeacherUser(bysjglxt_topic.getTopic_teacher());
			if (bysjglxt_teacher_user == null) {
				return -1;
			}
			flag = topicInformationManagementDao.addTeacherUserSrtudentNum(bysjglxt_teacher_user.getUser_teacher_id());
		}

		// 4.添加选题记录
		bysjglxt_topic_select.setTopic_select_id(TeamUtil.getUuid());
		bysjglxt_topic_select.setTopic_select_student(userId);
		bysjglxt_topic_select.setTopic_select_teacher_tutor(bysjglxt_topic.getTopic_teacher());
		bysjglxt_topic_select.setTopic_select_topic(topic);
		bysjglxt_topic_select.setTopic_select_gmt_create(TeamUtil.getStringSecond());
		bysjglxt_topic_select.setTopic_select_gmt_modified(bysjglxt_topic_select.getTopic_select_gmt_create());
		flag = topicInformationManagementDao.createStudentSclectInformation(bysjglxt_topic_select);
		if (!flag)
			return -1;
		// 通知学生
		bysjglxt_notice bysjglxt_notice = new bysjglxt_notice();
		bysjglxt_notice.setNotice_id(TeamUtil.getUuid());
		bysjglxt_notice.setNotice_launch(bysjglxt_topic.getTopic_teacher());
		bysjglxt_notice.setNotice_belong(userId);
		bysjglxt_notice.setNotice_content("老师分配你进如指定选题人选" + bysjglxt_topic.getTopic_name_chinese());
		bysjglxt_notice.setNotice_gmt_create(TeamUtil.getStringSecond());
		bysjglxt_notice.setNotice_state(2);
		bysjglxt_notice.setNotice_gmt_modified(bysjglxt_notice.getNotice_gmt_create());
		flag = topicInformationManagementDao.createNoti1ceRecord(bysjglxt_notice);
		return 1;
	}

	/**
	 * 显示所有有权限的学生并且可以进行指定
	 * 
	 * @param topicId
	 * @param studentMajor
	 * @param studentGrade
	 * @return
	 */
	@Override
	public List<DesignationStudentInformationDTO> listDesignationStudentInformation(String topicId, String studentMajor,
			String studentGrade) {
		List<bysjglxt_student_user> listStudentUser = new ArrayList<bysjglxt_student_user>();
		DesignationStudentInformationDTO designationStudentInformationDTO = null;
		List<DesignationStudentInformationDTO> listDesignation = new ArrayList<DesignationStudentInformationDTO>();
		bysjglxt_topic bysjglxt_topic = null;
		bysjglxt_student_basic student_basic = null;
		// bysjglxt_student_user
		// 获取所有拥有操作权限的学生,以及可以进行筛选
		listStudentUser = topicInformationManagementDao.getListStudentUserByDesignation(studentMajor, studentGrade);
		for (bysjglxt_student_user student_user : listStudentUser) {
			designationStudentInformationDTO = new DesignationStudentInformationDTO();
			bysjglxt_topic = new bysjglxt_topic();
			student_basic = new bysjglxt_student_basic();
			designationStudentInformationDTO.setBysjglxtStudentUser(student_user);
			if (student_user.getUser_student_basic() != null
					&& student_user.getUser_student_basic().trim().length() > 0) {
				// 根据basic_id获取baisc对象
				student_basic = topicInformationManagementDao
						.getStudentBasic(student_user.getUser_student_basic().trim());
				if (student_basic != null) {
					designationStudentInformationDTO.setBysjglxtStudentBasic(student_basic);
				}
			}
			// 判断学生是否已经被指定到当前课题
			// 根据课题ID,模糊查询学生是否被指定到当前课题
			bysjglxt_topic = topicInformationManagementDao.getTopicByIdAndStudent(student_user.getUser_student_id(),
					topicId);
			// 存在这个学生的指定
			if (bysjglxt_topic != null)
				designationStudentInformationDTO.setDesignation(2);
			listDesignation.add(designationStudentInformationDTO);
		}
		return listDesignation;
	}

	// 指定学生选题
	@Override
	public boolean distributionTopicStudent(String topicID, String studentID) {
		boolean flag = false;
		bysjglxt_notice bysjglxt_notice = null;
		bysjglxt_topic bysjglxt_topic = new bysjglxt_topic();
		bysjglxt_notice = new bysjglxt_notice();
		bysjglxt_topic = topicInformationManagementDao.getBysjglxtTopicById(topicID);
		if (bysjglxt_topic == null)
			return false;
		// 判断指定学生中是否存在某学生
		if (bysjglxt_topic.getTopic_student() != null && bysjglxt_topic.getTopic_student().trim().length() > 0) {
			if ((bysjglxt_topic.getTopic_student().trim()).indexOf(studentID) != -1) {
				// 如果包含 的话，也就是说是调用了取消指定的方法
				bysjglxt_topic.setTopic_student(bysjglxt_topic.getTopic_student().replaceAll(studentID + "#&#", ""));
				bysjglxt_notice.setNotice_content("老师取消你进入指定选题人选" + bysjglxt_topic.getTopic_name_chinese());
			} else {
				// 如果不包含,则是调用的指定的方法
				bysjglxt_topic.setTopic_student(bysjglxt_topic.getTopic_student() + studentID + "#&#");
				bysjglxt_notice.setNotice_content("老师添加你进入指定选题人选" + bysjglxt_topic.getTopic_name_chinese());
			}
		} else {
			bysjglxt_topic.setTopic_student(studentID + "#&#");
			bysjglxt_notice.setNotice_content("老师添加你进入指定选题人选" + bysjglxt_topic.getTopic_name_chinese());
		}
		// 通知学生
		bysjglxt_notice.setNotice_id(TeamUtil.getUuid());
		bysjglxt_notice.setNotice_launch(bysjglxt_topic.getTopic_teacher());
		bysjglxt_notice.setNotice_belong(studentID);
		bysjglxt_notice.setNotice_gmt_create(TeamUtil.getStringSecond());
		bysjglxt_notice.setNotice_state(2);
		bysjglxt_notice.setNotice_gmt_modified(bysjglxt_notice.getNotice_gmt_create());
		flag = topicInformationManagementDao.createNoti1ceRecord(bysjglxt_notice);
		// 课题表
		bysjglxt_topic.setTopic_gmt_modified(TeamUtil.getStringSecond());
		flag = topicInformationManagementDao.CreateTopic(bysjglxt_topic);
		return flag;
	}
}
