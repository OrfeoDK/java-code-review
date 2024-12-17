package schwarz.jobs.interview.coupon.web;


import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.services.CouponService;
import schwarz.jobs.interview.coupon.core.model.Basket;
import schwarz.jobs.interview.coupon.web.dto.ApplicationRequestDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponRequestDTO;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class CouponResource {

    private final CouponService couponService;

    /**
     * @param applicationRequestDTO
     * @return
     */
    //@ApiOperation(value = "Applies currently active promotions and coupons from the request to the requested Basket - Version 1")
    @PostMapping(value = "/apply")
    public ResponseEntity<Basket> apply(
            //@ApiParam(value = "Provides the necessary basket and customer information required for the coupon application", required = true)
            @RequestBody @Valid final ApplicationRequestDTO applicationRequestDTO) {

        log.info("Applying coupon for basket with value {}", applicationRequestDTO.getBasket().getValue());

        final Optional<Basket> basket = couponService.apply(applicationRequestDTO.getBasket(), applicationRequestDTO.getCode());

        if (basket.isEmpty()) {
            log.warn("Coupon application failed: Coupon not found");
            return ResponseEntity.notFound().build();
        }

        if (!basket.get().isApplicationSuccessful()) {
            log.warn("Coupon application failed: Conflict in basket application");
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        log.info("Applied coupon successfully");
        return ResponseEntity.ok().body(basket.get());
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody @Valid final CouponDTO couponDTO) {

        try {
            final Coupon coupon = couponService.createCoupon(couponDTO);

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(coupon.getId()).toUri();

            return ResponseEntity.created(location).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/coupons")
    public List<Coupon> getCoupons(@RequestBody @Valid final CouponRequestDTO couponRequestDTO) {

        return couponService.getCoupons(couponRequestDTO);
    }
}
