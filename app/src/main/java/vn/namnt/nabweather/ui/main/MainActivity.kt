package vn.namnt.nabweather.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView

/**
 * @author namnt
 * @since 01/12/2022
 */
class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = AppCompatTextView(this).apply {
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            gravity = Gravity.CENTER
            setBackgroundColor(Color.WHITE)
            setTextColor(Color.BLACK)
            text = "Welcome"
            textSize = 12f
        }

        setContentView(textView)
    }
}