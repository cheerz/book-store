package com.example.bookstock.observability

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RequestIdFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val requestId = RequestId.fromCaller(request.getHeader(RequestId.HEADER))
        response.setHeader(RequestId.HEADER, requestId)
        RequestId.runWith(requestId) { filterChain.doFilter(request, response) }
    }
}
