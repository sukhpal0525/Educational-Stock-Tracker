package com.aston.stockapp.domain.learn;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "learning_content")
public class LearningContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "category")
    private String category;

    @Column(name = "summary")
    private String summary;

    @Column(name = "detail", length = 4096)
    private String detail;

}