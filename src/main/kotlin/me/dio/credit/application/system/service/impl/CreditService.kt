package me.dio.credit.application.system.service.impl

import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.service.ICreditService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class CreditService(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService,
) : ICreditService {

    override fun save(credit: Credit): Credit {
        this.validDayFirstInstallment(credit.dayFirstInstallment)
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findAllByCustomer(customerId: Long): List<Credit> {
        val customer: Customer  = this.customerService.findById(customerId)
        return this.creditRepository.findAllByCustomerId(customer.id!!)
    }

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit: Credit = this.creditRepository.findByCreditCode(creditCode)
            ?: throw BusinessException("Creditcode $creditCode not found")
        return if (credit.customer?.id == customerId) credit else throw IllegalArgumentException("Contact admin")
    }

    private fun validDayFirstInstallment(dayFirstInstallment: LocalDate): Boolean {
        val maxMonth = LocalDate.now().plusMonths(3)
        return if (dayFirstInstallment.isBefore(maxMonth)) true else throw BusinessException("Invalid Date")
    }
}