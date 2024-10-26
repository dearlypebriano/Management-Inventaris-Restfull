package com.management.ManagementInventaris.product.coupon;

import com.management.ManagementInventaris.handler.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserCouponService userCouponService;

    @PostMapping(path = "/create/coupon", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CouponResponse> createNewCouponCode(@RequestBody CouponRequest request) {
        CouponResponse response = couponService.generateCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(path = "/applyCoupon", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> applyCouponCode(@RequestParam("code") String code) {
        String applyCoupon = userCouponService.applyCouponToUser(code);
        return ResponseEntity.status(HttpStatus.OK).body(applyCoupon);
    }

    @PatchMapping(path = "/update/{couponId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CouponResponse> updateCouponCode(
            @PathVariable String couponId,
            @RequestBody CouponRequest request
    ) {
        CouponResponse response = couponService.updateCoupon(couponId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(path = "/delete/{couponId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteCouponCode(@PathVariable String couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(path = "/allCoupons", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<CouponResponse>>> findAllCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        WebResponse<List<CouponResponse>> response = couponService.getAllCoupons(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}