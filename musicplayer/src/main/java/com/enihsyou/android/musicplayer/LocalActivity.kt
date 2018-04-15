package com.enihsyou.android.musicplayer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_local.*
import org.jetbrains.anko.appcompat.v7.coroutines.onQueryTextListener
import org.jetbrains.anko.ctx
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class LocalActivity : AppCompatActivity() {
    private lateinit var apiService: EverythingVolleyApiService
    private var items: MutableList<EverythingFile> = mutableListOf()
    private var keyword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local)
        setSupportActionBar(toolbar_local)
        apiService = EverythingVolleyApiService(ctx)

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        local_recycler.apply {
            adapter = EverythingFileListAdapter(items) { position, _ ->
                val file = items[position]
                toast("播放视频「${file.name}」")
                startActivity<VideoActivity>(file.url)
            }
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
            local_recycler.adapter.notifyDataSetChanged()
        }

        apiService.search(
            "path:file:$query ext:mp4|ext:mkv|ext:mp3",
            onSuccess,
            { items.clear().also { local_recycler.adapter.notifyDataSetChanged() } }
        )

        keyword = query
        return true
    }

    companion object {

        const val BUNDLE_SEARCH_KEYWORD = "keyword"
        const val BUNDLE_SEARCH_RESULTS = "results"
    }
}

/*https://www.jianshu.com/p/d6fa7bbe80af*/
private class EverythingFileListAdapter(
    private val items: List<EverythingFile>,
    private val clickListener: ((position: Int, v: View) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            VIEW_TYPE_EMPTY -> object : RecyclerView.ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_empty, parent, false)
            ) {}

            else            -> EverythingHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_everything, parent, false)
            )
        }

    override fun getItemCount(): Int = if (items.isEmpty()) 1 else items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when (holder) {
            is EverythingHolder -> holder.bind(items[position])

            else                -> Unit
        }

    /*https://stackoverflow.com/a/31671289*/
    inner class EverythingHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.item_text_filename)
        val size: TextView = view.findViewById(R.id.item_text_filesize)

        init {
            view.onClick { clickListener?.invoke(adapterPosition, view) }
        }

        fun bind(file: EverythingFile) {
            name.text = file.name
            size.text = file.size
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (items.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_EVERYTHING

    companion object {
        const val VIEW_TYPE_EMPTY = 0
        const val VIEW_TYPE_EVERYTHING = 1
    }
}
