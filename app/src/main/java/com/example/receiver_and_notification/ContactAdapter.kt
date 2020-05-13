package com.example.receiver_and_notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(private val contacts: ArrayList<Contact>, var clickListner: OnItemClickListner):
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.contact_info, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.initialize(contacts[position],clickListner)
    }
    class ViewHolder(item: View): RecyclerView.ViewHolder(item){
        var nom:TextView = item.findViewById(R.id.nom)
        var numero:TextView = item.findViewById(R.id.numero)
        var select:Button = item.findViewById(R.id.selectBtn)

        fun initialize(item:Contact,action:OnItemClickListner){
            nom.text=item.nom
            numero.text=item.numero

            select.setOnClickListener {
                action.OnSelectItemClick(item,adapterPosition,nom,numero,select)
            }

        }
    }
    interface OnItemClickListner{

        fun OnSelectItemClick(item:Contact , Position:Int,nom : TextView,numero: TextView,select:Button){

        }
    }
}