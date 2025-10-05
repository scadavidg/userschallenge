package com.data.mapper

import com.data.dto.LocationDto
import com.data.dto.UserCreateDto
import com.data.dto.UserFullDto
import com.data.dto.UserPreviewDto
import com.data.mapper.UserMapper.toCreateDto
import com.data.mapper.UserMapper.toDomain
import com.data.mapper.UserMapper.toDto
import com.data.mapper.UserMapper.toFullDto
import com.data.mapper.UserMapper.toPreviewDto
import com.domain.models.Location
import com.domain.models.UserDetail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DisplayName("UserMapper Tests")
class UserMapperTest {
    private lateinit var sampleLocationDto: LocationDto
    private lateinit var sampleLocation: Location
    private lateinit var sampleUserPreviewDto: UserPreviewDto
    private lateinit var sampleUserFullDto: UserFullDto
    private lateinit var sampleUserCreateDto: UserCreateDto
    private lateinit var sampleUser: UserDetail

    @BeforeEach
    fun setUp() {
        // Given - Sample data setup
        sampleLocationDto =
            LocationDto(
                street = "123 Main St",
                city = "New York",
                state = "NY",
                country = "USA",
                timezone = "-5:00",
            )

        sampleLocation =
            Location(
                street = "123 Main St",
                city = "New York",
                state = "NY",
                country = "USA",
                timezone = "-5:00",
            )

        sampleUserPreviewDto =
            UserPreviewDto(
                id = "1",
                title = "mr",
                firstName = "John",
                lastName = "Doe",
                picture = "https://example.com/picture.jpg",
            )

        sampleUserFullDto =
            UserFullDto(
                id = "1",
                title = "mr",
                firstName = "John",
                lastName = "Doe",
                picture = "https://example.com/picture.jpg",
                gender = "male",
                email = "john.doe@example.com",
                dateOfBirth = "1990-01-01",
                registerDate = "2023-01-01",
                phone = "123456789",
                location = sampleLocationDto,
            )

        sampleUserCreateDto =
            UserCreateDto(
                firstName = "John",
                lastName = "Doe",
                email = "john.doe@example.com",
                title = "mr",
                gender = "male",
                dateOfBirth = "1990-01-01",
                phone = "123456789",
                picture = "https://example.com/picture.jpg",
                location = sampleLocationDto,
            )

        sampleUser =
            UserDetail(
                id = "1",
                title = "mr",
                firstName = "John",
                lastName = "Doe",
                picture = "https://example.com/picture.jpg",
                gender = "male",
                email = "john.doe@example.com",
                dateOfBirth = "1990-01-01",
                phone = "123456789",
                location = sampleLocation,
                registerDate = "2023-01-01",
                updatedDate = "2023-01-01",
            )
    }

    @Nested
    @DisplayName("Given UserPreviewDto")
    inner class GivenUserPreviewDto {
        @Nested
        @DisplayName("When converting to domain")
        inner class WhenConvertingToDomain {
            @Test
            @DisplayName("Then should map all available fields correctly")
            fun `should map all available fields correctly`() {
                // When
                val result = sampleUserPreviewDto.toDomain()

                // Then
                assertEquals("1", result.id)
                assertEquals("mr", result.title)
                assertEquals("John", result.firstName)
                assertEquals("Doe", result.lastName)
                assertEquals("https://example.com/picture.jpg", result.picture)
                // UserPreview only has basic properties, no gender, email, etc.
            }
        }
    }

