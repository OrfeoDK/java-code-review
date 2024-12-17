# Coupon project code review

## General annotations
* Refactored model.basket to have the same level of depth as the other core segments.
* Erased debug print message.
* The understanding of the code would benefit from using more comments and explanations about what has been implemented and the different functionalities of the project.

## Code changes

### Basket.java
* 'applyDiscount' now throws an IllegalArgumentException when discount is null.
    * Also, applicationSuccessful is turned to true when applying the discount from a coupon on the current basket successfully.

### CouponService.java
* 'apply' now throws IllegalArgumentException instead of RunTimeException when trying to apply negative discounts.
    * Also, the clarity and readability of the code has been improved by simplifying the conditions.
* 'createCoupon' now throws IllegalArgumentException("Coupon code can't be null") when coupon code is null.
* 'getCoupons' now uses streams for greater clarity. Also, it now handles the potential empty Optional values to avoid exceptions.

### CouponResource.java
* Added/expanded a comment for each one of the methods implemented.
* 'apply' now uses 'basket' instead of 'applicationRequestDTO.getBasket()' to check if the application was successful.
    * Also, improved and incorporated new informative messages about the process and errors occurred.
* '/create' now returns an HTTP 201 Created response with the location of the newly created resource when the creation of the coupon goes as expected.
    * Also, it now returns HTTP 400 Bad Request when the code is null.
* '/coupons' now uses PostMapping to allow the method to receive a request body containing CouponRequestDTO.

## Testing
* Now all tests in CouponServiceTest pass successfully.
* Added a brief description for each test for more clarity.
