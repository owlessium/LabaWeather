package com.example.labaweather

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object DialogManager {
    fun locationSettingsDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("Разрешить определение местоположения?")
        dialog.setMessage("Определение местоположения запрещено, хотите разрешить?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Да") { _, _ ->
            listener.onClick(null)
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Нет") { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }


    fun searchByCityDialog(context: Context, listener: Listener) {
        val database = Firebase.database
        val myRef = database.getReference("message")

        // Создаем список для хранения истории запросов
        val searchHistory = ArrayList<String>()

        // Получаем список истории запросов из Firebase и добавляем их в список searchHistory
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                searchHistory.clear()
                for (postSnapshot in dataSnapshot.children) {
                    searchHistory.add(postSnapshot.key!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.search_dialog, null)
        builder.setView(dialogView)

// Получаем ссылки на элементы в диалоговом окне
        val editText = dialogView.findViewById<EditText>(R.id.edit_text)
        val listView = dialogView.findViewById<ListView>(R.id.list_view)

// Создаем адаптер для списка истории запросов
        val adapter =
            ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, searchHistory)
        val dialog = builder.create()
// Устанавливаем адаптер для списка в ListView
        listView.adapter = adapter

// Устанавливаем обработчик кликов на элементах списка
        listView.setOnItemClickListener { _, _, position, _ ->
            editText.setText(searchHistory[position])
        }

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ок") { _, _ ->
            val searchQuery = editText.text.toString().trim()

            // Если запрос не пустой, то сохраняем его в Firebase и закрываем диалоговое окно
            if (searchQuery.isNotEmpty()) {
                myRef.child(searchQuery).setValue(true)
                listener.onClick(searchQuery)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Введите название города", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Отмена") { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    fun getList(dRef: DatabaseReference) {
        dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    interface Listener {
        fun onClick(name: String?)
    }
}