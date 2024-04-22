package com.aston.stockapp.domain.learn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/learn")
public class LearnController {

    @Autowired
    private LearningContentRepository learningContentRepository;

    private Map<String, Long> calculateCategoryCounts(List<LearningContent> contentList) {
        Map<String, Long> categoryCounts = contentList.stream().collect(Collectors.groupingBy(LearningContent::getCategory, Collectors.counting()));
        categoryCounts.put("All", (long) contentList.size());
        return categoryCounts;
    }

    @GetMapping("")
    public String showLearningContent(Model model) {
        List<LearningContent> contentList = learningContentRepository.findAll();
        Map<String, Long> categoryCounts = calculateCategoryCounts(contentList);

        model.addAttribute("contentList", contentList);
        model.addAttribute("categoryCounts", categoryCounts);
        return "learn";
    }

    @GetMapping("/category/{category}")
    public String filterLearningContent(@PathVariable String category, Model model) {
        List<LearningContent> filteredContent = "All".equalsIgnoreCase(category) ?
                learningContentRepository.findAll() : learningContentRepository.findByCategory(category);

        // Recalculate category counts based on all content to keep the sidebar accurate
        List<LearningContent> allContentList = learningContentRepository.findAll();
        Map<String, Long> categoryCounts = calculateCategoryCounts(allContentList);

        model.addAttribute("contentList", filteredContent);
        model.addAttribute("categoryCounts", categoryCounts);
        model.addAttribute("filterCategory", category);
        return "learn";
    }
}