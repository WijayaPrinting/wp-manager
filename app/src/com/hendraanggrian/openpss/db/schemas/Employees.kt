package com.hendraanggrian.openpss.db.schemas

import com.hendraanggrian.openpss.db.Document
import com.hendraanggrian.openpss.db.Named
import com.hendraanggrian.openpss.db.NamedSchema
import kotlinx.nosql.Id
import kotlinx.nosql.mongodb.DocumentSchema
import kotlinx.nosql.string

object Employees : DocumentSchema<Employee>("employees", Employee::class), NamedSchema {
    override val name = string("name")
    val password = string("password")
}

data class Employee(
    override var name: String,
    var password: String
) : Document<Employees>, Named {
    companion object {
        const val DEFAULT_PASSWORD = "1234"
        val BACKDOOR: Employee = Employee("Test", "hendraganteng")

        fun new(name: String, password: String = DEFAULT_PASSWORD): Employee = Employee(name, password)
    }

    override lateinit var id: Id<String, Employees>

    var firstTimeLogin: Boolean = false

    /** Password are unused after login, clear for better security. */
    fun clearPassword() {
        firstTimeLogin = password == DEFAULT_PASSWORD
        password = ""
    }

    override fun toString(): String = name
}