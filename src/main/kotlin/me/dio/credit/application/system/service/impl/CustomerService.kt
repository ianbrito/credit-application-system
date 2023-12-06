package me.dio.credit.application.system.service.impl

import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.service.ICustomerService
import org.springframework.stereotype.Service

@Service
class CustomerService(private val repository: CustomerRepository) : ICustomerService {

    override fun save(customer: Customer): Customer {
        return this.repository.save(customer)
    }

    override fun findById(id: Long): Customer {
        return this.repository.findById(id).orElseThrow {
            throw BusinessException("Id not found")
        }
    }

    override fun delete(id: Long) {
        val customer: Customer  = this.findById(id)
        this.repository.delete(customer)
    }
}