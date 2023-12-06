package me.dio.credit.application.system.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "customers")
data class Customer(

    @Column(nullable = false)
    var firstName: String = "",

    @Column(nullable = false)
    var lastName: String = "",

    @Column(unique = true, nullable = false)
    val cpf: String = "",

    @Column(unique = true, nullable = false)
    var email: String = "",

    @Column(nullable = false)
    var password: String = "",

    @Column(nullable = false)
    @Embedded
    var address: Address = Address(),

    @Column(nullable = false)
    var income: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    @OneToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.REMOVE],
        mappedBy = "customer"
    )
    var credits: List<Credit> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
)
