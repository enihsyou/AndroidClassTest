package com.enihsyou.android.musicplayer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_pics.*
import org.jetbrains.anko.appcompat.v7.coroutines.onQueryTextListener
import org.jetbrains.anko.ctx

class PicsActivity : AppCompatActivity() {
    private lateinit var apiService: EverythingVolleyApiService
    private var items: MutableList<EverythingFile> = mutableListOf()
    private var keyword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pics)
        setSupportActionBar(toolbar_pics)
        apiService = EverythingVolleyApiService(ctx)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        pics_recycler?.apply {
            adapter = EverythingPicsListAdapter(items)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.apply {
            keyword = getString(BUNDLE_SEARCH_KEYWORD) ?: keyword
            items = getParcelableArrayList(BUNDLE_SEARCH_RESULTS) ?: items
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.apply {
            putString(BUNDLE_SEARCH_KEYWORD, keyword)
            putParcelableArrayList(BUNDLE_SEARCH_RESULTS, ArrayList(items))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.local, menu)
        val searchMenu = menu.findItem(R.id.menu_search).actionView as SearchView
        searchMenu.apply {
            queryHint = "键入搜索"
            setQuery(keyword, false)
            onQueryTextListener {
                onQueryTextSubmit { searchItem(it ?: "") }
                clearFocus()
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun searchItem(query: String): Boolean {
        val onSuccess: (List<EverythingFile>) -> Unit = {
            items.clear()
            items.addAll(it)
            pics_recycler.adapter.notifyDataSetChanged()
        }

        apiService.search(
            "D:/Pictures/blur file:$query ext:jpg|ext:png",
            onSuccess,
            { items.clear().also { pics_recycler.adapter.notifyDataSetChanged() } }
        )
        keyword = query
        return true
    }

    companion object {

        const val BUNDLE_SEARCH_KEYWORD = "keyword"
        const val BUNDLE_SEARCH_RESULTS = "results"
    }
}

private class EverythingPicsListAdapter(
    private val items: List<EverythingFile>
) : RecyclerView.Adapter<EverythingPicsListAdapter.EverythingHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EverythingHolder {
        return EverythingHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_picasso, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: EverythingHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class EverythingHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val image: ImageView = view.findViewById(R.id.item_img_main)
        val title: TextView = view.findViewById(R.id.item_img_name)

        /*https://futurestud.io/tutorials/picasso-image-resizing-scaling-and-fit*/
        fun bind(file: EverythingFile) {
            Picasso.get()
                .load(file.url)
                .also { println("load ${file.url}") }
                .fit()
                .centerInside()
                .into(image)
            title.text = file.name
        }
    }
}
