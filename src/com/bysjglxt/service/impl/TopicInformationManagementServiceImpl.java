package com.bysjglxt.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.bysjglxt.dao.TopicInformationManagementDao;
import com.bysjglxt.domain.DO.bysjglxt_section;
import com.bysjglxt.domain.DO.bysjglxt_teacher_basic;
import com.bysjglxt.domain.DO.bysjglxt_teacher_user;
import com.bysjglxt.domain.DO.bysjglxt_topic;
import com.bysjglxt.domain.DO.bysjglxt_topic_invite_teacher;
import com.bysjglxt.domain.DO.bysjglxt_topic_select;
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
		bysjglxt_topic newTopic = new bysjglxt_topic();
		bysjglxt_topic_invite_teacher invite_teacher = new bysjglxt_topic_invite_teacher();
		invite_teacher = topicInformationDTO.getBysjglxtTopicInviteTeacher();
		invite_teacher.setTopic_invite_teacher_id(TeamUtil.getUuid());
		invite_teacher.setTopic_invite_teacher_gmt_create(TeamUtil.getStringSecond());
		invite_teacher.setTopic_invite_teacher_gmt_modify(invite_teacher.getTopic_invite_teacher_gmt_create());
		flag = topicInformationManagementDao.createTopicInviteTeacher(invite_teacher);
		if (!flag) {
			return flag;
		}
		newTopic = topicInformationDTO.getBysjglxtTopic();
		newTopic.setTopic_id(TeamUtil.getUuid());
		newTopic.setTopic_gmt_create(TeamUtil.getStringSecond());
		newTopic.setTopic_gmt_modified(TeamUtil.getStringSecond());
		newTopic.setTopic_examine_state("未审核");
		newTopic.setTopic_teacher(
				topicInformationDTO.getTeacherInformationDTO().getBysjglxtTeacherUser().getUser_teacher_id());
		newTopic.setTopic_student_num(0);
		newTopic.setTopic_student_max(-1);
		newTopic.setTopic_invite_teache_id(invite_teacher.getTopic_invite_teacher_id());
		flag = topicInformationManagementDao.CreateTopic(newTopic);
		return flag;
	}

	@Override
	public boolean DeleteTopic(List<String> topic_id) {
		boolean flag = false;
		bysjglxt_topic bysjglxt_topic = null;
		for (String topicId : topic_id) {
			bysjglxt_topic = new bysjglxt_topic();
			// 根据课题ID获得课题对象
			bysjglxt_topic = topicInformationManagementDao.getBysjglxtTopicById(topicId);
			if (bysjglxt_topic != null) {
				// 根据课题对象中被邀请老师ID删除被邀请老师信息(这里是否能进行删除)
				flag = topicInformationManagementDao
						.deleteTopicInviteTeacher(bysjglxt_topic.getTopic_invite_teache_id());
				if (!flag)
					break;
				flag = topicInformationManagementDao.DeleteTopic(topicId);
			}
		}
		return flag;
	}

	@Override
	public boolean adoptTopic(List<String> topicID) {
		boolean flag = false;
		for (String string : topicID) {
			flag = topicInformationManagementDao.updateTopicState(string, TeamUtil.getStringSecond());
		}
		return flag;
	}

	@Override
	public boolean closeTopic(List<String> topicID) {
		boolean flag = false;
		for (String string : topicID) {
			flag = topicInformationManagementDao.closeTopic(string, TeamUtil.getStringSecond());
		}
		return flag;
	}

	@Override
	public boolean notAdoptTopic(List<String> topicID) {
		boolean flag = false;
		for (String string : topicID) {
			flag = topicInformationManagementDao.notAdoptTopic(string, TeamUtil.getStringSecond());
		}
		return flag;
	}

	@Override
	public TopicInformationManagementVO VO_Topic_By_PageAndSearch(TopicInformationManagementVO topicManagementVO) {
		List<TopicInformationManagementDTO> list_TopicInformationDTO = new ArrayList<TopicInformationManagementDTO>();
		TopicInformationManagementDTO topicInformationDTO = null;
		bysjglxt_topic_invite_teacher bysjglxt_topic_invite_teacher = null;
		List<bysjglxt_topic> list_bysjglxt_topic = new ArrayList<bysjglxt_topic>();
		TeacherInformationDTO teacherInformationDTO = null;
		bysjglxt_teacher_basic bysjglxt_teacher_basic = null;
		bysjglxt_teacher_user bysjglxt_teacher_user = null;
		bysjglxt_section bysjglxt_section = null;
		// 获得符合条件的所有课题
		list_bysjglxt_topic = topicInformationManagementDao.VO_Topic_By_PageAndSearch(topicManagementVO);
		int i = 0;
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
			bysjglxt_topic_invite_teacher = topicInformationManagementDao
					.getBysjglxtTopicInviteTeacher(tbysjglxt_topic.getTopic_invite_teache_id());
			topicInformationDTO.setBysjglxtTopicInviteTeacher(bysjglxt_topic_invite_teacher);
			list_TopicInformationDTO.add(topicInformationDTO);
			i++;
		}
		topicManagementVO.setList_TopicInformationDTO(list_TopicInformationDTO);
		System.out.println(i);
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
	public boolean selectTopic(String studentID, String topicID) {
		if (topicID == null || topicID.trim().length() <= 0) {
			return false;
		}
		if (studentID == null || studentID.trim().length() <= 0) {
			return false;
		}
		bysjglxt_topic_select bysjglxt_topic_select = new bysjglxt_topic_select();
		boolean flag = true;
		bysjglxt_topic bysjglxt_topic = new bysjglxt_topic();
		bysjglxt_teacher_user bysjglxt_teacher_user = new bysjglxt_teacher_user();
		bysjglxt_topic = topicInformationManagementDao.getBysjglxtTopicById(topicID);
		bysjglxt_teacher_user = topicInformationManagementDao.getTeacherUser(bysjglxt_topic.getTopic_teacher());
		// 1. ②判断是否达到教师可选上限
		if (bysjglxt_teacher_user != null && bysjglxt_teacher_user.getUser_teacher_max_guidance() != -1) {
			flag = topicInformationManagementDao.teacherIsSelect(bysjglxt_topic.getTopic_teacher());
		}
		if (!flag)
			return flag;
		// 2.②判断是否达到课题可选上限
		if (flag && bysjglxt_topic != null && bysjglxt_topic.getTopic_student_max() != -1) {
			flag = topicInformationManagementDao.topicIsSelect(bysjglxt_topic.getTopic_id());
		}
		if (!flag)
			return flag;
		// 创建学生选题记录
		bysjglxt_topic_select.setTopic_select_id(TeamUtil.getUuid());
		bysjglxt_topic_select.setTopic_select_student(studentID); // user表
		System.out.println(bysjglxt_teacher_user.getUser_teacher_id());
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
			return flag;
		}
		if (flag) {
			// 教师学生指导人数+1
			flag = topicInformationManagementDao.addTeacherUserSrtudentNum(bysjglxt_teacher_user.getUser_teacher_id());
		}
		return flag;
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

	@Override
	public boolean topicSelectStudent(String topicID, List<String> studentIDList) {
		boolean flag = false;
		String studentIdList = "";
		for (String string : studentIDList) {
			studentIdList = studentIdList + string + "#&#";
		}
		flag = topicInformationManagementDao.updateStudentList(topicID, studentIdList);
		return false;
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
		System.out.println(listAllBysjglxtTopic.size());
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
}
