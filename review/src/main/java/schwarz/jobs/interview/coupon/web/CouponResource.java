package schwarz.jobs.interview.coupon.web;


import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
     * @param applicationRequestDTO Provides the necessary basket and customer information required for the coupon application
     * @return ResponseEntity containing the updated Basket if the coupon application is successful, or an appropriate HTTP status if it fails.
     */
    //@ApiOperation(value = "Applies currently active promotions and coupons from the request to the requested Basket - Version 2")
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

    /** * Creates a new coupon based on the provided CouponDTO.
     *
     * @param couponDTO The data transfer object containing the details of the coupon to be created.
     * @return ResponseEntity containing the location of the newly created coupon if successful, or a bad request status with an error message if the input is invalid.
     */
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

    /**
     * Retrieves a list of coupons based on the provided CouponRequestDTO.
     *
     * @param couponRequestDTO The data transfer object containing the details of the coupon request.
     * @return A list of coupons that match the criteria specified in the CouponRequestDTO.
     * */
    @PostMapping("/coupons")
    public List<Coupon> getCoupons(@RequestBody @Valid final CouponRequestDTO couponRequestDTO) {

        return couponService.getCoupons(couponRequestDTO);
    }
}
