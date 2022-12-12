package vn.namnt.nabweather.domain

/**
 * @author namnt
 * @since 12/12/2022
 */

class DomainException @JvmOverloads constructor(
    val code: Int,
    override val message: String? = null,
    override val cause: Throwable? = null
): RuntimeException(message, cause)
