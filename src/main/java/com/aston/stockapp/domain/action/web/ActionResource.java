//package com.aston.stockapp.action.web;
//
//import com.aston.stockapp.action.model.Action;
//import com.aston.stockapp.action.service.ActionService;
//import org.springframework.ui.Model;
//
//import java.util.List;
//import java.util.Set;
//
//public class ActionResource {
//
//    private ActionService actionService;
//
//
//    public String getAllActions(Model model) {
//        List<Action> actions = actionService.getAllActions();
//        model.addAttribute("actions", actions);
//        return "actions";
//    }
//}