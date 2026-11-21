package com.Ecart.controller;

import com.Ecart.dto.FeedbackDto;
import com.Ecart.model.Customer;
import com.Ecart.model.Feedback;
import com.Ecart.model.Product;
import com.Ecart.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/add")
    public ResponseEntity<FeedbackDto> giveFeedback(@RequestBody FeedbackDto feedbackDto) {
        FeedbackDto saved = feedbackService.saveFeedback(feedbackDto);
        return ResponseEntity.ok(saved);
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFeedbackByProductIdAndCustomerId(
            @RequestParam Long customerId,
            @RequestParam Long productId) {
        feedbackService.deleteFeedbackByProductIdAndCustomerId(customerId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public List<Feedback> getAllFeedbacgetkForOrder() {
        return feedbackService.getAllFeedbacgetkForOrder();
    }
    
    @GetMapping("/product/{productid}")
    public List<Feedback> getAllFeedbackByProductId(@PathVariable("productid") Long productId) {

        return feedbackService.getAllFeedbackByProductId(productId);
    }
}