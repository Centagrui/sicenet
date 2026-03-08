package com.example.sicenet.data.local

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri


class SicenetProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.sicenet.provider"
        private const val CARGA = 1
        private const val KARDEX = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "carga_academica", CARGA)
            addURI(AUTHORITY, "kardex", KARDEX)
        }
    }

    private lateinit var database: SicenetDatabase

    override fun onCreate(): Boolean {
        // Inicializamos la base de datos (asegúrate de tener una instancia accesible)
        database = SicenetDatabase.getDatabase(context!!)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<out String>?, selection: String?,
        selectionArgs: Array<out String>?, sortOrder: String?
    ): Cursor? {
        val dao = database.sicenetDao()
        return when (uriMatcher.match(uri)) {
            CARGA -> dao.obtenerCargaCursor()
            KARDEX -> dao.obtenerKardexCursor()
            else -> null
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            CARGA -> "vnd.android.cursor.dir/$AUTHORITY.carga_academica"
            KARDEX -> "vnd.android.cursor.dir/$AUTHORITY.kardex"
            else -> null
        }
    }

    // Los métodos insert, delete y update pueden quedar vacíos o retornar 0/null
    // ya que la práctica se enfoca en la consulta (Read).
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}