package com.clay.informhalal

data class googlePlace (
    val html_attributions: List<Any>,
    val results: List<Result>,
    val status: String
) {
    data class Result (
        val formatted_address: String,
        val geometry: Geometry,
        val icon: String,
        val id: String,
        val name: String,
        val opening_hours: OpeningHours,
        val photos: List<Photo>,
        val place_id: String,
        val plus_code: PlusCode,
        val rating: Double,
        val reference: String,
        val types: List<String>,
        val user_ratings_total: Int
    ) {
        class OpeningHours(
        )

        data class Geometry(
            val location: Location,
            val viewport: Viewport
        ) {
            data class Viewport(
                val northeast: Northeast,
                val southwest: Southwest
            ) {
                data class Southwest(
                    val lat: Double,
                    val lng: Double
                )

                data class Northeast(
                    val lat: Double,
                    val lng: Double
                )
            }

            data class Location(
                val lat: Double,
                val lng: Double
            )
        }

        data class PlusCode(
            val compound_code: String,
            val global_code: String
        )

        data class Photo(
            val height: Int,
            val html_attributions: List<String>,
            val photo_reference: String,
            val width: Int
        )

        override fun toString(): String {
            return name
        }
    }
}