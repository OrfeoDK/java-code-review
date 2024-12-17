package schwarz.jobs.interview.coupon.core.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.core.model.Basket;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponRequestDTO;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public Optional<Coupon> getCoupon(final String code) {
        return couponRepository.findByCode(code);
    }

    public Optional<Basket> apply(final Basket basket, final String code) {

        return getCoupon(code).map(coupon -> {

            double basketValue = basket.getValue().doubleValue();

            if (basketValue < 0) {
                throw new IllegalArgumentException("Can't apply negative discounts");
            }
            else if (basketValue > 0) {
                basket.applyDiscount(coupon.getDiscount());
            }

            return basket;
        });
    }

    public Coupon createCoupon(final CouponDTO couponDTO) {

        Coupon coupon = null;

        try {
            coupon = Coupon.builder()
                    .code(couponDTO.getCode().toLowerCase())
                    .discount(couponDTO.getDiscount())
                    .minBasketValue(couponDTO.getMinBasketValue())
                    .build();

        } catch (final NullPointerException e) {
            // Don't coupon when code is null
            throw new IllegalArgumentException("Coupon code can't be null");
        }

        return couponRepository.save(coupon);
    }

    public List<Coupon> getCoupons(final CouponRequestDTO couponRequestDTO) {
        return couponRequestDTO.getCodes().stream()
                .map(couponRepository::findByCode)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
