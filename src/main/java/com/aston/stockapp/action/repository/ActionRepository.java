package com.aston.stockapp.action.repository;

import com.aston.stockapp.action.model.Action;
import com.aston.stockapp.action.model.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionRepository extends CrudRepository<Action, Long>, JpaRepository<Action, Long> {

    List<Action> findAllByCategory(ActionType actionType);
    List<Action> findAll();
    List<Action> findByNameContainingIgnoreCase(String query);
}