    @Nested
    @DisplayName("Given UserFullDto")
    inner class GivenUserFullDto {
        @Nested
        @DisplayName("When converting to domain")
        inner class WhenConvertingToDomain {
            @Test
            @DisplayName("Then should map all fields correctly")
            fun `should map all fields correctly`() {
                // When
                val result = sampleUserFullDto.toDomain()

                // Then
                assertEquals("1", result.id)
                assertEquals("mr", result.title)
                assertEquals("John", result.firstName)
                assertEquals("Doe", result.lastName)
                assertEquals("https://example.com/picture.jpg", result.picture)
                assertEquals("male", result.gender)
                assertEquals("john.doe@example.com", result.email)
                assertEquals("1990-01-01", result.dateOfBirth)
                assertEquals("123456789", result.phone)
                assertNotNull(result.location)
                assertEquals("123 Main St", result.location?.street)
                assertEquals("New York", result.location?.city)
                assertEquals("NY", result.location?.state)
                assertEquals("USA", result.location?.country)
                assertEquals("-5:00", result.location?.timezone)
                assertEquals("2023-01-01", result.registerDate)
                assertEquals("2023-01-01", result.updatedDate)
            }

            @Test
            @DisplayName("Then should handle null location correctly")
            fun `should handle null location correctly`() {
                // Given
                val userFullDtoWithNullLocation = sampleUserFullDto.copy(location = null)

                // When
                val result = userFullDtoWithNullLocation.toDomain()

                // Then
                assertEquals("1", result.id)
                assertEquals("mr", result.title)
                assertEquals("John", result.firstName)
                assertEquals("Doe", result.lastName)
                assertEquals("https://example.com/picture.jpg", result.picture)
                assertEquals("male", result.gender)
                assertEquals("john.doe@example.com", result.email)
                assertEquals("1990-01-01", result.dateOfBirth)
                assertEquals("123456789", result.phone)
                assertNull(result.location)
                assertEquals("2023-01-01", result.registerDate)
                assertEquals("2023-01-01", result.updatedDate)
            }
        }
    }

    @Nested
    @DisplayName("Given User")
    inner class GivenUser {
        @Nested
        @DisplayName("When converting to UserFullDto")
        inner class WhenConvertingToUserFullDto {
            @Test
            @DisplayName("Then should map all fields correctly")
            fun `should map all fields correctly`() {
                // When
                val result = sampleUser.toFullDto()

                // Then
                assertEquals("1", result.id)
                assertEquals("mr", result.title)
                assertEquals("John", result.firstName)
                assertEquals("Doe", result.lastName)
                assertEquals("https://example.com/picture.jpg", result.picture)
                assertEquals("male", result.gender)
                assertEquals("john.doe@example.com", result.email)
                assertEquals("1990-01-01", result.dateOfBirth)
                assertEquals("123456789", result.phone)
                assertNotNull(result.location)
                assertEquals("123 Main St", result.location?.street)
                assertEquals("New York", result.location?.city)
                assertEquals("NY", result.location?.state)
                assertEquals("USA", result.location?.country)
                assertEquals("-5:00", result.location?.timezone)
                assertEquals("2023-01-01", result.registerDate)
            }

            @Test
            @DisplayName("Then should handle null location correctly")
            fun `should handle null location correctly`() {
                // Given
                val userWithNullLocation = sampleUser.copy(location = null)

                // When
                val result = userWithNullLocation.toFullDto()

                // Then
                assertEquals("1", result.id)
                assertEquals("mr", result.title)
                assertEquals("John", result.firstName)
                assertEquals("Doe", result.lastName)
                assertEquals("https://example.com/picture.jpg", result.picture)
                assertEquals("male", result.gender)
                assertEquals("john.doe@example.com", result.email)
                assertEquals("1990-01-01", result.dateOfBirth)
                assertEquals("123456789", result.phone)
                assertNull(result.location)
                assertEquals("2023-01-01", result.registerDate)
            }
        }

        @Nested
        @DisplayName("When converting to UserPreviewDto")
        inner class WhenConvertingToUserPreviewDto {
            @Test
            @DisplayName("Then should map only preview fields")
            fun `should map only preview fields`() {
                // When
                // Create a UserPreview from UserDetail for testing
                val userPreview = com.domain.models.UserPreview(
                    id = sampleUser.id,
                    title = sampleUser.title,
                    firstName = sampleUser.firstName,
                    lastName = sampleUser.lastName,
                    picture = sampleUser.picture
                )
                val result = userPreview.toPreviewDto()

                // Then
                assertEquals("1", result.id)
                assertEquals("mr", result.title)
                assertEquals("John", result.firstName)
                assertEquals("Doe", result.lastName)
                assertEquals("https://example.com/picture.jpg", result.picture)
            }
        }

        @Nested
        @DisplayName("When converting to UserCreateDto")
        inner class WhenConvertingToUserCreateDto {
            @Test
            @DisplayName("Then should map only required and non-empty fields")
            fun `should map only required and non empty fields`() {
                // When
                val result = sampleUser.toCreateDto()

                // Then
                assertEquals("John", result.firstName)
                assertEquals("Doe", result.lastName)
                assertEquals("john.doe@example.com", result.email)
                assertEquals("mr", result.title)
                assertEquals("male", result.gender)
                assertEquals("1990-01-01", result.dateOfBirth)
                assertEquals("123456789", result.phone)
                assertEquals("https://example.com/picture.jpg", result.picture)
                assertNotNull(result.location)
            }

            @Test
            @DisplayName("Then should handle empty fields as null")
            fun `should handle empty fields as null`() {
                // Given
                val userWithEmptyFields =
                    sampleUser.copy(
                        title = "",
                        gender = "",
                        dateOfBirth = "",
                        phone = "",
                        picture = "",
                        location = null,
                    )

                // When
                val result = userWithEmptyFields.toCreateDto()

                // Then
                assertEquals("John", result.firstName)
                assertEquals("Doe", result.lastName)
                assertEquals("john.doe@example.com", result.email)
                assertNull(result.title)
                assertNull(result.gender)
                assertNull(result.dateOfBirth)
                assertNull(result.phone)
                assertNull(result.picture)
                assertNull(result.location)
            }
        }
    }

