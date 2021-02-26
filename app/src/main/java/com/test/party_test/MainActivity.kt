package com.test.party_test

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    lateinit var adapter_party: RecyclerAdapterPartyList
    var PartyList = mutableListOf<Party>() //список для вечеринок
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        val linearLayoutManager = LinearLayoutManager(this@MainActivity)
        recyclerview_party.layoutManager = linearLayoutManager
        val jsoncontent = assets.open("Parties.json").bufferedReader() //считываем json файл из ресурсов
        var jsonobj = JSONObject(jsoncontent.use { it.readText() })  //создаем JSON объект
        var parties = jsonobj.getJSONArray("Parties")
        for(party in parties){
            PartyList.add(Party(partyID = party.getInt("PartyID"), name = party.getString("PartyName"), authorID = party.getInt("AuthorID"), imageURL = party.getString("imageURL")))
        }
        adapter_party = RecyclerAdapterPartyList(PartyList)
        recyclerview_party.adapter = adapter_party
        adapter_party.notifyDataSetChanged()
    }
}

//Адаптер для отображения списка вечеринок
class RecyclerAdapterPartyList(var partylist: MutableList<Party>): RecyclerView.Adapter<RecyclerAdapterPartyList.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.party_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val partylist: Party = partylist[position]
        holder.bind(partylist.name)
        holder.itemView.setTag(this)
        holder.itemView.setTag(R.string.party_id, partylist.partyID.toString())
    }

    override fun getItemCount(): Int {
        return partylist.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val intent = Intent(itemView.context, PartyActivity::class.java)
            intent.putExtra("partyid", itemView.getTag(R.string.party_id) as String)  //передаём id Вечеринки на второе activity
            itemView.context.startActivity(intent)
        }

        fun bind(name: String){
            val text = itemView.findViewById<TextView>(R.id.text_partyname)
            text.text = name
        }
    }
}

operator fun JSONArray.iterator(): Iterator<JSONObject> = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()