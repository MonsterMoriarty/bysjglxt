package com.bysjglxt.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.bysjglxt.dao.ProcessManagementDao;
import com.bysjglxt.domain.DO.bysjglxt_notice;
import com.bysjglxt.domain.DO.bysjglxt_process_definition;
import com.bysjglxt.domain.DO.bysjglxt_process_instance;
import com.bysjglxt.domain.DO.bysjglxt_section;
import com.bysjglxt.domain.DO.bysjglxt_student_basic;
import com.bysjglxt.domain.DO.bysjglxt_student_user;
import com.bysjglxt.domain.DO.bysjglxt_task_definition;
import com.bysjglxt.domain.DO.bysjglxt_task_instance;
import com.bysjglxt.domain.DO.bysjglxt_teacher_user;
import com.bysjglxt.domain.DO.bysjglxt_topic_select;
import com.bysjglxt.domain.VO.ProcessManagementVO;

public class ProcessManagementDaoImpl implements ProcessManagementDao {
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getSession() {
		return this.sessionFactory.getCurrentSession();
	}

	@Override
	public List<String> getListStudentSelect(String process_definition_id, String college) {
		List<String> listStudentSelect = new ArrayList<>();
		String hql = "SELECT selectTopic.topic_select_student FROM bysjglxt_topic_select selectTopic, bysjglxt_student_user studentUser WHERE studentUser.user_student_belong_college='"
				+ college
				+ "' and selectTopic.topic_select_student = studentUser.user_student_id AND studentUser.user_student_is_operate_premission=1";
		hql = hql
				+ " AND selectTopic.topic_select_student NOT IN (SELECT processInstance.process_instance_man FROM bysjglxt_process_instance processInstance WHERE processInstance.process_instance_state = '活动' OR processInstance.process_instance_state = '结束' AND processInstance.process_instance_process_definition = '"
				+ process_definition_id + "')";
		Session session = getSession();
		Query query = session.createQuery(hql);
		listStudentSelect = query.list();
		return listStudentSelect;
	}