    @Nested
    @DisplayName("Given UserCreateDto")
    inner class GivenUserCreateDto {
        @Nested
        @DisplayName("When converting to domain")
        inner class WhenConvertingToDomain {
            @Test
            @DisplayName("Then should map all fields with defaults for missing values")
            fun `should map all fields with defaults for missing values`() {
                // When
                val result = sampleUserCreateDto.toDomain()

                // Then
                assertEquals("", result.id) // Will be generated by server
                assertEquals("mr", result.title)
                assertEquals("John", result.firstName)
                assertEquals("Doe", result.lastName)
                assertEquals("https://example.com/picture.jpg", result.picture)
                assertEquals("male", result.gender)
                assertEquals("john.doe@example.com", result.email)
                assertEquals("1990-01-01", result.dateOfBirth)
                assertEquals("123456789", result.phone)
                assertNotNull(result.location)
                assertEquals("", result.registerDate) // Will be set by server
                assertEquals("", result.updatedDate) // Will be set by server
            }

            @Test
            @DisplayName("Then should handle null optional fields with empty strings")
            fun `should handle null optional fields with empty strings`() {
                // Given
                val userCreateWithNulls =
                    UserCreateDto(
                        firstName = "Jane",
                        lastName = "Smith",
                        email = "jane.smith@example.com",
                        title = null,
                        gender = null,
                        dateOfBirth = null,
                        phone = null,
                        picture = null,
                        location = null,
                    )

                // When
                val result = userCreateWithNulls.toDomain()

                // Then
                assertEquals("", result.id)
                assertEquals("", result.title)
                assertEquals("Jane", result.firstName)
                assertEquals("Smith", result.lastName)
                assertEquals("", result.picture)
                assertEquals("", result.gender)
                assertEquals("jane.smith@example.com", result.email)
                assertEquals("", result.dateOfBirth)
                assertEquals("", result.phone)
                assertNull(result.location)
                assertEquals("", result.registerDate)
                assertEquals("", result.updatedDate)
            }
        }
    }

    @Nested
    @DisplayName("Given LocationDto")
    inner class GivenLocationDto {
        @Nested
        @DisplayName("When converting to domain")
        inner class WhenConvertingToDomain {
            @Test
            @DisplayName("Then should map all fields correctly")
            fun `should map all fields correctly`() {
                // When
                val result = sampleLocationDto.toDomain()

                // Then
                assertEquals("123 Main St", result.street)
                assertEquals("New York", result.city)
                assertEquals("NY", result.state)
                assertEquals("USA", result.country)
                assertEquals("-5:00", result.timezone)
            }
        }
    }

    @Nested
    @DisplayName("Given Location")
    inner class GivenLocation {
        @Nested
        @DisplayName("When converting to DTO")
        inner class WhenConvertingToDto {
            @Test
            @DisplayName("Then should map all fields correctly")
            fun `should map all fields correctly`() {
                // When
                val result = sampleLocation.toDto()

                // Then
                assertEquals("123 Main St", result.street)
                assertEquals("New York", result.city)
                assertEquals("NY", result.state)
                assertEquals("USA", result.country)
                assertEquals("-5:00", result.timezone)
            }
        }
    }
}
