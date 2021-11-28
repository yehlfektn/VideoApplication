package kz.laccent.util

import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.videoapplication.BuildConfig
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*


inline fun debug(code: () -> Unit) {
    if (BuildConfig.DEBUG) {
        code()
    }
}

//inline fun <T> catchIfError(code: () -> Resource<T>): Resource<T>? {
//    return try {
//        code()
//    } catch (e: HttpException) {
//        Resource.error(null, e.message())
//    } catch (e: ErrorResourceException) {
//        e.getErrorResponse() as Resource<T>
//    } catch (e: CancellationException) {
//        null
//    } catch (e: Exception) {
//        if (e is TranslatableException) {
//            Resource.error(null, e.getDefaultResourceId())
//        } else {
//            e.printStackTrace()
//            Resource.error(null, e.localizedMessage)
//        }
//    }
//}


fun SimpleDateFormat.formatOrNull(obj: Any?): String? {
    return try {
        format(obj)
    } catch (e: Exception) {
        null
    }
}

fun SimpleDateFormat.parseOrNull(dateInString: String): Date? {
    return try {
        parse(dateInString)
    } catch (e: Exception) {
        null
    }
}

fun Fragment.showMaterialDatePicker(
    minDate: Long = 631152060000,
    maxDate: Long = 18303062460000,
    callback: (date: Date) -> Unit
) {
    if (!isAdded) return
    val calendar = Calendar.getInstance()

    val dialog = DatePickerDialog(
        context!!,
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            callback(calendar.time)
        }, calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    // first, select year
    val modifiedMax = Calendar.getInstance()
    modifiedMax.timeInMillis = maxDate
    modifiedMax.set(Calendar.HOUR_OF_DAY, 23)
    modifiedMax.set(Calendar.MINUTE, 59)
    modifiedMax.set(Calendar.SECOND, 59)
    dialog.datePicker.maxDate = modifiedMax.timeInMillis //calendar.timeInMillis
    dialog.datePicker.minDate = minDate
    dialog.setTitle("")
    dialog.show()
}

fun Fragment.showToast(stringId: Int?) {
    if (!isAdded || stringId == null || stringId <= 0) return
    Toast.makeText(activity, stringId, Toast.LENGTH_LONG).show()
}

fun Fragment.showSnackBar(stringId: Int) {
    Snackbar.make(view!!, stringId, Snackbar.LENGTH_LONG).show()
}

fun Fragment.showToast(message: String?) {
    if (!isAdded || message == null) return
    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.focusAndShowKeyboard() {
    /**
     * This is to be called when the window already has focus.
     */
    fun View.showTheKeyboardNow() {
        if (isFocused) {
            post {
                // We still post the call, just in case we are being notified of the windows focus
                // but InputMethodManager didn't get properly setup yet.
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        // No need to wait for the window to get focus.
        showTheKeyboardNow()
    } else {
        // We need to wait until the window gets focus.
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    // This notification will arrive just before the InputMethodManager gets set up.
                    if (hasFocus) {
                        this@focusAndShowKeyboard.showTheKeyboardNow()
                        // It’s very important to remove this listener once we are done.
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

//fun <T> Resource<T>.getOrNull(): T? {
//    return if (this.status == Status.SUCCESS) this.data
//    else null
//}