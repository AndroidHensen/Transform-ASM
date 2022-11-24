package com.dreamer.uidemo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    private var list = ArrayList<String>()
    private var rv_list: RecyclerView? = null
    private var iv_banner: ImageView? = null
    private var cardView: CardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv_list = findViewById(R.id.rv_list)
        iv_banner = findViewById(R.id.iv_banner)
        cardView = findViewById(R.id.cardView)
        initList()
        initView()
        initBanner()
        testLog()
    }

    private fun initBanner() {
        Glide.with(this)
            .load("https://img2.baidu.com/it/u=3341773124,2828544310&fm=253&fmt=auto&app=138&f=GIF?w=550&h=321")
            .into(iv_banner)
    }

    private fun initList() {
        for (i in 1..2) {
            list.add(i.toString() + "号选手")
        }
    }

    private fun initView() {
        rv_list?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv_list?.adapter = CustomAdapter(list)
    }

    private fun testLog() {
        Log.e("啦啦啦", "testLog:改吧改吧")
        Log.e("啦啦啦", "testLog:改吧改吧")
    }
}

class CustomAdapter(private val dataSet: ArrayList<String>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.textView)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position]
    }

    override fun getItemCount() = dataSet.size
}
