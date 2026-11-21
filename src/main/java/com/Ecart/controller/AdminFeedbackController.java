package com.Ecart.controller;

import com.Ecart.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/feedback")
public class AdminFeedbackController {

    private final FeedbackService feedbackService;

     public AdminFeedbackController(FeedbackService feedbackService) {
	 	super();
	 	this.feedbackService = feedbackService;
	 }

	@GetMapping("/average-ratings")
    public ResponseEntity<List<Map<String, Object>>> getAverageRatings() {
        return ResponseEntity.ok(feedbackService.getAverageRatingsPerProduct());
    }
}
