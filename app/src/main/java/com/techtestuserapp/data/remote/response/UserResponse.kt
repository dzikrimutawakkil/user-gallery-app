package com.techtestuserapp.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.techtestuserapp.data.local.entity.UserEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("address") val address: AddressResponse,
    @SerializedName("phone") val phone: String,
    @SerializedName("website") val website: String,
    @SerializedName("company") val company: CompanyResponse
) : Parcelable {
    fun toUserEntity(): UserEntity {
        return UserEntity(
            id = this.id,
            name = this.name,
            email = this.email,
            phone = this.phone,
            address = "${this.address.street}, ${this.address.suite}, ${this.address.city}, ${this.address.zipcode}",
            company = this.company.name
        )
    }
}

@Parcelize
data class AddressResponse(
    @SerializedName("street") val street: String,
    @SerializedName("suite") val suite: String,
    @SerializedName("city") val city: String,
    @SerializedName("zipcode") val zipcode: String,
    @SerializedName("geo") val geo: GeoResponse
) : Parcelable

@Parcelize
data class GeoResponse(
    @SerializedName("lat") val lat: String,
    @SerializedName("lng") val lng: String
) : Parcelable

@Parcelize
data class CompanyResponse(
    @SerializedName("name") val name: String,
    @SerializedName("catchPhrase") val catchPhrase: String,
    @SerializedName("bs") val bs: String
) : Parcelable