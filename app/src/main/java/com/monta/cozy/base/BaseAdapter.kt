package com.monta.cozy.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.monta.cozy.BR

abstract class BaseAdapter<
        T,
        B : ViewDataBinding,
        L : BaseAdapter.OnItemClickListener>(private val onItemClickListener: L) :
    RecyclerView.Adapter<BaseAdapter.BaseViewHolder<B>>() {

    abstract val layoutItemRes: Int

    abstract val differ: AsyncListDiffer<T>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<B> {
        val binding = DataBindingUtil.inflate<B>(
            LayoutInflater.from(parent.context),
            layoutItemRes,
            parent,
            false
        )
        return BaseViewHolder(binding).also { setupViewHolder(it.binding) }
    }

    open fun setupViewHolder(binding: B) {
        binding.setVariable(BR.listener, onItemClickListener)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<B>, position: Int) {
        holder.binding.apply {
            setVariable(BR.item, differ.currentList[position])
            executePendingBindings()
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    open fun submitList(data: List<T>) {
        differ.submitList(data)
    }

    fun getList(): List<T> {
        return differ.currentList
    }

    class BaseViewHolder<B : ViewDataBinding>(val binding: B) :
        RecyclerView.ViewHolder(binding.root) {
    }

    interface OnItemClickListener {}
}