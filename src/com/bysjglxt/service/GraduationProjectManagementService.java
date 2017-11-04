package com.bysjglxt.service;

import com.bysjglxt.domain.DO.bysjglxt_defence;
import com.bysjglxt.domain.DO.bysjglxt_evaluate_review;
import com.bysjglxt.domain.DO.bysjglxt_evaluate_tutor;
import com.bysjglxt.domain.DO.bysjglxt_examination_formal;
import com.bysjglxt.domain.DO.bysjglxt_record_progress;
import com.bysjglxt.domain.DO.bysjglxt_report_opening;
import com.bysjglxt.domain.DO.bysjglxt_summary;
import com.bysjglxt.domain.DO.bysjglxt_taskbook;

public interface GraduationProjectManagementService {

	/**
	 * @说明 学生点击开启开始毕业设计流程 1、创建个人毕业设计过程管理手册中所有信息记录
	 * 
	 * @DATE 2017-11-03
	 * @param studentId
	 * @return 1.创建成功 2.创建失败
	 * 
	 */
	public int startGraduationProjectProcess(String studentId);

	/**
	 * @说明 更改任务书
	 * @param bysjglxt_taskbook
	 * @return 1.创建成功 2.创建失败
	 */
	public int updateTaskbook(bysjglxt_taskbook updateTaskbook);

	/**
	 * @说明 更改开题报告表
	 * @param updateReportOpening
	 * @return 1.成功 2.失败
	 */
	public int updateReportOpening(bysjglxt_report_opening updateReportOpening);

	/**
	 * @说明 更改前期进展情况记录
	 * @param updateRecordProgress
	 * @return 1.成功 2.失败
	 */
	public int updateRecordProgressEarlystage(bysjglxt_record_progress updateRecordProgress);

	/**
	 * @说明 更改中期进展情况记录
	 * @param updateRecordProgress
	 * @return
	 */
	public int updateRecordProgressMetaphase(bysjglxt_record_progress updateRecordProgress);

	/**
	 * @说明 更改后期进展情况记录
	 * @param updateRecordProgress
	 * @return
	 */
	public int updateRecordProgressLaterstage(bysjglxt_record_progress updateRecordProgress);

	/**
	 * @说明 更改完善期进展情况记录
	 * @param updateRecordProgress
	 * @return
	 */
	public int updateRecordProgressPerfect(bysjglxt_record_progress updateRecordProgress);

	/**
	 * @说明 更改个人学习工作总结
	 * @param bysjglxt_summary
	 * @return
	 */
	public int updateSummary(bysjglxt_summary bysjglxt_summary);

	/**
	 * @说明 更改形式审查表 是 1 否 2 无 3
	 * @param updateExaminationFormal
	 * @return
	 */
	public int updateExaminationFormal(bysjglxt_examination_formal updateExaminationFormal);

	/**
	 * @说明 更改指导教师评价表
	 * @param updateEvaluateTutor
	 * @return
	 */
	public int updateEvaluateTutor(bysjglxt_evaluate_tutor updateEvaluateTutor);

	/**
	 * @说明 更改评阅教师评阅表
	 * @param updateEvaluateReview
	 * @return
	 */
	public int bysjglxt_evaluate_review(bysjglxt_evaluate_review updateEvaluateReview);

	/**
	 * @说明 更改答辩评分以及成绩评定表
	 * @param updateDefence
	 * @return
	 */
	public int bysjglxt_defence(bysjglxt_defence updateDefence);

}
