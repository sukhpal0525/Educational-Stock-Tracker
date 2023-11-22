//package com.aston.stockapp.domain.action.model;
//
//import javax.persistence.*;
//import java.util.Date;
//
//@Entity
//@Table(name = "Action")
//public class Action {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "name")
//    private String name;
//
////    @Enumerated(EnumType.STRING)
////    @Column(name = "action_type")
////    private com.aston.stockapp.action.model.Category category;
//
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "timestamp")
//    private Date timestamp;
//
//    public Action() {
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
////    public com.aston.stockapp.action.model.Category getActionCategory() {
////        return category;
////    }
////
////    public void setActionType(com.aston.stockapp.action.model.Category category) {
////        this.category = category;
////    }
//
//    public Date getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(Date timestamp) {
//        this.timestamp = timestamp;
//    }
//}
//
//
