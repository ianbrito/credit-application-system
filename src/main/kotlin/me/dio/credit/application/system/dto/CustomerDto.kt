package me.dio.credit.application.system.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Customer
import org.hibernate.validator.constraints.br.CPF
import java.math.BigDecimal

data class CustomerDto(
    @field:NotEmpty(message = "firstName is required") val firstName: String,
    @field:NotEmpty(message = "lastName is required") val lastName: String,
    @field:NotEmpty(message = "cpf is required") @field:CPF(message = "cpf invalid") val cpf: String,
    @field:NotNull(message = "income is required") val income: BigDecimal,
    @field:NotEmpty(message = "email is required") @field:Email(message = "email invalid") val email: String,
    @field:NotEmpty(message = "password is required") val password: String,
    @field:NotEmpty(message = "zipCode is required") val zipCode: String,
    @field:NotEmpty(message = "street is required") val street: String
) {
    fun toEntity(): Customer = Customer(
        firstName = this.firstName,
        lastName = this.lastName,
        cpf = this.cpf,
        income = this.income,
        email = this.email,
        password = this.password,
        address = Address(
            zipCode = this.zipCode, street = this.street
        )
    )
}