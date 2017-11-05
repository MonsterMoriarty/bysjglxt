package com.bysjglxt.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.bysjglxt.dao.ProcessManagementDao;
import com.bysjglxt.domain.DO.bysjglxt_leader;
import com.bysjglxt.domain.DO.bysjglxt_process_definition;
import com.bysjglxt.domain.DO.bysjglxt_process_instance;
import com.bysjglxt.domain.DO.bysjglxt_section;
import com.bysjglxt.domain.DO.bysjglxt_student_user;
import com.bysjglxt.domain.DO.bysjglxt_task_definition;
import com.bysjglxt.domain.DO.bysjglxt_task_instance;

public class ProcessManagementDaoImpl implements ProcessManagementDao {
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getSession() {
		return this.sessionFactory.getCurrentSession();
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

	// 根据领导小组ID获取leader表
	@Override
	public bysjglxt_leader getLeader(String operation) {
		bysjglxt_leader bysjglxt_leader = new bysjglxt_leader();
		Session session = getSession();
		String hql = "from bysjglxt_leader where leader_teacher_id = '" + operation + "'";
		Query query = session.createQuery(hql);
		bysjglxt_leader = (com.bysjglxt.domain.DO.bysjglxt_leader) query.uniqueResult();
		return bysjglxt_leader;
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
				+ process_definition_id + "'";
		Query query = session.createQuery(hql);
		bysjglxt_task_definition = query.list();
		return bysjglxt_task_definition;
	}

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
}