	@Override
	public int createProcessDefine(bysjglxt_process_definition selectTopicProcessDefinition) {
		int flag = 1;
		try {
			Session session = getSession();
			session.saveOrUpdate(selectTopicProcessDefinition);
		} catch (Exception e) {
			flag = -1;
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public int createTaskDefine(bysjglxt_task_definition selectTopicTaskDefine) {
		int flag = 1;
		try {
			Session session = getSession();
			session.saveOrUpdate(selectTopicTaskDefine);
		} catch (Exception e) {
			flag = -1;
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public List<bysjglxt_process_definition> getAllProcessDefinition() {
		List<bysjglxt_process_definition> listProcessDefinition = new ArrayList<bysjglxt_process_definition>();
		Session session = getSession();
		String hql = "from bysjglxt_process_definition";
		Query query = session.createQuery(hql);
		listProcessDefinition = query.list();
		return listProcessDefinition;
	}

	// 根据流程定义Id获得流程定义
	@Override
	public bysjglxt_process_definition getProcessDefinition(String process_definition_id) {
		bysjglxt_process_definition bysjglxt_process_definition = new bysjglxt_process_definition();
		Session session = getSession();
		String hql = "from bysjglxt_process_definition where process_definition_id = '" + process_definition_id + "'";
		Query query = session.createQuery(hql);
		bysjglxt_process_definition = (com.bysjglxt.domain.DO.bysjglxt_process_definition) query.uniqueResult();
		return bysjglxt_process_definition;
	}

	// 根据学生UserId获取User表
	@Override
	public bysjglxt_student_user getStudentUser(String operation) {
		bysjglxt_student_user bysjglxt_student_user = new bysjglxt_student_user();
		Session session = getSession();
		String hql = "from bysjglxt_student_user where user_student_id = '" + operation + "'";
		Query query = session.createQuery(hql);
		bysjglxt_student_user = (com.bysjglxt.domain.DO.bysjglxt_student_user) query.uniqueResult();
		return bysjglxt_student_user;
	}

	// 实例化流程实例
	@Override
	public boolean instanceProcess(bysjglxt_process_instance processInstance) {
		boolean flag = true;
		try {
			Session session = getSession();
			session.saveOrUpdate(processInstance);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	// 获得属于该流程定义的所有任务定义
	@Override
	public List<bysjglxt_task_definition> getListBelongProcess(String process_definition_id) {
		Session session = getSession();
		List<bysjglxt_task_definition> bysjglxt_task_definition = new ArrayList<bysjglxt_task_definition>();
		String hql = "from bysjglxt_task_definition where task_definition_process_definition = '"
				+ process_definition_id + "' order by task_definition_gmt_create asc";
		Query query = session.createQuery(hql);
		bysjglxt_task_definition = query.list();
		return bysjglxt_task_definition;
	}

	/**
	 * 弃用
	 */
	@Override
	public bysjglxt_task_instance taskInstanceIsExistId(String operation, String task_definition_id) {
		bysjglxt_task_instance bysjglxt_task_instance = new bysjglxt_task_instance();
		Session session = getSession();
		String hql = "from bysjglxt_task_instance where task_instance_role = '" + operation
				+ "' and task_instance_task_definition = '" + task_definition_id + "'";
		Query query = session.createQuery(hql);
		bysjglxt_task_instance = (com.bysjglxt.domain.DO.bysjglxt_task_instance) query.uniqueResult();
		return bysjglxt_task_instance;
	}

	@Override
	public boolean instanceTask(bysjglxt_task_instance taskInstance) {
		boolean flag = true;
		try {
			Session session = getSession();
			session.saveOrUpdate(taskInstance);
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public List<bysjglxt_task_instance> getListTaskInstanceByPager(ProcessManagementVO processManagementVo,
			String userID) {
		Session session = getSession();
		List<bysjglxt_task_instance> listTaskInstance = new ArrayList<bysjglxt_task_instance>();
		String hql = "select taskInstance from bysjglxt_task_instance taskInstance,bysjglxt_task_definition taskDefinition,bysjglxt_process_definition processDefinition where taskInstance.task_instance_task_definition=taskDefinition.task_definition_id and processDefinition.process_definition_id=taskDefinition.task_definition_process_definition";
		// 筛选我的
		hql = hql + " and task_instance_role='" + userID + "'";
		// 搜索
		if (processManagementVo.getSearch() != null && processManagementVo.getSearch().trim().length() > 0) {
			String search = "%" + processManagementVo.getSearch().trim() + "%";
			hql = hql + " and taskDefinition.task_definition_name like '" + search + "'";
		}
		// 状态
		if (processManagementVo.getState() != -1) {
			hql = hql + " and taskInstance.task_instance_state='" + processManagementVo.getState() + "'";
		}
		// 据流程实例ID筛选
		if (processManagementVo.getProcessInstance() != null
				&& processManagementVo.getProcessInstance().trim().length() > 0) {
			hql = hql + " and taskInstance.task_instance_process_instance='"
					+ processManagementVo.getProcessInstance().trim() + "'";
		}
		// 根据流程定义ID筛选
		if (processManagementVo.getProcessDefinition() != null
				&& processManagementVo.getProcessDefinition().trim().length() > 0) {
			hql = hql + " and processDefinition.process_definition_id='"
					+ processManagementVo.getProcessDefinition().trim() + "'";
		}
		hql = hql + " order by taskInstance.task_instance_state";
		Query query = session.createQuery(hql);
		query.setFirstResult((processManagementVo.getPageIndex() - 1) * processManagementVo.getPageSize());
		query.setMaxResults(processManagementVo.getPageSize());
		listTaskInstance = query.list();
		session.clear();
		return listTaskInstance;
	}

	// 根据任务定义Id获取任务定义
	@Override
	public bysjglxt_task_definition getTaskDefinition(String task_instance_task_definition) {
		bysjglxt_task_definition bysjglxt_task_definition = new bysjglxt_task_definition();
		Session session = getSession();
		String hql = "from bysjglxt_task_definition where task_definition_id = '" + task_instance_task_definition + "'";
		Query query = session.createQuery(hql);
		bysjglxt_task_definition = (bysjglxt_task_definition) query.uniqueResult();
		return bysjglxt_task_definition;
	}

	// 根据流程实例ID获取流程实例对象
	@Override
	public bysjglxt_process_instance getProcessInstanceById(String task_instance_process_instance) {
		bysjglxt_process_instance bysjglxt_process_instance = new bysjglxt_process_instance();
		Session session = getSession();
		String hql = "from bysjglxt_process_instance where process_instance_id = '" + task_instance_process_instance
				+ "'";
		Query query = session.createQuery(hql);
		bysjglxt_process_instance = (bysjglxt_process_instance) query.uniqueResult();
		return bysjglxt_process_instance;
	}

	@Override
	public List<bysjglxt_task_instance> getAllTaskList(ProcessManagementVO processManagementVo, String userID) {
		Session session = getSession();
		List<bysjglxt_task_instance> listTaskInstance = new ArrayList<bysjglxt_task_instance>();
		String hql = "select taskInstance from bysjglxt_task_instance taskInstance,bysjglxt_task_definition taskDefinition,bysjglxt_process_definition processDefinition where taskInstance.task_instance_task_definition=taskDefinition.task_definition_id and processDefinition.process_definition_id=taskDefinition.task_definition_process_definition";
		// 筛选我的
		hql = hql + " and task_instance_role='" + userID + "'";
		// 搜索
		if (processManagementVo.getSearch() != null && processManagementVo.getSearch().trim().length() > 0) {
			String search = "%" + processManagementVo.getSearch().trim() + "%";
			hql = hql + " and taskDefinition.task_definition_name like '" + search + "'";
		}
		// 状态
		if (processManagementVo.getState() != -1) {
			hql = hql + " and taskInstance.task_instance_state='" + processManagementVo.getState() + "'";
		}
		// 据流程实例ID筛选
		if (processManagementVo.getProcessInstance() != null
				&& processManagementVo.getProcessInstance().trim().length() > 0) {
			hql = hql + " and taskInstance.task_instance_process_instance='"
					+ processManagementVo.getProcessInstance().trim() + "'";
		}
		// 根据流程定义ID筛选
		if (processManagementVo.getProcessDefinition() != null
				&& processManagementVo.getProcessDefinition().trim().length() > 0) {
			hql = hql + " and processDefinition.process_definition_id='"
					+ processManagementVo.getProcessDefinition().trim() + "'";
		}
		hql = hql + " order by taskInstance.task_instance_state";
		Query query = session.createQuery(hql);
		listTaskInstance = query.list();
		session.clear();
		return listTaskInstance;
	}

	@Override
	public bysjglxt_task_instance getTaskInstanceByProcessInstanceIdAndTaskDefinitionId(String process_instance_id,
			String task_definition_father) {
		bysjglxt_task_instance bysjglxt_task_instance = new bysjglxt_task_instance();
		Session session = getSession();
		String hql = "from bysjglxt_task_instance where task_instance_process_instance = '" + process_instance_id
				+ "' and task_instance_task_definition = '" + task_definition_father + "'";
		Query query = session.createQuery(hql);
		bysjglxt_task_instance = (bysjglxt_task_instance) query.uniqueResult();
		return bysjglxt_task_instance;
	}

	@Override
	public bysjglxt_topic_select getStudentSelectTopicByStudentUserID(String operation) {
		bysjglxt_topic_select bysjglxt_topic_select = new bysjglxt_topic_select();
		Session session = getSession();
		String hql = "from bysjglxt_topic_select where topic_select_student = '" + operation + "'";
		Query query = session.createQuery(hql);
		bysjglxt_topic_select = (bysjglxt_topic_select) query.uniqueResult();
		return bysjglxt_topic_select;
	}

	@Override
	public bysjglxt_teacher_user getTeacherUserByNum(String topic_select_teacher_tutor) {
		bysjglxt_teacher_user bysjglxt_teacher_user = new bysjglxt_teacher_user();
		Session session = getSession();
		String hql = "from bysjglxt_teacher_user where user_teacher_id = '" + topic_select_teacher_tutor + "'";
		Query query = session.createQuery(hql);
		bysjglxt_teacher_user = (bysjglxt_teacher_user) query.uniqueResult();
		return bysjglxt_teacher_user;
	}

	@Override
	public bysjglxt_section getSectionById(String user_section_id) {
		bysjglxt_section bysjglxt_section = new bysjglxt_section();
		Session session = getSession();
		String hql = "from bysjglxt_section where section_id = '" + user_section_id + "'";
		Query query = session.createQuery(hql);
		bysjglxt_section = (bysjglxt_section) query.uniqueResult();
		return bysjglxt_section;
	}

	@Override
	public List<bysjglxt_process_instance> getListProcessInstanceByDefinitionId(String processDefinitionId) {
		List<bysjglxt_process_instance> listProcessInstance = new ArrayList<bysjglxt_process_instance>();
		Session session = getSession();
		String hql = "from bysjglxt_process_instance where process_instance_process_definition = '"
				+ processDefinitionId + "'";
		Query query = session.createQuery(hql);
		listProcessInstance = query.list();
		return listProcessInstance;
	}

	// 根据流程实例ID获取任务实例
	@Override
	public List<bysjglxt_task_instance> getListTaskInstanceByProcessInstanceId(String process_instance_id) {
		List<bysjglxt_task_instance> listTaskInstance = new ArrayList<bysjglxt_task_instance>();
		Session session = getSession();
		String hql = "from bysjglxt_task_instance where task_instance_process_instance = '" + process_instance_id + "'";
		Query query = session.createQuery(hql);
		listTaskInstance = query.list();
		return listTaskInstance;
	}

	// 根据流程实例ID删除任务实例
	@Override
	public boolean deleteTaskInstanceByProcessInstance(String process_instance_id) {
		boolean flag = true;
		try {
			Session session = getSession();
			String hql = "delete bysjglxt_task_instance where task_instance_process_instance ='" + process_instance_id
					+ "'";
			Query query = session.createQuery(hql);
			query.executeUpdate();
		} catch (HibernateException e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	// 根据流程定义ID删除流程实例
	@Override
	public boolean deleteProcessInstanceByProcessDefinitionId(String processDefinitionId) {
		boolean flag = true;
		try {
			Session session = getSession();
			String hql = "delete bysjglxt_process_instance where process_instance_process_definition ='"
					+ processDefinitionId + "'";
			Query query = session.createQuery(hql);
			query.executeUpdate();
		} catch (HibernateException e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	// 根据流程定义ID删除任务定义
	@Override
	public boolean deleteTaskDefinitionByProcessDefinitionId(String processDefinitionId) {
		boolean flag = true;
		try {
			Session session = getSession();
			String hql = "delete bysjglxt_task_definition where task_definition_process_definition ='"
					+ processDefinitionId + "'";
			Query query = session.createQuery(hql);
			query.executeUpdate();
		} catch (HibernateException e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	// 根据流程定义ID删除流程定义
	@Override
	public boolean deleteProcessDefinitionByProcessDefinitionId(String processDefinitionId) {
		boolean flag = true;
		try {
			Session session = getSession();
			String hql = "delete bysjglxt_process_definition where process_definition_id ='" + processDefinitionId
					+ "'";
			Query query = session.createQuery(hql);
			query.executeUpdate();
		} catch (HibernateException e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public bysjglxt_process_instance getProcessInstanceByDefinitionAndMan(String process_definition_id,
			String operation) {
		bysjglxt_process_instance bysjglxt_process_instance = new bysjglxt_process_instance();
		Session session = getSession();
		String hql = "from bysjglxt_process_instance where process_instance_process_definition='"
				+ process_definition_id + "' and process_instance_man='" + operation
				+ "' and process_instance_state='活动' ";
		Query query = session.createQuery(hql);
		bysjglxt_process_instance = (bysjglxt_process_instance) query.uniqueResult();
		return bysjglxt_process_instance;
	}

	// 根据角色以及状态得到个人正在进行的任务实例
	// 弃用
	@Override
	public bysjglxt_task_instance getTaskInstanceing(String userId) {
		bysjglxt_task_instance bysjglxt_task_instance = new bysjglxt_task_instance();
		Session session = getSession();
		String hql = "from bysjglxt_task_instance where task_instance_role='" + userId + "' and task_instance_state=1 ";
		Query query = session.createQuery(hql);
		bysjglxt_task_instance = (com.bysjglxt.domain.DO.bysjglxt_task_instance) query.uniqueResult();
		return bysjglxt_task_instance;
	}

	/**
	 * 根据任务实例ID获得任务实例
	 */
	@Override
	public bysjglxt_task_instance getTaskInstanceingById(String taskInstanceId) {
		bysjglxt_task_instance bysjglxt_task_instance = new bysjglxt_task_instance();
		Session session = getSession();
		String hql = "from bysjglxt_task_instance where task_instance_id='" + taskInstanceId + "'";
		Query query = session.createQuery(hql);
		bysjglxt_task_instance = (bysjglxt_task_instance) query.uniqueResult();
		return bysjglxt_task_instance;
	}

	@Override
	public bysjglxt_task_instance getTaskInstanceByFatherTaskId(String task_instance_id) {
		bysjglxt_task_instance bysjglxt_task_instance = new bysjglxt_task_instance();
		Session session = getSession();
		String hql = "from bysjglxt_task_instance where task_instance_father='" + task_instance_id + "'";
		Query query = session.createQuery(hql);
		bysjglxt_task_instance = (bysjglxt_task_instance) query.uniqueResult();
		return bysjglxt_task_instance;
	}

	@Override
	public bysjglxt_process_instance getProcessInstanceByUserAndState(String userId, String college) {
		bysjglxt_process_instance bysjglxt_process_instance = new bysjglxt_process_instance();
		Session session = getSession();
		String hql = "select instance from bysjglxt_process_instance instance,bysjglxt_student_user studentUser where studentUser.user_student_id=instance.process_instance_man and studentUser.user_student_belong_college='"
				+ college + "' and instance.process_instance_man='" + userId
				+ "' and instance.process_instance_state='活动'";
		Query query = session.createQuery(hql);
		bysjglxt_process_instance = (bysjglxt_process_instance) query.uniqueResult();
		return bysjglxt_process_instance;
	}

	@Override
	public List<bysjglxt_task_definition> getTaskDefinitionByProcessDefinitionId(
			String task_definition_process_definition) {
		List<bysjglxt_task_definition> list_bysjglxt_task_definition = new ArrayList<bysjglxt_task_definition>();
		Session session = getSession();
		String hql = "from bysjglxt_task_definition where task_definition_process_definition='"
				+ task_definition_process_definition + "' order by task_definition_gmt_create desc";
		Query query = session.createQuery(hql);
		list_bysjglxt_task_definition = query.list();
		return list_bysjglxt_task_definition;
	}

	@Override
	public bysjglxt_student_basic getStudentBasicById(String user_student_basic) {
		bysjglxt_student_basic bysjglxt_student_basic = new bysjglxt_student_basic();
		Session session = getSession();
		String hql = "from bysjglxt_student_basic where student_basic_id='" + user_student_basic + "'";
		Query query = session.createQuery(hql);
		bysjglxt_student_basic = (bysjglxt_student_basic) query.uniqueResult();
		return bysjglxt_student_basic;
	}

	@Override
	public bysjglxt_section getSectionByName(String section) {
		bysjglxt_section bysjglxt_section = new bysjglxt_section();
		Session session = getSession();
		String major = "%" + section + "%";
		String hql = "from bysjglxt_section where section_major like '" + major + "'";
		Query query = session.createQuery(hql);
		bysjglxt_section = (bysjglxt_section) query.uniqueResult();
		return bysjglxt_section;
	}

	@Override
	public void fillNoticeRecord(bysjglxt_notice bysjglxt_notice) {
		try {
			Session session = getSession();
			session.saveOrUpdate(bysjglxt_notice);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public bysjglxt_process_instance getSelectProcessInstance(String college) {
		bysjglxt_process_instance bysjglxt_process_instance = new bysjglxt_process_instance();
		Session session = getSession();
		String hql = "select processInstance from bysjglxt_teacher_user teacherUser,bysjglxt_process_instance processInstance,bysjglxt_process_definition processDefinition where teacherUser.user_teacher_belong_college='"
				+ college
				+ "' and teacherUser.user_teacher_id=processInstance.process_instance_man and processInstance.process_instance_process_definition=processDefinition.process_definition_id ";
		hql = hql
				+ " and processDefinition.process_definition_name='选题流程' and processInstance.process_instance_state='活动'";
		Query query = session.createQuery(hql);
		bysjglxt_process_instance = (bysjglxt_process_instance) query.uniqueResult();
		session.clear();
		return bysjglxt_process_instance;
	}

	// 根据流程定义的名称获得流程定义对象
	@Override
	public bysjglxt_process_definition getProcessDefinitionByName(String string) {
		bysjglxt_process_definition bysjglxt_process_definition = new bysjglxt_process_definition();
		String hql = "from bysjglxt_process_definition where process_definition_name='" + string + "'";
		Session session = getSession();
		Query query = session.createQuery(hql);
		bysjglxt_process_definition = (bysjglxt_process_definition) query.uniqueResult();
		session.clear();
		return bysjglxt_process_definition;
	}

	// 根据学生userID获取学生姓名
	@Override
	public String getStudentNameByUserId(String stringId) {
		String hql = "select basic.student_basic_name from bysjglxt_student_basic basic,bysjglxt_student_user user where user.user_student_basic=basic.student_basic_id and user.user_student_id='"
				+ stringId + "'";
		Session session = getSession();
		Query query = session.createQuery(hql);
		return query.uniqueResult().toString();
	}

	// 根据学院获取该学院管理员
	@Override
	public List<bysjglxt_teacher_user> getListAdminCollegeByCollege(String user_student_belong_college) {
		List<bysjglxt_teacher_user> list_bysjglxt_teacher_user = new ArrayList<bysjglxt_teacher_user>();
		Session session = getSession();
		String hql = "from bysjglxt_teacher_user where user_teacher_is_college_admin=1 and user_teacher_belong_college='"
				+ user_student_belong_college + "'";
		Query query = session.createQuery(hql);
		list_bysjglxt_teacher_user = query.list();
		return list_bysjglxt_teacher_user;
	}

	// 根据专业Id获取教研室对象
	@Override
	public bysjglxt_section getSectionByMajorId(String user_student_belong_major) {
		Session session = getSession();
		bysjglxt_section bysjglxt_section = new bysjglxt_section();
		String hql = "select section from bysjglxt_section section,bysjglxt_major major where section.section_id=major.major_belong_section and major.major_id='"
				+ user_student_belong_major + "'";
		Query query = session.createQuery(hql);
		bysjglxt_section = (bysjglxt_section) query.uniqueResult();
		return bysjglxt_section;
	}

}
