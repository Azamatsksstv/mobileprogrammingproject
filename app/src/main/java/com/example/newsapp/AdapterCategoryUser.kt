package com.example.newsapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.TextView
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.*
import com.example.newsapp.databinding.RowCategoryUserBinding

class AdapterCategoryUser : RecyclerView.Adapter<AdapterCategoryUser.HolderCategoryUser>, Filterable {

    private lateinit var context: Context
    public var categoryArrayList: ArrayList<ModelCategory>
    private var filterList: ArrayList<ModelCategory>
    private var filterUser: FilterCategoryUser? = null
    private lateinit var binding: RowCategoryUserBinding
    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList = categoryArrayList
    }
    inner class HolderCategoryUser(itemView: View): RecyclerView.ViewHolder(itemView){
        var categoryTv: TextView = binding.categoryTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCategoryUser.HolderCategoryUser {
        binding = RowCategoryUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCategoryUser(binding.root)
    }

    override fun onBindViewHolder(holder: AdapterCategoryUser.HolderCategoryUser, position: Int) {
        val model = categoryArrayList[position]
        val id = model.id
        val category = model.category
        holder.categoryTv.text = category

        holder.itemView.setOnClickListener{
            val intent = Intent(context, PdfListAdminActivity::class.java)
            intent.putExtra("categoryId", id)
            intent.putExtra("category", category)
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return categoryArrayList.size
    }
    override fun getFilter(): Filter {
        if (filterUser == null){
            filterUser = FilterCategoryUser(filterList, this)
        }
        return filterUser as FilterCategoryUser
    }
}