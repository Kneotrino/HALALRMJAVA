package com.clay.halalrm.tools

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.github.kittinunf.fuel.httpGet
import java.io.IOException


object requestHandler {

    fun loadJSONFromAsset(context: Context, fileName : String ): String {
        Log.d("req JSON",fileName)
        var json: String
        try {
            val input = context.assets.open(fileName)
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            json = String(buffer)

        } catch (ex: IOException) {
            ex.printStackTrace()
            return "FAILED -1"
        }
        return json
    }

    //    var hasil:String = ""
    fun sendGetRequest(requestURL: String): String {
        val (_, _, result) =
            requestURL
                .httpGet()
                .responseString() // result is Result<String, FuelError>
        return result.get()
    }
//    fun sendDeleteRequest(requestURL: String): Response {
//        val (_, response, _) =
//            requestURL
//                .httpDelete()
//                .responseString() // result is Result<String, FuelError>
//        return response
//    }
//    fun sendPostRequest(requestURL: String, body : String): Response {
//        val (request, response, result) =
//            requestURL
//                .httpPost()
//                .header("Content-Type" to "application/xml")
//                .body(body)
//                .responseString() // result is Result<String, FuelError>
//        return response
//    }
//    fun sendPutRequest(requestURL: String, body : String): Response {
//        val (request, response, result) =
//            requestURL
//                .httpPut()
//                .header("Content-Type" to "application/xml")
//                .body(body)
//                .responseString() // result is Result<String, FuelError>
//        return response
//    }
//
    fun readingRest(context: Context, URL: String): String {
        class Reading : AsyncTask<Void, Void, String>() {

            override fun doInBackground(vararg params: Void): String {
                return sendGetRequest(URL)
            }
        }

        val reading = Reading()
        reading.execute()
        return reading.get()
    }
//    fun deletingRest(context: Context, URL: String): Response {
//        class Deleting : AsyncTask<Void, Void, Response>() {
//            override fun onPostExecute(r: Response) {
//                super.onPostExecute(r)
//                Log.d("Code",r.statusCode.toString() )
//                Log.d("MSG",r.responseMessage)
//                if (r.statusCode == 410)
//                    Toast.makeText(context, "TERHAPUS", Toast.LENGTH_SHORT).show()
//                else
//                    Toast.makeText(context, "DELETE ERROR", Toast.LENGTH_SHORT).show()
//            }
//            override fun doInBackground(vararg params: Void): Response {
//                return requestHandler.sendDeleteRequest(URL)
//            }
//        }
//        val deleting = Deleting()
//        deleting.execute()
//        return deleting.get()
//    }
//    fun editingRest(context: Context, URL: String, Body : String): Response {
//        class Editing : AsyncTask<Void, Void, Response>() {
//            override fun onPostExecute(r: Response) {
//                super.onPostExecute(r)
//                Log.d("Code",r.statusCode.toString() )
//                Log.d("MSG",r.responseMessage)
//                if (r.statusCode == 202)
//                    Toast.makeText(context, "TERSIMPAN", Toast.LENGTH_SHORT).show()
//                else
//                    Toast.makeText(context, "EDIT ERROR", Toast.LENGTH_SHORT).show()
//            }
//            override fun doInBackground(vararg params: Void): Response {
//                return requestHandler.sendPutRequest(URL,Body)
//            }
//        }
//        val editing = Editing()
//        editing.execute()
//        return editing.get()
//    }
//    fun creatingRest(context: Context, URL: String, Body : String): Response {
//        class Creating : AsyncTask<Void, Void, Response>() {
//            override fun onPostExecute(r: Response) {
//                super.onPostExecute(r)
//                Log.d("Code",r.statusCode.toString() )
//                Log.d("MSG",r.responseMessage)
//                if (r.statusCode == 201)
//                    Toast.makeText(context, "DAFTAR ERROR", Toast.LENGTH_SHORT).show()
//            }
//            override fun doInBackground(vararg params: Void): Response {
//                return requestHandler.sendPostRequest (URL,Body)
//            }
//        }
//        val creating = Creating()
//        creating.execute()
//        return creating.get()
//    }



}