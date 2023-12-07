package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.CreditDto
import me.dio.credit.application.system.dto.CustomerDto
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.enummeration.Status
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Random
import java.util.UUID

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/credits"
    }

    @BeforeEach
    fun setup() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        creditRepository.deleteAll()
        customerRepository.deleteAll()
    }

    @Test
    fun `should create a credit and return 201 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDto()
        val customer: Customer = customerRepository.save(customerDto.toEntity())

        val creditDto: CreditDto = builderCreditDto(customerId = customer.id)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(CreditResourceTest.URL).contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        ).andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(creditDto.creditValue))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallment").value(creditDto.numberOfInstallments))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.IN_PROGRESS.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value(customer.income))
            .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(customer.id))
            .andDo(MockMvcResultHandlers.print())
    }


    @Test
    fun `should not find credit with invalid creditCode and return 400 status`() {
        val invalidCreditCode: String = UUID.randomUUID().toString()

        val customerDto: CustomerDto = builderCustomerDto()
        val customer: Customer = customerRepository.save(customerDto.toEntity())

        mockMvc.perform(
            MockMvcRequestBuilders
                .get("${CreditResourceTest.URL}/${invalidCreditCode}")
                .param("customerId", customer.id.toString())
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class me.dio.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find credit with valid creditCode and invalid customerId and return 400 status`() {
        val customerDto: CustomerDto = builderCustomerDto()
        val customer: Customer = customerRepository.save(customerDto.toEntity())

        val creditDto: CreditDto = builderCreditDto(customerId = customer.id)
        val credit: Credit = creditRepository.save(creditDto.toEntity())

        val invalidCustomerId: Long = Random().nextLong()

        mockMvc.perform(
            MockMvcRequestBuilders
                .get("${CreditResourceTest.URL}/${credit.creditCode.toString()}")
                .param("customerId", invalidCustomerId.toString())
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class java.lang.IllegalArgumentException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }


    @Test
    fun `should find credits by customerId and return 200 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDto()
        val customer: Customer = customerRepository.save(customerDto.toEntity())

        val creditDto: CreditDto = builderCreditDto(customerId = customer.id)
        val credit: Credit = creditRepository.save(creditDto.toEntity())

        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("${CreditResourceTest.URL}")
                .param("customerId", customer.id.toString())
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.[*]").isNotEmpty())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find credits with invalid customerId and return 400 status`() {
        //given
        val invalidCustomerId: Long = Random().nextLong()

        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders
                .get("${CreditResourceTest.URL}")
                .param("customerId", invalidCustomerId.toString())
                .accept(MediaType.APPLICATION_JSON)
        ) .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class me.dio.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    private fun builderCustomerDto(
        firstName: String = "Cami",
        lastName: String = "Cavalcante",
        cpf: String = "28475934625",
        email: String = "camila@email.com",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        password: String = "1234",
        zipCode: String = "000000",
        street: String = "Rua da Cami, 123",
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )


    private fun builderCreditDto(
        creditValue: BigDecimal = BigDecimal.valueOf(3000.0),
        dayFirstOfInstallment: LocalDate = LocalDate.now().plusMonths(1),
        numberOfInstallments: Int = 12,
        customerId: Long? = null
    ) = CreditDto(
        creditValue = creditValue,
        dayFirstOfInstallment = dayFirstOfInstallment,
        numberOfInstallments = numberOfInstallments,
        customerId = customerId!!
    )

}