package com.bysjglxt.dao;

import com.bysjglxt.domain.DO.bysjglxt_defence;
import com.bysjglxt.domain.DO.bysjglxt_evaluate_review;
import com.bysjglxt.domain.DO.bysjglxt_evaluate_tutor;
import com.bysjglxt.domain.DO.bysjglxt_examination_formal;
import com.bysjglxt.domain.DO.bysjglxt_record_progress;
import com.bysjglxt.domain.DO.bysjglxt_report_opening;
import com.bysjglxt.domain.DO.bysjglxt_summary;
import com.bysjglxt.domain.DO.bysjglxt_taskbook;

public interface GraduationProjectManagementDao {

	public int fillEmptyInTaskBook(bysjglxt_taskbook bysjglxt_taskbook);

	public int fillEmptyInOpening(bysjglxt_report_opening bysjglxt_report_opening);

	public int fillEmptyInProgressEarlystage(bysjglxt_record_progress bysjglxt_record_progressEarlystage);

	public int fillEmptyInSummary(bysjglxt_summary bysjglxt_summary);

	public int fillEmptyInExaminationFormal(bysjglxt_examination_formal bysjglxt_examination_formal);

	public int fillEmptyInEvaluateTutor(bysjglxt_evaluate_tutor bysjglxt_evaluate_tutor);

	public int fillEmptyEvaluateReview(bysjglxt_evaluate_review bysjglxt_evaluate_review);

	public int fillEmptyDefence(bysjglxt_defence bysjglxt_defence);

	public com.bysjglxt.domain.DO.bysjglxt_taskbook getTaskbookById(String taskbook_id);

	public com.bysjglxt.domain.DO.bysjglxt_report_opening getReportOpening(String report_opening_id);

	public bysjglxt_record_progress getRecordProgress(String record_progress_id);

	public com.bysjglxt.domain.DO.bysjglxt_record_progress findRecordProgressByuserIdAndStage(
			String report_opening_student, String string);

}