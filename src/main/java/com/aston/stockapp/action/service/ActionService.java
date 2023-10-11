package com.aston.stockapp.action.service;

import com.aston.stockapp.action.model.Action;
import com.aston.stockapp.action.repository.ActionRepository;

import java.util.List;

public class ActionService {

    private ActionRepository actionRepository;

    public List<Action> getAllActions() {
        return actionRepository.findAll();
    }

    public Action createAction(Action action) {
        return actionRepository.save(action);
    }
}