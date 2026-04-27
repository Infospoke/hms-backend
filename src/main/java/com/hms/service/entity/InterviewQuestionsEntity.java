package com.hms.service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_interview_questions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterviewQuestionsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer interviewQuestionId;

    private Integer jobId;

    private Integer questionId;

    private Integer assignedWeightage;

}