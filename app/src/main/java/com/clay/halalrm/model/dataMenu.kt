package com.clay.informhalal

data class dataMenu(
    var results: List<Result?>?,
    var status: String?
) {
    data class Result(
        var Harga: String?,
        var id: Int?,
        var menu: String?


    ) {
        override fun toString(): String {
            return "$menu \t $Harga)"
        }
    }
}