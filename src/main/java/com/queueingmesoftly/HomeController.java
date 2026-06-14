package com.queueingmesoftly;

import com.queueingmesoftly.model.SkillLevel;
import com.queueingmesoftly.service.QueueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
class HomeController {

    private final QueueService queueService;

    HomeController(QueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping("/")
    String home(Model model) {
        populateModel(model);
        return "home";
    }

    @PostMapping("/players/add")
    String addPlayer(@RequestParam String name, @RequestParam SkillLevel skillLevel, Model model) {
        queueService.addPlayer(name, skillLevel);
        populateModel(model);
        return "fragments/dashboard";
    }

    @PostMapping("/players/{id}/remove")
    String removePlayer(@PathVariable String id, Model model) {
        queueService.removePlayer(id);
        populateModel(model);
        return "fragments/dashboard";
    }

    @PostMapping("/players/{id}/join-queue")
    String joinQueue(@PathVariable String id, Model model) {
        queueService.joinQueue(id);
        populateModel(model);
        return "fragments/dashboard";
    }

    @PostMapping("/players/{id}/leave-queue")
    String leaveQueue(@PathVariable String id, Model model) {
        queueService.leaveQueue(id);
        populateModel(model);
        return "fragments/dashboard";
    }

    @PostMapping("/courts/{courtNumber}/generate-match")
    String generateMatch(@PathVariable int courtNumber, Model model) {
        queueService.generateMatch(courtNumber);
        populateModel(model);
        return "fragments/dashboard";
    }

    @PostMapping("/courts/{courtNumber}/start")
    String startMatch(@PathVariable int courtNumber, Model model) {
        queueService.startMatch(courtNumber);
        populateModel(model);
        return "fragments/dashboard";
    }

    @PostMapping("/courts/{courtNumber}/end")
    String endMatch(@PathVariable int courtNumber, Model model) {
        queueService.endMatch(courtNumber);
        populateModel(model);
        return "fragments/dashboard";
    }

    @PostMapping("/courts/auto-assign")
    String autoAssign(Model model) {
        queueService.autoAssign();
        populateModel(model);
        return "fragments/dashboard";
    }

    @GetMapping("/refresh")
    String refresh(Model model) {
        populateModel(model);
        return "fragments/dashboard";
    }

    private void populateModel(Model model) {
        model.addAttribute("waitingQueue", queueService.getWaitingQueue());
        model.addAttribute("courts", queueService.getCourts());
        model.addAttribute("allPlayers", queueService.getAllPlayers());
        model.addAttribute("gamesCompleted", queueService.getGamesCompleted());
        model.addAttribute("activeCourtCount", queueService.getActiveCourtCount());
        model.addAttribute("averageWaitTime", queueService.getAverageWaitTime());
        model.addAttribute("leaderboard", queueService.getLeaderboard());
        model.addAttribute("queueService", queueService);
        model.addAttribute("skillLevels", SkillLevel.values());
    }
}
