@file:Suppress("NOTHING_TO_INLINE")

package com.hendraanggrian.openpss.widget

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

inline fun TextView.watcher(noinline onTextChanged: (s: CharSequence, count: Int) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            onTextChanged(s, count)
        }
    })
}