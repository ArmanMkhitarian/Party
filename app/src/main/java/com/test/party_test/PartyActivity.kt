package com.test.party_test

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject

class PartyActivity : AppCompatActivity(){
    var partyid: String = ""
    var UserList = mutableListOf<User>() //список user
    var UserOfPartyList = mutableListOf<UserOfParty>() //список UserOfParty
    var PartyList = mutableListOf<Party>() //список вечеринок
    var GuestList = mutableListOf<User>() //список приглашенных гостей
    lateinit var adapter_guest: RecyclerAdapterGuestList
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_party)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        val linearLayoutManager = LinearLayoutManager(this@PartyActivity)
        val text_partyname = findViewById<TextView>(R.id.text_partyname)
        val text_partyauthorname = findViewById<TextView>(R.id.text_partyauthorname)
        val image_party = findViewById<ImageView>(R.id.image_party)
        val image_partyauthor = findViewById<CircleImageView>(R.id.image_partyauthor)
        val recyclerview_guests = findViewById<RecyclerView>(R.id.recyclerview_guests)
        recyclerview_guests.layoutManager = linearLayoutManager
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  //добавляем кнопку назад на ActionBar
        (this as AppCompatActivity).supportActionBar?.title = "Вечеринка"  //устанавливаем текст на actonbar
        partyid = intent?.extras?.getString("partyid") ?: ""  //получаем PartyId

        val jsoncontent = assets.open("Parties.json").bufferedReader() //считываем json файл из ресурсов
        val jsonobj = JSONObject(jsoncontent.use { it.readText() })  //создаем JSON объект

        val Users = jsonobj.getJSONArray("Users") //получаем json-массив users
        for(user in Users){
            UserList.add(User(user.getInt("UserID"), user.getString("UserName"), user.getString("imageURL")))
        }

        val UsersofPartys = jsonobj.getJSONArray("UsersofParty") //получаем json-массив UsersofParty
        for(UsersofParty in UsersofPartys){
            UserOfPartyList.add(UserOfParty(UsersofParty.getInt("PartyID"), UsersofParty.getInt("UserID")))
        }

        val parties = jsonobj.getJSONArray("Parties")  //получаем json-массив Вечеринок
        for(party in parties){
            PartyList.add(Party(partyID = party.getInt("PartyID"), name = party.getString("PartyName"), authorID = party.getInt("AuthorID"), imageURL = party.getString("imageURL")))
        }

        val party_Select = PartyList.find { it.partyID == partyid.toInt() } //Находим выбранную вечеринку из списка вечеринок
        if(party_Select != null){
            text_partyname.text = party_Select.name
            Glide.with(this).load(party_Select.imageURL).into(image_party); //загрузка фото по URL
            val user_select = UserList.find { it.UserID == party_Select.authorID } //ищем автора вечеринки
            if(user_select != null){
                text_partyauthorname.text = "Пригласил(а):" + user_select.UserName
                Glide.with(this).load(user_select.imageURL).into(image_partyauthor); //загрузка фото по URL
            }

            for(user in UserOfPartyList){
                if(user.PartyID == partyid.toInt()){
                    val guest = UserList.find { it.UserID == user.UserID }
                    if(guest != null)
                        GuestList.add(guest)
                }
            }

            adapter_guest = RecyclerAdapterGuestList(GuestList)
            recyclerview_guests.adapter = adapter_guest
            adapter_guest.notifyDataSetChanged()
        }
    }

    override fun onSupportNavigateUp(): Boolean {  //на главную форму
        onBackPressed()
        return true
    }
}

//адаптер для отображения списка гостей
class RecyclerAdapterGuestList(var UserList: MutableList<User>): RecyclerView.Adapter<RecyclerAdapterGuestList.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.guest_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       var UserList: User = UserList[position]
        holder.bind(UserList.UserName, UserList.imageURL)
    }

    override fun getItemCount(): Int {
        return UserList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(guestname: String, imageURL: String){
            val text_guestname = itemView.findViewById<TextView>(R.id.text_guestname)
            val circleImageView_guest = itemView.findViewById<CircleImageView>(R.id.circleImageView_guest)
            text_guestname.text = guestname
            Glide.with(itemView.context).load(imageURL).into(circleImageView_guest);
        }
    }
}