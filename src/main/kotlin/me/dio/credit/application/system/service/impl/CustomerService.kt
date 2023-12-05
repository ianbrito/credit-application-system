package me.dio.credit.application.system.service.impl

import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.service.ICustomerService
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class CustomerService(private val repository: CustomerRepository) : ICustomerService {

    override fun save(customer: Customer): Customer {
        return this.repository.save(customer)
    }

    override fun findById(id: Long): Customer {
        return this.repository.findById(id).orElseThrow {
            throw RuntimeException("Id not found")
        }
    }

    override fun delete(id: Long) {
        this.repository.deleteById(id)
    }
}