package com.example.mvisample.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.mvisample.Model.MainView
import com.example.mvisample.Presenter.MainPresenter
import com.example.mvisample.R
import com.example.mvisample.Utils.DataSource
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.jakewharton.rxbinding2.view.RxView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*

class MainActivity : MviActivity<MainView, MainPresenter>(), MainView {

    internal lateinit var imageList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageList = createImageList()
    }

    private fun createImageList(): List<String> {
        return Arrays.asList(
            "https://images.all-free-download.com/images/graphiclarge/cute_cat_02_hq_pictures_168994.jpg",
            "https://images.all-free-download.com/images/graphiclarge/white_cat_515522.jpg",
            "https://images.all-free-download.com/images/graphiclarge/cute_cat_03_hd_pictures_168993.jpg",
            "https://images.all-free-download.com/images/graphiclarge/cute_cat_514193.jpg"
        )
    }

    override fun createPresenter(): MainPresenter {
        return MainPresenter(DataSource(imageList))
    }

    override val imageIntent: Observable<Int>
        get() = RxView.clicks(get_pic_btn)
            .map {
                getRandomNumberInRange(0, imageList.size - 1)
            }

    private fun getRandomNumberInRange(min: Int, max: Int): Int? {
        if (min > max)
            throw IllegalArgumentException("Max must be greater than Min")
        val r = Random()
        return r.nextInt(max - min + 1) + min
    }

    override fun render(viewState: MainViewState) {
        if (viewState.isLoading) {

            progress_bar.visibility = View.VISIBLE
            imageview.visibility = View.GONE
            get_pic_btn.isEnabled = false

        } else if (viewState.error != null) {

            progress_bar.visibility = View.GONE
            imageview.visibility = View.GONE
            get_pic_btn.isEnabled = true
            Toast.makeText(this@MainActivity, viewState.error!!.message, Toast.LENGTH_LONG).show()

        } else if (viewState.isImageViewShow) {


            get_pic_btn.isEnabled = true
            Picasso.get().load(viewState.imageLink)
                .fetch(object : Callback{

                    override fun onSuccess() {
                        Picasso.get().load(viewState.imageLink).into(imageview)
                        progress_bar.visibility = View.GONE
                        imageview.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception?) {
                        progress_bar.visibility = View.GONE
                        Toast.makeText(this@MainActivity, e!!.message, Toast.LENGTH_LONG).show()
                    }
                })

        }
    }
}
